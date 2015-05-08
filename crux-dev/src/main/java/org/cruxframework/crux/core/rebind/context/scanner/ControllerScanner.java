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
package org.cruxframework.crux.core.rebind.context.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Global;
import org.cruxframework.crux.core.client.controller.WidgetController;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.JClassScanner;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetScanner;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.ui.IsWidget;


/**
 * Maps all controllers in a module.
 * @author Thiago Bustamante
 *
 */
public class ControllerScanner 
{
	private Map<String, Map<String, String>> controllersCanonicalNames;
	private List<String> globalControllers;
	private boolean initialized = false;
	private JClassScanner jClassScanner;
	private Map<String, Set<String>> widgetControllers;
	
	public ControllerScanner(JClassScanner jClassScanner)
    {
		this.jClassScanner = jClassScanner;
    }
	
	/**
	 * @param name
	 * @return
	 */
	public String getController(String name, Device device) throws ResourceNotFoundException
	{
		initializeControllers();
		Map<String, String> map = controllersCanonicalNames.get(name);
		String result = map.get(device.toString());
		if (result == null && !device.equals(Device.all))
		{
			result = map.get(Device.all.toString());
		}
		if (result == null)
		{
        	throw new ResourceNotFoundException("Can not found requested controller ["+name+"], for device ["+device+"].");
		}
		return result;
	}

	/**
	 * 
	 * @param controller
	 * @return
	 */
	public Class<?> getControllerClass(String controller, Device device) throws ResourceNotFoundException
	{
		String result = getController(controller, device);
		try
        {
	        return Class.forName(result);
        }
        catch (Exception e)
        {
        	throw new ResourceNotFoundException("Can not found requested controller ["+controller+"], for device ["+device+"].");
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
			try 
			{
				controllersCanonicalNames = new HashMap<String, Map<String, String>>();
				globalControllers = new ArrayList<String>();
				widgetControllers = new HashMap<String, Set<String>>();

				JClassType[] controllerTypes =  jClassScanner.searchClassesByAnnotation(Controller.class);
				if (controllerTypes != null)
				{
					for (JClassType controllerClass : controllerTypes) 
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
				}	
				initialized = true;
			}
			catch (Exception e) 
			{
				throw new CruxGeneratorException("Error initializing controllers scanner.", e);
			}
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
	    }
	    Map<String, String> canonicallClassNamesByDevice = controllersCanonicalNames.get(controllerKey);
	    
	    String deviceKey = device.toString();
		if (controllersCanonicalNames.containsKey(deviceKey))
	    {
	    	throw new CruxGeneratorException("Duplicated Client Controller: ["+controllerKey+"].");
	    }
		canonicallClassNamesByDevice.put(deviceKey, controllerClass.getQualifiedSourceName());
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
	    		String widgetType = WidgetScanner.getWidgetType(widgetClass);
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
