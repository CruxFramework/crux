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
package org.cruxframework.crux.core.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.dispatch.ServiceFactoryInitializer;
import org.cruxframework.crux.core.server.rest.core.registry.RestServiceFactoryInitializer;
import org.cruxframework.crux.scanner.ClassScanner;
import org.cruxframework.crux.scanner.Scanners;
import org.cruxframework.crux.scanner.Scanners.ScannerRegistrations;


/**
 * When the application starts, register clientHandlers
 * and widgets into this module. 
 * 
 * @author Thiago
 *
 */
public class InitializerListener implements ServletContextListener
{
	private static ServletContext context;
	private static final Log logger = LogFactory.getLog(InitializerListener.class);
	
	/**
	 * @return
	 */
	public static ServletContext getContext()
	{
		return context;
	}
	
	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent contextEvent) 
	{
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent contextEvent) 
	{
		try
		{
			context = contextEvent.getServletContext();
			if (Environment.isProduction())
			{
				CruxBridge.getInstance().setSingleVM(true);
				Scanners.registerScanners(new ScannerRegistrations()
				{
					@Override
					public boolean initializeEagerly()
					{
						return false;
					}
					
					@Override
					public void doRegistrations()
					{
						ClassScanner.initializeScanner();
					}
				});
			}
			ServiceFactoryInitializer.initialize(context);
			RestServiceFactoryInitializer.initialize(context);
			
		}
		catch (Throwable e) 
		{
			logger.error(e.getMessage(), e);
		}
	}
}
