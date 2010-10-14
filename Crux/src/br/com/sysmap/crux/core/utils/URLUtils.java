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
package br.com.sysmap.crux.core.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class URLUtils
{
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static boolean existsResource(URL url)
	{
		if (url == null)
		{
			return false;
		}
		if ("file".equals(url.getProtocol()))
		{
			try
			{
				return new File(url.toURI()).exists();
			}
			catch (URISyntaxException e)
			{
				return false;
			}
		}
		
		try
		{
			InputStream inputStream = url.openStream();
			if (inputStream != null)
			{
				inputStream.close();
				return true;
			}
		}
		catch(Throwable e)
		{
			return false;
		}
		return false;
	}
	
	/**
	 * If url exists, open a stream to it. Returns null otherwise.
	 * @param url
	 * @return an inputStream or null if the resource does not exist or can not be read
	 */
	public static InputStream openStream(URL url)
	{
		if (url == null)
		{
			return null;
		}
		if ("file".equals(url.getProtocol()))
		{
			try
			{
				if (new File(url.toURI()).exists())
				{
					return url.openStream();
				}
			}
			catch (Exception e)
			{
				return null;
			}
		}
		
		try
		{
			InputStream inputStream = url.openStream();
			if (inputStream != null)
			{
				return inputStream;
			}
		}
		catch(Throwable e)
		{
			return null;
		}
		return null;
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
