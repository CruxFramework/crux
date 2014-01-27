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
package org.cruxframework.crux.core.server.scan;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.scannotation.AbstractScanner;
import org.cruxframework.crux.scannotation.ClasspathUrlFinder;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScannerURLS extends AbstractScanner 
{
	static URL[] urls;
	private static final Lock lock = new ReentrantLock();

	private static final Log logger = LogFactory.getLog(ScannerURLS.class);
	
	private static final ScannerURLS instance = new ScannerURLS();
	
	/**
	 * 
	 */
	private ScannerURLS()
	{
		//TODO: check why letting DEFAULT_IGNORED_PACKAGES doesn't allow application to run!
		setIgnoredPackages(new String[0]);
		
		initializeAllowedOrIgnoredPackages();
		
		String[] ignoredPackages = null;
		if(!StringUtils.isEmpty(ConfigurationFactory.getConfigurations().scanIgnoredPackages()))
		{
			ignoredPackages = RegexpPatterns.REGEXP_COMMA.split(ConfigurationFactory.getConfigurations().scanIgnoredPackages());
		}
		String[] allowedPackages = null; 
		if(!StringUtils.isEmpty(ConfigurationFactory.getConfigurations().scanAllowedPackages()))
		{
			allowedPackages = RegexpPatterns.REGEXP_COMMA.split(ConfigurationFactory.getConfigurations().scanAllowedPackages());
		}
		
		if(ignoredPackages != null)
		{
			for (String ignored : ignoredPackages) 
			{
				addIgnoredPackage(ignored.trim());
			}
		}
		
		if(allowedPackages != null)
		{
			for (String allowed : allowedPackages) 
			{
				addAllowedPackage(allowed.trim());
			}
		}
	}
	
	/**
	 * 
	 * @param urls
	 */
	public static void setURLsForSearch(URL[] urls)
	{
		lock.lock();
		try
		{
			ScannerURLS.urls = urls;
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 * @return
	 */
	public static URL[] getURLsForSearch()
	{
		if (urls != null) return urls;
		lock.lock();
		if (urls != null) return urls;
		try
		{
			if (Environment.isProduction())
			{
				try
				{
					urls = ClassPathResolverInitializer.getClassPathResolver().findWebInfLibJars();
				}
				catch (Throwable e) 
				{
					logger.error("Error searching /WEB-INF/lib dir.", e);
				}
				URL webInfClasses = ClassPathResolverInitializer.getClassPathResolver().findWebInfClassesPath();
				
				if (webInfClasses != null)
				{
					urls = urls != null ? urls : new URL[0];
					URL[] tempUrls = new URL[urls.length + 1];
					System.arraycopy(urls, 0, tempUrls, 0, urls.length);
					
					urls = tempUrls;		
					
					try
					{
						urls[urls.length -1] = webInfClasses;
					}
					catch (Throwable e) 
					{
						logger.error("Error searching /WEB-INF/classes dir.", e);
					}
				}
			}
			else
			{
				urls = ClasspathUrlFinder.findClassPaths();
			}
			
			//add or remove allowed or ignored packages
			ArrayList<URL> filteredURLs = new ArrayList<URL>();
			boolean packageRestrictionEnabled = false;
			for(URL url : urls)
			{
				if(instance.getIgnoredPackages() != null && instance.getIgnoredPackages().length > 0)
				{
					packageRestrictionEnabled = true;
					
					boolean hasAnyIgnoredPackage = false;
					for(String ignoredPackage : instance.getIgnoredPackages())
					{
						if(url.getPath().contains(ignoredPackage))
						{
							hasAnyIgnoredPackage = true;
							break;
						}	
					}
					if(hasAnyIgnoredPackage)
					{
						continue;
					} else
					{
						filteredURLs.add(url);
					}
				}
				
				if(instance.getAllowedPackages() != null && instance.getAllowedPackages().length > 0)
				{
					packageRestrictionEnabled = true;
					
					for(String allowedPackage : instance.getAllowedPackages())
					{
						if(url.getPath().contains(allowedPackage))
						{
							filteredURLs.add(url);
						}	
					}
				}
			}
			
			if(packageRestrictionEnabled) 
			{
				urls = new URL[filteredURLs.size()];
				urls = filteredURLs.toArray(urls);
			}
		}
		finally
		{
			lock.unlock();
		}
		
		return urls;
	}
	
	private void initializeAllowedOrIgnoredPackages()
	{
		addRequiredPackage("org.cruxframework.crux");
		String scanAllowedPackages = CruxBridge.getInstance().getScanAllowedPackages();
		if (!StringUtils.isEmpty(scanAllowedPackages))
		{
			String[] allowedPackages = RegexpPatterns.REGEXP_COMMA.split(scanAllowedPackages);
			for (String allowed : allowedPackages) 
			{
				addAllowedPackage(allowed.trim());
			}
		}
		
		String scanIgnoredPackages = CruxBridge.getInstance().getScanIgnoredPackages();
		if (!StringUtils.isEmpty(scanIgnoredPackages))
		{
			String[] ignoredPackages = RegexpPatterns.REGEXP_COMMA.split(scanIgnoredPackages);
			for (String ignored : ignoredPackages) 
			{
				addIgnoredPackage(ignored.trim());
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static URL[] getWebURLsForSearch()
	{
		URL[] urls = getURLsForSearch();
		URL[] webDirs = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDirs();
		if (webDirs == null)
		{
			return urls;
		}
		URL[] result = new URL[urls.length + webDirs.length];
		System.arraycopy(urls, 0, result, 0, urls.length);
		System.arraycopy(webDirs, 0, result, urls.length, webDirs.length);
		return result;
	}
}
