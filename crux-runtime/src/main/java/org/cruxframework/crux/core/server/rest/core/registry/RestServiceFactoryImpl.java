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
package org.cruxframework.crux.core.server.rest.core.registry;

import java.util.Iterator;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.server.Environment;


/**
 * Scanner for rest services
 *  
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RestServiceFactoryImpl implements RestServiceFactory
{
	private static final Log logger = LogFactory.getLog(RestServiceFactoryImpl.class);
	
	/**
	 * This class uses a file generated during application compilation to find out rest service classes.
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class CompileTimeStrategy implements ScanningStrategy
	{

		public String getServiceClassName(String serviceName)
		{
			return RestServicesCompileMap.getService(serviceName);
		}

		public boolean initialize(ServletContext context)
		{
			return RestServicesCompileMap.initialize(context);
		}
		
		@Override
		public Iterator<String> iterateRestServices()
		{
		    return RestServicesCompileMap.iterateServices();
		}
	}

	/**
	 * Describes a strategy for service scanning.
	 * @author Thiago da Rosa de Bustamante
	 */
	private static interface ScanningStrategy
	{
		String getServiceClassName(String serviceName);
		boolean initialize(ServletContext context);
		Iterator<String> iterateRestServices();
	}
	
	/**
	 * This class scan the application classpath to build a map of 
	 * rest service implementations and uses it to find out which
	 * class it must use to each service path. For debug purposes, it is 
	 * better, once it supports hot deployment of resources, but it waste memory in 
	 * production.
	 *
	 * @author Thiago da Rosa de Bustamante
	 */
	private static class RuntimeStrategy implements ScanningStrategy
	{
		public String getServiceClassName(String serviceName)
		{
			return RestServices.getService(serviceName);
		}

		public boolean initialize(ServletContext context)
		{
			return true;
		}
		
		@Override
		public Iterator<String> iterateRestServices()
		{
		    return RestServices.iterateServices();
		}
	}

	private ScanningStrategy strategy;
	
	
	/**
	 * This Constructor select the best strategy to use. 
	 */
	public RestServiceFactoryImpl()
	{
		if (Environment.isProduction() ||  Boolean.parseBoolean(ConfigurationFactory.getConfigurations().useCompileTimeClassScanningForDevelopment()))
		{
			strategy = new CompileTimeStrategy();
		}
		else
		{
			strategy = new RuntimeStrategy();
		}
	}

	/**
	 * 
	 * @param serviceName
	 * @return
	 */
	public Class<?> getServiceClass(String serviceName) 
	{
		try
        {
	        return Class.forName(strategy.getServiceClassName(serviceName));
        }
        catch (ClassNotFoundException e)
        {
			String msg = "Can not found class  associated with service ["+serviceName+"]";
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
        }
	}
	
	public Iterator<String> iterateRestServices()
	{
		return strategy.iterateRestServices();
	}
	
	@Override
	public void initialize(ServletContext context)
	{
		if (!strategy.initialize(context))
		{
			if (strategy instanceof CompileTimeStrategy)
			{
				logger.info("Error initializing REST services. Using runtime strategy for services...");
				strategy = new RuntimeStrategy();
				strategy.initialize(context);
			}
		}
	}
	
	@Override
    public Object getService(Class<?> serviceClass)
    {
		try 
		{
			return serviceClass.newInstance();
		} 
		catch (Exception e) 
		{
			String msg = "Error creating REST service for class [" + serviceClass.getCanonicalName() + "].";
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		}
    }
}
