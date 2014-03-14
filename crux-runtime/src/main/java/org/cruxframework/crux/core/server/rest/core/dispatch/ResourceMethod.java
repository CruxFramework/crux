/*
 * Copyright 2011 cruxframework.org
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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.jackson.map.ObjectWriter;
import org.cruxframework.crux.core.server.rest.annotation.RestService.CorsSupport;
import org.cruxframework.crux.core.server.rest.annotation.RestService.JsonPSupport;
import org.cruxframework.crux.core.server.rest.core.EntityTag;
import org.cruxframework.crux.core.server.rest.core.HttpRequestAware;
import org.cruxframework.crux.core.server.rest.core.HttpResponseAware;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.HttpResponse;
import org.cruxframework.crux.core.server.rest.spi.HttpServletResponseHeaders;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;
import org.cruxframework.crux.core.server.rest.state.ResourceStateConfig;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;
import org.cruxframework.crux.core.server.rest.util.JsonUtil;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.EncryptUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class ResourceMethod
{
	private static final Lock lock = new ReentrantLock();
	private static final Lock exceptionlock = new ReentrantLock();

	protected String httpMethod;
	protected Method method;
	protected Class<?> resourceClass;
	protected Type genericReturnType;
	protected MethodInvoker methodInvoker;
	protected ObjectWriter writer;
	protected Map<String, ObjectWriter> exceptionWriters = new HashMap<String, ObjectWriter>();
	protected Map<String, String> exceptionIds = new HashMap<String, String>();
	protected CacheInfo cacheInfo;
	protected boolean hasReturnType;
	protected JsonPData jsonPData;
	protected CorsData corsData;
	private boolean etagGenerationEnabled = false;
	private boolean isRequestAware;
	private boolean isResponseAware;;

	public ResourceMethod(Class<?> clazz, Method method, String httpMethod)
	{
		this.httpMethod = httpMethod;
		this.resourceClass = clazz;
		this.isRequestAware = HttpRequestAware.class.isAssignableFrom(resourceClass);
		this.isResponseAware = HttpResponseAware.class.isAssignableFrom(resourceClass);
		this.method = method;
		this.genericReturnType = ClassUtils.getGenericReturnTypeOfGenericInterfaceMethod(clazz, method);
		this.hasReturnType = genericReturnType != null && !genericReturnType.equals(Void.class) && !genericReturnType.equals(Void.TYPE);
		if (!hasReturnType && httpMethod.equals("GET"))
		{
			throw new InternalServerErrorException("Invalid rest method: " + method.toString() + ". @GET methods " +
					"can not be void.", "Can not execute requested service");
		}

		this.methodInvoker = new MethodInvoker(resourceClass, method, httpMethod);
		this.cacheInfo = HttpMethodHelper.getCacheInfoForGET(method);
		CorsSupport corsSupport = method.getAnnotation(CorsSupport.class);
		if (corsSupport == null)
		{
			corsSupport = resourceClass.getAnnotation(CorsSupport.class);			
		}
		this.corsData = CorsData.parseCorsData(corsSupport);
		JsonPSupport jsonPSupport = method.getAnnotation(JsonPSupport.class);
		if (corsSupport == null)
		{
			jsonPSupport = resourceClass.getAnnotation(JsonPSupport.class);			
		}
		this.jsonPData = JsonPData.parseJsonPData(jsonPSupport);
	}

	public boolean supportsCors()
	{
		return corsData != null;
	}
	
	public boolean supportsJsonP()
	{
		return jsonPData != null;
	}
	
	public void setCorsAllowedMethods(List<String> methods)
	{
		if (supportsCors())
		{
			for (String method : methods)
            {
				corsData.addAllowMethod(method);
            }
		}
	}
	
	public Type getGenericReturnType()
	{
		return genericReturnType;
	}

	public Class<?> getResourceClass()
	{
		return resourceClass;
	}

	public Annotation[] getMethodAnnotations()
	{
		return method.getAnnotations();
	}

	public Method getMethod()
	{
		return method;
	}

	public void forceEtagGeneration()
	{
		etagGenerationEnabled = true;
	}

	public boolean isEtagGenerationEnabled()
	{
		return etagGenerationEnabled || (cacheInfo != null && cacheInfo.isCacheEnabled()); 

	}

	public MethodReturn invoke(HttpRequest request, HttpResponse response)
	{
		try
		{
			if (ResourceStateConfig.isResourceStateCacheEnabled())
			{
				StateHandler stateHandler = new StateHandler(this, request);
				MethodReturn ret = stateHandler.handledByCache();
				if (ret == null)
				{
					Object target = createTarget(request, response);
					ret = invoke(request, target);
					if (ret.getCheckedExceptionData() == null)
					{
						stateHandler.updateState(ret);
					}
				}
				return ret;
			}
			else
			{
				Object target = createTarget(request, response);
				return invoke(request, target);
			}
		}
		catch (RestFailure e)
		{
			throw e; 
		}
		catch (Exception e)
		{
			throw new InternalServerErrorException("Error invoking rest service endpoint", "Error processing requested service", e); 
		}
	}

	public String getHttpMethod()
	{
		return httpMethod;
	}

	public boolean checkCorsPermissions(HttpRequest request, HttpResponse response, boolean preflightRequest)
    {
		boolean allowed = true;
		if (supportsCors())
		{
			String origin = request.getHttpHeaders().getHeaderString(HttpHeaderNames.ORIGIN);
			if (corsData.isOriginAllowed(origin))
			{
				HttpServletResponseHeaders outputHeaders = response.getOutputHeaders();
				outputHeaders.putSingle(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, corsData.isAllOriginsAllowed()?"*":origin);
				outputHeaders.add(HttpHeaderNames.VARY, HttpHeaderNames.ORIGIN);// Needed to make proxy caches works
				if (corsData.isAllowCredentials())
				{
					outputHeaders.putSingle(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
				}
				Iterator<String> exposeHeaders = corsData.getExposeHeaders();
				while (exposeHeaders.hasNext())
				{
					outputHeaders.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, exposeHeaders.next());
				}
				if (preflightRequest)
				{
					Iterator<String> allowMethods = corsData.getAllowMethods();
					while (allowMethods.hasNext())
					{
						outputHeaders.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, allowMethods.next());
					}
					if (corsData.getMaxAge() >= 0)
					{
						outputHeaders.putSingle(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, corsData.getMaxAge());
					}
					if (!corsData.isAllowMethod(request.getHttpMethod()))
					{
						allowed = false;
					}
				}
			}
			else
			{
				allowed = (origin == null); // Same origin requests can send no origin header.
			}
		}
		return allowed;
    }

	private Object createTarget(HttpRequest request, HttpResponse response) throws InstantiationException, IllegalAccessException
	{
		Object target = getRestServiceFactory(request).getService(resourceClass);
		
		if (isRequestAware)
		{
			((HttpRequestAware)target).setRequest(request);
		}
		if (isResponseAware)
		{
			((HttpResponseAware)target).setResponse(response);
		}
		return target;
	}


	private MethodReturn invoke(HttpRequest request, Object target)
	{
		Object rtn = methodInvoker.invoke(request, target);
		String retVal = null;
		String exeptionData = null;
		try
		{
			if (rtn != null && rtn instanceof Exception)
			{
				exeptionData = getReturnedValue(request, getExceptionData((Exception) rtn));
			}
			else if (hasReturnType && rtn != null)
			{
				retVal = getReturnedValue(request, getReturnWriter().writeValueAsString(rtn));
			}
		}
		catch (Exception e)
		{
			throw new InternalServerErrorException("Error serializing rest service return", "Error processing requested service", e); 
		}
		return new MethodReturn(hasReturnType, retVal, exeptionData, cacheInfo, null, isEtagGenerationEnabled());
	}

	private String getReturnedValue(HttpRequest request, String value)
	{
		if (supportsJsonP())
		{
			String callbackParam = request.getUri().getQueryParameters().getFirst(jsonPData.getCallbackParameter());
			if (callbackParam != null && callbackParam.length() > 0)
			{
				value = callbackParam+"("+value+");";
			}
		}
		return value;
	}
	
	private String getExceptionData(Exception e) throws IOException 
    {
	    return "{\"exId\": \"" + getExceptionId(e) + "\", \"exData\": " + getExceptionWriter(e).writeValueAsString(e) + "}";
    }

	private String getExceptionId(Exception e)
	{
		String name = e.getClass().getCanonicalName();
		String exceptionId = exceptionIds.get(name);
		if (exceptionId == null)
		{
			initializeExceptionObjects(e, name);
		}
		return exceptionIds.get(name);
	}

	private ObjectWriter getExceptionWriter(Exception e)
	{
		String name = e.getClass().getCanonicalName();
		ObjectWriter objectWriter = exceptionWriters.get(name);
		if (objectWriter == null)
		{
			initializeExceptionObjects(e, name);
		}
		return exceptionWriters.get(name);
	}

	private void initializeExceptionObjects(Exception e, String name)
    {
	    exceptionlock.lock();
	    try
	    {
	    	ObjectWriter objectWriter = exceptionWriters.get(name);
	    	if (objectWriter == null)
	    	{
	    		objectWriter = JsonUtil.createWriter(e.getClass());
	    		exceptionWriters.put(name, objectWriter);
	    		exceptionIds.put(name, hash(name));
	    	}
	    }
	    finally
	    {
	    	exceptionlock.unlock();
	    }
    }

	private String hash(String s)
	{
		try
		{
			return EncryptUtils.hash(s);
		}
		catch (NoSuchAlgorithmException ns)
		{
			throw new InternalServerErrorException("Error generating MD5 hash for String["+s+"]", "Error processing requested service", ns); 
		}
	}

	private ObjectWriter getReturnWriter()
	{
		if (writer == null)
		{
			lock.lock();
			try
			{
				if (writer == null)
				{
					if (genericReturnType instanceof TypeVariable)
					{
						Class<?> returnType = ClassUtils.getTypeVariableTarget((TypeVariable<?>) genericReturnType, resourceClass, method.getDeclaringClass());
						writer = JsonUtil.createWriter(returnType);
					}
					else
					{
						writer = JsonUtil.createWriter(genericReturnType);
					}
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		return writer;
	}

	public static class MethodReturn
	{
		protected final boolean hasReturnType;
		protected final String ret;
		private final CacheInfo cacheInfo;
		private final ConditionalResponse conditionalResponse;
		protected EntityTag etag;
		protected long dateModified;
		protected final boolean etagGenerationEnabled;
		protected String checkedExceptionData;

		protected MethodReturn(boolean hasReturnType, String ret, String exceptionData, CacheInfo cacheInfo, ConditionalResponse conditionalResponse, 
							   boolean etagGenerationEnabled)
		{
			this.hasReturnType = hasReturnType;
			this.ret = ret;
			this.checkedExceptionData = exceptionData;
			this.cacheInfo = cacheInfo;
			this.conditionalResponse = conditionalResponse;
			this.etagGenerationEnabled = etagGenerationEnabled;
		}

		public boolean hasReturnType()
		{
			return hasReturnType;
		}

		public String getReturn()
		{
			return ret;
		}

		public CacheInfo getCacheInfo()
		{
			return cacheInfo;
		}

		public ConditionalResponse getConditionalResponse()
		{
			return conditionalResponse;
		}

		public EntityTag getEtag()
		{
			return etag;
		}

		public void setEtag(EntityTag etag)
		{
			this.etag = etag;
		}

		public long getDateModified()
		{
			return dateModified;
		}

		public void setDateModified(long dateModified)
		{
			this.dateModified = dateModified;
		}

		public boolean isEtagGenerationEnabled()
		{
			return etagGenerationEnabled;
		}

		public String getCheckedExceptionData()
		{
			return checkedExceptionData;
		}

		public void setCheckedExceptionData(String checkedExceptionData)
		{
			this.checkedExceptionData = checkedExceptionData;
		}
	}
	
	private RestServiceFactory getRestServiceFactory(HttpRequest request)
	{
		if (!RestServiceFactoryInitializer.isFactoryInitialized())
		{
			RestServiceFactoryInitializer.initialize(request.getSession(true).getServletContext());
		}
		
		return RestServiceFactoryInitializer.getServiceFactory();
	}
}