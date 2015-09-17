/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.dispatch.ServicesCompileMap;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Builds a map of all widget libraries  
 * @author Thiago da Rosa de Bustamante
 */
public class WidgetLibraryMap
{
	private static WidgetLibraryMap instance = new WidgetLibraryMap();
	private static final Log logger = LogFactory.getLog(WidgetLibraryMap.class); 
	
	private Map<String, String> factoryClass = null;
	private Map<String, Set<String>> registeredLibraries = null;
	private Map<String, String> widgetTypes = null;
	
	private WidgetLibraryMap(){}
	
	/**
	 * @param serviceName
	 * @return
	 */
	public String getFactoryClass(String serviceName)
	{
		return factoryClass.get(serviceName);	
	}

	public Set<String> getRegisteredLibraries()
    {
	    return registeredLibraries.keySet();
    }

	public Set<String> getRegisteredLibraryFactories(String library)
    {
	    return registeredLibraries.get(library);
    }

	public String getWidgetType(Class<? extends IsWidget> widgetClass)
    {
		return widgetTypes.get(widgetClass.getCanonicalName());
    }

	/**
	 * @param context
	 */
	public boolean initialize(ServletContext context)
	{
		try
		{
			initializeFactoryClass(context);
			initializeWidgetType(context);
			logger.info("Widget Libraries initialized using maps strategy.");
			return true;
		}
		catch (Exception e)
		{
			logger.info("Error initializing WidgetLibraries with maps strategy...");
		}
		return false;
	}

	private void initializeFactoryClass(ServletContext context) throws IOException
    {
	    factoryClass = new HashMap<String, String>(100);
		registeredLibraries = new HashMap<String, Set<String>>();
	    Properties widgets = new Properties();
	    if (context != null)
	    {
	    	widgets.load(context.getResourceAsStream("/META-INF/crux-widgets-factory"));
	    }
	    else
	    {
	    	widgets.load(ServicesCompileMap.class.getResourceAsStream("/META-INF/crux-widgets-factory"));
	    }
	    Enumeration<?> widgetTypes = (Enumeration<?>) widgets.propertyNames();
	    while (widgetTypes.hasMoreElements())
	    {
	    	String widgetType = (String) widgetTypes.nextElement();
	    	
	    	registerWidgetLibrary(widgetType);
	    	
	    	factoryClass.put(widgetType, widgets.getProperty(widgetType));
	    }
    }

	private void initializeWidgetType(ServletContext context) throws IOException
    {
		widgetTypes = new HashMap<String, String>(100);
	    Properties widgets = new Properties();
	    if (context != null)
	    {
	    	widgets.load(context.getResourceAsStream("/META-INF/crux-widgets-type"));
	    }
	    else
	    {
	    	widgets.load(ServicesCompileMap.class.getResourceAsStream("/META-INF/crux-widgets-type"));
	    }
	    Enumeration<?> widgetClasses = (Enumeration<?>) widgets.propertyNames();
	    while (widgetClasses.hasMoreElements())
	    {
	    	String widgetClass = (String) widgetClasses.nextElement();
	    	widgetTypes.put(widgetClass, widgets.getProperty(widgetClass));
	    }
    }

	private void registerWidgetLibrary(String widgetType)
    {
		int index = widgetType.indexOf('_');
		
		if (index < 0 || index >= widgetType.length()-1)
		{
			logger.error("Invalid entry on crux-widgets-class map. Ignoring entry ["+widgetType+"]...");
			return;
		}
		String library = widgetType.substring(0, index);
		String widget = widgetType.substring(index + 1);
		if (!registeredLibraries.containsKey(library))
		{
			registeredLibraries.put(library, new HashSet<String>());
		}
		registeredLibraries.get(library).add(widget);
    }
	
	public static WidgetLibraryMap getInstance()
	{
		return instance;
	}
}
