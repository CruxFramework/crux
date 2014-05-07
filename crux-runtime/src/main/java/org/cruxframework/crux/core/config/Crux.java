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

public interface Crux 
{
	@DefaultServerMessage("org.cruxframework.crux.core.server.dispatch.ServiceFactoryImpl")
	String serviceFactory();
	
	@DefaultServerMessage("crux_comp")
	String cruxCompilationTempFolder();
	
	@DefaultServerMessage("true")
	String isRelativeCruxCompilationTempFolder();
	
	@DefaultServerMessage("org.cruxframework.crux.core.server.rest.core.registry.RestServiceFactoryImpl")
	String restServiceFactory();
	
	@DefaultServerMessage("org.cruxframework.crux.core.server.rest.core.dispatch.RestErrorHandlerImpl")
	String restErrorHandler();

	@DefaultServerMessage("org.cruxframework.crux.core.i18n.LocaleResolverImpl")
	String localeResolver();

	@DefaultServerMessage("org.cruxframework.crux.core.declarativeui.DeclarativeUIScreenResolver")
	String screenResourceResolver();

	@DefaultServerMessage("org.cruxframework.crux.core.server.classpath.ClassPathResolverImpl")
	String classPathResolver();
	
	//TODO: Vintage Crux: Remove it!
	@DefaultServerMessage("true")
	String enableChildrenWindowsDebug();

	@DefaultServerMessage("true")
	String enableHotDeploymentForWebDirs();
	
	@DefaultServerMessage("false")
	String enableRestHostPageBaseURL();

	@DefaultServerMessage("")
	String scanAllowedPackages();
	
	@DefaultServerMessage("")
	String scanIgnoredPackages();
	
	@DefaultServerMessage("")
	String scanIgnoredLibs();
	
	@DefaultServerMessage("")
	String scanAllowedLibs();

	@DefaultServerMessage("true")
	String enableGenerateHTMLDoctype();
	
	//TODO: Vintage Crux: Remove it!
	@DefaultServerMessage("false")
	String enableCrux2OldInterfacesCompatibility();
	
	@DefaultServerMessage("false")
	String preferWebSQLForNativeDB();

	@DefaultServerMessage("false")
	String renderWidgetsWithIDs();

	@DefaultServerMessage("false")
	String useCompileTimeClassScanningForDevelopment();

	void setEnableChildrenWindowsDebug(Boolean value);
	void setEnableHotDeploymentForWebDirs(Boolean value);
	void setRenderWidgetsWithIDs(Boolean value);
	void setUseCompileTimeClassScanning(Boolean value);
	void setEnableCrux2OldInterfacesCompatibility(Boolean value);
	void setPreferWebSQLForNativDB(Boolean value);
	void setEnableCrossDocumentSupport(Boolean value);
	void setClassPathResolver(String value);
	void setScreenResourceResolver(String value);
	void setLocaleResolver(String value);
	void setServiceFactory(String value);
	void setParameterBindGenerator(String value);
	void setScanAllowedPackages(String value);
	void setScanIgnoredPackages(String value);
	void setScanAllowedLibs(String value);
	void setScanIgnoredLibs(String value);
	
	
	@DefaultServerMessage("org.cruxframework.crux.core.server.rest.state.ClusteredResourceStateHandler")
	String restServiceResourceStateHandler();
	void setRestServiceResourceStateHandler(String value);

	@DefaultServerMessage("true")
	String enableResourceStateCacheForRestServices();
	void setEnableResourceStateCacheForRestServices(String value);

	@DefaultServerMessage("false")
	String disableRefreshByDefault();
	void setDisableRefreshByDefault(String value);
}
