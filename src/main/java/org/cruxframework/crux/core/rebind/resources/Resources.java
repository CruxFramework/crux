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
package org.cruxframework.crux.core.rebind.resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.resources.Resource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.scan.ClassScanner;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Resources 
{
	private static final Log logger = LogFactory.getLog(Resources.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, Map<String, String>> resourcesCanonicalNames;
	private static Map<String, Map<String, String>> resourcesClassNames;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (resourcesCanonicalNames != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (resourcesCanonicalNames != null)
			{
				return;
			}
			
			initializeResources();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * 
	 */
	protected static void initializeResources()
	{
		resourcesCanonicalNames = new HashMap<String, Map<String, String>>();
		resourcesClassNames = new HashMap<String, Map<String, String>>();
		Set<String> resourceNames =  ClassScanner.searchClassesByAnnotation(Resource.class);
		if (resourceNames != null)
		{
			for (String resource : resourceNames) 
			{
				try 
				{
					Class<?> resourceClass = Class.forName(resource);
					Resource annot = resourceClass.getAnnotation(Resource.class);
					if (annot != null)
					{
						Device[] devices = annot.supportedDevices();
						String resourceKey = annot.value();
						if (devices == null || devices.length ==0)
						{
							addResource(resourceClass, resourceKey, Device.all);
						}
						else
						{
							for (Device device : devices)
                            {
								addResource(resourceClass, resourceKey, device);
                            }
						}
					}
				} 
				catch (Throwable e) 
				{
					logger.error("Error initializing resource.",e);
				}
			}
		}
	}

	/**
	 * 
	 * @param resourceClass
	 * @param resourceKey
	 * @param device
	 */
	private static void addResource(Class<?> resourceClass, String resourceKey, Device device)
    {
	    if (!resourcesCanonicalNames.containsKey(resourceKey))
	    {
	    	resourcesCanonicalNames.put(resourceKey, new HashMap<String, String>());
	    	resourcesClassNames.put(resourceKey, new HashMap<String, String>());
	    }
	    Map<String, String> canonicallCassNamesByDevice = resourcesCanonicalNames.get(resourceKey);
	    Map<String, String> classNamesByDevice = resourcesClassNames.get(resourceKey);
	    
	    String deviceKey = device.toString();
		if (resourcesCanonicalNames.containsKey(deviceKey))
	    {
	    	throw new CruxGeneratorException("Duplicated resource: ["+resourceKey+"].");
	    }
		canonicallCassNamesByDevice.put(deviceKey, resourceClass.getCanonicalName());
		classNamesByDevice.put(deviceKey, resourceClass.getName());
    }

	/**
	 * 
	 * @param name
	 * @param device
	 * @return
	 */
	public static String getResource(String name, Device device)
	{
		if (resourcesCanonicalNames == null)
		{
			initialize();
		}
		Map<String, String> map = resourcesCanonicalNames.get(name);
		String result = map.get(device.toString());
		if (result == null && !device.equals(Device.all))
		{
			result = map.get(Device.all.toString());
		}
		return result;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static boolean hasResource(String name)
	{
		if (resourcesCanonicalNames == null)
		{
			initialize();
		}
		return (name != null && resourcesCanonicalNames.containsKey(name));
	}
	
	/**
	 * 
	 * @param name
	 * @param device
	 * @return
	 */
	public static Class<?> getResourceClass(String name, Device device)
	{
		try
        {
			if (resourcesClassNames == null)
			{
				initialize();
			}
			Map<String, String> map = resourcesClassNames.get(name);
			String result = map.get(device.toString());
			if (result == null && !device.equals(Device.all))
			{
				result = map.get(Device.all.toString());
			}
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
	public static Iterator<String> iterateResources()
	{
		if (resourcesCanonicalNames == null)
		{
			initialize();
		}
		
		return resourcesCanonicalNames.keySet().iterator();
	}

}
