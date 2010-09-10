package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

public class PageParameter implements Serializable
{
	private static final long serialVersionUID = 5929000399062427319L;

	private String name;
	private String type;
	private boolean required;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public boolean isRequired()
	{
		return required;
	}
	public void setRequired(boolean required)
	{
		this.required = required;
	}
}
