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
package org.cruxframework.crux.core.declarativeui.hotdeploy;

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
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.declarativeui.template.Templates;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.screen.ViewFactory;
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
	
	/**
	 * 
	 * @param poolSize
	 */
	private HotDeploymentScanner(int poolSize)
	{
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
	public void startScanner(final boolean scanFilesToCodeServer)
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
							if(scanFilesToCodeServer)
							{
								if(scanFilesToCodeServer(file))
								{
									break;
								}
							} else
							{
								scan(file);
							}
						}
						catch (Exception e) 
						{
							logger.info("Error scanning dir: ["+file.getName()+"].", e);
						}
					}
				}
			}, 0, 5, TimeUnit.SECONDS);
		}
	}

	/**
	 * 
	 * @param file
	 */
	protected boolean scanFilesToCodeServer(File file)
	{
		if (!Templates.isStarting())
		{
			if (file.isDirectory())
			{
				for (File child : file.listFiles())
				{
					if(scanFilesToCodeServer(child))
					{
						return true;
					}
				}
			}
			else
			{
				String fileName = file.getName();
				boolean fileChanged = false;
				if (fileName.endsWith("template.xml"))
				{
					fileChanged = checkTemplateFile(file, fileName);
				}
				else if (fileName.endsWith("view.xml") || fileName.endsWith("crux.xml"))
				{
					fileChanged = checkViewFile(file, fileName);
				}
				else if (fileName.endsWith("java") || fileName.endsWith("class"))
				{
					fileChanged = checkJavaFile(file, fileName);
				}
				
				if(fileChanged)
				{
//					lastModified.clear(); 
			    	//TODO: Remove when we migrate to GWT 2.6.1
			    	//CruxBridge.removeOldGWTCodeServerCompilationFiles();
			    	try 
			    	{
						recompileCodeServer();
						return true;
					} catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * @param file
	 * @param fileName
	 */
	private boolean checkJavaFile(File file, String fileName) 
	{
		long modified = file.lastModified();
	    Long viewLastModified = lastModified.get("java_"+fileName);
	    if (viewLastModified == null)
	    {
	    	lastModified.put("java_"+fileName, modified);
	    }
	    else if (viewLastModified < modified)
	    {
	    	lastModified.put("java_"+fileName, modified);
	    	logger.info("Java file modified: ["+fileName+"].");
	    	ScreenFactory.getInstance().clearScreenCache();
	    	ViewFactory.getInstance().clearViewCache();
	    	return true;
	    }	
	    return false;
	}

	/**
	 * @throws IOException
	 */
	private void recompileCodeServer() throws IOException 
	{
		//curl 'http://smbh-020452:9876/recompile/crossdeviceshowcase?user.agent=gecko1_8&_callback=__gwt_bookmarklet_globals.callbacks.c0' -H 'Host: smbh-020452:9876' -H 'User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0' -H 'Accept: */*' -H 'Accept-Language: pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3' -H 'Accept-Encoding: gzip, deflate' -H 'Referer: http://localhost:8080/crossdeviceshowcase/' -H 'Connection: keep-alive'
		HttpUtil.wGet(
				ConfigurationFactory.getConfigurations().superDevModeHost() +
				":" + ConfigurationFactory.getConfigurations().superDevModePort() +
				"/recompile/" + ConfigurationFactory.getConfigurations().superDevModeModuleToCompile(), 
				StringUtils.isEmpty(ConfigurationFactory.getConfigurations().superDevModeBrowserUsed()) ? "" : ("user.agent=" + ConfigurationFactory.getConfigurations().superDevModeBrowserUsed()), 
				"GET", 
				ConfigurationFactory.getConfigurations().superDevModeLocale());
	}

	/**
	 * 
	 * @param file
	 */
	protected void scan(File file)
	{
		if (!Templates.isStarting())
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
				String fileName = file.getName();
				if (fileName.endsWith("template.xml"))
				{
					checkTemplateFile(file, fileName);
				}
				else if (fileName.endsWith("view.xml") || fileName.endsWith("crux.xml"))
				{
					checkViewFile(file, fileName);
				}
			}
		}
	}

	/**
	 * 
	 * @param file
	 * @param fileName
	 */
	protected boolean checkViewFile(File file, String fileName)
    {
	    long modified = file.lastModified();
	    Long viewLastModified = lastModified.get("view_"+fileName);
	    if (viewLastModified == null)
	    {
	    	lastModified.put("view_"+fileName, modified);
	    }
	    else if (viewLastModified < modified)
	    {
	    	lastModified.put("view_"+fileName, modified);
	    	logger.info("View file modified: ["+fileName+"].");
	    	ScreenFactory.getInstance().clearScreenCache();
	    	ViewFactory.getInstance().clearViewCache();
	    	return true;
	    }
	    return false;
    }

	/**
	 * 
	 * @param file
	 * @param fileName
	 */
	protected boolean checkTemplateFile(File file, String fileName)
    {
	    long modified = file.lastModified();
	    Long templateLastModified = lastModified.get("template_"+fileName);
	    if (templateLastModified == null)
	    {
	    	lastModified.put("template_"+fileName, modified);
	    }
	    else if (templateLastModified < modified)
	    {
	    	lastModified.put("template_"+fileName, modified);
	    	logger.info("Template file modified: ["+fileName+"].");
	    	Templates.restart();
	    	ScreenFactory.getInstance().clearScreenCache();
	    	return true;
	    }
	    return false;
    }
	
	/**
	 * 
	 */
	public static void scanWebDirs()
	{
		List<URL> srcDir = getNonJarFiles();
		
		HotDeploymentScanner scanner = new HotDeploymentScanner(srcDir.size());
		
		for (URL url : srcDir)
		{
			try
			{
				final File file = new File(url.toURI());
				
				if (isCruxXMLFile(file))
				{
					scanner.addFile(file);
				}
			}
			catch (URISyntaxException e)
			{
				logger.info("Error scanning dir: ["+url.toString()+"].", e);
			}
		}
		scanner.startScanner(false);
	}

	private static boolean isCruxXMLFile(final File file) 
	{
		return file.isDirectory() || 
				file.getName().endsWith("template.xml") || 
				file.getName().endsWith("view.xml") || 
				file.getName().endsWith("crux.xml") || 
				file.getName().endsWith("xdevice.xml");
	}

	public static void scanFilesToCodeServer() 
	{
		List<URL> srcDir = getNonJarFiles();
		
		HotDeploymentScanner scanner = new HotDeploymentScanner(srcDir.size());
		
		for (URL url : srcDir)
		{
			try
			{
				final File file = new File(url.toURI());
				
				if (isCruxXMLFile(file) ||
						file.getName().endsWith("java") || file.getName().endsWith("class"))
				{
					scanner.addFile(file);
				}
			}
			catch (URISyntaxException e)
			{
				logger.info("Error scanning dir: ["+url.toString()+"].", e);
			}
		}
		scanner.startScanner(true);
	}

	private static List<URL> getNonJarFiles() 
	{
		URL[] dirs = ClasspathUrlFinder.findClassPaths();
		List<URL> srcDir = new ArrayList<URL>(0);
		
		/* avoid scanning jar files */
		for (URL url : dirs)
		{
			if (!url.toString().endsWith(".jar"))
			{
				srcDir.add(url);
			}
		}
		return srcDir;
	}
}
