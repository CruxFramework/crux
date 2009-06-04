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

	@DefaultMessage("[clientHandlers 001] - Error initializing clientHandler: {0}.")
	String clientHandlersHandlerInitializeError(String errMsg);

	@DefaultMessage("[widgetConfig - 001] - Error parsing crux configuration file: {0}.")
	String widgetConfigParserError(String errMsg);

	@DefaultMessage("[widgetConfigScanner 001] - Error initializing widgetConfigScanner: {0}.")
	String widgetConfigScannerInitializationError(String errMsg);

	@DefaultMessage("[widgetParserImpl - 001] - Error setting property {0} for widget {1}.")
	String widgetParserImplWidgetPropertyError(String property, String widgetId);

	@DefaultMessage("[controllers 001] - Error initializing controllers: {0}.")
	String controllersInitializeError(String errMsg);

	@DefaultMessage("[controllers 002] - Error initializing controllerFactory: {0}.")
	String controllerFactoryInitializerError(String errMsg);

	@DefaultMessage("[initializerListener - 001] - Widgets registered.")
	String initializerListenerWidgetsRegistered();

	@DefaultMessage("[initializerListener - 002] - Server Controllers registered.")
	String initializerListenerControllersRegistered();

	@DefaultMessage("[scannerURLS - 001] - Error searching /WEB-INF/classes dir: {0}.")
	String scannerURLSErrorSearchingClassesDir(String errMsg);

	@DefaultMessage("[scannerURLS - 001] - Error searching /WEB-INF/lib dir: {0}.")
	String scannerURLSErrorSearchingLibDir(String errMsg);

	@DefaultMessage("[screenFactory - 004] - The id attribute is required for CRUX Widgets.")
	String screenFactoryWidgetIdRequired();

	@DefaultMessage("[screenFactory - 005] - Can not create widget {0}. Verify the widget type.")
	String screenFactoryErrorCreateWidget(String widgetId);

	@DefaultMessage("[screenFactory - 006] - Error creating widget. Duplicated identifier: {0}.")
	String screenFactoryErrorDuplicatedWidget(String widgetId);

	@DefaultMessage("[screenFactory 002] - Error retrieving screen {0}. Error: {1}.")
	String screenFactoryErrorRetrievingScreen(String screenId, String errMsg);

	@DefaultMessage("[screenFactory 003] - Error Creating widget {0}. Error: {1}.")
	String screenFactoryGenericErrorCreateWidget(String screenId, String errMsg);

	@DefaultMessage("[screenFactory 001] - Screen {0} not found!")
	String screenFactoryScreeResourceNotFound(String screenId);

	@DefaultMessage("[Screen - 001] - Error setting property {0} for widget {1}.")
	String screenPropertyError(String property, String widgetId);

	@DefaultMessage("[formatters 001] - Error initializing formatter: {0}.")
	String formattersFormatterInitializeError(String errMsg);

	@DefaultMessage("[localeResolver - 001] - Error initializing LocaleResolver: {0}.")
	String localeResolverInitialisationError(String errMsg);

	@DefaultMessage("[screenResolver - 001] - Error initializing ScreenResolver: {0}.")
	String screenResourceResolverInitializerError(String message);
	
	@DefaultMessage("[screenResolver - 002] - Error obtaining screen resource. Screen id: {0}. Message: {1}")
	String screenResourceResolverFindResourceError(String screenId, String message);

	@DefaultMessage("[screenBridge - 001] - Error registering screen: {0}.")
	String screenBridgeErrorRegisteringScreen(String errMsg);

	@DefaultMessage("[screenBridge - 002] - Error reading screen id: {0}.")
	String screenBridgeErrorReadingScreenId(String errMsg);

	@DefaultMessage("[screenBridge - 003] - Error registering screen resolver: {0}.")
	String screenBridgeErrorRegisteringScreenResolver(String localizedMessage);

	@DefaultMessage("[screenBridge - 004] - Error reading screen resolver. Using default.")
	String screenBridgeErrorReadingScreenResolver();

	@DefaultMessage("[screenBridge - 005] - Error registering eb base dir: {0}.")
	String screenBridgeErrorRegisteringWebBaseDir(String localizedMessage);	

	@DefaultMessage("[screenBridge - 006] - Error reading web base dir: {0}.")
	String screenBridgeErrorReadingwebBaseDir(String localizedMessage);

	@DefaultMessage("[messages 001] - Error initializing messagesClasses: {0}.")
	Object messagesClassesInitializeError(String localizedMessage);

	@DefaultMessage("[classpath 001] - Error initializing classPathResolver: {0}.")
	Object classPathResolverInitializerError(String message);

	@DefaultMessage("[serializers 001] - Error initializing serializer: {0}.")
	Object serializersSerializersInitializeError(String localizedMessage);
}