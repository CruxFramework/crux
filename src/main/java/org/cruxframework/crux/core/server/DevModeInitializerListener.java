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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfig;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DevModeInitializerListener implements ServletContextListener
{
	private static final Log logger = LogFactory.getLog(InitializerListener.class);

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent contextEvent)
    {
		String charset = contextEvent.getServletContext().getInitParameter("outputCharset");
		//TODO remover isso aqui... fixar o uso em UTF-8
		if(charset != null)
		{
			ViewProcessor.setOutputCharset(charset);
		}
		else
		{
			logger.error("Missing required parameter for [DevModeInitializerListener] in web.xml: [outputCharset].");
		}
		if (!Environment.isProduction())
		{
			WidgetConfig.initialize();
		}
    }

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce)
    {
    }
}
