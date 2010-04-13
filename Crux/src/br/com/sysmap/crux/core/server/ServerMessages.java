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

import br.com.sysmap.crux.core.i18n.DefaultServerMessage;


/**
 * Contains all server messages used by Crux Framework
 * @author Thiago Bustamante
 *
 */
public interface ServerMessages 
{
	@DefaultServerMessage("[annotationScanner 001] - Building index of annotations for classes.")
	String annotationScannerBuildIndex();
	
	@DefaultServerMessage("[annotationScanner 002] - Error creating index of annotations: {0}.")
	String annotationScannerBuildIndexError(String errMsg);

	@DefaultServerMessage("[annotationScanner 003] - The class {0} is not an interface.")
	String annotationScannerInterfaceRequired(String className);

	@DefaultServerMessage("[clientHandlers 001] - Error initializing clientHandler: {0}.")
	String clientHandlersHandlerInitializeError(String errMsg);

	@DefaultServerMessage("[clientHandlers 002] - Duplicated Client Controller: {0}.")
	String clientHandlersDuplicatedController(String value);
	
	@DefaultServerMessage("[widgetConfig 001] - Error initializing widgets.")
	String widgetConfigInitializeError(String errMsg);

	@DefaultServerMessage("[widgetConfig 002] - Widgets registered.")
	String widgetCongigWidgetsRegistered();

	@DefaultServerMessage("[services 001] - Error initializing services: {0}.")
	String servicesInitializeError(String errMsg);

	@DefaultServerMessage("[services 002] - No implementation class found to service interface: {0}.")
	String servicesNoImplementationFound(String interfaceName);
	
	@DefaultServerMessage("[serviceFactoryInitializer 001] - Server services registered.")
	String serviceFactoryInitializerServicesRegistered();

	@DefaultServerMessage("[serviceFactoryInitializer 002] - Error initializing serviceFactory: {0}.")
	String serviceFactoryInitializerError(String errMsg);

	@DefaultServerMessage("[scannerURLS 001] - Error searching /WEB-INF/classes dir: {0}.")
	String scannerURLSErrorSearchingClassesDir(String errMsg);

	@DefaultServerMessage("[scannerURLS 002] - Error searching /WEB-INF/lib dir: {0}.")
	String scannerURLSErrorSearchingLibDir(String errMsg);

	@DefaultServerMessage("[formatters 001] - Error initializing formatters: {0}.")
	String formattersFormatterInitializeError(String errMsg);

	@DefaultServerMessage("[formatters 002] - Duplicated formatter: {0}.")
	String formattersDuplicatedDataSource(String value);
	
	@DefaultServerMessage("[localeResolver 001] - Error initializing LocaleResolver: {0}.")
	String localeResolverInitialisationError(String errMsg);

	@DefaultServerMessage("[localeResolver 002] - User LocaleResolver not initialized.")
	String localeResolverNotInitialized();

	@DefaultServerMessage("[screenResolver 001] - Error initializing ScreenResolver: {0}.")
	String screenResourceResolverInitializerError(String message);
	
	@DefaultServerMessage("[screenResolver 002] - Error obtaining screen resource. Screen id: {0}. Message: {1}")
	String screenResourceResolverFindResourceError(String screenId, String message);

	@DefaultServerMessage("[screenBridge 001] - Error registering screen: {0}.")
	String screenBridgeErrorRegisteringScreen(String errMsg);

	@DefaultServerMessage("[screenBridge 002] - Error reading screen id: {0}.")
	String screenBridgeErrorReadingScreenId(String errMsg);

	@DefaultServerMessage("[screenBridge 003] - Error registering ignoredPackages: {0}.")
	String screenBridgeErrorRegisteringIgnoredPackages(String localizedMessage);

	@DefaultServerMessage("[screenBridge 004] - Error reading ignoredPackages: {0}.")
	String screenBridgeErrorReadingIgnoredPackages(String localizedMessage);

	@DefaultServerMessage("[screenBridge 005] - Error registering allowedPackages: {0}.")
	String screenBridgeErrorRegisteringAllowedPackages(String localizedMessage);

	@DefaultServerMessage("[screenBridge 006] - Error reading allowedPackages: {0}.")
	String screenBridgeErrorReadingAllowedPackages(String localizedMessage);

	@DefaultServerMessage("[messages 001] - Error initializing messagesClasses: {0}.")
	String messagesClassesInitializeError(String localizedMessage);

	@DefaultServerMessage("[messages 002] - Duplicated Message Key: {0}.")
	String messagesClassesDuplicatedMessageKey(String value);
	
	@DefaultServerMessage("[classpath 001] - Error initializing classPathResolver: {0}.")
	String classPathResolverInitializerError(String message);

	@DefaultServerMessage("[serializers 001] - Error initializing serializer: {0}.")
	String serializersSerializersInitializeError(String localizedMessage);

	@DefaultServerMessage("[screenResourceScanner 001] - Error initializing screenResourceScanner: {0}.")
	String screenResourceScannerInitializationError(String localizedMessage);

	@DefaultServerMessage("[screenResourceResolver 001] - Using Default ScreenResourceResouver.")
	String screenResourceResolverUsingDefault();

	@DefaultServerMessage("[datasources 001] - Error initializing datasource: {0}.")
	String dataSourcesDataSourceInitializeError(String localizedMessage);

	@DefaultServerMessage("[datasources 002] - Duplicated datasource: {0}.")
	String dataSourcesDuplicatedDataSource(String value);
	
	@DefaultServerMessage("[synchronizerToken 001] - Invalid Synchronizer Token for method {0}. Possible CSRF attack.")
	String synchronizerTokenServiceInvalidTokenError(String methodFullSignature);
}