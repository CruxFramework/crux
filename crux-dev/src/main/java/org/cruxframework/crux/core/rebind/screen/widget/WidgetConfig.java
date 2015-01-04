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
package org.cruxframework.crux.core.rebind.screen.widget;
 
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.RequiresResizeFactory;
import org.cruxframework.crux.scanner.ClassScanner;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetConfig 
{
	private static Map<String, String> config = null;
	private static Map<String, String> widgets = null;
	private static Set<String> widgetContainers = null;
	private static Set<String> requiresResizeWidgets = null;
	private static Map<String, Set<String>> registeredLibraries = null;
	private static final Log logger = LogFactory.getLog(WidgetConfig.class);
	private static final Lock lock = new ReentrantLock();

	/**
	 * 
	 */
	public static void initialize()
	{
		if (config != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (config != null)
			{
				return;
			}
			
			initializeWidgetConfig();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static void initializeWidgetConfig()
	{
		config = new HashMap<String, String>(100);
		widgets = new HashMap<String, String>();
		widgetContainers = new HashSet<String>();
		requiresResizeWidgets = new HashSet<String>();
		registeredLibraries = new HashMap<String, Set<String>>();
		Set<String> factoriesNames =  ClassScanner.searchClassesByAnnotation(org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory.class);
		if (factoriesNames != null)
		{
			for (String name : factoriesNames) 
			{
				try 
				{
					Class<? extends WidgetCreator<?>> factoryClass = (Class<? extends WidgetCreator<?>>)Class.forName(name);
					org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory annot = 
						factoryClass.getAnnotation(org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory.class);
					if (!registeredLibraries.containsKey(annot.library()))
					{
						registeredLibraries.put(annot.library(), new HashSet<String>());
					}
					registeredLibraries.get(annot.library()).add(annot.id());
					String widgetType = annot.library() + "_" + annot.id();
					
					config.put(widgetType, factoryClass.getCanonicalName());
					widgets.put(annot.targetWidget().getCanonicalName(), widgetType);
					
					if (WidgetContainer.class.isAssignableFrom(factoryClass))
					{
						widgetContainers.add(widgetType);
					}
					
					if (RequiresResizeFactory.class.isAssignableFrom(factoryClass))
					{
						requiresResizeWidgets.add(widgetType);
					}
				} 
				catch (ClassNotFoundException e) 
				{
					throw new WidgetConfigException("Error initializing widgets.",e);
				}
			}
		}
		if (logger.isInfoEnabled())
		{
			logger.info("Widgets registered.");
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static String getClientClass(String id)
	{
		if (config == null)
		{
			initialize();
		}
		return config.get(id);
	}

	/**
	 * 
	 * @param library
	 * @param id
	 * @return
	 */
	public static String getClientClass(String library, String id)
	{
		if (config == null)
		{
			initialize();
		}
		return config.get(library+"_"+id);
	}

	/**
	 * 
	 * @return
	 */
	public static Set<String> getRegisteredLibraries()
	{
		if (registeredLibraries == null)
		{
			initialize();
		}
		
		return registeredLibraries.keySet();
	}

	/**
	 * 
	 * @param library
	 * @return
	 */
	public static Set<String> getRegisteredLibraryFactories(String library)
	{
		if (registeredLibraries == null)
		{
			initializeWidgetConfig();
		}
		
		return registeredLibraries.get(library);
	}

	/**
	 * @param widgetClass
	 * @return
	 */
	public static String getWidgetType(Class<?> widgetClass)
    {
		if (widgets == null)
		{
			initializeWidgetConfig();
		}
		return widgets.get(widgetClass.getCanonicalName());
    }
	
	/**
	 * @param type
	 * @return
	 */
	@Deprecated
	@Legacy
	public static boolean isWidgetContainer(String type)
	{
		if (widgetContainers == null)
		{
			initializeWidgetConfig();
		}
		return widgetContainers.contains(type);
	}
	
	/**
	 * @param type
	 * @return
	 */
	public static boolean isRequiresResizeWidget(String type)
	{
		if (requiresResizeWidgets == null)
		{
			initializeWidgetConfig();
		}
		return requiresResizeWidgets.contains(type);
	}
}
