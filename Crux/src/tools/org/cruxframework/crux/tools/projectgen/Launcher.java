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
package org.cruxframework.crux.tools.projectgen;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.utils.FileUtils;
import org.mortbay.jetty.AbstractConnector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;


/**
 * Complete Crux installation and starts the quickstart application.
 * @author Thiago da Rosa de Bustamante
 */
public class Launcher
{

	/**
	 * Start the jetty server, containing the quickstart and helloworld applications 
	 * and the Crux documentation.
	 * @param port port used by jetty to serve the applicatoins.
	 * @throws Exception
	 */
	public void startServer(int port) throws Exception
	{
		Server server = new Server();
		
		AbstractConnector connector =  new SelectChannelConnector();
        connector.setHost("127.0.0.1");
        connector.setPort(port);
        connector.setReuseAddress(false);
        connector.setSoLingerTime(0);
        
        server.addConnector(connector);

		HandlerList hl = new HandlerList();
		hl.setHandlers(getApplications(new File(".", "apps")));
		server.setHandler(hl);

		server.start();
	    server.setStopAtShutdown(true);		
	}
	
	/**
	 * Open the default braowser pointing to the quickstart application URL.
	 * @param port
	 * @throws Exception 
	 */
	public void openQuickStart(int port) throws Exception
	{
		String url = "http://localhost:"+port+"/quickstart/quickstart/index.html";
		
		
		if(!java.awt.Desktop.isDesktopSupported()) 
		{
            System.err.println( "Desktop is not supported (fatal)" );
        }
		else
		{
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			if(!desktop.isSupported( java.awt.Desktop.Action.BROWSE )) 
			{
				System.err.println( "Desktop doesn't support the browse action" );
			}
			else
			{
				desktop.browse(new URL(url).toURI());
			}
		}
	}
	
	/**
	 * Create a jetty handler for each application located on the appDir directory.
	 * <p>
	 * Applications must be deployed as an .war package.
	 * @param appDir directory that contains the applications to be installed.
	 * @return
	 */
	protected Handler[] getApplications(File appDir) throws Exception
	{
		List<Handler> result = new ArrayList<Handler>();
		
		File[] files = appDir.listFiles();
		for (File file : files)
        {
			String webappPath = file.getCanonicalPath();
			if (webappPath.endsWith(".war"))
			{
				int indexLastSlash = webappPath.lastIndexOf(File.separatorChar)+1;
				String contextPath = webappPath.substring(indexLastSlash, webappPath.length()-4);
				
				WebAppContext webAppContext = new WebAppContext(webappPath, "/"+contextPath);
				result.add(webAppContext);
			}
        }
		
		result.add(getDocumentationHandler());

		return result.toArray(new Handler[result.size()]);
	}

	/**
	 * Create a handler to serve the Crux javadoc on the embedded jetty
	 * @return
	 */
	protected ContextHandler getDocumentationHandler()
    {
	    ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase("./docs/");

        ContextHandler contextHandler = new ContextHandler("/docs");
        contextHandler.setResourceBase("./docs/");
		contextHandler.addHandler(resourceHandler);
	    return contextHandler;
    }
	
	/**
	 * Install Quickstart application on the embedded jetty
	 * @throws Exception
	 */
	protected void installQuickStartApp() throws Exception
	{
		File webAppLib = new File("./apps/quickstart.war/WEB-INF/lib");
		File buildLib = new File("./lib/build");
		File webInfLib = new File("./lib/web-inf");
		File gadgetBuildLib = new File("./lib/gadget/build");
		
		FileUtils.copyFilesFromDir(gadgetBuildLib, webAppLib);
		FileUtils.copyFilesFromDir(buildLib, webAppLib);
		FileUtils.copyFilesFromDir(webInfLib, webAppLib);
	}
	
	/**
	 * Install HelloWorld application on the embedded jetty
	 * @throws Exception
	 */
	protected void installHelloWorldApp() throws Exception
	{
		File webAppLib = new File("./apps/helloworld.war/WEB-INF/lib");
		File webInfLib = new File("./lib/web-inf");
		
		FileUtils.copyFilesFromDir(webInfLib, webAppLib);
	}
	
	/**
     * Complete Crux installation and starts the quickstart application.
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
    {
		Launcher launcher = new Launcher();
		int port = 8910;
		
		File webAppLib = new File("./apps/quickstart.war/WEB-INF/lib");
		if (!webAppLib.exists() || webAppLib.list().length == 0)
		{
			System.out.println("Installing quickstart application...");
			launcher.installQuickStartApp();

			System.out.println("Installing helloworld application...");
			launcher.installHelloWorldApp();

			System.out.println("Applications installed. To Uninstall, just remove the crux distribution folder.");
		}
		
		launcher.startServer(port);
		launcher.openQuickStart(port);
    }
}
