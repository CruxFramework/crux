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
import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.module.CruxModuleHandler;


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
		String[] developmentModules = CruxModuleHandler.getDevelopmentModules();
		if (developmentModules==null || developmentModules.length ==0)
		{
			throw new RuntimeException("No development module specified.");
		}
		
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
	        } else if("-war".equals(arg))
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
		
		File webinfClassesDir = new File(webDir, "WEB-INF/classes");
		File webinfLibDir = new File(webDir, "WEB-INF/lib");
		
		CruxBridge.getInstance().registerWebinfClasses(webinfClassesDir.toURI().toURL().toString());
		CruxBridge.getInstance().registerWebinfLib(webinfLibDir.toURI().toURL().toString());

		List<String> modules = new ArrayList<String>();
		for (String moduleName : developmentModules)
		{
			Module module = Modules.getInstance().getModule(moduleName);
			if (module != null)
			{
				modules.add(module.getFullName());
			}
		}
		String[] newArgs = new String[args.length+modules.size()];
		System.arraycopy(args, 0, newArgs, 0, args.length);
		System.arraycopy(modules.toArray(new String[modules.size()]), 0, newArgs, args.length, modules.size());
		
		com.google.gwt.dev.DevMode.main(newArgs);
	}
}
