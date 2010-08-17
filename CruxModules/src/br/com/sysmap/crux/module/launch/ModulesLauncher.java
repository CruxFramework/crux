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

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxScreenBridge;
import br.com.sysmap.crux.core.rebind.module.Modules;
import br.com.sysmap.crux.module.CruxModuleHandler;
import br.com.sysmap.crux.module.CruxModuleMessages;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModulesLauncher
{
	private static CruxModuleMessages messages = (CruxModuleMessages)MessagesFactory.getMessages(CruxModuleMessages.class);

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
			throw new RuntimeException(messages.launcherErrorNoDevelopmentModulesSpecified());
		}
		
		String webDir = "./war/";
		for (int i=0; i< (args.length-1); i++)
        {
			String arg = args[i];
	        if ("-webDir".equals(arg) )
	        {
	        	webDir = args[i+1];
	        }
        }
		
		File webinfClassesDir = new File(webDir, "WEB-INF/classes");
		File webinfLibDir = new File(webDir, "WEB-INF/lib");
		
		CruxScreenBridge.getInstance().registerWebinfClasses(webinfClassesDir.toURI().toURL().toString());
		CruxScreenBridge.getInstance().registerWebinfLib(webinfLibDir.toURI().toURL().toString());
		CruxScreenBridge.getInstance().registerScanIgnoredPackages("");
		CruxScreenBridge.getInstance().registerScanAllowedPackages("");

		List<String> modules = new ArrayList<String>();
		for (String moduleName : developmentModules)
		{
			modules.add(Modules.getInstance().getModule(moduleName).getFullName());
		}
		String[] newArgs = new String[args.length+modules.size()];
		System.arraycopy(args, 0, newArgs, 0, args.length);
		System.arraycopy(modules.toArray(new String[modules.size()]), 0, newArgs, args.length, modules.size());
		
		com.google.gwt.dev.DevMode.main(newArgs);
	}
}
