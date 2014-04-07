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
package org.cruxframework.crux.core.declarativeui.view;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.server.scan.ScannerURLS;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.scannotation.AbstractScanner;
import org.cruxframework.crux.scannotation.archiveiterator.Filter;
import org.cruxframework.crux.scannotation.archiveiterator.IteratorFactory;
import org.cruxframework.crux.scannotation.archiveiterator.URLIterator;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewsScanner extends AbstractScanner
{
	private static final ViewsScanner instance = new ViewsScanner();
	private static URL[] urlsForSearch = null;
	private static final Lock lock = new ReentrantLock(); 
	
	/**
	 * 
	 */
	private ViewsScanner() 
	{
	}

	/**
	 * 
	 * @param urls
	 */
	private void scanArchives(URL... urls)
	{
		// used to handle duplicated entries on classpath
		Set<String> foundViews = new HashSet<String>();
		
		for (final URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String fileName)
				{
					if (fileName.endsWith(".view.xml"))
					{
						if (!ignoreScan(url, fileName))
						{
							return true;
						}
					}
					return false;
				}
			};

			try
			{
				URLIterator it = IteratorFactory.create(url, filter);
				if (it == null)
				{
					return;
				}
				URL found;
				while ((found = it.next()) != null)
				{
					String urlString = found.toString();
					if (!foundViews.contains(urlString))
					{
						foundViews.add(urlString);
						Views.registerView(getViewId(urlString), found);
					}
				}
			}
			catch (IOException e)
			{
				throw new ViewException("Error initializing ViewScanner.", e);
			}
		}
	}

	/**
	 * 
	 */
	public void scanArchives()
	{
		if (Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableWebRootScannerCache()))
		{
			if (urlsForSearch == null)
			{
				initialize(ScannerURLS.getWebURLsForSearch());
			}
			scanArchives(urlsForSearch);
		}
		else
		{
			scanArchives(ScannerURLS.getWebURLsForSearch());
		}
	}
	
	/**
	 * 
	 * @param urls
	 */
	public static void initialize(URL[] urls)
	{
		lock.lock();
		try
		{
			urlsForSearch = urls;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private String getViewId(String fileName)
	{
		fileName = fileName.substring(0, fileName.length() - 9);
		fileName = RegexpPatterns.REGEXP_BACKSLASH.matcher(fileName).replaceAll("/");
		int indexStartId = fileName.lastIndexOf('/');
		if (indexStartId > 0)
		{
			fileName = fileName.substring(indexStartId+1);
		}
		
		return fileName;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ViewsScanner getInstance()
	{
		return instance;
	}
}
