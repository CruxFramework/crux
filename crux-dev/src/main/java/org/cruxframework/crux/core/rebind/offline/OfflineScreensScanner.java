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

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.utils.XMLUtils;
import org.cruxframework.crux.core.utils.XMLUtils.XMLException;
import org.cruxframework.crux.scanner.AbstractScanner;
import org.cruxframework.crux.scanner.ScannerException;
import org.cruxframework.crux.scanner.ScannerRegistration.ScannerMatch;
import org.cruxframework.crux.scanner.Scanners;
import org.cruxframework.crux.scanner.Scanners.ScannerCallback;
import org.cruxframework.crux.scanner.URLStreamManager;
import org.cruxframework.crux.scanner.archiveiterator.Filter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OfflineScreensScanner extends AbstractScanner
{
	private static boolean initialized = false;
	private static OfflineScreensScanner instance = new OfflineScreensScanner();
	
	public static OfflineScreensScanner getInstance()
	{
		return instance;
	}
	
	public static synchronized void initializeScanner()
	{
		if (!initialized)
		{
			Scanners.registerScanner(getInstance());
			initialized = true;
		}
	}

	@Override
	public Filter getScannerFilter()
	{
		return new Filter()
		{
			public boolean accepts(String fileName)
			{
				if (fileName.endsWith(".offline.xml"))
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
				// used to handle duplicated entries on classpath
				Set<String> foundViews = new HashSet<String>();
				
				for (ScannerMatch match : scanResult)
				{
					URL found = match.getMatch();
					
					String urlString = found.toString();
					if (!foundViews.contains(urlString))
					{
						foundViews.add(urlString);
						Document screen = getOfflineScreen(found);
						OfflineScreens.registerOfflinePageForModule(screen, found.toString(), getModule(screen));
					}
				}
				OfflineScreens.setInitialized();
			}
		};
	}
		
	@Override
	public void resetScanner()
	{
		OfflineScreens.reset();	    
	}
	
	public void scanArchives()
	{
		runScanner();
	}
	
	private String getModule(Document screen)
	{
		Element screenElement = screen.getDocumentElement();
		return screenElement.getAttribute("moduleName");
	}
	
	private Document getOfflineScreen(URL screenURL)
	{
		URLStreamManager manager = new URLStreamManager(screenURL);
		InputStream stream = manager.open();
		try
        {
	        Document screen = XMLUtils.createNSUnawareDocument(stream, screenURL.getPath());
	        return screen;
        }
        catch (XMLException e)
        {
	        throw new ScannerException("Error reading offline screen.", e);
        }
        finally
        {
        	manager.close();
        }
	}
}
