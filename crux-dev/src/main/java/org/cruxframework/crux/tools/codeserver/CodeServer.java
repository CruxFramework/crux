/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.tools.codeserver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.rebind.DevelopmentScanners;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.dispatch.ServiceFactoryInitializer;
import org.cruxframework.crux.core.server.rest.core.registry.RestServiceFactoryInitializer;
import org.cruxframework.crux.scanner.ClasspathUrlFinder;
import org.cruxframework.crux.scanner.Scanners;
import org.cruxframework.crux.tools.compile.CruxRegisterUtil;
import org.cruxframework.crux.tools.compile.utils.ModuleUtils;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessingException;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.gwt.dev.codeserver.Options;
import com.google.gwt.dev.codeserver.WebServer;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CodeServer
{
	private static final String STOP_CODE_SERVER_PATH = "/stopcodeserver";
	private static final Log logger = LogFactory.getLog(CodeServer.class);

	private String moduleName;
	private String sourceDir;
	private String bindAddress;
	private boolean noPrecompile = false;
	private String port = "9876";
	private String workDir;
	private WebServer webServer;
	
	public String getModuleName()
    {
    	return moduleName;
    }

	public void setModuleName(String moduleName)
    {
    	this.moduleName = moduleName;
    }

	public String getSourceDir()
    {
    	return sourceDir;
    }

	public void setSourceDir(String sourceDir)
    {
    	this.sourceDir = sourceDir;
    }

	public String getBindAddress()
    {
    	return bindAddress;
    }

	public void setBindAddress(String bindAddress)
    {
    	this.bindAddress = bindAddress;
    }

	public String getPort()
    {
    	return port;
    }

	public void setPort(String port)
    {
    	this.port = port;
    }

	public String getWorkDir()
    {
    	return workDir;
    }

	public void setWorkDir(String workDir)
    {
    	this.workDir = workDir;
    }

	protected void execute() throws Exception
    {
		killOldServer();
		
		URL[] urls = ClasspathUrlFinder.findClassPaths();
		Scanners.setSearchURLs(urls);
		ModuleUtils.initializeScannerURLs(urls);
		CruxRegisterUtil.registerFilesCruxBridge(null);
		DevelopmentScanners.initializeScanners();
		ServiceFactoryInitializer.initialize(null);
		RestServiceFactoryInitializer.initialize(null);
		
		Set<String> screenIDs = null;
		try
        {
	        screenIDs = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllScreenIDs(moduleName);
        }
    	catch (Exception e)
    	{
    		logger.info("Error retrieving crux pages list for module ["+moduleName+"]. "
    				+ "Please, verify if the module name parameter matches the module short name on your .gwt file", e);
    	}
		if (screenIDs != null && !screenIDs.isEmpty())
		{
			logger.info("Starting code server for module ["+moduleName+"]");
			CruxBridge.getInstance().registerLastPageRequested(screenIDs.iterator().next());
			String[] args = getServerParameters();
			runGWTCodeServer(args);
		}
    }

	protected void runGWTCodeServer(String[] args) throws Exception 
	{
		Options options = new Options();
		if (!options.parseArgs(args)) 
		{
			System.exit(1);
		}
		try 
		{
			webServer = com.google.gwt.dev.codeserver.CodeServer.start(options);
			addStopCapabilitiesToJettyServer();
			String url = "http://localhost:" + port + "/";
			logger.info("The code server is ready.");
			logger.info("Next, visit: " + url);
		} 
		catch (Throwable t) 
		{
			logger.error("Error running code server", t);;
			System.exit(1);
		}
	}

	/**
	 * It is an workaround for a bug o eclipse IDE. When stopping a debug on Eclipse, 
	 * it kills the JVM not gracefully and the Jetty server does not die on Windows.
	 * So, we need to create a strategy to kill those "zombie" jetty servers that stays
	 * running after a JVM kill. If we does not kill, another jetty can not start a code
	 * server session, because the port is being listened by the old jetty process.
	 */
	private void killOldServer() 
	{
        URLConnection connection;
        String uri = "http://localhost:" + port + STOP_CODE_SERVER_PATH;
        try 
        {
			connection = new URL(uri).openConnection();
			connection.connect();
			connection.getContent();
			logger.info("Shutting down Old Code server instance...");
			Thread.sleep(2500);
		}
        catch (Exception e) 
        {
        	//IGNORE. We don't have any old server
		}
    }

	/**
	 * It is an workaround for a bug o eclipse IDE. When stopping a debug on Eclipse, 
	 * it kills the JVM not gracefully and the Jetty server does not die on Windows.
	 * So, we need to create a strategy to kill those "zombie" jetty servers that stays
	 * running after a JVM kill. If we does not kill, another jetty can not start a code
	 * server session, because the port is being listened by the old jetty process.
	 */
	private void addStopCapabilitiesToJettyServer() 
	{
		try 
		{
			Field serverField = WebServer.class.getDeclaredField("server");
			serverField.setAccessible(true);
			Server server = (Server) serverField.get(webServer);
			
		    ServletContextHandler handler = (ServletContextHandler) server.getHandler();
		    handler.addServlet(new ServletHolder(new HttpServlet() 
		    {
				private static final long serialVersionUID = -2492061626388189841L;

				@Override
				protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
				{
					stopServer(request, response);
				}
		    }), STOP_CODE_SERVER_PATH);
		} 
		catch (Exception e) 
		{
			logger.error("Error registering stop servlet", e);
		}
	}

	private void stopServer(HttpServletRequest request, HttpServletResponse response) 
	{
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK); //200
		setRequestHandled(request);

		logger.info("Stopping jetty server...");
		new Thread()
		{
			public void run() 
			{
				try 
				{
					Thread.sleep(1000);
					webServer.stop();
					Thread.sleep(1000);
					logger.info("Terminating code server");
					System.exit(0);
				} 
				catch (Exception e) 
				{
					logger.error(e.getMessage(), e);
				}
			};
		}.start();
	}

	private void setRequestHandled(HttpServletRequest request) 
	{
		try
		{
			Method setHandledMethod = WebServer.class.getDeclaredMethod("setHandled", new Class<?>[]{HttpServletRequest.class});
			setHandledMethod.setAccessible(true);
			setHandledMethod.invoke(null, request);
		}
		catch(Exception e)
		{
			logger.error("Error handling request to stopJetty servlet", e);
		}
	}

	protected String[] getServerParameters()
    {
		List<String> args = new ArrayList<String>();

		if (noPrecompile)
		{
			args.add("-noprecompile");
		}
		if (bindAddress != null && bindAddress.length() > 0)
		{
			args.add("-bindAddress");
			args.add(bindAddress);
		}
		if (port != null && port.length() > 0)
		{
			args.add("-port");
			args.add(port);
		}
		if (workDir != null && workDir.length() > 0)
		{
			args.add("-workDir");
			args.add(workDir);
		}
		if (sourceDir != null && sourceDir.length() > 0)
		{
			args.add("-src");
			args.add(sourceDir);
		}
		if (moduleName != null && moduleName.length() > 0)
		{
			String moduleFullName = Modules.getInstance().getModule(moduleName).getFullName();
			args.add(moduleFullName);
		}
	    return args.toArray(new String[args.size()]);
    }

	protected void processParameters(Collection<ConsoleParameter> parameters)
    {
		for (ConsoleParameter parameter : parameters)
        {
	        if (parameter.getName().equals("-moduleName"))
	        {
	        	moduleName = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-noprecompile"))
	        {
	        	this.noPrecompile = true;
	        }
	        else if (parameter.getName().equals("-sourceDir"))
	        {
	        	sourceDir = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-bindAddress"))
	        {
	        	bindAddress = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-port"))
	        {
	        	port = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-workDir"))
	        {
	        	workDir = parameter.getValue();
	        }
        }
    }

	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("CodeServer");

		parameter = new ConsoleParameter("-moduleName", "The name of the module to be compiled.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("name", "Module name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("-sourceDir", "The application source folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dir", "Source dir"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-bindAddress", "The ip address of the code server. Defaults to 127.0.0.1.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("ip", "Ip address"));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-noprecompile", "If informed, code server will not pre compile the source.", false, true));
		
		parameter = new ConsoleParameter("-port", "The port where the code server will run.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("port", "Port"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-workDir", " The root of the directory tree where the code server will write compiler output. If not supplied, a temporary directory will be used.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dir", "Work dir"));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
	}	
	
	public static void main(String[] args)
    {
		try
		{
			CodeServer codeServer = new CodeServer();
			ConsoleParametersProcessor parametersProcessor = codeServer.createParametersProcessor();
			Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

			if (parameters.containsKey("-help") || parameters.containsKey("-h"))
			{
				parametersProcessor.showsUsageScreen();
			}
			else
			{
				codeServer.processParameters(parameters.values());
				codeServer.execute();
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
