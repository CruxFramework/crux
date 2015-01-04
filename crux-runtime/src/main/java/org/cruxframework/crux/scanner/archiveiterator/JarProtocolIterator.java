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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.jar.JarInputStream;

import org.cruxframework.crux.scanner.ScannerRegistration;


/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class JarProtocolIterator extends ZIPProtocolIterator
{
	public JarProtocolIterator(URL zip, List<ScannerRegistration> scanners, String pathInZip) throws IOException, URISyntaxException
	{
		super(scanners);
		if (zip.toString().startsWith("file:"))
		{
			this.zip = toJarURL(zip);
			this.zipStream = new JarInputStream(new FileInputStream(new File(zip.toURI())));
		}
		else
		{
			this.zip = zip;
			this.zipStream = new JarInputStream(zip.openStream());
		}
		this.pathInZip = (pathInZip==null?"":pathInZip);
		if (this.pathInZip.startsWith("/"))
		{
			this.pathInZip = this.pathInZip.substring(1);
		}
	}
	
	@Override
	protected String getProtocol()
	{
		return "jar";
	}
	
	private URL toJarURL(URL fileURL) throws MalformedURLException
	{
		return new URL("jar:"+fileURL.toString()+"!/");
	}	
}
