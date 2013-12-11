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
import java.util.Iterator;
import java.util.List;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OfflineScreen
{
	private final String module;
	private final String refScreen;
	private final List<String> offlineResources;
	private final String id;
	
	public OfflineScreen(String id, String module, String refScreen)
    {
		this.id = id;
		this.module = module;
		this.refScreen = refScreen;
		this.offlineResources = new ArrayList<String>();
    }

	public String getModule()
    {
    	return module;
    }

	public String getRefScreen()
    {
    	return refScreen;
    }

	public Iterator<String> iterateOfflineResources()
	{
		return offlineResources.iterator();
	}
	
	public void addOfflineResource(String path)
	{
		offlineResources.add(path);
	}

	public String getId()
    {
	    return id;
    }
}
