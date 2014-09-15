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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.scanner.ScannerException;
import org.cruxframework.crux.scanner.ScannerRegistration;
import org.cruxframework.crux.scanner.Scanners;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class FileIterator extends URLIterator
{
	private File rootDir;
	private URL parentURL;

	public FileIterator(File file, List<ScannerRegistration> scanners)
	{
		super(scanners);
		this.rootDir = file;
		try
        {
	        parentURL = rootDir.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
			throw new ScannerException("Error running scanner.", e);
        }
	}

	@Override
	public void search()
	{
		try
		{
			search(rootDir);
		}
		catch (IOException e)
		{
			throw new ScannerException("Error running scanner.", e);
		}
	}
	
	protected void search(File dir) throws IOException 
	{
		File[] files = dir.listFiles();

		if (files != null)
		{
			for (int i = 0; i < files.length; i++)
			{
				URL url = files[i].toURI().toURL();
				if (files[i].isDirectory())
				{
					search(files[i]);
				}
				else if (Scanners.ignoreScan(parentURL, url.toString()))
				{
					continue;
				}
				else
				{
					consumeWhenAccepted(parentURL, url, url.toString());
				}
			}
		}
	}
}
