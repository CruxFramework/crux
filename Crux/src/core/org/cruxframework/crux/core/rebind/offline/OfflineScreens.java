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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.rebind.screen.OfflineScreen;
import org.cruxframework.crux.core.rebind.screen.OfflineScreenFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.ScreenResourcesScannerException;
import org.cruxframework.crux.core.server.scan.ScannerURLS;
import org.cruxframework.crux.scannotation.archiveiterator.Filter;
import org.cruxframework.crux.scannotation.archiveiterator.IteratorFactory;
import org.cruxframework.crux.scannotation.archiveiterator.URLIterator;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OfflineScreens
{
	/**
	 * 
	 * @param module
	 * @return
	 * @throws ScreenConfigException
	 */
	public static Set<OfflineScreen> getOfflinePages(String module) throws ScreenConfigException
    {
		HashMap<String, Set<OfflineScreen>> modulePages = new HashMap<String, Set<OfflineScreen>>();
		createOfflinePagesMapForModule(modulePages);
		return modulePages.get(module);
    }
		
	/**
	 * 
	 * @param modulePages
	 * @return
	 * @throws ScreenConfigException
	 */
	private static void createOfflinePagesMapForModule(Map<String, Set<OfflineScreen>> modulePages) throws ScreenConfigException
	{
		Set<URL> archives = scanOfflineArchives();
		for (URL screenURL : archives)
		{
			OfflineScreen screen = OfflineScreenFactory.getInstance().getOfflineScreen(screenURL);
			if(screen != null)
			{
				Set<OfflineScreen> pages = modulePages.get(screen.getModule());
				if (pages == null)
				{
					pages = new HashSet<OfflineScreen>();
					modulePages.put(screen.getModule(), pages);
				}
				pages.add(screen);
			}
		}
	}

	private static Set<URL> scanOfflineArchives()
	{
//		URL[] urls = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDirs();
		URL[] urls = ScannerURLS.getWebURLsForSearch();
		final Set<URL> screens = new HashSet<URL>();
		
		for (URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String filename)
				{
					return (acceptsOffline(filename));
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
	
	protected static boolean acceptsOffline(String urlString)
	{
		return urlString != null && urlString.endsWith(".offline.xml");
	}
}
