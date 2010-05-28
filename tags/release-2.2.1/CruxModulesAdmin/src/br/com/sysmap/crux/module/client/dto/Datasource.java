package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Datasource implements Serializable
{
	private static final long serialVersionUID = 3779095915487544145L;

	private String name;
	private String className;
	private boolean autoBind;
	
	public Datasource()
	{
	}
	
	public Datasource(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public boolean getAutoBind()
	{
		return autoBind;
	}

	public void setAutoBind(boolean autoBind)
	{
		this.autoBind = autoBind;
	}
}

