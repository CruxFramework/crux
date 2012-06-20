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

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.datasource.RegisteredDataSources;
import org.cruxframework.crux.core.client.event.RegisteredControllers;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.RegisteredClientFormatters;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.parser.CruxMetaData;


import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * Factory for CRUX screen. 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenFactory 
{
	private static ScreenFactory instance = null;

	private static Logger logger = Logger.getLogger(ScreenFactory.class.getName());
	 
	private RegisteredClientFormatters registeredClientFormatters = null;
	private RegisteredControllers registeredControllers = null;
	private RegisteredDataSources registeredDataSources = null;
	private Screen screen = null;
	private ViewFactory viewFactory = null;
	private Device currentDevice = null;	
	
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
	 * Create a new DataSource instance
	 * @param dataSource dataSource name, declared with <code>@DataSource</code> annotation
	 * @return new dataSource instance
	 */
	public DataSource<?> createDataSource(String dataSource)
	{
		return this.registeredDataSources.getDataSource(dataSource);
	}

	/**
	 * 
	 * @param formatter
	 * @return
	 */
	public Formatter getClientFormatter(String formatter)
	{
		assert(Crux.getConfig().enableCrux2OldInterfacesCompatibility()):Crux.getMessages().screenFactoryCrux2OldInterfacesCompatibilityDisabled();
		if (this.registeredClientFormatters == null)
		{
			this.registeredClientFormatters = (RegisteredClientFormatters) GWT.create(RegisteredClientFormatters.class);
		}

		return this.registeredClientFormatters.getClientFormatter(formatter);
	}
	
	/**
	 * @deprecated - Use createDataSource(java.lang.String) instead.
	 * @param dataSource
	 * @return
	 */
	@Deprecated
	public DataSource<?> getDataSource(String dataSource)
	{
		return createDataSource(dataSource);
	}
	
	/**
	 * Retrieve the list of controllers registered into this screen
	 * @return
	 */
	public RegisteredControllers getRegisteredControllers()
    {
    	return registeredControllers;
    }

	/**
	 * Get the screen associated with current page. If not created yet, create it.
	 * @return
	 */
	public Screen getScreen()
	{
		if (screen == null)
		{
			create();
		}
		return screen;
	}
	
	/**
	 * Retrieve the device type for the client running the application
	 * @return
	 */
	public Device getCurrentDevice()
	{
		return currentDevice;
	}
	
	/**
	 * Called by ViewFactory to initialize the screen controllers 
	 * @param registeredControllers
	 */
	void setRegisteredControllers(RegisteredControllers registeredControllers)
	{
		this.registeredControllers = registeredControllers;
	}
	
	/**
	 * Called by ViewFactory to initialize the screen dataSources 
	 * @param registeredDataSources
	 */
	void setRegisteredDataSources(RegisteredDataSources registeredDataSources)
	{
		this.registeredDataSources = registeredDataSources;
	}

	/**
	 * 
	 */
	private void create()
	{
		CruxMetaData metaData = CruxMetaData.loadMetaData();
		screen = new Screen(metaData.getScreenId(), metaData.getLazyDependencies());
		this.viewFactory = (ViewFactory) GWT.create(ViewFactory.class);
		currentDevice = this.viewFactory.getCurrentDevice();
		
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.info(Crux.getMessages().screenFactoryCreatingView(screen.getIdentifier()));
		}
		try
        {
	        this.viewFactory.createView(screen.getIdentifier());
        }
        catch (InterfaceConfigException e)
        {
        	Crux.getErrorHandler().handleError(Crux.getMessages().screenFactoryErrorCreatingView(), e);
        }
	}
}
