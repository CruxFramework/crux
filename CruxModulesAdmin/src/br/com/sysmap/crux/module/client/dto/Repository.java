package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Repository implements Serializable
{
	private static final long serialVersionUID = 7533151862147765690L;

	private String url;

	public Repository()
	{
	}
	
	public Repository(String url)
	{
		this.url = url;
	}
	
	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
}
