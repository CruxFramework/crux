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
package br.com.sysmap.crux.core.declarativeui.template;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.declarativeui.DeclarativeUIMessages;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class TemplatesHotDeploymentScanner
{
	private static DeclarativeUIMessages messages = MessagesFactory.getMessages(DeclarativeUIMessages.class);
	private static final Log logger = LogFactory.getLog(TemplatesHotDeploymentScanner.class);

	private ScheduledExecutorService threadPool;
	private Map<String, Long> lastModified = new HashMap<String, Long>();
	
	/**
	 * 
	 * @param poolSize
	 */
	private TemplatesHotDeploymentScanner(int poolSize)
	{
		threadPool = Executors.newScheduledThreadPool(poolSize);
	}
	
	/**
	 * 
	 * @param url
	 */
	public void scan(final URL url)
	{
		try
		{
			final File file = new File(url.toURI());
			
			threadPool.scheduleWithFixedDelay(new Runnable(){
				public void run()
				{
					try
					{
						scan(file);
					}
					catch (Exception e) 
					{
						logger.info(messages.templatesHotDeploymentScannerErrorScanningDir(file.getName()), e);
					}
				}
			}, 0, 5, TimeUnit.SECONDS);
		}
		catch (URISyntaxException e)
		{
			logger.info(messages.templatesHotDeploymentScannerErrorScanningDir(url.toString()), e);
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
					long modified = file.lastModified();
					Long templateLastModified = lastModified.get(fileName);
					if (templateLastModified == null)
					{
						lastModified.put(fileName, modified);
					}
					else if (templateLastModified < modified)
					{
						lastModified.put(fileName, modified);
						logger.info(messages.templatesHotDeploymentScannerTemplateFileModified(fileName));
						Templates.restart();
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public static void scanWebDirs()
	{
		URL[] dirs = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDirs();
		TemplatesHotDeploymentScanner scanner = new TemplatesHotDeploymentScanner(dirs.length);

		for (URL url : dirs)
		{
			if (url.getProtocol().equalsIgnoreCase("file"))
			{
				scanner.scan(url);
			}
		}
	}
}
