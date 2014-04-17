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

import org.cruxframework.crux.scannotation.archiveiterator.DirectoryIteratorFactory;
import org.cruxframework.crux.scannotation.archiveiterator.ZIPProtocolIteratorFactory;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ZIPURLResourceHandler extends AbstractURLResourceHandler implements PackageFileURLResourceHandler
{
	/**
	 * 
	 */
	public String getProtocol()
	{
		return "zip";
	}
	
	/**
	 * 
	 */
	public URL getParentDir(URL url)
	{
		try
		{
			String path = url.toString();
			if (path.endsWith("!") || path.endsWith("!/"))
			{
				int firstSlash = path.indexOf("/");
				path = path.substring(firstSlash + 1);
				path = "file:///" + path.replace("!", "");
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

	/**
	 * 
	 */
	public DirectoryIteratorFactory getDirectoryIteratorFactory()
	{
		return new ZIPProtocolIteratorFactory();
	}

	/**
	 * @see org.cruxframework.crux.classpath.PackageFileURLResourceHandler#getPackageFile(java.net.URL)
	 */
	public URL getPackageFile(URL url)
	{
		try
		{
			String urlString = url.toString();
			
			urlString = "file"+urlString.substring(3);
			
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

	/**
	 * @see org.cruxframework.crux.classpath.PackageFileURLResourceHandler#getPackaegResourceName(java.net.URL)
	 */
	public String getPackaegResourceName(URL url)
	{
		try
		{
			String resourceString = url.toString();
			
			if (resourceString.indexOf("!/") > 0)
			{
				resourceString = resourceString.substring(resourceString.indexOf("!/")+2);
			}

			return resourceString;
		}
		catch (Exception e)
		{
			throw new URLResourceException(e.getLocalizedMessage(), e);
		}
	}
}
