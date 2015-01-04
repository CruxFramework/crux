/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.tools.compile.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ClassPathUtils 
{
	private static final Class<?>[] parameters = new Class[]{URL.class};

	public static void addURL(URL u) throws IOException 
	{

		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;

		try 
		{
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });

			String classpath = System.getProperty("java.class.path");
	    	System.setProperty("java.class.path", classpath + File.pathSeparatorChar + new File(u.toURI()).getCanonicalPath());
		} 
		catch (Throwable t) 
		{
			throw new IOException("Error, could not add URL to system classloader.");
		}
	}
}
