package org.cruxframework.crux.showcase.client.controller;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SourceTab implements Serializable
{
	private String location;
	private String label;
	private boolean isJava;
	
	public SourceTab()
	{
	}
	
	public SourceTab(String location, String label, boolean isJava)
	{
		this.location = location;
		this.label = label;
		this.isJava = isJava;			
	}
	
	public String getLocation()
	{
		return location;
	}
	
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}

	boolean isJava()
	{
		return isJava;
	}

	void setJava(boolean isJava)
	{
		this.isJava = isJava;
	}		
}