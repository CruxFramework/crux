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

import java.io.File;
import java.net.URL;

import org.cruxframework.crux.scanner.archiveiterator.DirectoryIteratorFactory;
import org.cruxframework.crux.scanner.archiveiterator.FileProtocolIteratorFactory;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class FileURLResourceHandler extends AbstractURLResourceHandler
{
	/**
	 * 
	 */
	public String getProtocol()
	{
		return "file";
	}

	/**
	 * 
	 */
	public URL getParentDir(URL url)
	{
		checkProtocol(url);
		
		try
		{
			File file = new File(url.toURI());
			return file.getParentFile().toURI().toURL();
		}
		catch (Exception e)
		{
			throw new URLResourceException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 
	 */
	public URL getChildResource(URL url, String path)
	{
		checkProtocol(url);
		
		try
		{
			File parentFile = new File(url.toURI());
			String parentPath = parentFile.getCanonicalPath();
			String parentPathURL = parentFile.toURI().toURL().toString();
			if (path.startsWith(parentPathURL))
			{
				path = path.substring(parentPathURL.length());
			}
			else if (path.startsWith(parentPath))
			{
				path = path.substring(parentPath.length());
			}
			
			return new File(parentFile, path).toURI().toURL();
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
		return new FileProtocolIteratorFactory();
	}
}
