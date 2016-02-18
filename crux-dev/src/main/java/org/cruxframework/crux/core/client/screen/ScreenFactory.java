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
package org.cruxframework.crux.core.client.screen;

import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.controller.RegisteredControllers;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewContainer;
import org.cruxframework.crux.core.client.utils.StringUtils;

/**
 * Factory for CRUX screen. 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenFactory 
{
	private static ScreenFactory instance = null;

	private static Logger logger = Logger.getLogger(ScreenFactory.class.getName());
	 
	private Screen screen = null;
	
	/**
	 * Constructor
	 */
	private ScreenFactory()
	{
	}
	
	/**
	 * Retrieve the ScreenFactory instance.
	 * @return
	 */
	public static ScreenFactory getInstance()
	{
		if (instance == null)
		{
			instance = new ScreenFactory();
		}
		return instance;
	}
	
	/**
	 * Get the screen associated with current page. If not created yet, create it.
	 * @return
	 */
	public Screen getScreen()
	{
		return getScreen(null, null);
	}
	
	/**
	 * Get the screen associated with current page. If not created yet, create it.
	 * @return
	 */
	public Screen getScreen(String screenName, String rootViewElementID)
	{
		if (screen == null)
		{
			create(screenName, rootViewElementID);
		}
		return screen;
	}
	
	/**
	 * Retrieve the device type for the client running the application
	 * @return
	 */
	public Device getCurrentDevice()
	{
		return ViewContainer.getViewFactory().getCurrentDevice();
	}
		
	private void create()
	{
		create(null, null);
	}
	
	private void create(String screenName, String rootViewElementID)
	{
		String currentScreenName = StringUtils.isEmpty(screenName) ? getCurrentScreenName() : screenName;
		if(!StringUtils.isEmpty(currentScreenName))
		{
			screen = new Screen(currentScreenName, rootViewElementID);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private native String getCurrentScreenName()/*-{
		return $wnd.__CruxScreen_;
	}-*/;
	
	/**
	 * 
	 * @param formatter
	 * @return
	 */
	@Legacy
	@Deprecated
	public Formatter getClientFormatter(String formatter)
	{
		return screen.getRegisteredFormatters().getClientFormatter(formatter);
	}
	
	/**
	 * Create a new DataSource instance
	 * @param dataSource dataSource name, declared with <code>@DataSource</code> annotation
	 * @return new dataSource instance
	 * @deprecated Use {@link View}.createDataSource()
	 */
	@Legacy
	@Deprecated
	public DataSource<?> createDataSource(String dataSource)
	{
		return Screen.createDataSource(dataSource);
	}

	/**
	 * @deprecated - Use createDataSource(java.lang.String) instead.
	 * @param dataSource
	 * @return
	 */
	@Legacy
	@Deprecated
	public DataSource<?> getDataSource(String dataSource)
	{
		return createDataSource(dataSource);
	}
	
	/**
	 * Retrieve the list of controllers registered into this screen
	 * @return
	 * @deprecated Use {@link View}.getRegisteredControllers()
	 */
	@Legacy
	@Deprecated
	public RegisteredControllers getRegisteredControllers()
    {
    	return screen.getRegisteredControllers();
    }
}
