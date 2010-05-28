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

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.scannotation.archiveiterator.Filter;
import br.com.sysmap.crux.scannotation.archiveiterator.URLIterator;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ClassPathResolverImpl implements ClassPathResolver
{
	// This very ugly code exists to solve an issue in weblogic, 
	// where is impossible to find the lib directory URL in zipped war
	private static final String A_RESOUCE_FROM_WEBINF_LIB = "/"+ClassPathResolverImpl.class.getName().replaceAll("\\.", "/")+".class";

	private URL webInfClassesPath = null;
	private URL webInfLibPath = null;
	private URL[] webBaseDirs = null;
	private List<URL> webInfLibJars = null;
	
	/**
	 * @param context
	 * @return
	 */
	public synchronized URL findWebInfClassesPath()
	{
		if (webInfClassesPath == null)
		{
			try
			{
				URL url = findWebBaseDirs()[0];
				URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(url.getProtocol());

				webInfClassesPath = resourceHandler.getChildResource(url, "WEB-INF/classes");
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return webInfClassesPath;
	}
	
	/**
	 * @param context
	 * @return
	 */
	public synchronized URL findWebInfLibPath()
	{
		if (webInfLibPath == null)
		{
			try
			{
				URL url = new ClassPathResolverImpl().getClass().getResource(A_RESOUCE_FROM_WEBINF_LIB);
				String path = url.toString().replace(A_RESOUCE_FROM_WEBINF_LIB, "")+"/";
				URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(url.getProtocol());

				webInfLibPath = resourceHandler.getParentDir(new URL(path));
			}
			catch (MalformedURLException e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return webInfLibPath;
	}
	
	/**
	 * 
	 * @return
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
	 * 
	 */
	public synchronized URL[] findWebBaseDirs()
	{
		if (webBaseDirs == null)
		{
			try
			{
				URL url = findWebInfLibPath();
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
}
