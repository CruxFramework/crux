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
package br.com.sysmap.crux.module.validation;

import br.com.sysmap.crux.module.CruxModule;
import br.com.sysmap.crux.module.CruxModuleHandler;
import br.com.sysmap.crux.module.ModuleRef;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class CruxModuleValidator
{
	/**
	 * 
	 * @param module
	 * @return
	 */
	public static boolean isDependenciesOk(String module)
	{
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(module);
		
		ModuleRef[] requiredModules = cruxModule.getInfo().getRequiredModules();
		if (requiredModules != null)
		{
			for (ModuleRef moduleRef : requiredModules)
			{
				CruxModule requiredModule = CruxModuleHandler.getCruxModule(moduleRef.getName());
				if (requiredModule == null || !checkMinVerion(requiredModule, moduleRef.getMinVersion()) || 
						                      !checkMaxVerion(requiredModule, moduleRef.getMaxVersion()))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static boolean checkMaxVerion(CruxModule module, String maxVersion)
	{
		return CruxModuleVersionCheckerInitializer.getVersionChecker().checkMaxVersion(maxVersion, module.getInfo().getVersion());
	}

	public static boolean checkMinVerion(CruxModule module, String minVersion)
	{
		return CruxModuleVersionCheckerInitializer.getVersionChecker().checkMinVersion(minVersion, module.getInfo().getVersion());
	}
}
