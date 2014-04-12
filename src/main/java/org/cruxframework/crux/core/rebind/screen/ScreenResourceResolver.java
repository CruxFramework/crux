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
package org.cruxframework.crux.core.rebind.screen;

import java.io.InputStream;
import java.util.Set;

import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.w3c.dom.Document;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ScreenResourceResolver
{
	/**
	 * Gets the inputStream for a given screenId
	 * @param screenId
	 * @return
	 * @throws CruxGeneratorException 
	 */
	InputStream getScreenResource(String screenId) throws CruxGeneratorException;
	
	/**
	 * Gets the inputStream for a given screenId
	 * @param screenId
	 * @param module
	 * @return
	 * @throws CruxGeneratorException 
	 */
	InputStream getScreenResource(String screenId, String module) throws CruxGeneratorException;

	/**
	 * Gets the Document representing the root view for a given screenId
	 * @param screenId
	 * @param device
	 * @return
	 * @throws CruxGeneratorException 
	 */
	Document getRootView(String screenId, String device) throws CruxGeneratorException;

	/**
	 * Gets the Document representing the root view for a given screenId
	 * @param relativeScreenId
	 * @param module
	 * @param device
	 * @return
	 * @throws CruxGeneratorException 
	 */
	Document getRootView(String relativeScreenId, String module, String device) throws CruxGeneratorException;

	/**
	 * List all Crux screen IDs
	 * @return
	 * @throws ScreenConfigException 
	 */
	Set<String> getAllScreenIDs(String module) throws ScreenConfigException;
	
	/**
	 * List all modules that have at least one crux.xml page (and needs to be compiled)
	 * @return
	 */
	Set<String> getAllAppModules();
}
