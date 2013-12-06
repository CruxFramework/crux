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
package org.cruxframework.crux.tools.compile;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;


/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
public class MonolithicAppCompileUtils
{
	/**
	 * Gets the list of URLs that will be compiled
	 * @return
	 */
	public static List<URL> getURLs(File pagesDir) throws Exception
	{
		List<URL> files = new LinkedList<URL>();
		File[] listFiles = pagesDir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.isDirectory() || pathname.getName().endsWith(".crux.xml");
			}
		});
		
		if (listFiles != null)
		{
			for (File file : listFiles)
			{
				if (file.isDirectory())
				{
					files.addAll(getURLs(file));
				}
				else
				{
					files.add(file.toURI().toURL());
				}
			}
		}

		return files;
	}
	

	/**
	 * @param parametersProcessor
	 */
	public static void addParametersToProcessor(ConsoleParametersProcessor parametersProcessor)
	{
		ConsoleParameter parameter = new ConsoleParameter("keepPagesUnder", "A Directory that will be used as parent of generated files.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("cruxPagesDir", "The source folder(s), where crux.xml files will be searched.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirNames", "Folder name(s). Separeted by commas"));
		parametersProcessor.addSupportedParameter(parameter);		
	}
}