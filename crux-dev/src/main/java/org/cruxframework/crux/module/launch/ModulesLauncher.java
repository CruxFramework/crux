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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.tools.ajc.Main;
import org.cruxframework.crux.core.aspect.LoggingErrorHandlerAspect;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.module.CruxModuleHandler;
import org.cruxframework.crux.tools.compile.utils.ClassPathUtils;


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
		//tryingToInvokeAspect();
        
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

	/**
	 * TODO: finish this!!!
	 * Is generating the correct file!
	 */
	@SuppressWarnings("unused")
	private static void tryingToInvokeAspect() 
	{
		String jarToWeave = "gwt-dev-2.5.1.jar";
		String classpath = System.getProperty("java.class.path");
		String jarToWeavePath = classpath.substring(0, classpath.indexOf(jarToWeave) + jarToWeave.length());
		jarToWeavePath = jarToWeavePath.substring(jarToWeavePath.lastIndexOf(";")+1, jarToWeavePath.length());
		
		String jarWeavedPath = jarToWeavePath.substring(0, jarToWeavePath.lastIndexOf(".jar"))+"_weaved.jar";
		
		String aspect = LoggingErrorHandlerAspect.class.getProtectionDomain().getCodeSource().getLocation().getPath()+(LoggingErrorHandlerAspect.class.getPackage().getName()).replace(".", "/");
		aspect = aspect.substring(1, aspect.length());
		
		String[] ajcArgs = {
            "-sourceroots", aspect,
            "-inpath", jarToWeavePath,
            "-noExit",
            "-Xlint:ignore",
            "-outjar", jarWeavedPath,
            "-1.5"
        };
		
		File fileJarToWeave = new File(jarToWeavePath);
		File fileJarWeaved = new File(jarWeavedPath);
		
        try 
        {
			Main.main(ajcArgs);
			ClassPathUtils.removeURL(fileJarToWeave.toURI().toURL());
			ClassPathUtils.addURL(fileJarWeaved.toURI().toURL());
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
