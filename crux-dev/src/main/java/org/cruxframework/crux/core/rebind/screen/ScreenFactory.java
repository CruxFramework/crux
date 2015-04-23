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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.core.declarativeui.screen.ScreenProvider;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Creates a representation for Crux screens
 *  
 * @author Thiago Bustamante
 *
 */
public class ScreenFactory 
{
	private Map<String, Screen> screenCache = new HashMap<String, Screen>();
	private ScreenProvider screenProvider;
	private ViewFactory viewFactory;
	private ViewProcessor viewProcessor;

	/**
	 * Singleton Constructor
	 */
	public ScreenFactory(ScreenProvider screenProvider) 
	{
		this.screenProvider = screenProvider;
		this.viewProcessor = new ViewProcessor(screenProvider.getViewProvider());
		this.viewFactory = new ViewFactory(screenProvider.getViewProvider());
	}
	
	/**
	 * Factory method for screens.
	 * @param id
	 * @param device device property for this permutation being compiled
	 * @return
	 * @throws ScreenConfigException
	 */
	public Screen getScreen(String id, String device) throws ScreenConfigException
	{
		try 
		{
			String cacheKey = device==null?id:id+"_"+device;
			
			Screen screen = screenCache.get(cacheKey);
			if (screen != null)
			{
				return screen;
			}

			InputStream inputStream = screenProvider.getScreen(id);
			Document screenView = viewProcessor.getView(inputStream, id, device);
			StreamUtils.safeCloseStream(inputStream);
			if (screenView == null)
			{
				throw new ScreenConfigException("Screen ["+id+"] not found!");
			}
			screen = parseScreen(id, device, screenView);
			if(screen != null)
			{
				screenCache.put(cacheKey, screen);
			}
			return screen;
			
		} 
		catch (Exception e) 
		{
			throw new ScreenConfigException("Error retrieving screen ["+id+"].", e);
		}
	}
	
	public Set<String> getScreens(String module)
    {
	    return screenProvider.getScreens(module);
    }

	/**
	 * Retrieve the viewFactory.
	 * @return
	 */
	public ViewFactory getViewFactory()
	{
		return viewFactory;
	}
	
	/**
	 * Parse the HTML page and build the Crux Screen. 
	 * @param id
	 * @param device
	 * @param screenView
	 * @return
	 * @throws IOException
	 * @throws ScreenConfigException 
	 */
	private Screen parseScreen(String id, String device, Document screenView) throws IOException, ScreenConfigException
	{
		Screen screen = null;
		String screenModule = getScreenModule(screenView);

		if(screenModule != null)
		{
			String relativeScreenId = getRelativeScreenId(id, screenModule);
			View rootView = viewFactory.getView(relativeScreenId, device, screenView, true);
			
			screen = new Screen(id, relativeScreenId, screenModule, rootView);
		}
		else
		{
			throw new ScreenConfigException("No module declared on screen ["+id+"].");
		}
		
		return screen;
	}
	
	/**
	 * @param id
	 * @param module
	 * @return
	 */
	public static String getRelativeScreenId(String id, String module)
	{
		return module+"/"+id;
	}

	/**
	 * @param nodeList
	 * @return
	 * @throws ScreenConfigException
	 */
	public static String getScreenModule(Document source) throws ScreenConfigException
	{
		String result = null;
		
		NodeList nodeList = source.getElementsByTagName("script");
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++)
		{
			Element item = (Element) nodeList.item(i);
			
			String src = item.getAttribute("src");
			
			if (src != null && src.endsWith(".nocache.js"))
			{
				if (result != null)
				{
					throw new ScreenConfigException("Multiple modules in the same html page is not allowed in CRUX.");
				}
				
				int lastSlash = src.lastIndexOf("/");
				
				if(lastSlash >= 0)
				{
					int firstDotAfterSlash = src.indexOf(".", lastSlash);
					result = src.substring(lastSlash + 1, firstDotAfterSlash);
				}
				else
				{
					int firstDot = src.indexOf(".");
					result = src.substring(0, firstDot);
				}
			}
		}
		return result;
	}
}
