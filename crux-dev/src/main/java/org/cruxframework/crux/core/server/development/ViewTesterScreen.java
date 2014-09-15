/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.server.development;

import org.cruxframework.crux.core.server.CruxBridge;

public class ViewTesterScreen
{
	private static final String CRUX_VIEW_TEST_PAGE = "__CRUX_VIEW_TEST_PAGE__";


	/**
	 * Inform that the view test page was requested on dev mode
	 */
	public static void registerTestViewPageRequested(String moduleName)
    {
		CruxBridge.getInstance().registerLastPageRequested(getTestViewScreenId(moduleName));
    }
	
	/**
	 * Retrieve the screen id used to test views on development mode
	 * @return
	 */
	public static String getTestViewScreenSuffix()
	{
		return CRUX_VIEW_TEST_PAGE;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isTestViewScreen()
	{
		String screenID = CruxBridge.getInstance().getLastPageRequested();
		return (screenID != null && screenID.endsWith(getTestViewScreenSuffix()));
	}
	
	/**
	 * 
	 * @param moduleName
	 * @return
	 */
	public static String getTestViewScreenId(String moduleName)
	{
		return moduleName+"/"+CRUX_VIEW_TEST_PAGE;
	}
	
	public static String getModuleForViewTesting()
	{
		String screenID = CruxBridge.getInstance().getLastPageRequested();
		if (screenID != null && screenID.endsWith(getTestViewScreenSuffix()))
		{
			String[] strings = screenID.split("/");
			if (strings.length == 2)
			{
				return strings[0];
			}
		}
		return null;
	}

}
