/*
 * Copyright 2013 cruxframework.org
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
package org.cruxframework.crux.core.server.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.mortbay.jetty.webapp.WebAppContext;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.shell.jetty.JettyLauncher;

/**
 * Extends JettyLauncher to allow applications to use custom resources on jetty 
 * (like datasources).
 * 
 * This class implements the solution described 
 * <a href="http://groups.google.com/group/Google-Web-Toolkit/browse_thread/thread/3f5369b0aea1a265?pli=1">here</a>
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxJettyLauncher extends JettyLauncher
{
	protected static String[] __dftConfigurationClasses = 
    { 
        "org.mortbay.jetty.webapp.WebInfConfiguration", // 
        "org.mortbay.jetty.plus.webapp.EnvConfiguration",//jetty-env 
        "org.mortbay.jetty.plus.webapp.Configuration", //web.xml 
        "org.mortbay.jetty.webapp.JettyWebXmlConfiguration",//jettyWeb 
    } ;
	
	@Override
	protected WebAppContext createWebAppContext(TreeLogger logger, File appRootDir)
	{
	    WebAppContext webAppContext = super.createWebAppContext(logger, appRootDir);
	    webAppContext.setConfigurationClasses(__dftConfigurationClasses);
	    System.setProperty("java.naming.factory.url.pkgs", "org.mortbay.naming");
	    System.setProperty("java.naming.factory.initial", "org.mortbay.naming.InitialContextFactory");
	    try
        {
	        initializeCruxBridge(webAppContext.getWebInf().getURL());
        }
        catch (IOException e)
        {
        	throw new CruxLaunchException("Error starting crux launcher.", e);
        }
	    
	    return webAppContext;
	}

	/**
	 * Initialize Crux Bridge
	 * @param webInf
	 */
	protected void initializeCruxBridge(URL webInf)
    {
		URLResourceHandler urlResourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(webInf.getProtocol());
		URL webInfClasses = urlResourceHandler.getChildResource(webInf, "classes/");
		URL webInfLib = urlResourceHandler.getChildResource(webInf, "lib/");
		
    	CruxBridge.getInstance().registerWebinfClasses(webInfClasses.toString());
    	CruxBridge.getInstance().registerWebinfLib(webInfLib.toString());
		ClassPathResolverInitializer.getClassPathResolver().initialize();
    }
}
