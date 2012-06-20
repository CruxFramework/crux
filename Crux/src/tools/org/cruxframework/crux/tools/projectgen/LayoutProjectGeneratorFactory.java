/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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
package org.cruxframework.crux.tools.projectgen;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.scannotation.ClasspathUrlFinder;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class LayoutProjectGeneratorFactory
{
	private static boolean initialized = false;
	private static Map<String, Class<?>> layoutProjectGenerators = new HashMap<String, Class<?>>();
	
	/**
	 * 
	 */
	private static synchronized void initialize()
	{
		if (!initialized)
		{
			try
			{
				ClassScanner.initialize(ClasspathUrlFinder.findClassPaths());
				Set<String> layoutClasses = ClassScanner.searchClassesByInterface(LayoutProjectGenerator.class);

				if (layoutClasses != null)
				{
					for (String className : layoutClasses)
					{
						Class<?> layoutClass = Class.forName(className);
						
						if (!Modifier.isAbstract(layoutClass.getModifiers()))
						{
							LayoutProjectGenerator projectGen = (LayoutProjectGenerator) layoutClass.newInstance();
							layoutProjectGenerators.put(projectGen.getProjectLayout(), layoutClass);
						}
					}
				}
				initialized = true;
			}
			catch (Exception e)
			{
				throw new LayoutProjectGeneratorException("Can not initialize the LazyProjectGeneratorFactory. ", e);
			}
		}
	}
	
	/**
	 * @param projectLayout
	 * @param workspaceDir
	 * @param projectName
	 * @param hostedModeStartupModule
	 * @return
	 */
	public static LayoutProjectGenerator getLayoutProjectGenerator(String projectLayout, File workspaceDir, String projectName, String hostedModeStartupModule)
    {
	    if (!initialized)
	    {
	    	initialize();
	    }
		
	    Class<?> layoutClass = layoutProjectGenerators.get(projectLayout);
	    if (layoutClass == null)
	    {
	    	throw new LayoutProjectGeneratorException("Invalid Project Layout: "+projectLayout);
	    }
	    
	    try
	    {
	    	LayoutProjectGenerator projectGenerator = (LayoutProjectGenerator) layoutClass.newInstance();
	    	projectGenerator.init(workspaceDir, projectName, hostedModeStartupModule);
	    	return projectGenerator;
	    }
	    catch (Exception e)
	    {
	    	throw new LayoutProjectGeneratorException("Error creating generator for project layout: "+projectLayout, e);
	    }
    }

}
