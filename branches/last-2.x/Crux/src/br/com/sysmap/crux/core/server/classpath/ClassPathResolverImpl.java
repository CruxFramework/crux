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
package br.com.sysmap.crux.core.server.classpath;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.rebind.CruxScreenBridge;
import br.com.sysmap.crux.core.server.InitializerListener;
import br.com.sysmap.crux.scannotation.archiveiterator.Filter;
import br.com.sysmap.crux.scannotation.archiveiterator.URLIterator;

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
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolver#findWebBaseDirs()
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
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolver#findWebInfClassesPath()
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
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolver#findWebInfLibJars()
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
				URLIterator iterator = resourceHandler.getDirectoryIteratorFactory().create(libDir, new Filter(){
					public boolean accepts(String filename)
					{
						return (filename.endsWith(".jar"));
					}
				});
				URL found;
				while ((found = iterator.next()) != null)
				{
					webInfLibJars.add(found);
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
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolver#findWebInfLibPath()
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
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolver#setWebBaseDirs(java.net.URL[])
	 */
	public synchronized void setWebBaseDirs(URL[] url)
    {
		webBaseDirs = url;
    }

	/**
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolver#setWebInfClassesPath(java.net.URL)
	 */
	public synchronized void setWebInfClassesPath(URL url)
    {
	    webInfClassesPath = url;
    }

	/**
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolver#setWebInfLibJars(java.net.URL[])
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
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolver#setWebInfLibPath(java.net.URL)
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
	    	CruxScreenBridge.getInstance().registerWebinfClasses(webInfClasses.toString());
			return webInfClasses;
	    }
	    else
	    {
	    	return new URL(CruxScreenBridge.getInstance().getWebinfClasses());
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
	    	CruxScreenBridge.getInstance().registerWebinfLib(webInfClasses.toString());
			return webInfClasses;
	    }
	    else
	    {
	    	return new URL(CruxScreenBridge.getInstance().getWebinfLib());
	    }
    }
}
