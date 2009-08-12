/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.tools.htmltags;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.com.sysmap.crux.core.server.scan.ScannerURLS;

public class WidgetTagConfig 
{
	private static List<String> config = null;
	private static final Lock lock = new ReentrantLock();

	public static void initializeWidgetConfig()
	{
		initializeWidgetConfig(ScannerURLS.getURLsForSearch());
	}
	
	/**
	 * Searches for crux htmlTags style sheet files and registers them.
	 * @param urls
	 */
	public static void initializeWidgetConfig(URL[] urls)
	{
		if (config != null) return;
		lock.lock();
		try
		{
			if (config != null) return;
			config = new ArrayList<String>(100);
			WidgetTagConfigScanner.getInstance().scanArchives(urls);
		}
		catch (RuntimeException e) 
		{
			config = null;
			throw (e);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Register a style sheet. Called by WidgetTagConfigScanner when find a crux htmlTags style sheet
	 * @param name
	 * @param inputStream
	 */
	static void registerSheet(String xsltString)
	{
		config.add(xsltString);
	}
		
	/**
	 * Iterates over style sheets streams
	 * @return
	 */
	public static Iterator<String> iterateStyleSheets()
	{
		if (config == null)
		{
			initializeWidgetConfig();
		}
		return config.iterator();
	}
}
