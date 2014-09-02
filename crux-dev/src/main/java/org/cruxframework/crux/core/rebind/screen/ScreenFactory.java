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
 
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
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
	private static ScreenFactory instance = new ScreenFactory();
	
	private static final Lock screenLock = new ReentrantLock();
	private static final long UNCHANGED_RESOURCE = -1;
	private static final long REPROCESS_RESOURCE = -2;	

	private Map<String, Screen> screenCache = new HashMap<String, Screen>();
	private Map<String, Long> screenLastModified = new HashMap<String, Long>();		

	/**
	 * Singleton Constructor
	 */
	private ScreenFactory() 
	{
	}
	
	/**
	 * Singleton method
	 * @return
	 */
	public static ScreenFactory getInstance()
	{
		return instance;
	}
	
	/**
	 * Clear the screen cache
	 */
	public void clearScreenCache()
	{
		screenCache.clear();
		ViewFactory.getInstance().clearViewCache();
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
			long lastModified = getScreenLastModified(id);
			String cacheId = device==null?id:id+"_"+device;
			
			Screen screen = getFromCache(id, cacheId, lastModified);
			if (screen != null)
			{
				return screen;
			}

			
			screenLock.lock();
			try
			{
				if (getFromCache(id, cacheId, lastModified) == null)
				{
					Document screenView;
					String relativeScreenId = Modules.getInstance().removeModuleIfPresent(id);
			    	if (!relativeScreenId.equals(id))
			    	{
			    		String module = id.substring(0, id.indexOf("/"));
			    		screenView = ScreenResourceResolverInitializer.getScreenResourceResolver().getRootView(relativeScreenId, module, device);
			    	}
			    	else
			    	{
			    		screenView = ScreenResourceResolverInitializer.getScreenResourceResolver().getRootView(id, device);
			    	}
			    	
					if (screenView == null)
					{
						throw new ScreenConfigException("Screen ["+id+"] not found!");
					}
					screen = parseScreen(id, device, screenView);
					if(screen != null)
					{
						screenCache.put(cacheId, screen);
						saveScreenLastModified(cacheId, lastModified);
					}
				}
			}
			finally
			{
				screenLock.unlock();
			}
			return getFromCache(id, cacheId, lastModified);
			
		} 
		catch (Throwable e) 
		{
			throw new ScreenConfigException("Error retrieving screen ["+id+"].", e);
		}
	}

	/**
	 * @param id
	 * @param module
	 * @return
	 * @throws ScreenConfigException 
	 */
	public String getRelativeScreenId(String id, String module) throws ScreenConfigException
	{
		Module mod = Modules.getInstance().getModule(module);
		if (mod == null)
		{
			throw new ScreenConfigException("No module declared on screen ["+id+"].");
		}
		return Modules.getInstance().getRelativeScreenId(mod, id);
	}
	
	/**
	 * @param nodeList
	 * @return
	 * @throws ScreenConfigException
	 */
	public String getScreenModule(Document source) throws ScreenConfigException
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
	
	/**
	 * 
	 * @param id
	 * @param cacheId
	 * @param lastModified
	 * @return
	 */
	private Screen getFromCache(String id,String cacheId, long lastModified)
	{
		Screen screen = screenCache.get(cacheId);
		if (screen != null)
		{
			if (mustReprocessScreen(cacheId, lastModified))
			{
				screenCache.remove(cacheId);
				screen = null;
			}
		}
		return screen;
	}

	/**
	 * @param id
	 * @param lastModified
	 * @return
	 */
	private boolean mustReprocessScreen(String id, long lastModified)
	{
		if (lastModified == REPROCESS_RESOURCE)
		{
			return true;
		}
		if (lastModified == UNCHANGED_RESOURCE)
		{
			return false;
		}
		
		return (!screenLastModified.containsKey(id) || !screenLastModified.get(id).equals(lastModified));
	}

	/**
	 * @param id
	 * @param lastModified
	 */
	private void saveScreenLastModified(String id, long lastModified)
	{
	    if (id.toLowerCase().startsWith("file:"))
	    {
	    	screenLastModified.put(id, lastModified);
	    }
	    else
	    {
	    	screenLastModified.put(id, UNCHANGED_RESOURCE);
	    }
	}
	
	/**
	 * @param id
	 * @return
	 */
	private long getScreenLastModified(String id)
    {
	    if (id.toLowerCase().startsWith("file:"))
	    {
	    	try
            {
	            File screenFile = new File(new URL(id).toURI());
	            return screenFile.lastModified();
            }
            catch (Exception e)
            {
            	return REPROCESS_RESOURCE;
            }
	    }
	    
	    return UNCHANGED_RESOURCE;
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
			View rootView = ViewFactory.getInstance().getView(screenModule+"/"+relativeScreenId, device, screenView, true);
			
			screen = new Screen(id, relativeScreenId, screenModule, rootView);
		}
		else
		{
			throw new ScreenConfigException("No module declared on screen ["+id+"].");
		}
		
		return screen;
	}
}
