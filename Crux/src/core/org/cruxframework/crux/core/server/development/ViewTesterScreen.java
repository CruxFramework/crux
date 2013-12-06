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
