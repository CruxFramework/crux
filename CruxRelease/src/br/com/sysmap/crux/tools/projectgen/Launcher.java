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
package br.com.sysmap.crux.tools.projectgen;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.mortbay.jetty.AbstractConnector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import br.com.sysmap.crux.core.utils.FileUtils;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class Launcher
{

	/**
	 * @param port
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
	 * @param appDir
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
	
	protected void installQuickStartApp() throws Exception
	{
		File webAppLib = new File("./apps/quickstart.war/WEB-INF/lib");
		File buildLib = new File("./lib/build");
		File webInfLib = new File("./lib/web-inf");
		File modulesBuildLib = new File("./lib/modules/build");
		File modulesWebInfLib = new File("./lib/modules/web-inf");
		
		FileUtils.copyFilesFromDir(buildLib, webAppLib);
		FileUtils.copyFilesFromDir(webInfLib, webAppLib);
		FileUtils.copyFilesFromDir(modulesBuildLib, webAppLib);
		FileUtils.copyFilesFromDir(modulesWebInfLib, webAppLib);
	}
	
	protected void installHelloWorldApp() throws Exception
	{
		File webAppLib = new File("./apps/helloworld.war/WEB-INF/lib");
		File webInfLib = new File("./lib/web-inf");
		
		FileUtils.copyFilesFromDir(webInfLib, webAppLib);
	}
	
	/**
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
			launcher.installHelloWorldApp();
			System.out.println("Quickstart application installed. To Uninstall, just remove the crux distribution folder.");
		}
		
		launcher.startServer(port);
		launcher.openQuickStart(port);
    }
}
