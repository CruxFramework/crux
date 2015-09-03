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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.server.rest.core.EntityTag;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod.MethodReturn;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.HttpResponse;
import org.cruxframework.crux.core.server.rest.spi.UriInfo;
import org.cruxframework.crux.core.server.rest.state.ETagHandler;
import org.cruxframework.crux.core.server.rest.state.ResourceStateConfig;
import org.cruxframework.crux.core.server.rest.state.ResourceStateHandler;
import org.cruxframework.crux.core.server.rest.state.ResourceStateHandler.ResourceState;
import org.cruxframework.crux.core.server.rest.util.DateUtil;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.HttpResponseCodes;
import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.cruxframework.crux.core.shared.rest.annotation.HttpMethod;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class StateHandler
{
	private static final Log logger = LogFactory.getLog(StateHandler.class);
	private static final Lock eTagLock = new ReentrantLock();
	private static ETagHandler eTagHandler = null;
	private final HttpRequest request;
	private final ResourceMethod resourceMethod;
	private String httpMethod;
	private String key;
	private HttpResponse response;

	public StateHandler(ResourceMethod resourceMethod, HttpRequest request, HttpResponse response)
    {
		this.resourceMethod = resourceMethod;
		this.request = request;
		this.response = response;
		this.httpMethod = request.getHttpMethod();
		this.key = request.getUri().getRequestUri().toString();
    }
	
	public MethodReturn handledByCache() throws Exception
	{
		MethodReturn ret = null;
		if (resourceMethod.cacheInfo != null && resourceMethod.cacheInfo.isCacheEnabled())
		{
			ret = handleCacheableOperation();
		}
		else
		{
			ret = handleUncacheableOperation();
		}
		
		if (ret == null)
		{
			ret = resourceMethod.doInvoke(request, response);
			if (ret.getCheckedExceptionData() == null)
			{
				updateState(request.getUri(), ret);
			}
		}
		return ret;
	}

	public void updateState(UriInfo uriInfo, MethodReturn ret)
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
				etag = getETagHandler().generateEtag(uriInfo, ret.getReturn());
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
	private MethodReturn handleCacheableOperation() throws Exception
	{
		ResourceStateHandler resourceStateHandler = ResourceStateConfig.getResourceStateHandler();
		ResourceState resourceState = resourceStateHandler.get(key);
		MethodReturn ret = null;
		if (resourceState == null)
		{
			return null;
		}
		else
		{
			if (resourceState.isExpired())
			{
				ret = resourceMethod.doInvoke(request, response);
				if (ret.getCheckedExceptionData() == null)
				{
					updateState(request.getUri(), ret);
					resourceState = resourceStateHandler.get(key);
				}
				else
				{
					resourceStateHandler.remove(key);
				}
			}
			ConditionalResponse conditionalResponse = evaluatePreconditions(resourceState);
			if (conditionalResponse != null)
			{
				return new MethodReturn(resourceMethod.hasReturnType, null, null, resourceMethod.cacheInfo, conditionalResponse, resourceMethod.isEtagGenerationEnabled());
			}
		}
		return ret;
	}

	/**
	 * Handle PUT/POST/DELETE/uncacheable GETs
	 * @return
	 */
	private MethodReturn handleUncacheableOperation() throws Exception
	{
		ResourceStateHandler resourceStateHandler = ResourceStateConfig.getResourceStateHandler();
		ResourceState resourceState = resourceStateHandler.get(key);
		
		MethodReturn ret = null;
		if (resourceMethod.getHttpMethod().equals(HttpMethod.GET) && resourceState != null && resourceState.isExpired())
		{
			ret = resourceMethod.doInvoke(request, response);
			if (ret.getCheckedExceptionData() == null)
			{
				updateState(request.getUri(), ret);
				resourceState = resourceStateHandler.get(key);
			}
			else
			{
				resourceStateHandler.remove(key);
				resourceState = null;
			}
		}
		
		ConditionalResponse conditionalResponse = evaluatePreconditions(resourceState);
		if (conditionalResponse == null)
		{
			return ret;
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
	
	public static ETagHandler getETagHandler()
	{
		if (eTagHandler != null) return eTagHandler;
		
		try
		{
			eTagLock.lock();
			if (eTagHandler != null) return eTagHandler;
			eTagHandler = (ETagHandler) Class.forName(ConfigurationFactory.getConfigurations().eTagHandler()).newInstance(); 
		}
		catch (Exception e)
		{
			logger.error("Error initializing eTagHandler.", e);
		}
		finally
		{
			eTagLock.unlock();
		}
		return eTagHandler;
	}
}