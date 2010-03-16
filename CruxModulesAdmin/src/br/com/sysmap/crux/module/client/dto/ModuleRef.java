package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ModuleRef implements Serializable
{
	private static final long serialVersionUID = -8579210264236044384L;
	
	private String name;
	private String minVersion;
	private String maxVersion;
	private Boolean statusVersion;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getMinVersion()
	{
		return minVersion;
	}
	public void setMinVersion(String minVersion)
	{
		this.minVersion = minVersion;
	}
	public String getMaxVersion()
	{
		return maxVersion;
	}
	public void setMaxVersion(String maxVersion)
	{
		this.maxVersion = maxVersion;
	}
	public Boolean isStatusVersion()
	{
		return statusVersion;
	}
	public void setStatusVersion(Boolean statusVersion)
	{
		this.statusVersion = statusVersion;
	}
}
