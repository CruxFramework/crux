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
package br.com.sysmap.crux.core.rebind.jsonparser;


/**
 * Helper Class used by Generators to deserialise objects returned by server before they
 * are passed to callback handlers.
 * 
 * @author Thiago Bustamante
 *
 */
public class JSONListParser extends JSONCollectionParser
{
	/**
	 * Singleton instance
	 */
	private static JSONListParser instance = new JSONListParser();
	
	/**
	 * Private constructor
	 */
	private JSONListParser() 
	{
		defaultClass = "java.util.ArrayList";
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static JSONListParser getInstance()
	{
		return instance;
	}
	
}
