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
package org.cruxframework.crux.core.server.classpath;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.InitializerListener;
import org.cruxframework.crux.scanner.AbstractScanner;
import org.cruxframework.crux.scanner.ScannerRegistration;
import org.cruxframework.crux.scanner.ScannerRegistration.ScannerMatch;
import org.cruxframework.crux.scanner.Scanners.ScannerCallback;
import org.cruxframework.crux.scanner.archiveiterator.Filter;
import org.cruxframework.crux.scanner.archiveiterator.URLIterator;


/**
 * This basic classPathResolver works on most application servers.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ClassPathResolverImpl implements ClassPathResolver
{
	private URL[] webBaseDirs = null;
	private URL webInfClassesPath = null;
	private List<URL> webInfLibJars = null;
	private URL webInfLibPath = null;
	
	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#findWebBaseDirs()
	 */
	public synchronized URL[] findWebBaseDirs()
	{
		if (webBaseDirs == null)
		{
			try
			{
				URL url = findWebInfClassesPath();
				URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(url.getProtocol());

				url = resourceHandler.getParentDir(url);
				url = resourceHandler.getParentDir(url);
				webBaseDirs = new URL[1]; 
				webBaseDirs[0] = url;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return webBaseDirs;
	}

	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#findWebInfClassesPath()
	 */
	public synchronized URL findWebInfClassesPath()
	{
		if (webInfClassesPath == null)
		{
			try
			{
				webInfClassesPath = getWebinfClasses();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return webInfClassesPath;
	}
	
	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#findWebInfLibJars()
	 */
	public synchronized URL[] findWebInfLibJars() 
	{		
		if (webInfLibJars == null)
		{
			try
			{
				webInfLibJars = new ArrayList<URL>();
				final URL libDir = findWebInfLibPath();

				final URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(libDir.getProtocol());
				List<ScannerRegistration> scanners = new ArrayList<ScannerRegistration>();
				
				ScannerRegistration scanner = new ScannerRegistration(new AbstractScanner()
				{
					@Override
					public Filter getScannerFilter()
					{
						return new Filter(){
							public boolean accepts(String filename)
							{
								return (filename.endsWith(".jar"));
							}
						};
					}
					@Override
					public ScannerCallback getScannerCallback()
					{
					    return null;
					}
					
					@Override
					public void resetScanner()
					{
					}
				});
				scanners.add(scanner);
				
				URLIterator iterator = resourceHandler.getDirectoryIteratorFactory().create(libDir, scanners);
				iterator.search();

				for (ScannerMatch match : scanner.getAllMatches())
				{
					webInfLibJars.add(match.getMatch());
				}
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return webInfLibJars.toArray(new URL[webInfLibJars.size()]);
	}

	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#findWebInfLibPath()
	 */
	public synchronized URL findWebInfLibPath()
	{
		if (webInfLibPath == null)
		{
			try
			{
				webInfLibPath = getWebinfLib();
			}
			catch (MalformedURLException e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return webInfLibPath;
	}
	
	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#initialize()
	 */
	public void initialize()
    {
		findWebBaseDirs();
		findWebInfClassesPath();
		findWebInfLibJars();
		findWebInfLibPath();
    }

	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#setWebBaseDirs(java.net.URL[])
	 */
	public synchronized void setWebBaseDirs(URL[] url)
    {
		webBaseDirs = url;
    }

	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#setWebInfClassesPath(java.net.URL)
	 */
	public synchronized void setWebInfClassesPath(URL url)
    {
	    webInfClassesPath = url;
    }

	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#setWebInfLibJars(java.net.URL[])
	 */
	public synchronized void setWebInfLibJars(URL[] url)
    {
		webInfLibJars = new ArrayList<URL>();
		if (url != null)
		{
			for (URL u : url)
            {
				webInfLibJars.add(u);
            }
		}
    }

	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolver#setWebInfLibPath(java.net.URL)
	 */
	public synchronized void setWebInfLibPath(URL url)
    {
		webInfLibPath = url;
    }

	/**
	 * @return
	 * @throws MalformedURLException
	 */
	private URL getWebinfClasses() throws MalformedURLException
    {
	    ServletContext context = InitializerListener.getContext();
	    if (context != null)
	    {
	    	URL webInfClasses = context.getResource("/WEB-INF/classes/");
	    	CruxBridge.getInstance().registerWebinfClasses(webInfClasses.toString());
			return webInfClasses;
	    }
	    else
	    {
	    	return new URL(CruxBridge.getInstance().getWebinfClasses());
	    }
    }

	/**
	 * @return
	 * @throws MalformedURLException
	 */
	private URL getWebinfLib() throws MalformedURLException
    {
	    ServletContext context = InitializerListener.getContext();
	    if (context != null)
	    {
	    	URL webInfClasses = context.getResource("/WEB-INF/lib/");
	    	CruxBridge.getInstance().registerWebinfLib(webInfClasses.toString());
			return webInfClasses;
	    }
	    else
	    {
	    	return new URL(CruxBridge.getInstance().getWebinfLib());
	    }
    }
}
