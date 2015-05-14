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
			ServletContext context = contextEvent.getServletContext();
			ServiceFactoryInitializer.initialize(context);
			RestServiceFactoryInitializer.initialize(context);
			
		}
		catch (Throwable e) 
		{
			logger.error(e.getMessage(), e);
		}
	}
}
