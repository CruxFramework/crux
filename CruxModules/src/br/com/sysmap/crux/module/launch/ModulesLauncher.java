/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.module.launch;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.rebind.module.Modules;
import br.com.sysmap.crux.module.CruxModuleHandler;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ModulesLauncher
{
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		String[] developmentModules = CruxModuleHandler.getDevelopmentModules();
		if (developmentModules==null || developmentModules.length ==0)
		{
			throw new RuntimeException();//TODO - Thiago - message here.
		}
		
		List<String> modules = new ArrayList<String>();
		for (String moduleName : developmentModules)
		{
			modules.add(Modules.getModule(moduleName).getFullName());
		}
		String[] newArgs = new String[args.length+modules.size()];
		System.arraycopy(args, 0, newArgs, 0, args.length);
		System.arraycopy(modules.toArray(new String[modules.size()]), 0, newArgs, args.length, modules.size());
		
		com.google.gwt.dev.DevMode.main(newArgs);
	}
}
