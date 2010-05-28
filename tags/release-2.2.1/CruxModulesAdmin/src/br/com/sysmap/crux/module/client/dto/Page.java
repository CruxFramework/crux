package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

public class Page implements Serializable
{
	private static final long serialVersionUID = -3765803971963190613L;

	private String name;

	public Page()
	{
	}
	
	public Page(String name)
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
}

