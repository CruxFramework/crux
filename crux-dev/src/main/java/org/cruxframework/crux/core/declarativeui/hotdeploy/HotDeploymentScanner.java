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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.declarativeui.template.Templates;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.screen.ViewFactory;
import org.cruxframework.crux.scanner.ClasspathUrlFinder;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HotDeploymentScanner
{
	private static final Log logger = LogFactory.getLog(HotDeploymentScanner.class);

	private ScheduledExecutorService threadPool;
	private Map<String, Long> lastModified = new HashMap<String, Long>();
	private Set<File> files = new HashSet<File>();
	
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
	public void startScanner()
	{
		if (files.size() > 0)
		{
			threadPool.scheduleWithFixedDelay(new Runnable(){
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
				}
			}, 0, 5, TimeUnit.SECONDS);
		}
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
	protected void checkViewFile(File file, String fileName)
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
	    }
    }

	/**
	 * 
	 * @param file
	 * @param fileName
	 */
	protected void checkTemplateFile(File file, String fileName)
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
	    }
    }
	
	/**
	 * 
	 */
	public static void scanWebDirs()
	{
		URL[] dirs = ClasspathUrlFinder.findClassPaths();
		HotDeploymentScanner scanner = new HotDeploymentScanner(dirs.length);

		for (URL url : dirs)
		{
			if (url.getProtocol().equalsIgnoreCase("file"))
			{
				try
				{
					final File file = new File(url.toURI());
					
					if (file.isDirectory() || file.getName().endsWith("template.xml") || file.getName().endsWith("view.xml"))
					{
						scanner.addFile(file);
					}
				}
				catch (URISyntaxException e)
				{
					logger.info("Error scanning dir: ["+url.toString()+"].", e);
				}
			}
		}
		scanner.startScanner();
	}
}
