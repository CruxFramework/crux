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
package org.cruxframework.crux.scanner.archiveiterator;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.scanner.ScannerRegistration;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class JARProtocolIteratorFactory implements DirectoryIteratorFactory
{

	public URLIterator create(URL url, List<ScannerRegistration> scanners) throws IOException
	{
		try
		{
			String pathInJar = null;
			String urlPath = url.toString().substring(4);
			int jarPathEnd = urlPath.indexOf("!");
			if (jarPathEnd > 0)
			{
				url = new URL(urlPath.substring(0, jarPathEnd));
				pathInJar = urlPath.substring(jarPathEnd+1);
			}
			
			return new JarProtocolIterator(url, scanners, pathInJar);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error trying invoke .toURI() over the URL object: [" + url + "]", e);
		}
	}
}
