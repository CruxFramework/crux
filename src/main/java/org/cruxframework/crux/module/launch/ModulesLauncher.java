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

import java.net.MalformedURLException;

import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.tools.compile.CruxRegisterUtil;


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
		CruxRegisterUtil.registerFilesCruxBridge(args);
		
		String initialModule = CruxRegisterUtil.getModule(args);
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
	
}
