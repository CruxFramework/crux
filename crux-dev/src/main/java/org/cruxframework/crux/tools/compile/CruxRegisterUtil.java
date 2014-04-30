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
package org.cruxframework.crux.tools.compile;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.cruxframework.crux.core.rebind.DevelopmentScanners;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.utils.FileUtils;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
public class CruxRegisterUtil 
{
	public static void registerFilesCruxBridge(String[] args) throws MalformedURLException 
	{
		removeOldTempFiles();
		
		String newParams[] = adaptArgumentToWebdir(args);
		
		if(!StringUtils.isEmpty(newParams[0]))
		{
			File webinfClassesDir = new File(newParams[0], "WEB-INF/classes");
			CruxBridge.getInstance().registerWebinfClasses(webinfClassesDir.toURI().toURL().toString());
		
			File webinfLibDir = new File(newParams[0], "WEB-INF/lib");
			CruxBridge.getInstance().registerWebinfLib(webinfLibDir.toURI().toURL().toString());
		}
		
		if(!StringUtils.isEmpty(newParams[1]))
		{
			CruxBridge.getInstance().registerLastPageRequested(newParams[1]);
		}
		
		DevelopmentScanners.initializeScanners();
	}

	public static void removeOldTempFiles() 
	{
		//remove old temp files
		FileUtils.setTempDir();
		CruxBridge.removeOldCompilationFiles();
		FileUtils.getTempDirFile();
	}

	private static String[] adaptArgumentToWebdir(String[] args) 
	{
		String lastPageVisitedFile = StringUtils.EMPTY;
		String webDir = "./war/";
		if(args != null)
		{
			boolean warParamFound = false;
			for (int i=0; i< (args.length-1); i++)
			{
				String arg = args[i];
				if ("-webDir".equals(arg))
				{
					webDir = args[i+1];
					//change to -war
					args[i] = "-war";
				} 
				else if("-war".equals(arg))
				{
					webDir = args[i+1];
					if(warParamFound)
					{
						//kill this duplicated param
						args[i] = "";
						args[i+1] = "";
					}
					warParamFound = true;
				} else if(arg.endsWith(".html"))
				{
					lastPageVisitedFile = arg; 
				}
			}
		}
		return new String[]{webDir, lastPageVisitedFile};
	}
	
	public static String getModule(String[] args)
    {
		String startupUrl = null;
		String module = null;
		for (int i=0; i< (args.length-1); i++)
        {
			String arg = args[i];
	        if ("-startupUrl".equals(arg))
	        {
	        	startupUrl = args[i+1];
	        }
	        else if ("-module".equals(arg))
	        {
	        	module = args[i+1];
	        } 
        }
		if (module == null || module.length() == 0)
		{
			if (startupUrl != null && startupUrl.length() > 0)
			{
				int index = startupUrl.indexOf('/');
				if (index > 0)
				{
					module = startupUrl.substring(0, index);
				}
			}
		}
		
	    return module;
    }
}
