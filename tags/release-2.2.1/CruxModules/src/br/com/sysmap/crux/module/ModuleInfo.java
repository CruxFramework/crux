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
package br.com.sysmap.crux.module;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleInfo
{
	private String description;
	private String startPage;
	private String version;
	private ModuleRef[] dependencies;
	
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getStartPage()
	{
		return startPage;
	}
	public void setStartPage(String startPage)
	{
		this.startPage = startPage;
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	public ModuleRef[] getDependencies()
	{
		return dependencies;
	}
	public void setDependencies(ModuleRef[] dependencies)
	{
		this.dependencies = dependencies;
	}
}
