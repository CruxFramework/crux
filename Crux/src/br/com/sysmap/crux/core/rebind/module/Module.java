/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.core.rebind.module;

import java.util.Set;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class Module
{
	private String name;
	private String fullName;
	private String source;
	private String publicPath;
	private Set<String> inherits;
	
	public Set<String> getInherits()
	{
		return inherits;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getSource()
	{
		return source;
	}
	public void setSource(String source)
	{
		this.source = source;
	}
	public String getPublicPath()
	{
		return publicPath;
	}
	public void setPublicPath(String publicPath)
	{
		this.publicPath = publicPath;
	}
	public void setInherits(Set<String> inherits)
	{
		this.inherits = inherits;
	}
	public String getFullName()
	{
		return fullName;
	}
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}
}
