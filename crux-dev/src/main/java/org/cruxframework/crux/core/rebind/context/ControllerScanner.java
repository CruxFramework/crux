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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Global;
import org.cruxframework.crux.core.client.controller.WidgetController;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.JClassScanner;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfig;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.ui.IsWidget;


/**
 * Maps all controllers in a module.
 * @author Thiago Bustamante
 *
 */
public class ControllerScanner 
{
	private static final Log logger = LogFactory.getLog(ControllerScanner.class);
	private Map<String, Map<String, String>> controllersCanonicalNames;
	private Map<String, Map<String, String>> controllersNames;
	private List<String> globalControllers;
	private boolean initialized = false;
	private JClassScanner jClassScanner;
	private Map<String, Set<String>> widgetControllers;
	
	public ControllerScanner(GeneratorContext context)
    {
		jClassScanner = new JClassScanner(context);
    }
	
	/**
	 * @param name
	 * @return
	 */
	public String getController(String name, Device device)
	{
		initializeControllers();
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
	 * @param controller
	 * @return
	 */
	public Class<?> getControllerClass(String controller, Device device)
	{
		initializeControllers();
		try
        {
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
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasController(String name)
	{
		initializeControllers();
		return (name != null && controllersCanonicalNames.containsKey(name));
	}
	
	/**
	 * @return
	 */
	public Iterator<String> iterateControllers()
	{
		initializeControllers();
		return controllersCanonicalNames.keySet().iterator();
	}

	/**
	 * @return
	 */
	public Iterator<String> iterateGlobalControllers()
	{
		initializeControllers();
		return globalControllers.iterator();
	}
	
	/**
	 * @param widgetClass
	 * @return
	 */
	public Iterator<String> iterateWidgetControllers(String widgetType)
	{
		initializeControllers();
		Set<String> controllers = widgetControllers.get(widgetType);
		if (controllers == null)
		{
			return null;
		}
		return controllers.iterator();
	}
	
	/**
	 * 
	 */
	protected void initializeControllers()
	{
		if (!initialized)
		{
			controllersCanonicalNames = new HashMap<String, Map<String, String>>();
			controllersNames = new HashMap<String, Map<String, String>>();
			globalControllers = new ArrayList<String>();
			widgetControllers = new HashMap<String, Set<String>>();
			
			JClassType[] controllerTypes =  jClassScanner.searchClassesByAnnotation(Controller.class);
			if (controllerTypes != null)
			{
				for (JClassType controllerClass : controllerTypes) 
				{
					try 
					{
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
					catch (Exception e) 
					{
						logger.error("Error initializing client controller ["+controllerClass.getQualifiedSourceName()+"].",e);
					}
				}
			}	
			initialized = true;
		}
	}
	
	/**
	 * 
	 * @param controllerClass
	 * @param controllerKey
	 * @param device
	 */
	private void addResource(JClassType controllerClass, String controllerKey, Device device)
    {
	    if (!controllersCanonicalNames.containsKey(controllerKey))
	    {
	    	controllersCanonicalNames.put(controllerKey, new HashMap<String, String>());
	    	controllersNames.put(controllerKey, new HashMap<String, String>());
	    }
	    Map<String, String> canonicallClassNamesByDevice = controllersCanonicalNames.get(controllerKey);
	    Map<String, String> classNamesByDevice = controllersNames.get(controllerKey);
	    
	    String deviceKey = device.toString();
		if (controllersCanonicalNames.containsKey(deviceKey))
	    {
	    	throw new CruxGeneratorException("Duplicated Client Controller: ["+controllerKey+"].");
	    }
		canonicallClassNamesByDevice.put(deviceKey, controllerClass.getQualifiedSourceName());
		classNamesByDevice.put(deviceKey, controllerClass.getName());
    }
	
	/**
	 * @param controllerClass
	 * @param annot
	 */
	private void initWidgetControllers(JClassType controllerClass, Controller annot)
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
}
