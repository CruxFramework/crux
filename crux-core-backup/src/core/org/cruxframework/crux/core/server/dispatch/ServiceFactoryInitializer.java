/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.server.dispatch;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.config.ConfigurationFactory;


public class ServiceFactoryInitializer 
{
	private static final Log logger = LogFactory.getLog(ServiceFactoryInitializer.class);
	private static ServiceFactory serviceFactory;
	private static final Lock lock = new ReentrantLock();
	private static final Lock initializeLock = new ReentrantLock();
	private static boolean factoryInitialized = false;
	
	/**
	 * 
	 * @return
	 */
	public static ServiceFactory getServiceFactory()
	{
		if (serviceFactory != null) return serviceFactory;
		
		try
		{
			lock.lock();
			if (serviceFactory != null) return serviceFactory;
			serviceFactory = (ServiceFactory) Class.forName(ConfigurationFactory.getConfigurations().serviceFactory()).newInstance(); 
		}
		catch (Throwable e)
		{
			logger.error("Error initializing serviceFactory.", e);
		}
		finally
		{
			lock.unlock();
		}
		return serviceFactory;
	}

	/**
	 * 
	 * @param serviceFactory
	 */
	public static void registerServiceFactory(ServiceFactory serviceFactory)
	{
		ServiceFactoryInitializer.serviceFactory = serviceFactory;
		factoryInitialized = false;
	}
	
	/**
	 * 
	 * @param context
	 */
	public static void initialize(ServletContext context)
	{
		if (!factoryInitialized)
		{
			initializeLock.lock();
			try
			{
				if (!factoryInitialized)
				{
					getServiceFactory().initialize(context);
					if (logger.isInfoEnabled())
					{
						logger.info("Server services registered.");
					}
					factoryInitialized = true;
				}
			}
			finally
			{
				initializeLock.unlock();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isFactoryInitialized()
	{
		return factoryInitialized;
	}
}
