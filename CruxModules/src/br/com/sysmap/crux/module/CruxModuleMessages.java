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
package br.com.sysmap.crux.module;

import br.com.sysmap.crux.core.i18n.DefaultMessage;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public interface CruxModuleMessages
{
	@DefaultMessage("[crux-modules 001] - No development module found. Define it using the 'developmentModule' properties on CruxModuleConfig.properties file.")
	String developmentCruxModuleNotDefined();

	@DefaultMessage("[crux-modules 002] - Error initializing the crux module handler.")
	String errorInitializingCruxModuleHandler();

	@DefaultMessage("[crux-modules 003] - The page {0} is not transformed... Accessing directly.")
	String modulesFilterDoesNotTransformPage(String requestedScreen);

	@DefaultMessage("[crux-modules 004] - Error searching for pages into module {0}.")
	String errorSearchingForModulePages(String moduleName);

	@DefaultMessage("[crux-modules 005] - Error registering current module {0}.")
	String moduleBridgeErrorRegisteringModule(String localizedMessage);

	@DefaultMessage("[crux-modules 006] - No module registered under the bridge module. Assuming the development module" )
	String moduleBridgeErrorReadingModule();

	@DefaultMessage("[crux-modules 007] - No development module specified." )
	String launcerErrorNoDevelopmentModulesSpecified();

	@DefaultMessage("[crux-modules 008] - Error initializing crux module version checker: {0}." )
	Object versionChekerInitializerError(String message);
}
