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
package org.cruxframework.crux.core.config;

import org.cruxframework.crux.core.i18n.DefaultServerMessage;

/**
 * Contains Crux configuration properties
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface Crux 
{
	@DefaultServerMessage("")
	String customCruxXmlPreProcessors();
	
	@DefaultServerMessage("false")
	String disableRefreshByDefault();
		
	//TODO: Vintage Crux: Remove it!
	@DefaultServerMessage("true")
	String enableChildrenWindowsDebug();
	
	//TODO: Vintage Crux: Remove it!
	@DefaultServerMessage("false")
	String enableCrux2OldInterfacesCompatibility();
	
	@DefaultServerMessage("true")
	String enableGenerateHTMLDoctype();

	@DefaultServerMessage("true")
	String enableResourceStateCacheForRestServices();

	@DefaultServerMessage("false")
	String enableRestHostPageBaseURL();

	//Add in documentation!
	@DefaultServerMessage("org.cruxframework.crux.core.server.rest.state.ETagHandlerImpl")
	String eTagHandler();

	@DefaultServerMessage("org.cruxframework.crux.core.i18n.LocaleResolverImpl")
	String localeResolver();
	
	@DefaultServerMessage("false")
	String preferWebSQLForNativeDB();
	
	@DefaultServerMessage("false")
	String renderWidgetsWithIDs();
	
	@DefaultServerMessage("org.cruxframework.crux.core.server.rest.core.dispatch.RestErrorHandlerImpl")
	String restErrorHandler();

	@DefaultServerMessage("org.cruxframework.crux.core.server.rest.core.registry.RestServiceFactoryImpl")
	String restServiceFactory();
	
	@DefaultServerMessage("org.cruxframework.crux.core.server.rest.state.ClusteredResourceStateHandler")
	String restServiceResourceStateHandler();
	
	@DefaultServerMessage("")
	String scanAllowedLibs();

	@DefaultServerMessage("")
	String scanAllowedPackages();

	@DefaultServerMessage("")
	String scanIgnoredLibs();

	@DefaultServerMessage("")
	String scanIgnoredPackages();
	@DefaultServerMessage("false")
	String sendCruxViewNameOnClientRequests();
	@DefaultServerMessage("org.cruxframework.crux.core.server.dispatch.ServiceFactoryImpl")
	String serviceFactory();
	void setDisableRefreshByDefault(String value);
	void setEnableChildrenWindowsDebug(Boolean value);
	void setEnableCrux2OldInterfacesCompatibility(Boolean value);
	void setEnableResourceStateCacheForRestServices(String value);
	void setETagHandler(String value);
	void setLocaleResolver(String value);
	void setPreferWebSQLForNativDB(Boolean value);
	void setRenderWidgetsWithIDs(Boolean value);
	void setRestServiceResourceStateHandler(String value);
	void setScanAllowedLibs(String value);
	void setScanAllowedPackages(String value);
	void setScanIgnoredLibs(String value);
	void setScanIgnoredPackages(String value);
	void setSendCruxViewNameOnClientRequests(Boolean value);
	void setServiceFactory(String value);

	void setUseCompileTimeClassScanning(Boolean value);
	@DefaultServerMessage("false")
	String useCompileTimeClassScanningForDevelopment();	
	
	//Add in documentation!
	@DefaultServerMessage("false")
	String useHTML5XSD();
}
