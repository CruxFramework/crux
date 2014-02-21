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
package org.cruxframework.crux.core.rebind.screen;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.server.scan.ScannerURLS;
import org.cruxframework.crux.scannotation.AbstractScanner;
import org.cruxframework.crux.scannotation.archiveiterator.Filter;
import org.cruxframework.crux.scannotation.archiveiterator.IteratorFactory;
import org.cruxframework.crux.scannotation.archiveiterator.URLIterator;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ScreenResourcesScanner extends AbstractScanner
{
	private static Map<String, Set<String>> pagesPerModule = null;
	private static Lock lock = new ReentrantLock();
	
	private Set<String> scanArchives()
	{
		URL[] urls = ScannerURLS.getWebURLsForSearch();
		final Set<String> screens = new HashSet<String>();
		final ScreenResourcesScanner scanner = this;
		
		for (final URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String filename)
				{
					if (scanner.accepts(filename))
					{
						if (filename.startsWith("/"))
						{
							filename = filename.substring(1);
						}
						if (!ignoreScan(url, filename))
						{
							screens.add(filename);
						}
						return true;
					}
					return false;
				}
			};

			try
			{
				URLIterator it = IteratorFactory.create(url, filter);
				if (it != null)
				{
					while (it.next() != null); 
				}
			}
			catch (IOException e)
			{
				throw new ScreenResourcesScannerException("Error initializing screenResourceScanner.", e);
			}
		}
		return screens;
	}
	
	public Set<String> getPages(String module) throws ScreenConfigException
	{
		if (Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableWebRootScannerCache()))
		{
			return getCachedPages(module);
		}
		else
		{
			HashMap<String, Set<String>> modulePages = new HashMap<String, Set<String>>();
			createPagesMapForModule(modulePages);
			return modulePages.get(module);
		}
	}
	
	/**
	 * 
	 * @param module
	 * @return
	 * @throws ScreenConfigException
	 */
	private Set<String> getCachedPages(String module) throws ScreenConfigException
	{
		if (pagesPerModule == null)
		{
			lock.lock();
			try
			{
				if (pagesPerModule == null)
				{
					pagesPerModule = new HashMap<String, Set<String>>();
					createPagesMapForModule(pagesPerModule);
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		
		return pagesPerModule.get(module);
	}

	/**
	 * 
	 * @param modulePages
	 * @param includeInternal
	 * @return
	 * @throws ScreenConfigException
	 */
	private void createPagesMapForModule(Map<String, Set<String>> modulePages) throws ScreenConfigException
	{
		Set<String> archives = scanArchives();
		for (String screenID : archives)
		{
			Screen screen = ScreenFactory.getInstance().getScreen(screenID, null);
			if(screen != null)
			{
				Set<String> pages = modulePages.get(screen.getModule());
				if (pages == null)
				{
					pages = new HashSet<String>();
					modulePages.put(screen.getModule(), pages);
				}
				pages.add(screenID);
			}
		}
	}
	
	protected abstract boolean accepts(String urlString);
}
