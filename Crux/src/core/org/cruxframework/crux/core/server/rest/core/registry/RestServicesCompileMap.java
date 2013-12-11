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
package org.cruxframework.crux.core.server.rest.core.registry;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Builds a map of all rest implementation classes 
 * @author Thiago da Rosa de Bustamante
 */
public class RestServicesCompileMap
{
	private static final Log logger = LogFactory.getLog(RestServicesCompileMap.class);
	private static Map<String, String> remoteServices = new HashMap<String, String>();
	
	/**
	 * @param serviceName
	 * @return
	 */
	public static String getService(String serviceName)
	{
		String implementationClassName =  remoteServices.get(serviceName);
		if (implementationClassName != null)
		{
			return implementationClassName;
		}
		logger.error("No implementation class found to rest service: ["+serviceName+"].");
		return null;	
	}

	public static Iterator<String> iterateServices()
	{
		return remoteServices.keySet().iterator();
	}
	
	/**
	 * @param context
	 */
	public static boolean initialize(ServletContext context)
	{
		if (context != null)
		{
			Properties properties = new Properties();
			try
			{
				properties.load(context.getResourceAsStream("/META-INF/crux-rest"));
				Enumeration<?> serviceNames = (Enumeration<?>) properties.propertyNames();
				while (serviceNames.hasMoreElements())
				{
					String serviceName = (String) serviceNames.nextElement();
					remoteServices.put(serviceName, properties.getProperty(serviceName));
				}
				return true;
			}
			catch (Exception e)
			{
				logger.info("Error initializing services.",e);
			}
		}
		return false;
	}
}
