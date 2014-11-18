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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.scanner.ClassScanner;


/**
 * Class for retrieve the service class, based on the remote interface it implements
 * @author Thiago Bustamante
 */
public class Services 
{
	private static final Log logger = LogFactory.getLog(Services.class);
	
	/**
	 * Return the service that implements the interface informed.
	 * @param interfaceName
	 * @return
	 */
	public static Class<?> getService(String interfaceName)
	{
		try 
		{
			Set<String> serviceNames =  ClassScanner.searchClassesByInterface(Class.forName(interfaceName));
			if (serviceNames != null)
			{
				for (String service : serviceNames) 
				{
					Class<?> serviceClass = Class.forName(service);
					if (!serviceClass.isInterface())
					{
						return serviceClass;
					}
				}
			}
			logger.info("No implementation class found to service interface: ["+interfaceName+"].");
		} 
		catch (ClassNotFoundException e) 
		{
			logger.error("Error creating service ["+interfaceName+"].",e);
		}
		return null;
	}
}
