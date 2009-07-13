/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.screen.config.WidgetConfig;
import br.com.sysmap.crux.core.server.dispatch.ControllerFactoryInitializer;

/**
 * When the application starts, register clientHandlers
 * and widgets into this module. 
 * 
 * @author Thiago
 *
 */
public class InitializerListener implements ServletContextListener
{
	private static final Log logger = LogFactory.getLog(InitializerListener.class);

	public void contextDestroyed(ServletContextEvent contextEvent) 
	{
	}

	/**
	 * 
	 */
	public void contextInitialized(ServletContextEvent contextEvent) 
	{
		try
		{
			ConfigurationFactory.getConfigurations();
			initialize(contextEvent.getServletContext());
		}
		catch (Throwable e) 
		{
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected void initialize(ServletContext context) throws Exception
	{
		// Initialise the isProduction static variable.
		Environment.isProduction();
		WidgetConfig.initializeWidgetConfig();
		
		if ("true".equals(ConfigurationFactory.getConfigurations().initializeControllersAtStartup()))
		{
			ControllerFactoryInitializer.initialize(context);
		}
	}
}
