package br.com.sysmap.crux.module.client.dto;

import java.io.Serializable;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class Controller implements Serializable
{
	private static final long serialVersionUID = -6253865699498601747L;

	private String name;
	private String className;
	private boolean lazy;
	private String fragment;
	private boolean autoBind;
	private boolean stateful;
	
	public Controller()
	{
	}
	
	public Controller(String name)
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

	public boolean getLazy()
	{
		return lazy;
	}

	public void setLazy(boolean lazy)
	{
		this.lazy = lazy;
	}

	public String getFragment()
	{
		return fragment;
	}

	public void setFragment(String fragment)
	{
		this.fragment = fragment;
	}

	public boolean getAutoBind()
	{
		return autoBind;
	}

	public void setAutoBind(boolean autoBind)
	{
		this.autoBind = autoBind;
	}

	public boolean getStateful()
	{
		return stateful;
	}

	public void setStateful(boolean stateful)
	{
		this.stateful = stateful;
	}
}

