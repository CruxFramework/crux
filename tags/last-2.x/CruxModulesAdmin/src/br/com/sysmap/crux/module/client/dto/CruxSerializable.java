package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxSerializable implements Serializable
{
	private static final long serialVersionUID = -4979470674369472134L;

	private String name;
	private String className;
	
	public CruxSerializable()
	{
	}
	
	public CruxSerializable(String name)
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
}

