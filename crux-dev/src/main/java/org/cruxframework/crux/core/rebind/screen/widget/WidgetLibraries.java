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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author thiago
 *
 */
public class WidgetLibraries
{
	private static boolean initialized = false;
	private static WidgetLibraries instance = new WidgetLibraries();
	private static final Log logger = LogFactory.getLog(WidgetLibraries.class);
	
	private ScanningStrategy strategy;
	
	public WidgetLibraries()
    {
		strategy = new MapStrategy();
    }
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public String getClientClass(String id)
	{
		initialize();
		return strategy.getClientClass(id);
	}
	
	/**
	 * 
	 * @param library
	 * @param id
	 * @return
	 */
	public String getClientClass(String library, String id)
	{
		initialize();
		return strategy.getClientClass(library+"_"+id);
	}
	
	public String getWidgetType(Class<? extends IsWidget> widgetClass)
    {
		initialize();
		return strategy.getWidgetType(widgetClass);
    }

	/**
	 * 
	 * @return
	 */
	public Iterator<String> iterateRegisteredLibraries()
	{
		initialize();
		return strategy.iterateRegisteredLibraries();
	}

	/**
	 * 
	 * @param library
	 * @return
	 */
	public Iterator<String> iterateRegisteredLibraryWidgetCreators(String library)
	{
		initialize();
		return strategy.iterateRegisteredLibraryWidgetCreators(library);
	}

	
	
	void initialize()
	{
		if (!initialized)
		{
			if (!strategy.initialize())
			{
				if (strategy instanceof MapStrategy)
				{
					logger.info("Widget Libraries map not found. Run the widgetLibrariesImporter tool to avoid runtime scanning. Using runtime strategy for scan libraries...");
					strategy = new RuntimeStrategy();
					strategy.initialize();
				}
				else
				{
					logger.error("Error initializing Widget Libraries.");
				}
			}
			initialized = true;
		}
	}
	
	public static WidgetLibraries getInstance()
	{
		return instance;
	}

	/**
	 * This class uses a file generated during application compilation to find out rest service classes.
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class MapStrategy implements ScanningStrategy
	{

		public String getClientClass(String id)
		{
			return null;
//			return WidgetCreatorsMap.getService(id);
		}

		@Override
        public String getWidgetType(Class<? extends IsWidget> widgetClass)
        {
			return null;
//	        return WidgetCreatorsMap.getWidgetType(widgetClass);
        }
		
		public boolean initialize()
		{
			return false;
//			return WidgetCreatorsMap.initialize();
		}

		@Override
		public Iterator<String> iterateRegisteredLibraries()
		{
			return null;
//		    return WidgetCreatorsMap.getRegisteredLibraries().iterator();
		}

		@Override
        public Iterator<String> iterateRegisteredLibraryWidgetCreators(String library)
        {
			return null;
//	        return WidgetCreatorsMap.getRegisteredLibraryFactories(library).iterator();
        }
	}

	/**
	 * This class scan the application classpath to build a map of 
	 * widget creators.
	 *
	 * @author Thiago da Rosa de Bustamante
	 */
	private static class RuntimeStrategy implements ScanningStrategy
	{
		public String getClientClass(String id)
		{
			return WidgetScanner.getClientClass(id);
		}

		@Override
        public String getWidgetType(Class<? extends IsWidget> widgetClass)
        {
	        return WidgetScanner.getWidgetType(widgetClass);
        }
		
		public boolean initialize()
		{
			try
			{
				WidgetScanner.initialize();
				return true;
			}
			catch (Exception e)
			{
				logger.error("Error initializing widget scanner.", e);
				return false;
			}
		}

		@Override
		public Iterator<String> iterateRegisteredLibraries()
		{
		    return WidgetScanner.getRegisteredLibraries().iterator();
		}

		@Override
        public Iterator<String> iterateRegisteredLibraryWidgetCreators(String library)
        {
	        return WidgetScanner.getRegisteredLibraryFactories(library).iterator();
        }
	}

	/**
	 * Describes a strategy for widgets scanning.
	 * @author Thiago da Rosa de Bustamante
	 */
	private static interface ScanningStrategy
	{
		String getClientClass(String id);
		String getWidgetType(Class<? extends IsWidget> widgetClass);
		boolean initialize();
		Iterator<String> iterateRegisteredLibraries();
		Iterator<String> iterateRegisteredLibraryWidgetCreators(String library);
	}
}
