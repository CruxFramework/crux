/*
 * Copyright 2013 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.server.rest.core.dispatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cruxframework.crux.core.server.rest.core.EntityTag;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod.MethodReturn;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.state.ResourceStateConfig;
import org.cruxframework.crux.core.server.rest.state.ResourceStateHandler;
import org.cruxframework.crux.core.server.rest.state.ResourceStateHandler.ResourceState;
import org.cruxframework.crux.core.server.rest.util.DateUtil;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.HttpResponseCodes;
import org.cruxframework.crux.core.shared.rest.annotation.GET;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class StateHandler
{
	private final HttpRequest request;
	private final ResourceMethod resourceMethod;
	private String httpMethod;
	private String key;

	public StateHandler(ResourceMethod resourceMethod, HttpRequest request)
    {
		this.resourceMethod = resourceMethod;
		this.request = request;
		this.httpMethod = request.getHttpMethod();
		this.key = request.getUri().getRequestUri().toString();
    }
	
	public MethodReturn handledByCache()
	{
		if (resourceMethod.cacheInfo != null && resourceMethod.cacheInfo.isCacheEnabled())
		{
			return handleCacheableOperation();
		}
		return handleUncacheableOperation();
	}

	public void updateState(MethodReturn ret)
	{
		ResourceStateHandler resourceStateHandler = ResourceStateConfig.getResourceStateHandler();
		if (ret.getCacheInfo() != null && (ret.getCacheInfo().isCacheEnabled() || ret.isEtagGenerationEnabled())) // only GET can declare cache
		{
			long dateModified;
			long expires = 0;
			String etag;
			ResourceState resourceState = resourceStateHandler.get(key);
			if (!ret.getCacheInfo().isCacheEnabled())
			{
				expires = (GET.ONE_DAY*1000)+System.currentTimeMillis();
			}
			if (resourceState != null && !resourceState.isExpired())
			{
				etag = resourceState.getEtag();
				dateModified = resourceState.getDateModified();
				if (ret.getCacheInfo().isCacheEnabled())
				{
					expires = ret.getCacheInfo().defineExpires(System.currentTimeMillis());
				}
			}
			else
			{
				etag = generateEtag(ret.getReturn());
				dateModified = System.currentTimeMillis();
				if (ret.getCacheInfo().isCacheEnabled())
				{
					expires = ret.getCacheInfo().defineExpires(dateModified);
				}
			}
			resourceStateHandler.add(key, dateModified, expires, etag);
			ret.setDateModified(dateModified);
			EntityTag entityTag = (etag != null)?new EntityTag(etag):null;
			ret.setEtag(entityTag);
		}
		else
		{
			resourceStateHandler.remove(key);
		}
	}

	/**
	 * Handle Cacheable GETS
	 * @return
	 */
	private MethodReturn handleCacheableOperation()
	{
		ResourceStateHandler resourceStateHandler = ResourceStateConfig.getResourceStateHandler();
		ResourceState resourceState = resourceStateHandler.get(key);
		if (resourceState == null)
		{
			return null;
		}
		else
		{
			if (resourceState.isExpired())
			{
				return null;
			}
			ConditionalResponse conditionalResponse = evaluatePreconditions(resourceState);
			if (conditionalResponse != null)
			{
				return new MethodReturn(resourceMethod.hasReturnType, null, null, resourceMethod.cacheInfo, conditionalResponse, resourceMethod.isEtagGenerationEnabled());
			}
		}
		return null;
	}

	/**
	 * Handle PUT/POST/DELETE/uncacheable GETs
	 * @return
	 */
	private MethodReturn handleUncacheableOperation()
	{
		ResourceStateHandler resourceStateHandler = ResourceStateConfig.getResourceStateHandler();
		ResourceState resourceState = resourceStateHandler.get(key);
		ConditionalResponse conditionalResponse = evaluatePreconditions(resourceState == null || resourceState.isExpired()?null:resourceState);
		if (conditionalResponse == null)
		{
			return null;
		}
		return new MethodReturn(resourceMethod.hasReturnType, null, null, resourceMethod.cacheInfo, conditionalResponse, resourceMethod.isEtagGenerationEnabled());
	}

	private ConditionalResponse evaluatePreconditions(ResourceState resourceState)
	{
		ConditionalResponse etag = evaluateEtagPreConditions(resourceState);
		ConditionalResponse dateModified = evaluateDateModifiedPreConditions(resourceState);
		if (etag == null && dateModified == null)
		{
			return null;
		}
		else if (etag != null && dateModified == null)
		{
			return etag;
		}
		else if (etag == null && dateModified != null)
		{
			return dateModified;
		}
		// (etag != null && dateModified != null)
		etag.setLastModified(dateModified.getLastModified());
		return etag;
	}

	private ConditionalResponse evaluateEtagPreConditions(ResourceState resourceState)
	{
		ConditionalResponse result = null;
		EntityTag eTag = (resourceState!= null && resourceState.getEtag() != null)?new EntityTag(resourceState.getEtag()):null;
		List<String> ifMatch = request.getHttpHeaders().getRequestHeader(HttpHeaderNames.IF_MATCH);
		if (ifMatch != null && ifMatch.size() > 0)
		{
			if (!ifMatch(convertEtag(ifMatch), eTag))
			{
				result = new ConditionalResponse(eTag, 0, HttpResponseCodes.SC_PRECONDITION_FAILED);
			}
		}
		if (result == null)
		{
			List<String> ifNoneMatch = request.getHttpHeaders().getRequestHeader(HttpHeaderNames.IF_NONE_MATCH);
			if (ifNoneMatch != null && ifNoneMatch.size() > 0)
			{
				if (!ifNoneMatch(convertEtag(ifNoneMatch), eTag))
				{
					if (httpMethod.equals("GET"))
					{
						result = new ConditionalResponse(eTag, 0, HttpResponseCodes.SC_NOT_MODIFIED);
					}
					else
					{
						result = new ConditionalResponse(eTag, 0, HttpResponseCodes.SC_PRECONDITION_FAILED);
					}
				}
			}
		}
		return result;
	}

	private ConditionalResponse evaluateDateModifiedPreConditions(ResourceState resourceState)
	{
		ConditionalResponse result = null;

		String ifModifiedSince = request.getHttpHeaders().getHeaderString(HttpHeaderNames.IF_MODIFIED_SINCE);
		long dateModified = (resourceState!=null?resourceState.getDateModified():0);
		if (ifModifiedSince != null)
		{
			if (!ifModifiedSince(ifModifiedSince, dateModified))
			{
				result = new ConditionalResponse(null, dateModified, HttpResponseCodes.SC_NOT_MODIFIED);
			}
		}
		if (result == null)
		{
			String ifUnmodifiedSince = request.getHttpHeaders().getHeaderString(HttpHeaderNames.IF_UNMODIFIED_SINCE);
			if (ifUnmodifiedSince != null)
			{
				if (!ifUnmodifiedSince(ifUnmodifiedSince, dateModified))
				{
					result = new ConditionalResponse(null, dateModified, HttpResponseCodes.SC_PRECONDITION_FAILED);
				}
			}
		}
		return result;
	}

	private boolean ifMatch(List<EntityTag> ifMatch, EntityTag eTag)
	{
		if (eTag != null)
		{
			for (EntityTag tag : ifMatch)
			{
				if (tag.equals(eTag) || tag.getValue().equals("*"))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean ifNoneMatch(List<EntityTag> ifMatch, EntityTag eTag)
	{
		if (eTag != null)
		{
			if (ifMatch != null && ifMatch.size() > 0)
			{
				for (EntityTag tag : ifMatch)
				{
					if (tag.equals(eTag) || tag.getValue().equals("*"))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean ifModifiedSince(String strDate, long lastModified)
	{
		Date date = DateUtil.parseDate(strDate);

		if (date.getTime() >= lastModified)
		{
			return false;
		}
		return true;

	}

	private boolean ifUnmodifiedSince(String strDate, long lastModified)
	{
		Date date = DateUtil.parseDate(strDate);

		if (date.getTime() >= lastModified)
		{
			return true;
		}
		return false;
	}

	private List<EntityTag> convertEtag(List<String> tags)
	{
		ArrayList<EntityTag> result = new ArrayList<EntityTag>();
		if (tags != null)
		{
			for (String tag : tags)
			{
				String[] split = tag.split(",");
				for (String etag : split)
				{
					result.add(EntityTag.valueOf(etag.trim()));
				}
			}
		}
		return result;
	}
	
	private String generateEtag(String content)
    {
		if (StringUtils.isEmpty(content))
		{
			return null;
		} 
		return Long.toHexString(System.currentTimeMillis()) + Integer.toHexString(content.hashCode());
    }
}