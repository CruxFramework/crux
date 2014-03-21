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
package org.cruxframework.crux.core.rebind.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Global;
import org.cruxframework.crux.core.client.controller.WidgetController;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfig;
import org.cruxframework.crux.core.server.scan.ClassScanner;

import com.google.gwt.user.client.ui.IsWidget;


/**
 * Maps all controllers in a module.
 * @author Thiago Bustamante
 *
 */
public class ClientControllers 
{
	private static final Log logger = LogFactory.getLog(ClientControllers.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, Map<String, String>> controllersCanonicalNames;
	private static Map<String, Map<String, String>> controllersNames;
	private static List<String> globalControllers;
	private static Map<String, Set<String>> widgetControllers;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (controllersCanonicalNames != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (controllersCanonicalNames != null)
			{
				return;
			}
			
			initializeControllers();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializeControllers()
	{
		controllersCanonicalNames = new HashMap<String, Map<String, String>>();
		controllersNames = new HashMap<String, Map<String, String>>();
		globalControllers = new ArrayList<String>();
		widgetControllers = new HashMap<String, Set<String>>();
		
		Set<String> controllerNames =  ClassScanner.searchClassesByAnnotation(Controller.class);
		if (controllerNames != null)
		{
			for (String controller : controllerNames) 
			{
				try 
				{
					Class<?> controllerClass = Class.forName(controller);
					Controller annot = controllerClass.getAnnotation(Controller.class);
					Device[] devices = annot.supportedDevices();
					String resourceKey = annot.value();
					if (devices == null || devices.length ==0)
					{
						addResource(controllerClass, resourceKey, Device.all);
					}
					else
					{
						for (Device device : devices)
						{
							addResource(controllerClass, resourceKey, device);
						}
					}
					if (controllerClass.getAnnotation(Global.class) != null)
					{
						globalControllers.add(annot.value());
					}
					initWidgetControllers(controllerClass, annot);
				}
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing client controller ["+controller+"].",e);
				}
			}
		}
	}

	/**
	 * 
	 * @param controllerClass
	 * @param controllerKey
	 * @param device
	 */
	private static void addResource(Class<?> controllerClass, String controllerKey, Device device)
    {
	    if (!controllersCanonicalNames.containsKey(controllerKey))
	    {
	    	controllersCanonicalNames.put(controllerKey, new HashMap<String, String>());
	    	controllersNames.put(controllerKey, new HashMap<String, String>());
	    }
	    Map<String, String> canonicallCassNamesByDevice = controllersCanonicalNames.get(controllerKey);
	    Map<String, String> classNamesByDevice = controllersNames.get(controllerKey);
	    
	    String deviceKey = device.toString();
		if (controllersCanonicalNames.containsKey(deviceKey))
	    {
	    	throw new CruxGeneratorException("Duplicated Client Controller: ["+controllerKey+"].");
	    }
		canonicallCassNamesByDevice.put(deviceKey, controllerClass.getCanonicalName());
		classNamesByDevice.put(deviceKey, controllerClass.getName());
    }
	
	/**
	 * @param controllerClass
	 * @param annot
	 */
	private static void initWidgetControllers(Class<?> controllerClass, Controller annot)
    {
	    WidgetController widgetControllerAnnot = controllerClass.getAnnotation(WidgetController.class);
	    if (widgetControllerAnnot != null)
	    {
	    	
	    	Class<? extends IsWidget>[] widgets = widgetControllerAnnot.value();
	    	for (Class<? extends IsWidget> widgetClass : widgets)
	        {
	    		String widgetType = WidgetConfig.getWidgetType(widgetClass);
	    		if (!StringUtils.isEmpty(widgetType))
	    		{
	    			Set<String> controllers = widgetControllers.get(widgetType);
	    			if (controllers == null)
	    			{
	    				controllers = new HashSet<String>();
	    				widgetControllers.put(widgetType, controllers);
	    			}
	    			controllers.add(annot.value());
	    		}
	        }
	    }
    }
	
	/**
	 * @param name
	 * @return
	 */
	public static String getController(String name, Device device)
	{
		if (controllersCanonicalNames == null)
		{
			initialize();
		}
		Map<String, String> map = controllersCanonicalNames.get(name);
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
	public static boolean hasController(String name)
	{
		if (controllersCanonicalNames == null)
		{
			initialize();
		}
		return (name != null && controllersCanonicalNames.containsKey(name));
	}
	
	/**
	 * 
	 * @param controller
	 * @return
	 */
	public static Class<?> getControllerClass(String controller, Device device)
	{
		try
        {
			if (controllersNames == null)
			{
				initialize();
			}
			Map<String, String> map = controllersNames.get(controller);
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
	public static Iterator<String> iterateControllers()
	{
		if (controllersCanonicalNames == null)
		{
			initialize();
		}
		return controllersCanonicalNames.keySet().iterator();
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateGlobalControllers()
	{
		if (globalControllers == null)
		{
			initialize();
		}
		return globalControllers.iterator();
	}
	
	/**
	 * @param widgetClass
	 * @return
	 */
	public static Iterator<String> iterateWidgetControllers(String widgetType)
	{
		if (widgetControllers == null)
		{
			initialize();
		}
		Set<String> controllers = widgetControllers.get(widgetType);
		if (controllers == null)
		{
			return null;
		}
		return controllers.iterator();
	}
}
