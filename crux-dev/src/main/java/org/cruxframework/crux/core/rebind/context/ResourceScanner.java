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
package org.cruxframework.crux.core.rebind.context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.resources.Resource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.JClassScanner;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ResourceScanner 
{
	private static final Log logger = LogFactory.getLog(ResourceScanner.class);
	private boolean initialized = false;
	private JClassScanner jClassScanner;
	private Map<String, Map<String, String>> resourcesCanonicalNames;
	private Map<String, Map<String, String>> resourcesClassNames;
	
	public ResourceScanner(GeneratorContext context)
    {
		jClassScanner = new JClassScanner(context);
    }
	
	/**
	 * 
	 * @param name
	 * @param device
	 * @return
	 */
	public String getResource(String name, Device device)
	{
		initializeResources();
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
	 * @param device
	 * @return
	 */
	public Class<?> getResourceClass(String name, Device device)
	{
		try
        {
			initializeResources();
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
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasResource(String name)
	{
		initializeResources();
		return (name != null && resourcesCanonicalNames.containsKey(name));
	}
	
	/**
	 * @return
	 */
	public Iterator<String> iterateResources()
	{
		initializeResources();
		return resourcesCanonicalNames.keySet().iterator();
	}
	
	/**
	 * 
	 */
	protected void initializeResources()
	{
		if (!initialized)
		{
			resourcesCanonicalNames = new HashMap<String, Map<String, String>>();
			resourcesClassNames = new HashMap<String, Map<String, String>>();
			JClassType[] resources =  jClassScanner.searchClassesByAnnotation(Resource.class);
			if (resources != null)
			{
				for (JClassType resourceClass : resources) 
				{
					try 
					{
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
					catch (Exception e) 
					{
						logger.error("Error initializing resource.",e);
					}
				}
			}
			initialized = true;
		}
	}
	
	/**
	 * 
	 * @param resourceClass
	 * @param resourceKey
	 * @param device
	 */
	private void addResource(JClassType resourceClass, String resourceKey, Device device)
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
		canonicallCassNamesByDevice.put(deviceKey, resourceClass.getQualifiedSourceName());
		classNamesByDevice.put(deviceKey, resourceClass.getName());
    }
}
