/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.module;

import java.net.URL;
import java.util.Set;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Module
{
	private String name;
	private String fullName;
	private String[] sources;
	private String[] publicPaths;
	private Set<String> inherits;
	private URL descriptorURL;
	private URL location;
	private String rootPath;
	
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
	public String[] getSources()
	{
		return sources;
	}
	public void setSources(String[] sources)
	{
		this.sources = sources;
	}
	public String[] getPublicPaths()
	{
		return publicPaths;
	}
	public void setPublicPaths(String[] publicPaths)
	{
		this.publicPaths = publicPaths;
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
	public URL getDescriptorURL()
	{
		return descriptorURL;
	}
	public void setDescriptorURL(URL descriptorURL)
	{
		this.descriptorURL = descriptorURL;
	}
	public URL getLocation()
	{
		return location;
	}
	public void setLocation(URL location)
	{
		this.location = location;
	}
	public String getRootPath()
	{
		return rootPath;
	}
	public void setRootPath(String rootPath)
	{
		this.rootPath = rootPath;
	}
}
