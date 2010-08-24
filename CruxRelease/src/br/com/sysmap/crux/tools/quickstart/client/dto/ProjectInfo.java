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
package br.com.sysmap.crux.tools.quickstart.client.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.controller.ValueObject;

/**
 * @author Thiago da Rosa de Bustamante -
 * 
 */
@ValueObject
public class ProjectInfo implements Serializable
{
	private static final long serialVersionUID = -8209132866842980452L;

	private String workspaceDir;
	private String projectName;
	private String hostedModeStartupURL;
	private String hostedModeStartupModule;
	private String hostedModeVMArgs;
	private String projectLayout;
	private String appDescription;
	private List<String[]> allProjectLayouts = new ArrayList<String[]>();

	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	public String getHostedModeStartupURL()
	{
		return hostedModeStartupURL;
	}

	public void setHostedModeStartupURL(String hostedModeStartupURL)
	{
		this.hostedModeStartupURL = hostedModeStartupURL;
	}

	public String getHostedModeStartupModule()
	{
		return hostedModeStartupModule;
	}

	public void setHostedModeStartupModule(String hostedModeStartupModule)
	{
		this.hostedModeStartupModule = hostedModeStartupModule;
	}

	public String getHostedModeVMArgs()
	{
		return hostedModeVMArgs;
	}

	public void setHostedModeVMArgs(String hostedModeVMArgs)
	{
		this.hostedModeVMArgs = hostedModeVMArgs;
	}

	public String getWorkspaceDir()
	{
		return workspaceDir;
	}

	public void setWorkspaceDir(String workspaceDir)
	{
		this.workspaceDir = workspaceDir;
	}

	/**
	 * @return the appDescription
	 */
	public String getAppDescription()
	{
		return appDescription;
	}

	/**
	 * @param appDescription the appDescription to set
	 */
	public void setAppDescription(String appDescription)
	{
		this.appDescription = appDescription;
	}
	
	public void addProjectLayout(String name, String value)
	{
		String[] layout = {name, value};
		allProjectLayouts.add(layout);
	}

	/**
	 * @return the allProjectLayouts
	 */
	public List<String[]> getAllProjectLayouts()
	{
		return allProjectLayouts;
	}

	/**
	 * @return the projectLayout
	 */
	public String getProjectLayout()
	{
		return projectLayout;
	}

	/**
	 * @param projectLayout the projectLayout to set
	 */
	public void setProjectLayout(String projectLayout)
	{
		this.projectLayout = projectLayout;
	}
}
