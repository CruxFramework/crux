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
package org.cruxframework.crux.classpath;

import java.net.URL;

import org.cruxframework.crux.scanner.archiveiterator.DirectoryIteratorFactory;
import org.cruxframework.crux.scanner.archiveiterator.JARProtocolIteratorFactory;


public class JARURLResourceHandler extends ZIPURLResourceHandler
{
	/**
	 * 
	 */
	@Override
	public String getProtocol()
	{
		return "jar";
	}

	/**
	 * 
	 */
	@Override
	public URL getParentDir(URL url)
	{
		checkProtocol(url);

		try
		{
			String path = url.toString();
			if (path.endsWith("!") || path.endsWith("!/"))
			{
				path = path.substring(4).replace("!", "");
			}
			
			if (path.endsWith("/"))
			{
				path = path.substring(0, path.length() - 1);
			}
			
			int lastSlash = path.lastIndexOf("/");
			path = path.substring(0, lastSlash);

			if (path.endsWith("!"))
			{
				path = path +"/";
			}
			
			return new URL(path);
		}
		catch (Exception e)
		{
			throw new URLResourceException(e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public DirectoryIteratorFactory getDirectoryIteratorFactory()
	{
		return new JARProtocolIteratorFactory();
	}
	
	@Override
	public URL getPackageFile(URL url)
	{
		try
		{
			String urlString = url.toString();
			
			urlString = urlString.substring(4);
			
			if (urlString.indexOf("!/") > 0)
			{
				urlString = urlString.substring(0, urlString.indexOf("!/"));
			}
			
			return new URL(urlString);
		}
		catch (Exception e)
		{
			throw new URLResourceException(e.getLocalizedMessage(), e);
		}
	}
}
