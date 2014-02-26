/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.client.utils;

/**
 * @author Samuel Almeida Cardoso
 *
 */
public class Base64Utils
{
	private static final String BASE64 = "base64";
	private static final String DATA = "data:";

	/**
	 * Get the mimeType prefix from a base64 string.
	 * For instance:
	 * If you pass: data:image/jpg;base64,/9...
	 * It will return: image/jpg
	 * @param base64
	 * @return
	 */
	public static String getMimeTypeFromBase64Data(String base64)
	{
		if(base64.contains(BASE64) && base64.contains(DATA))
		{
			return base64.substring(base64.indexOf(DATA) + DATA.length(), base64.indexOf(BASE64) - 1);
		}
		return "";
	}
	
	/**
	 * Add a mimeType to a clean base64 String
	 * @param base64
	 * @param mimeType
	 * @return
	 */
	public static String addMimeTypePrefixToBase64Data(String base64, String mimeType)
	{
		assert (mimeType != null && mimeType != "") : "mimeType must not be null";
		String plainBase64 = ensurePlainBase64(base64);
		return "data:" + mimeType + ";base64," + plainBase64;
	}
	
	/**
	 * Ensure that base64 string doesn't have any prefix like data:image/jpg;base64
	 * @param base64
	 * @return
	 */
	public static String ensurePlainBase64(String base64)
	{
		if(base64 == null)
		{
			return "";
		}
		
		if(base64.contains(BASE64))
		{
			return base64.substring(base64.indexOf(BASE64) + (BASE64+",").length(), base64.length());
		}
		return base64;
	}
	
	public static void main(String[] args) 
	{
		System.out.println(getMimeTypeFromBase64Data("data:image/jpg;base64,/9"));
	}
}
