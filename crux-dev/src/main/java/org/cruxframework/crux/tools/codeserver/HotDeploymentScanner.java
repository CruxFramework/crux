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
package org.cruxframework.crux.tools.codeserver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.declarativeui.template.Templates;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.server.rest.spi.HttpUtil;
import org.cruxframework.crux.scanner.ClasspathUrlFinder;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HotDeploymentScanner
{
	private static final Log logger = LogFactory.getLog(HotDeploymentScanner.class);

	private final ScheduledExecutorService threadPool;
	private final Map<String, Long> lastModified = new HashMap<String, Long>();
	private final Set<File> files = new HashSet<File>();

	private final String hostName;
	private final int port;
	private final String moduleToCompile;
	private final String userAgent;
	private final String locale;
	private boolean compilationFired = false; 

	/**
	 * 
	 * @param hostName
	 * @param port
	 * @param moduleToCompile
	 * @param userAgent
	 * @param locale
	 * @param poolSize
	 */
	private HotDeploymentScanner(String hostName, int port, String moduleToCompile, String userAgent, String locale, int poolSize)
	{
		this.hostName = hostName;
		this.port = port;
		this.moduleToCompile = moduleToCompile;
		this.userAgent = userAgent;
		this.locale = locale;
		threadPool = Executors.newScheduledThreadPool(poolSize);
	}
	
	/**
	 * 
	 * @param file
	 */
	public void addFile(final File file)
	{
		this.files.add(file);
	}
	
	/**
	 * 
	 */
	public void startScanner()
	{
		if (files.size() > 0)
		{
			threadPool.scheduleWithFixedDelay(new Runnable(){
				@Override
				public void run()
				{
					for (File file : files)
					{
						try
						{
							scan(file);
						}
						catch (Exception e) 
						{
							logger.info("Error scanning dir: ["+file.getName()+"].", e);
						}
					}
					compilationFired = false;
				}
			}, 0, 5, TimeUnit.SECONDS);
		}
	}

	/**
	 * 
	 * @param file
	 * @throws IOException 
	 */
	protected void scan(File file) throws IOException
	{
		if (file.isDirectory())
		{
			for (File child : file.listFiles())
			{
				scan(child);
			}
		}
		else
		{
			String fileName = file.getCanonicalPath();
			boolean fileChanged = checkFile(file, fileName);

			if(fileChanged)
			{
				try 
				{
					maybeClearViewFilesCache(fileName);
					recompileCodeServer();
				} 
				catch (IOException e) 
				{
					logger.error("Error recompiling module.", e);
				}
			}
		}
	}

	private void maybeClearViewFilesCache(String fileName) 
	{
		if (fileName.endsWith("template.xml"))
		{
			Templates.restart();
			ScreenFactory.getInstance().clearScreenCache();
		}
		else if (fileName.endsWith("view.xml") || fileName.endsWith("crux.xml"))
		{
			ScreenFactory.getInstance().clearScreenCache();
		}
	}
	
	/**
	 * @param file
	 * @param fileName
	 */
	private boolean checkFile(File file, String fileName) throws IOException
	{
		long modified = file.lastModified();
	    Long viewLastModified = lastModified.get(fileName);
	    if (viewLastModified == null)
	    {
	    	lastModified.put(fileName, modified);
	    }
	    else if (viewLastModified < modified)
	    {
	    	lastModified.put(fileName, modified);
			if (fileName.endsWith(".class")) 
			{
				if (!Modules.getInstance().isClassOnModulePath(file.toURI().toURL(), moduleToCompile))
				{
					return false;
				}
			}
			else if (fileName.endsWith(".jar") || fileName.endsWith(".java") || 
					!Modules.getInstance().isResourceOnModulePathOrContext(file.toURI().toURL(), moduleToCompile))
			{
				return false;
			}
			logger.info("File modified: ["+fileName+"].");
	    	return true;
	    }	
	    return false;
	}

	/**
	 * @throws IOException
	 */
	private void recompileCodeServer() throws IOException 
	{
		if (!compilationFired)
		{
			compilationFired = true;
			HttpUtil.wGet(
					"http://" + hostName + ":" + port + "/recompile/" + moduleToCompile, 
					StringUtils.isEmpty(userAgent) ? "" : ("user.agent=" + userAgent)+"&_callback=x", 
					"GET", 
					locale);
		}
	}

	/**
	 * 
	 * @param hostName
	 * @param port
	 * @param moduleToCompile
	 * @param userAgent
	 * @param locale
	 */
	public static void scanProjectDirs(String hostName, int port, String moduleToCompile, String userAgent, String locale)
	{
		List<File> srcDir = getSearchableFiles(moduleToCompile);
		
		HotDeploymentScanner scanner = new HotDeploymentScanner(hostName, port, moduleToCompile, userAgent, locale, srcDir.size());
		
		for (File file : srcDir)
		{
			scanner.addFile(file);
		}
		scanner.startScanner();
	}

	private static List<File> getSearchableFiles(String moduleToCompile) 
	{
		URL[] dirs = ClasspathUrlFinder.findClassPaths();
		List<File> srcDir = new ArrayList<File>();

		for (URL url : dirs)
		{
			try{
				File file = new File(url.toURI());
				if (file.isDirectory())
				{
					srcDir.add(file);
				}
				else if (!url.toString().endsWith(".class") && Modules.getInstance().isClassOnModulePath(url, moduleToCompile))
				{
					srcDir.add(file);
				}
				/* avoid scanning jar files */
				else if (!url.toString().endsWith(".jar") && !url.toString().endsWith(".java") && 
						Modules.getInstance().isResourceOnModulePathOrContext(url, moduleToCompile))
				{
					srcDir.add(file);
				}
			}
			catch (URISyntaxException e)
			{
				logger.info("Error scanning dir: ["+url.toString()+"].", e);
			}
		}

		return srcDir;
	}
}
