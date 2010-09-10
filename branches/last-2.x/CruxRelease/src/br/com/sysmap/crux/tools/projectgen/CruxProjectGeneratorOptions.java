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
package br.com.sysmap.crux.tools.projectgen;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import br.com.sysmap.crux.core.utils.FileUtils;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class CruxProjectGeneratorOptions
{
	private final String cruxModuleDescription;
	private final String hostedModeStartupModule;
	private final String hostedModeStartupURL;
	private final String hostedModeVMArgs;
	private File libDir;
	private final String modulePackage;
	private final String moduleSimpleName;
	private File projectDir;
	private final String projectName;
	
	private final boolean useCruxModuleExtension;
	private final File workspaceDir;

	public CruxProjectGeneratorOptions(File workspaceDir, String projectName, String hostedModeStartupModule, String hostedModeStartupURL,
			String hostedModeVMArgs, boolean useCruxModuleExtension, String cruxModuleDescription) throws Exception
    {
		this.workspaceDir = workspaceDir;
		this.projectName = projectName;
		this.hostedModeStartupModule = hostedModeStartupModule;
		this.hostedModeStartupURL = hostedModeStartupURL;
		this.hostedModeVMArgs = hostedModeVMArgs;
		this.cruxModuleDescription = cruxModuleDescription;
		this.moduleSimpleName = getModuleSimpleName(hostedModeStartupModule) ;
		this.modulePackage = getModulePackage(hostedModeStartupModule);
		this.useCruxModuleExtension = useCruxModuleExtension;
		
		this.projectDir = createProjectDir();
		this.libDir = findLibDir();
    }

	public String getCruxModuleDescription()
    {
    	return cruxModuleDescription;
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

	public boolean isUseCruxModuleExtension()
    {
    	return useCruxModuleExtension;
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
