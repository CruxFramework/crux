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
package br.com.sysmap.crux.core.server.scan;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ClasspathUtil
{
	// This very ugly code exists to solve an issue in weblogic, 
	// where is impossible to find the lib directory URL in zipped war
	private static final String A_RESOUCE_FROM_WEBINF_LIB = "/"+ClasspathUtil.class.getName().replaceAll("\\.", "/")+".class";

	/**
	 * @param context
	 * @return
	 */
	public static URL findWebInfClassesPath()
	{
		try
		{
			URL url = findWebInfLibPath();
			String path = url.toString();
			int lastSlash = path.lastIndexOf("/");
			path = path.substring(0, lastSlash);
			lastSlash = path.lastIndexOf("/");
			path = path.substring(0, lastSlash) + "/classes/"; 
			URL ret = new URL(path);
			if (new File(ret.toURI()).exists())
				return ret;
			return null;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static URL findWebInfLibPath()
	{
		try
		{
			URL url = new ClasspathUtil().getClass().getResource(A_RESOUCE_FROM_WEBINF_LIB);
			String path = url.toString().replace(A_RESOUCE_FROM_WEBINF_LIB, "");
			if (path.endsWith("!"))
			{
				path = path.substring(0, path.length() - 1);
			}
			
			if(path.toUpperCase().startsWith("ZIP:"))
			{
				int firstSlash = path.indexOf("/");
				path = path.substring(firstSlash + 1);
				path = "file:///" + path;
			}
			else if(path.toUpperCase().startsWith("JAR:"))
			{
				path = path.substring(4);
			}

			int lastSlash = path.lastIndexOf("/");
			path = path.substring(0, lastSlash);
			
			return new URL(path+"/");
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static URL[] findWebInfLibJars()
	{		
		try
		{
			List<URL> urlList = new ArrayList<URL>();
			URL libDir = findWebInfLibPath();
			File libDirFile = new File(libDir.toURI());
			File[] jars = libDirFile.listFiles();
			for (File jar : jars)
			{
				if (jar.getName().endsWith(".jar"))
				{
					urlList.add(jar.toURI().toURL());
				}
			}
			return urlList.toArray(new URL[urlList.size()]);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
