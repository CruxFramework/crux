package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

import br.com.sysmap.crux.core.client.controller.ValueObject;

@ValueObject
public class ModuleInfo implements Serializable
{
	private static final long serialVersionUID = -5471248479511530302L;

	private String name;
	private String description;
	private ModuleRef[] requiredModules;
	private String group;
	private String version;
	
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public ModuleRef[] getRequiredModules()
	{
		return requiredModules;
	}
	public void setRequiredModules(ModuleRef[] requiredModules)
	{
		this.requiredModules = requiredModules;
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getGroup()
    {
    	return group;
    }
	public void setGroup(String group)
    {
    	this.group = group;
    }
}
