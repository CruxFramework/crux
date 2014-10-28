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
package org.cruxframework.crux.core.client.config;

/**
 * <p>
 * Internal interface. Do not extend or implement this directly.
 * Contains some configuration properties to inform Crux how to work.
 * </p>
 * <p>
 * To change any of this configuration properties, create a property file for the
 * {@link org.cruxframework.crux.core.config.Crux} resource interface.
 * </p>
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface CruxClientConfig
{
	boolean enableDebugForURL(String url);
	boolean enableCrux2OldInterfacesCompatibility();
	boolean preferWebSQLForNativeDB();
	String notifierCompilerPort();
}
