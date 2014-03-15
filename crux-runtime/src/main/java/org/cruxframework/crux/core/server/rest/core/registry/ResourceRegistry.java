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
package org.cruxframework.crux.core.server.rest.core.registry;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.rest.core.UriBuilder;
import org.cruxframework.crux.core.server.rest.core.dispatch.CacheInfo;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;
import org.cruxframework.crux.core.server.rest.util.InvalidRestMethod;
import org.cruxframework.crux.core.shared.rest.annotation.Path;
import org.cruxframework.crux.core.shared.rest.annotation.StateValidationModel;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ResourceRegistry
{
	private static final Log logger = LogFactory.getLog(ResourceRegistry.class);
	private static final ResourceRegistry instance = new ResourceRegistry();
	private static final Lock lock = new ReentrantLock();
	private static boolean initialized = false;
	
	protected int size;
	protected RootSegment rootSegment = new RootSegment();
	
	/**
	 * Singleton constructor
	 */
	private ResourceRegistry() {}

	/**
	 * Singleton accessor
	 * @return
	 */
	public static ResourceRegistry getInstance()
	{
		return instance;
	}
	
	public RootSegment getRoot()
	{
		if (!initialized)
		{
			initialize();
		}
		return rootSegment;
	}

	/**
	 * Number of endpoints registered
	 * 
	 * @return
	 */
	public int getSize()
	{
		if (!initialized)
		{
			initialize();
		}
		return size;
	}

	/**
	 * Find a resource to invoke on
	 * 
	 * @return
	 */
	public ResourceMethod getResourceMethod(HttpRequest request)
	{
		if (!initialized)
		{
			initialize();
		}
		List<String> matchedUris = request.getUri().getMatchedURIs(false);
		if (matchedUris == null || matchedUris.size() == 0)
		{
			return rootSegment.matchRoot(request);
		}
		// resource location
		String currentUri = request.getUri().getMatchedURIs(false).get(0);
		return rootSegment.matchRoot(request, currentUri.length());
	}

	/**
	 * 
	 * @param clazz
	 * @param base
	 */
	protected void addResource(Class<?> clazz, String base)
	{
		Set<String> restMethodNames = new HashSet<String>();
		Map<String, List<RestMethodRegistrationInfo>> validRestMethods = new HashMap<String, List<RestMethodRegistrationInfo>>();
		for (Method method : clazz.getMethods())
		{
			if (!method.isSynthetic())
			{
				RestMethodRegistrationInfo methodRegistrationInfo = null;
				try
				{
					methodRegistrationInfo = processMethod(base, clazz, method, restMethodNames);	
				} catch (Exception e)
				{
					throw new InternalServerErrorException("Error to processMethod: " + method.toString(), "Can not execute requested service", e);
				}
				if (methodRegistrationInfo != null)
				{
					List<RestMethodRegistrationInfo> methodsForPath = validRestMethods.get(methodRegistrationInfo.pathExpression);
					if (methodsForPath == null)
					{
						methodsForPath = new ArrayList<RestMethodRegistrationInfo>();
						validRestMethods.put(methodRegistrationInfo.pathExpression, methodsForPath);
						
					}
					methodsForPath.add(methodRegistrationInfo);
				}
			}
		}
		checkConditionalWriteMethods(validRestMethods);
		createCorsAllowedMethodsList(validRestMethods);
	}

	private void createCorsAllowedMethodsList(Map<String, List<RestMethodRegistrationInfo>> validRestMethods)
    {
		for (Entry<String, List<RestMethodRegistrationInfo>> entry : validRestMethods.entrySet())
        {
	        List<RestMethodRegistrationInfo> methods = entry.getValue();
			if (methods.size() > 0)
	        {
				List<String> allowedMethodsForPath = new ArrayList<String>();
				for (RestMethodRegistrationInfo methodInfo : methods)
                {
					if (methodInfo.invoker.supportsCors())
					{
						allowedMethodsForPath.add(methodInfo.invoker.getHttpMethod());
					}
                }
				if (allowedMethodsForPath.size() > 0)
				{
					for (RestMethodRegistrationInfo methodInfo : methods)
	                {
						if (methodInfo.invoker.supportsCors())
						{
							methodInfo.invoker.setCorsAllowedMethods(allowedMethodsForPath);
						}
	                }
				}
	        }
        }
    }

	private void checkConditionalWriteMethods(Map<String, List<RestMethodRegistrationInfo>> validRestMethods)
    {
		for (Entry<String, List<RestMethodRegistrationInfo>> entry : validRestMethods.entrySet())
        {
	        List<RestMethodRegistrationInfo> methods = entry.getValue();
			if (methods.size() > 0)
	        {
				for (RestMethodRegistrationInfo methodInfo : methods)
                {
					StateValidationModel stateValidationModel = HttpMethodHelper.getStateValidationModel(methodInfo.invoker.getMethod());
					if (stateValidationModel != null && !stateValidationModel.equals(StateValidationModel.NO_VALIDATE))
					{
						if (!ensureReaderMethod(methods))
						{
							logger.error(" Method: " + methodInfo.invoker.getResourceClass().getName() + "." + methodInfo.invoker.getMethod().getName() + "() " +
									"uses a stateValidationModel. It requires a valid GET method to provides the resource for validation.");
						}
					}
                }
	        }
        }
    }

	private boolean ensureReaderMethod(List<RestMethodRegistrationInfo> methods)
    {
		for (RestMethodRegistrationInfo methodInfo : methods)
        {
			CacheInfo cacheInfo = HttpMethodHelper.getCacheInfoForGET(methodInfo.invoker.getMethod());
			if (cacheInfo != null)
			{
				if (!cacheInfo.isCacheEnabled())
				{
					//for cacheable resources, eTag generation is already enabled.
					methodInfo.invoker.forceEtagGeneration();
				}
				return true;
			}
        }
		return false;
    }

	/**
	 * 
	 * @param classes
	 * @param base
	 */
	protected void addResource(Class<?>[] classes, String base)
	{
		for (Class<?> clazz : classes)
		{
			addResource(clazz, base);
		}
	}

	/**
	 * 
	 * @param base
	 * @param clazz
	 * @param method
	 * @param restMethodNames 
	 */
	protected RestMethodRegistrationInfo processMethod(String base, Class<?> clazz, Method method, Set<String> restMethodNames)
	{
		if (method != null)
		{
			Path path = method.getAnnotation(Path.class);
			String httpMethod = null;
            try
            {
	            httpMethod = HttpMethodHelper.getHttpMethod(method.getAnnotations());
            }
            catch (InvalidRestMethod e)
            {
				logger.error("Invalid Method: " + method.getDeclaringClass().getName() + "." + method.getName() + "().", e);
            }

			boolean pathPresent = path != null;
			boolean restAnnotationPresent = pathPresent || (httpMethod != null);
			
			UriBuilder builder = new UriBuilder();
			if (base != null)
				builder.path(base);
			if (clazz.isAnnotationPresent(Path.class))
			{
				builder.path(clazz);
			}
			if (path != null)
			{
				builder.path(method);
			}
			String pathExpression = builder.getPath();
			if (pathExpression == null)
			{
				pathExpression = "";
			}
			if (restAnnotationPresent && !Modifier.isPublic(method.getModifiers()))
			{
				logger.error("Rest annotations found at non-public method: " + method.getDeclaringClass().getName() + "." + method.getName() + "(); Only public methods may be exposed as resource methods.");
			}
			else if (httpMethod != null)
			{
				if (restMethodNames.contains(method.getName()))
				{
					logger.error("Overloaded rest method: " + method.getDeclaringClass().getName() + "." + method.getName() + " found. It is not supported for Crux REST services.");
				}
				else
				{
					ResourceMethod invoker = new ResourceMethod(clazz, method, httpMethod);
					rootSegment.addPath(pathExpression, invoker);
					restMethodNames.add(method.getName());
					size++;
					return new RestMethodRegistrationInfo(pathExpression, invoker);
				}
			}
			else 
			{
				if (restAnnotationPresent)
				{
					logger.error("Method: " + method.getDeclaringClass().getName() + "." + method.getName() + "() declares rest annotations, but it does not inform the methods it must handle. Use one of @PUT, @POST, @GET or @DELETE.");
				}
				else if (logger.isDebugEnabled())
				{
					logger.debug("Method: " + method.getDeclaringClass().getName() + "." + method.getName() + "() ignored. It is not a rest method.");
				}
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public static void initialize()
	{
		if (initialized)
		{
			return;
		}
		try
		{
			lock.lock();
			if (initialized)	
			{
				return;
			}
			
			initializeRegistry();
		}
		finally
		{
			lock.unlock();
		}
	}

	private static void initializeRegistry()
    {
	    RestServiceFactory serviceScanner = RestServiceFactoryInitializer.getServiceFactory();
		Iterator<String> restServices = serviceScanner.iterateRestServices();
		
		while (restServices.hasNext())
		{
			String service = restServices.next();
			try
			{
				Class<?> serviceClass = serviceScanner.getServiceClass(service);
	            instance.addResource(serviceClass, "");
            }
            catch (Exception e)
            {
            	logger.error("Error initializing rest service class for service ["+service+"]", e);
            }
		}
		initialized = true;
    }
	
	private static class RestMethodRegistrationInfo
	{
		private String pathExpression;
		private ResourceMethod invoker;
		
		private RestMethodRegistrationInfo(String pathExpression, ResourceMethod invoker)
        {
			this.pathExpression = pathExpression;
			this.invoker = invoker;
        }
	}
}