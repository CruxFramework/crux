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
package org.cruxframework.crux.scanner;

import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScannerURLS 
{
	static URL[] urls;
	private static final Lock lock = new ReentrantLock();
	private static final Log logger = LogFactory.getLog(ScannerURLS.class);
	
	/**
	 * 
	 */
	private ScannerURLS()
	{
		
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
		}
		finally
		{
			lock.unlock();
		}
		
		return urls;
	}
}
