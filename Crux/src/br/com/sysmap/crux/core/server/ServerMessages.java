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
	
	@DefaultMessage("[annotationScanner 002] - Error creating index of annotations: {0}.")
	String annotationScannerBuildIndexError(String errMsg);

	@DefaultMessage("[annotationScanner 003] - The class {0} is not an interface.")
	String annotationScannerInterfaceRequired(String className);

	@DefaultMessage("[clientHandlers 001] - Error initializing clientHandler: {0}.")
	String clientHandlersHandlerInitializeError(String errMsg);

	@DefaultMessage("[clientHandlers 002] - Duplicated Client Controller: {0}.")
	String clientHandlersDuplicatedController(String value);
	
	@DefaultMessage("[widgetConfig 001] - Error initializing widgets.")
	String widgetConfigInitializeError(String errMsg);

	@DefaultMessage("[widgetConfig 002] - Widgets registered.")
	String widgetCongigWidgetsRegistered();

	@DefaultMessage("[services 001] - Error initializing services: {0}.")
	String servicesInitializeError(String errMsg);

	@DefaultMessage("[serviceFactoryInitializer 001] - Server services registered.")
	String serviceFactoryInitializerServicesRegistered();

	@DefaultMessage("[serviceFactoryInitializer 002] - Error initializing serviceFactory: {0}.")
	String serviceFactoryInitializerError(String errMsg);

	@DefaultMessage("[scannerURLS 001] - Error searching /WEB-INF/classes dir: {0}.")
	String scannerURLSErrorSearchingClassesDir(String errMsg);

	@DefaultMessage("[scannerURLS 002] - Error searching /WEB-INF/lib dir: {0}.")
	String scannerURLSErrorSearchingLibDir(String errMsg);

	@DefaultMessage("[formatters 001] - Error initializing formatters: {0}.")
	String formattersFormatterInitializeError(String errMsg);

	@DefaultMessage("[formatters 002] - Duplicated formatter: {0}.")
	String formattersDuplicatedDataSource(String value);
	
	@DefaultMessage("[localeResolver 001] - Error initializing LocaleResolver: {0}.")
	String localeResolverInitialisationError(String errMsg);

	@DefaultMessage("[localeResolver 002] - User LocaleResolver not initialized.")
	String localeResolverNotInitialized();

	@DefaultMessage("[screenResolver 001] - Error initializing ScreenResolver: {0}.")
	String screenResourceResolverInitializerError(String message);
	
	@DefaultMessage("[screenResolver 002] - Error obtaining screen resource. Screen id: {0}. Message: {1}")
	String screenResourceResolverFindResourceError(String screenId, String message);

	@DefaultMessage("[screenBridge 001] - Error registering screen: {0}.")
	String screenBridgeErrorRegisteringScreen(String errMsg);

	@DefaultMessage("[screenBridge 002] - Error reading screen id: {0}.")
	String screenBridgeErrorReadingScreenId(String errMsg);

	@DefaultMessage("[messages 001] Error initializing messagesClasses: {0}.")
	String messagesClassesInitializeError(String localizedMessage);

	@DefaultMessage("[messages 002] - Duplicated Message Key: {0}.")
	String messagesClassesDuplicatedMessageKey(String value);
	
	@DefaultMessage("[classpath 001] Error initializing classPathResolver: {0}.")
	String classPathResolverInitializerError(String message);

	@DefaultMessage("[serializers 001] Error initializing serializer: {0}.")
	String serializersSerializersInitializeError(String localizedMessage);

	@DefaultMessage("[screenResourceScanner 001] - Error initializing screenResourceScanner: {0}.")
	String screenResourceScannerInitializationError(String localizedMessage);

	@DefaultMessage("[screenResourceResolver 001] - Using Default ScreenResourceResouver.")
	String screenResourceResolverUsingDefault();

	@DefaultMessage("[datasources 001] - Error initializing datasource: {0}.")
	String dataSourcesDataSourceInitializeError(String localizedMessage);

	@DefaultMessage("[datasources 002] - Duplicated datasource: {0}.")
	String dataSourcesDuplicatedDataSource(String value);
}