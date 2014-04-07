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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.rest.annotation.RestService;
import org.cruxframework.crux.scanner.ClassScanner;


/**
 * Maps all application rest services.
 * @author Thiago Bustamante
 *
 */
public class RestServices 
{
	private static final Log logger = LogFactory.getLog(RestServices.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> serviceNames;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (serviceNames != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (serviceNames != null)	
			{
				return;
			}
			
			initializeServices();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializeServices()
	{
		serviceNames = new HashMap<String, String>();
		
		Set<String> restServices =  ClassScanner.searchClassesByAnnotation(RestService.class);
		if (restServices != null)
		{
			for (String service : restServices) 
			{
				try 
				{
					Class<?> serviceClass = Class.forName(service);
					RestService annot = serviceClass.getAnnotation(RestService.class);
					if (serviceNames.containsKey(annot.value()))
					{
						logger.error("Duplicated rest service [{"+annot.value()+"}]. Overiding previous registration...");
					}
					serviceNames.put(annot.value(), service);
				}
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing rest service class.",e);
				}
			}
		}
	}
	
	/**
	 * @param name
	 * @return
	 */
	public static String getService(String name)
	{
		if (serviceNames == null)
		{
			initialize();
		}
		return serviceNames.get(name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Class<?> getServiceClass(String name)
	{
		try
        {
			if (serviceNames == null)
			{
				initialize();
			}
			String result = serviceNames.get(name);;
	        return Class.forName(result);
        }
        catch (Exception e)
        {
        	return null;
        }
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateServices()
	{
		if (serviceNames == null)
		{
			initialize();
		}
		return serviceNames.keySet().iterator();
	}
}
