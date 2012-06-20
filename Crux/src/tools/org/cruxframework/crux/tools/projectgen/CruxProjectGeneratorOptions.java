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
package org.cruxframework.crux.tools.projectgen;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cruxframework.crux.core.utils.FileUtils;


/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class CruxProjectGeneratorOptions
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class GeneratorOption
	{
		private String name;
		private String value;
		private Class<?> type;

		private GeneratorOption()
        {
        }
		
		public String getName()
        {
        	return name;
        }
		public void setName(String name)
        {
        	this.name = name;
        }
		public String getValue()
        {
        	return value;
        }
		public void setValue(String value)
        {
        	this.value = value;
        }
		public Class<?> getType()
        {
        	return type;
        }
		public void setType(Class<?> type)
        {
        	this.type = type;
        }
	}
	
	private Map<String, GeneratorOption> options = new HashMap<String, CruxProjectGeneratorOptions.GeneratorOption>();

	
	/**
	 * @param name
	 * @param value
	 * @param type
	 */
	public void addOption(String name, String value, Class<?> type)
	{
		GeneratorOption option = new GeneratorOption();
		option.setName(name);
		option.setType(type);
		option.setValue(value);
		
		options.put(name, option);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public GeneratorOption getOption(String name)
	{
		return options.get(name);
	}
	
	/**
	 * @return
	 */
	public Iterator<GeneratorOption> iterateOptions()
	{
		return options.values().iterator();
	}
	
	/**
	 * @return
	 */
	public Iterator<String> iterateOptionNames()
	{
		return options.keySet().iterator();
	}

	private String appDescription;
	private final String hostedModeStartupModule;
	private String hostedModeStartupURL;
	private String hostedModeVMArgs;
	private final File libDir;
	private final String modulePackage;
	private final String moduleSimpleName;
	private final File projectDir;
	private final String projectName;
	
	private final File workspaceDir;

	/**
	 * @param workspaceDir
	 * @param projectName
	 * @param hostedModeStartupModule
	 * @throws Exception
	 */
	public CruxProjectGeneratorOptions(File workspaceDir, String projectName, String hostedModeStartupModule) throws Exception
    {
		this.workspaceDir = workspaceDir;
		this.projectName = projectName;
		this.hostedModeStartupModule = hostedModeStartupModule;
		this.moduleSimpleName = getModuleSimpleName(hostedModeStartupModule) ;
		this.modulePackage = getModulePackage(hostedModeStartupModule);
		
		this.projectDir = createProjectDir();
		this.libDir = findLibDir();
    }

	public String getHostedModeStartupModule()
    {
    	return hostedModeStartupModule;
    }

	public String getHostedModeStartupURL()
    {
    	return hostedModeStartupURL;
    }

	public String getHostedModeVMArgs()
    {
    	return hostedModeVMArgs;
    }

	public File getLibDir()
    {
    	return libDir;
    }

	public String getModulePackage()
    {
    	return modulePackage;
    }

	public String getModuleSimpleName()
    {
    	return moduleSimpleName;
    }

	public File getProjectDir()
    {
    	return projectDir;
    }

	public String getProjectName()
    {
    	return projectName;
    }
	
	public File getWorkspaceDir()
    {
    	return workspaceDir;
    }

	/**
	 * @return the appDescription
	 */
	public String getAppDescription()
	{
		return appDescription;
	}

	public void setAppDescription(String appDescription)
    {
    	this.appDescription = appDescription;
    }

	public void setHostedModeStartupURL(String hostedModeStartupURL)
    {
    	this.hostedModeStartupURL = hostedModeStartupURL;
    }

	public void setHostedModeVMArgs(String hostedModeVMArgs)
    {
    	this.hostedModeVMArgs = hostedModeVMArgs;
    }

	/**
	 * @return
	 * @throws IOException
	 */
	private File createProjectDir() throws IOException
	{
		File projectDir = new File(workspaceDir, projectName);
		
		if(projectDir.exists())
		{
			FileUtils.recursiveDelete(projectDir);
		}
		
		boolean created = projectDir.mkdirs();
		if(!projectDir.exists() && !created)
		{
			throw new IOException("Could not create " + projectDir.getCanonicalPath());
		}
		return projectDir;
	}
	
	/**
	 * @return
	 * @throws Exception 
	 */
	private File findLibDir() throws Exception
	{
		String resourceFromClasspathRoot = "/project.properties";
		URL url = this.getClass().getResource(resourceFromClasspathRoot);
		File rootDir = new File(url.toURI()).getParentFile();
		return new File(rootDir, "lib");
	}
	
	/**
	 * @return
	 */
	private String getModulePackage(String hostedModeStartupModule)
	{
		int lastDot = hostedModeStartupModule.lastIndexOf(".");
		return hostedModeStartupModule.substring(0, lastDot);
	}
	
	/**
	 * @return
	 */
	private String getModuleSimpleName(String hostedModeStartupModule)
	{
		int lastDot = hostedModeStartupModule.lastIndexOf(".");
		
		if(lastDot >= 0)
		{
			return hostedModeStartupModule.substring(lastDot + 1);
		}
		
		return hostedModeStartupModule;
	}
}