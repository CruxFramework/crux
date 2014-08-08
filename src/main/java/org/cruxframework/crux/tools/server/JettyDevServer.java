/*
 * Copyright 2014 cruxframework.org
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
package org.cruxframework.crux.tools.server;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.declarativeui.filter.DeclarativeUIFilter;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.DevModeInitializerListener;
import org.cruxframework.crux.core.server.InitializerListener;
import org.cruxframework.crux.core.server.development.ViewTester;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessingException;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JettyDevServer
{
	private static final Log logger = LogFactory.getLog(JettyDevServer.class);

	private String bindAddress = "localhost";
	private int port = getDefaultPort();

	private File appRootDir;
	private String pageOutputCharset = "UTF-8";
	private boolean addDevelopmentComponents = false;

	public JettyDevServer() 
	{
		CruxBridge.getInstance().setSingleVM(true);
	}
	
	public void start() throws Exception 
	{
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setHost(bindAddress);
		connector.setPort(port);
		connector.setReuseAddress(false);
		connector.setSoLingerTime(0);

		Server server = new Server();
		server.addConnector(connector);

		WebAppContext webContext = new WebAppContext();
		webContext.setWar(appRootDir.getCanonicalPath());
		webContext.setParentLoaderPriority(true);
		webContext.setContextPath("/");
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		server.setHandler(webContext);
		if (addDevelopmentComponents)
		{
			webContext.addFilter(DeclarativeUIFilter.class, "*.html", EnumSet.allOf(DispatcherType.class));
			webContext.addServlet(ViewTester.class, "/viewTester/*");
			if (!webContext.getInitParams().containsKey(DevModeInitializerListener.OUTPUT_CHARSET))
			{
				webContext.setInitParameter(DevModeInitializerListener.OUTPUT_CHARSET, pageOutputCharset);
			}
			webContext.addEventListener(new InitializerListener());
			webContext.addEventListener(new DevModeInitializerListener());
		}
		try 
		{
			server.start();
			server.join();
		} 
		catch (Exception e) 
		{
			logger.error("cannot start web server", e);
			throw new UnableToCompleteException();
		}
	}

	protected int getDefaultPort() 
	{
		return 8080;
	}
	
	protected void processParameters(Collection<ConsoleParameter> parameters)
    {
		for (ConsoleParameter parameter : parameters)
        {
	        if (parameter.getName().equals("-appRootDir"))
	        {
	        	appRootDir = new File(parameter.getValue());
	        }
	        else if (parameter.getName().equals("-addDevelopmentComponents"))
	        {
	        	this.addDevelopmentComponents = true;
	        }
	        else if (parameter.getName().equals("-pageOutputCharset"))
	        {
	        	pageOutputCharset = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-bindAddress"))
	        {
	        	bindAddress = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-port"))
	        {
	        	port = Integer.parseInt(parameter.getValue());
	        }
        }
    }

	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("JettyDevServer");
		
		parameter = new ConsoleParameter("-appRootDir", "The application web folder.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("dir", "root dir"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-bindAddress", "The ip address of the code server. Defaults to 127.0.0.1.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("ip", "Ip address"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-port", "The port where the jetty server will run.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("port", "Port"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-pageOutputCharset", "Output charset for generated pages.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dir", "Work dir"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-addDevelopmentComponents", "If informed, Server will add all components required for development on application context.", false, false);
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
	}	
	
	public static void main(String[] args)
    {
		try
		{
			JettyDevServer jettyServer = new JettyDevServer();
			ConsoleParametersProcessor parametersProcessor = jettyServer.createParametersProcessor();
			Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

			if (parameters.containsKey("-help") || parameters.containsKey("-h"))
			{
				parametersProcessor.showsUsageScreen();
			}
			else
			{
				jettyServer.processParameters(parameters.values());
				jettyServer.start();
			}
		}
		catch (ConsoleParametersProcessingException e)
		{
			logger.error("Error processing program parameters: "+e.getLocalizedMessage()+". Program aborted.",e);
		}
		catch (Exception e)
		{
			logger.error("Error running code server: "+e.getLocalizedMessage()+". Program aborted.",e);
		}
    }
}
