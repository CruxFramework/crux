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
package br.com.sysmap.crux.core.server;

import br.com.sysmap.crux.core.i18n.DefaultMessage;

/**
 * Contains all server messages used by Crux Framework
 * @author Thiago Bustamante
 *
 */
public interface ServerMessages 
{
	@DefaultMessage("[annotationScanner 001] - Building index of annotations for classes.")
	String annotationScannerBuildIndex();
	
	@DefaultMessage("[annotationScanner 003] - Error creating index of annotations: {0}.")
	String annotationScannerBuildIndexError(String errMsg);

	@DefaultMessage("[annotationScanner 002] - Index not created. Run the buildIndex method first.")
	String annotationScannerIndexNotFound();

	@DefaultMessage("[clientHandlers 001] - Error initializing clientHandler: {0}.")
	String clientHandlersHandlerInitializeError(String errMsg);

	@DefaultMessage("[componentConfig - 001] - Error parsing crux configuration file: {0}.")
	String componentConfigParserError(String errMsg);

	@DefaultMessage("[componentConfigScanner 001] - Error initializing componentConfigScanner: {0}.")
	String componentConfigScannerInitializationError(String errMsg);

	@DefaultMessage("[componentParserImpl - 001] - Error setting property {0} for component {1}.")
	String componentParserImplComponentPropertyError(String property, String componentId);

	@DefaultMessage("[controllers 001] - Error initializing controllers: {0}.")
	String controllersInitializeError(String errMsg);

	@DefaultMessage("[controllers 002] - Error initializing controllerFactory: {0}.")
	String controllerFactoryInitializerError(String errMsg);

	@DefaultMessage("[initializerListener - 001] - Components registered.")
	String initializerListenerComponentsRegistered();

	@DefaultMessage("[initializerListener - 002] - Server Controllers registered.")
	String initializerListenerControllersRegistered();

	@DefaultMessage("[scannerURLS - 001] - Error searching /WEB-INF/classes dir: {0}.")
	String scannerURLSErrorSearchingClassesDir(String errMsg);

	@DefaultMessage("[scannerURLS - 001] - Error searching /WEB-INF/lib dir: {0}.")
	String scannerURLSErrorSearchingLibDir(String errMsg);

	@DefaultMessage("[screenFactory - 004] - The id attribute is required for CRUX Components.")
	String screenFactoryComponentIdRequired();

	@DefaultMessage("[screenFactory - 005] - Can not create component ''{0}''. Verify the component type.")
	String screenFactoryErrorCreateComponent(String componentId);

	@DefaultMessage("[screenFactory - 006] - Error creating component. Duplicated identifier: {0}.")
	String screenFactoryErrorDuplicatedComponent(String componentId);

	@DefaultMessage("[screenFactory 002] - Error retrieving screen {0}. Error: {1}.")
	String screenFactoryErrorRetrievingScreen(String screenId, String errMsg);

	@DefaultMessage("[screenFactory 003] - Error Creating component {0}. Error: {1}.")
	String screenFactoryGenericErrorCreateComponent(String screenId, String errMsg);

	@DefaultMessage("[screenFactory 001] - Screen {0} not found!")
	String screenFactoryScreeResourceNotFound(String screenId);

	@DefaultMessage("[Screen - 001] - Error setting property {0} for component {1}.")
	String screenPropertyError(String property, String componentId);

	@DefaultMessage("[formatters 002] - Error initializing formatter: {0}.")
	String formattersFormatterInitializeError(String errMsg);

	@DefaultMessage("[screenBridge - 001] - Error registering screen: {0}.")
	String screenBridgeErrorRegisteringScreen(String errMsg);

	@DefaultMessage("[screenBridge - 002] - Error reading screen id: {0}.")
	String screenBridgeErrorReadingScreenId(String errMsg);

	@DefaultMessage("[localeResolver - 001] - Error initializing LocaleResolver: {0}.")
	String localeResolverInitialisationError(String errMsg);	
}
