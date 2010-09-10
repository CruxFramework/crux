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
package br.com.sysmap.crux.tools.compile.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TaskClassPathUtils
{
	public static URL[] getUrlsFromPaths(List<Path> classpath)
	{
		List<URL> urls = new ArrayList<URL>();
		try
		{
		
		for (Path path : classpath)
		{
			String[] paths = path.list();
			for (String p : paths)
			{
					urls.add(new File(p).toURI().toURL());
			}
		}
		}
		catch (MalformedURLException e)
		{
			throw new BuildException(e);
		}
		
		return urls.toArray(new URL[urls.size()]);
	}
}
