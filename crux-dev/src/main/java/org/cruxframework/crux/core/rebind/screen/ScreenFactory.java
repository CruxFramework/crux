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
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.screen.ScreenLoader;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.dev.resource.Resource;


/**
 * Creates a representation for Crux screens
 *  
 * @author Thiago Bustamante
 *
 */
public class ScreenFactory 
{
	private Map<String, Screen> screenCache = new HashMap<String, Screen>();
	private ScreenLoader screenLoader;
	private ViewFactory viewFactory;
	private RebindContext context;

	/**
	 * Singleton Constructor
	 */
	public ScreenFactory(RebindContext context) 
	{
		this.context = context;
		this.screenLoader = context.getScreenLoader();
		this.viewFactory = new ViewFactory(context);
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

			Resource resource = screenLoader.getScreen(id);
			if (resource == null)
			{
				throw new ScreenConfigException("Error retrieving screen ["+id+"].crux.xml.");
			}
			InputStream inputStream = resource.openContents();
			Document screenView = viewFactory.getViewDocument(id, device, inputStream);
			StreamUtils.safeCloseStream(inputStream);
			if (screenView == null)
			{
				throw new ScreenConfigException("Screen ["+id+"].crux.xml not found!");
			}
			screen = parseScreen(id, device, screenView, resource.getLastModified());
			if(screen != null)
			{
				screen.setLastModified(resource.getLastModified());
				screenCache.put(cacheKey, screen);
			}
			return screen;

		} 
		catch (IOException e) 
		{
			throw new ScreenConfigException("Error retrieving screen ["+id+"].crux.xml.", e);
		}
	}

	public Set<String> getScreens()
	{
		return screenLoader.getScreens();
	}

	/**
	 * Retrieve the viewFactory.
	 * @return
	 */
	public ViewFactory getViewFactory()
	{
		return viewFactory;
	}

	public void generateHostPages(String device) throws ScreenConfigException
	{
		Set<String> screenIDs = getScreens();
		for (String screenId : screenIDs)
		{
			try
			{
				OutputStream stream = context.getGeneratorContext().tryCreateResource(context.getLogger(), screenId+".crux.xml");
				if (stream != null) // was not already generated during this compilation
				{
					Resource resource = screenLoader.getScreen(screenId);
					InputStream inputStream = resource.openContents();
					Document screenView = viewFactory.getViewDocument(screenId, device, inputStream);
					StreamUtils.safeCloseStream(inputStream);
					viewFactory.generateHTML(screenId, screenView, stream);
					StreamUtils.safeCloseStream(stream);
					context.getGeneratorContext().commitResource(context.getLogger(), stream).setVisibility(Visibility.Private);
				}
			}
			catch (Exception e)
			{
				throw new ScreenConfigException("Error generating HTML file for crux page ["+screenId+".crux.xml]", e);
			}
		}
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
	private Screen parseScreen(String id, String device, Document screenView, long lastModified) throws IOException, ScreenConfigException
	{
		Screen screen = null;
		String screenModule = getScreenModule(screenView);

		if(screenModule != null)
		{
			View rootView = viewFactory.getView(id, screenView, lastModified, true);

			screen = new Screen(id, screenModule, rootView);
		}
		else
		{
			throw new ScreenConfigException("No module declared on screen ["+id+"].crux.xml.");
		}

		return screen;
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
