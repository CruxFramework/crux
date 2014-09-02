/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OfflineScreen
{
	private final String module;
	private final String refScreen;
	private final List<String> includes;
	private final List<String> excludes;
	private final String id;
	
	public OfflineScreen(String id, String module, String refScreen)
    {
		this.id = id;
		this.module = module;
		this.refScreen = refScreen;
		this.includes = new ArrayList<String>();
		this.excludes = new ArrayList<String>();
    }

	public String getModule()
    {
    	return module;
    }

	public String getRefScreen()
    {
    	return refScreen;
    }

	public String getId()
    {
	    return id;
    }

	public void addInclude(String include)
	{
		includes.add(include);
	}

	public void addExclude(String exclude)
	{
		excludes.add(exclude);
	}
	
	public String[] getIncludes()
    {
	    return includes.toArray(new String[includes.size()]);
    }

	public String[] getExcludes()
    {
	    return excludes.toArray(new String[excludes.size()]);
    }
}
