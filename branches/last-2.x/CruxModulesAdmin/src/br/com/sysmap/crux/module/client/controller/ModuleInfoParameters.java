package br.com.sysmap.crux.module.client.controller;

import br.com.sysmap.crux.core.client.controller.Parameter;
import br.com.sysmap.crux.core.client.controller.ParameterObject;

@ParameterObject
public class ModuleInfoParameters
{
	@Parameter(value="module", required=true)
	private String moduleName;

	public String getModuleName()
	{
		return moduleName;
	}

	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

}
