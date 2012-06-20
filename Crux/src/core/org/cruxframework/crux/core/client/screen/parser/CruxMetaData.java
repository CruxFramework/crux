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
package org.cruxframework.crux.core.client.screen.parser;

import org.cruxframework.crux.core.client.collection.Map;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxMetaData extends JavaScriptObject
{
	/**
	 * Loads the Crux Meta Data information. It is represented in JSON format and is retrieved through 
	 * a native JS function.
	 * 
	 * @param metadata Crux metadata to parse
	 * @return an object that has been built by parsing the metadata string
	 * @throws CruxMetaDataException 
	 */
	public static CruxMetaData loadMetaData() throws CruxMetaDataException
	{
		try
		{
			return callMetaDataNativeLoader();
		}
		catch (JavaScriptException ex)
		{
			throw new CruxMetaDataException(ex);
		}
	}

	/**
	 * Call the native function that loads the CruxMetaData object.
	 */
	private static native CruxMetaData callMetaDataNativeLoader() /*-{
		return $wnd.__CruxMetaData_();
	}-*/;

	/**
	 * 
	 */
	protected CruxMetaData()
    {
    }
	
	/**
	 * @return
	 */
	public final native String getScreenId()/*-{
		return this['id'];
	}-*/;
	
	/**
	 * @return
	 */
	public final native Map<String> getLazyDependencies()/*-{
		return this['lazyDeps'];
	}-*/;
}
