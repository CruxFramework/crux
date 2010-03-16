package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

public class ModuleInformation implements Serializable
{
	private static final long serialVersionUID = 1641788818025519852L;

	private ModuleInfo moduleInfo;
	private boolean authenticated = false;
	private boolean hasRepositories = false;
	
	public ModuleInfo getModuleInfo()
	{
		return moduleInfo;
	}
	public void setModuleInfo(ModuleInfo moduleInfo)
	{
		this.moduleInfo = moduleInfo;
	}
	public boolean isAuthenticated()
	{
		return authenticated;
	}
	public void setAuthenticated(boolean authenticated)
	{
		this.authenticated = authenticated;
	}
	public boolean isHasRepositories()
	{
		return hasRepositories;
	}
	public void setHasRepositories(boolean hasRepositories)
	{
		this.hasRepositories = hasRepositories;
	}
}
