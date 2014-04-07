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

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.scanner.AbstractScanner;
import org.cruxframework.crux.scanner.ScannerException;
import org.cruxframework.crux.scanner.ScannerRegistration.ScannerMatch;
import org.cruxframework.crux.scanner.Scanners.ScannerCallback;
import org.cruxframework.crux.scanner.URLStreamManager;
import org.cruxframework.crux.scanner.archiveiterator.Filter;
import org.w3c.dom.Document;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ScreenResourcesScanner extends AbstractScanner
{
	private static Map<String, Set<String>> pagesPerModule = new HashMap<String, Set<String>>();
	private static boolean initialized = false;
	
	@Override
	public Filter getScannerFilter()
	{
		final ScreenResourcesScanner scanner = this;
		return new Filter()
		{
			public boolean accepts(String fileName)
			{
				if (scanner.accepts(fileName))
				{
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public ScannerCallback getScannerCallback()
	{
	    return new ScannerCallback()
		{
			@Override
			public void onFound(List<ScannerMatch> scanResult)
			{
				try
				{
					for (ScannerMatch match : scanResult)
					{
						URL found = match.getMatch();
						Document screenDocument = getScreenDocument(found);
						String module = ScreenFactory.getInstance().getScreenModule(screenDocument);
						registerPageForModule(module, found.toString());
					}
					setInitialized();
				}
				catch(Exception e)
				{
					throw new ScannerException("Error searching for screen files", e);
				}
			}

		};
	}
	
	public Set<String> getPages(String module) throws ScreenConfigException
	{
		if (!initialized)
		{
			initialize();
		}
		return pagesPerModule.get(module);
	}
	
	/**
	 * 
	 * @param module
	 * @return
	 * @throws ScreenConfigException
	 */
	public synchronized void initialize()
	{
		if (!initialized)
		{
			pagesPerModule.clear();
			scanArchives();
			initialized = true;
		}
	}

	@Override
	public void resetScanner()
	{
		initialized = false;
		pagesPerModule.clear();
	}
	
	/**
	 * @param module
	 * @param screenId
	 * @return
	 * @throws ScreenConfigException
	 */
	private void registerPageForModule(String module, String screenId)
	{
		Set<String> pages = pagesPerModule.get(module);
		if (pages == null)
		{
			pages = new HashSet<String>();
			pagesPerModule.put(module, pages);
		}
		pages.add(screenId);
	}
	
	private Document getScreenDocument(URL screenURL)
	{
		URLStreamManager manager = new URLStreamManager(screenURL);
		InputStream stream = manager.open();
		try
        {
			Document screen = ViewProcessor.getView(stream, null);
	        return screen;
        }
        catch (Exception e)
        {
	        throw new ScannerException("Error reading offline screen.", e);
        }
        finally
        {
        	manager.close();
        }
	}
	
	private void scanArchives()
	{
		runScanner();
	}
	
	private static void setInitialized()
	{
		initialized = true;
	}

	protected abstract boolean accepts(String urlString);
}
