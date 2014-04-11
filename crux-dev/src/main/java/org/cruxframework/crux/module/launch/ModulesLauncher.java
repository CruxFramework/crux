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
package org.cruxframework.crux.module.launch;

import java.io.File;
import java.net.MalformedURLException;

import org.cruxframework.crux.core.rebind.DevelopmentScanners;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.module.CruxModuleBridge;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModulesLauncher
{
	/**
	 * 
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException
	{
		CruxModuleBridge.getInstance().registerCurrentModule("");
		DevelopmentScanners.initializeScanners();
		
		String webDir = "./war/";
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
	        }
        }
		
		String initialModule = getModule(args);
		if (initialModule != null && initialModule.length() > 0)
		{
			CruxModuleBridge.getInstance().registerCurrentModule(initialModule);
		}
		
		File webinfClassesDir = new File(webDir, "WEB-INF/classes");
		File webinfLibDir = new File(webDir, "WEB-INF/lib");
		
		CruxBridge.getInstance().registerWebinfClasses(webinfClassesDir.toURI().toURL().toString());
		CruxBridge.getInstance().registerWebinfLib(webinfLibDir.toURI().toURL().toString());

		Module module = Modules.getInstance().getModule(initialModule);
		if (module != null)
		{
			initialModule = module.getFullName();
		}
		
		String[] newArgs = new String[args.length+1];
		System.arraycopy(args, 0, newArgs, 0, args.length);
		newArgs[args.length] = initialModule;
		
		com.google.gwt.dev.DevMode.main(newArgs);
	}

	private static String getModule(String[] args)
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
