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
package org.cruxframework.crux.core.rebind.offline;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.cruxframework.crux.core.rebind.screen.ScreenResourcesScannerException;
import org.cruxframework.crux.core.server.scan.ScannerURLS;
import org.cruxframework.crux.scannotation.AbstractScanner;
import org.cruxframework.crux.scannotation.archiveiterator.Filter;
import org.cruxframework.crux.scannotation.archiveiterator.IteratorFactory;
import org.cruxframework.crux.scannotation.archiveiterator.URLIterator;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OfflineScreensScanner extends AbstractScanner
{
	public Set<URL> scanOfflineArchives()
	{
		URL[] urls = ScannerURLS.getWebURLsForSearch();
		final Set<URL> screens = new HashSet<URL>();
		
		for (final URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String filename)
				{
					return (!ignoreScan(url, filename) && acceptsOffline(filename));
				}
			};

			try
			{
				URLIterator it = IteratorFactory.create(url, filter);
				URL found;
				if (it != null)
				{
					while ((found = it.next()) != null)
					{
						screens.add(found);
					}
				}
			}
			catch (IOException e)
			{
				throw new ScreenResourcesScannerException("Error initializing screenResourceScanner.", e);
			}
		}
		return screens;
	}
	
	protected boolean acceptsOffline(String urlString)
	{
		return urlString != null && urlString.endsWith(".offline.xml");
	}

}
