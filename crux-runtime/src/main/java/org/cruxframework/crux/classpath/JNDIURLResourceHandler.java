/*
 * Copyright 2014 cruxframework.org.
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
import org.cruxframework.crux.scanner.archiveiterator.JNDIProtocolIteratorFactory;

/**
 * 
 * @author Samuel Almeida Cardoso
 *
 */
public class JNDIURLResourceHandler extends AbstractURLResourceHandler implements URLResourceHandler
{
	public static final String PROTOCOL = "jndi"; 
	
	/**
	 * 
	 */
	@Override
	public String getProtocol()
	{
		return PROTOCOL;
	}
	
	/**
	 * 
	 */
	@Override
	public URL getParentDir(URL url)
	{
		try
		{
			String path = url.toString();
			
			if (path.endsWith("/"))
			{
				path = path.substring(0, path.length() - 1);
			}
			
			int lastSlash = path.lastIndexOf("/");
			path = path.substring(0, lastSlash);

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
	@Override
	public DirectoryIteratorFactory getDirectoryIteratorFactory()
	{
		return new JNDIProtocolIteratorFactory();
	}
}
