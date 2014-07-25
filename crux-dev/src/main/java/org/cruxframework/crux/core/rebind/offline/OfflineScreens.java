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
package org.cruxframework.crux.core.rebind.offline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.w3c.dom.Document;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OfflineScreens
{
	private static Map<String, Set<String>> moduleOfflineIDs = new HashMap<String, Set<String>>();
	private static Map<String, Document> moduleOfflineDocument = new HashMap<String, Document>();
	private static boolean initialized = false;
	
	private OfflineScreens(){}
	
	/**
	 * 
	 * @param module
	 * @return
	 * @throws ScreenConfigException
	 */
	public static Set<String> getOfflineIds(String module)
    {
		if (!initialized)
		{
			initialize();
		}
		return moduleOfflineIDs.get(module);
    }
	
	public static Document getOfflineScreen(String id)
    {
		if (!initialized)
		{
			initialize();
		}
		return moduleOfflineDocument.get(id);
    }
	
	/**
	 * 
	 */
	static void setInitialized()
	{
		initialized = true;
	}
	
	static synchronized void initialize()
	{
		if (!initialized)
		{
			moduleOfflineIDs.clear();
			moduleOfflineDocument.clear();
			OfflineScreensScanner.getInstance().scanArchives();
			setInitialized();
		}
	}
	
	static void restart()
	{
		initialized = false;
		initialize();
	}
	
	static void reset()
	{
		initialized = false;
		moduleOfflineDocument.clear();
		moduleOfflineIDs.clear();
	}
	
	/**
	 * 
	 * @param modulePages
	 * @return
	 */
	static void registerOfflinePageForModule(Document screen, String id, String module) 
	{
		Set<String> ids = moduleOfflineIDs.get(module);
		if (ids == null)
		{
			ids = new HashSet<String>();
			moduleOfflineIDs.put(module, ids);
		}
		ids.add(id);
		moduleOfflineDocument.put(id, screen);
	}
}
