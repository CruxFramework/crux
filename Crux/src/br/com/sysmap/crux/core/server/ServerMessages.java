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
/**
 * Contains all server messages used by Crux Framework
 * @author Thiago Bustamante
 *
 */
public interface ServerMessages 
{
	String annotationScannerBuildIndex();
	String annotationScannerBuildIndexError(String errMsg);
	String annotationScannerIndexNotFound();
	String clientHandlersHandlerInitializeError(String errMsg);
	String componentConfigParserError(String errMsg);
	String componentConfigScannerInitializationError(String errMsg);
	String componentParserImplComponentPropertyError(String property, String componentId);
	String controllersInitializeError(String errMsg);
	String controllerFactoryInitializerUsingDefaultFactory();
	String initializerListenerComponentsRegistered();
	String initializerListenerControllersRegistered();
	String scannerURLSErrorSearchingClassesDir(String errMsg);
	String scannerURLSErrorSearchingLibDir(String errMsg);
	String screenFactoryComponentIdRequired();
	String screenFactoryErrorCreateComponent(String componentId);
	String screenFactoryErrorDuplicatedComponent(String componentId);
	String screenFactoryErrorRetrievingScreen(String screenId, String errMsg);
	String screenFactoryGenericErrorCreateComponent(String screenId, String errMsg);
	String screenFactoryScreeResourceNotFound(String screenId);
	String screenPropertyError(String property, String componentId);
	String formattersFormatterInitializeError(String errMsg);
	String screenBridgeErrorRegisteringScreen(String errMsg);
	String screenBridgeErrorReadingScreenId(String errMsg);
}
