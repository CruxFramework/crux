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

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.scanner.AbstractScanner;
import org.cruxframework.crux.scanner.ScannerRegistration.ScannerMatch;
import org.cruxframework.crux.scanner.Scanners;
import org.cruxframework.crux.scanner.Scanners.ScannerCallback;
import org.cruxframework.crux.scanner.archiveiterator.Filter;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewsScanner extends AbstractScanner
{
	private static final ViewsScanner instance = new ViewsScanner();
	private static boolean initialized = false;
	
	/**
	 * 
	 */
	private ViewsScanner() 
	{
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
				if (fileName.endsWith(".view.xml"))
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
						Views.registerView(getViewId(urlString), found);
					}
				}
				Views.setInitialized();
			}
		};
	}
	
	@Override
	public void resetScanner()
	{
		Views.reset();
	}
	
	/**
	 * 
	 * @param urls
	 */
	public void scanArchives()
	{
		runScanner();
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
