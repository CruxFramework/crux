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
package org.cruxframework.crux.core.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.cruxframework.crux.scannotation.URLStreamManager;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class URLUtils
{
	/**
	 * Returns <code>true</code> if the resource referred by an URL really exists
	 * @param url
	 * @return
	 */
	public static boolean existsResource(URL url)
	{
		boolean result = false;
		
		if (url != null)
		{
			if ("file".equals(url.getProtocol()))
			{
				try
				{
					result = new File(url.toURI()).exists();
				}
				catch (URISyntaxException e)
				{
					result = false;
				}
			}
			else
			{
				URLStreamManager manager = new URLStreamManager(url);
				InputStream stream = manager.open();
				manager.close();
				
				result = stream != null;
			}
		}
		
		return result;
	}
	
	/**
	 * @param urlString
	 * @return
	 */
	public static URL isValidURL(String urlString)
	{
	
		try 
		{
			return new URL(urlString);
		} 
		catch (Exception e) 
		{
			return null;
		}
	}
	
}
