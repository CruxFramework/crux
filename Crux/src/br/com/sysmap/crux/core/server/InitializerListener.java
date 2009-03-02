package br.com.sysmap.crux.core.server;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.config.ConfigurationFactory;
import br.com.sysmap.crux.core.server.event.clienthandlers.ClientControllers;
import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.ControllerFactoryInitializer;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;
import br.com.sysmap.crux.core.server.screen.ScreenFactory;
import br.com.sysmap.crux.core.server.screen.config.ComponentConfig;
import br.com.sysmap.crux.core.server.screen.formatter.Formatters;

/**
 * When the application starts, create the web module and register clientHandlers
 * and components into this module. Then, compile the module and generate the script
 * to use into the web pages.
 * 
 * @author Thiago
 *
 */
public class InitializerListener implements ServletContextListener
{
	private static final Log logger = LogFactory.getLog(InitializerListener.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) 
	{
	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) 
	{
		try
		{
			ConfigurationFactory.getConfiguration();
			ServletContext contexto = contextEvent.getServletContext();
			initialize(contexto);
		}
		catch (Throwable e) 
		{
			logger.error(e.getMessage(), e);
		}
	}
	
	protected void initialize(ServletContext contexto) throws Exception
	{
		URL[] urls;
		if ("true".equals(ConfigurationFactory.getConfiguration().lookupWebInfOnly()))
		{
			urls = ScannerURLS.getURLsForSearch(contexto);
		}
		else
		{
			urls = ScannerURLS.getURLsForSearch(null);
		}

		ComponentConfig.initializeComponentConfig(urls);
		if (logger.isInfoEnabled())
		{
			logger.info(messages.initializerListenerComponentsRegistered());
		}
		
		if ("true".equals(ConfigurationFactory.getConfiguration().initializeControllersAtStartup()))
		{
			ControllerFactoryInitializer.getControllerFactory().initialize(contexto);
			if (logger.isInfoEnabled())
			{
				logger.info(messages.initializerListenerControllersRegistered());
			}
			ClientControllers.initialize(urls);
			if (logger.isInfoEnabled())
			{
				logger.info(messages.initializerListenerClientModulesInitialized());
			}
		}

		if ("true".equals(ConfigurationFactory.getConfiguration().initializeFormattersAtStartup()))
		{
			Formatters.initialize(urls);
			if (logger.isInfoEnabled())
			{
				logger.info(messages.initializerListenerClientFormattersInitialized());
			}
		}
		
		ScreenFactory.getInstance(contexto);
		if (logger.isInfoEnabled())
		{
			logger.info(messages.initializerListenerScreenFactoryInitialized());
		}
	}
}
