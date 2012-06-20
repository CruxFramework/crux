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
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.datasource.DataSources;
import org.cruxframework.crux.core.rebind.formatter.Formatters;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.core.utils.XMLUtils;
import org.cruxframework.crux.core.utils.XMLUtils.XMLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Factory for screens at the application's server side. It is necessary for GWT generators 
 * and for parameters binding.
 *  
 * @author Thiago Bustamante
 *
 */
public class ScreenFactory 
{
	private static ScreenFactory instance = new ScreenFactory();
	private static final Log logger = LogFactory.getLog(ScreenFactory.class);
	
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
			String cacheId = device==null?id:id+device;
			
			Screen screen = getFromCache(id, cacheId, lastModified);
			if (screen != null)
			{
				return screen;
			}

			InputStream stream = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenXMLResource(id, device);
			if (stream == null)
			{
				throw new ScreenConfigException("Screen ["+id+"] not found!");
			}
			
			screenLock.lock();
			try
			{
				if (getFromCache(id, cacheId, lastModified) == null)
				{
					screen = parseScreen(id, stream);
					if(screen != null)
					{
						screenCache.put(cacheId, screen);
						saveScreenLastModified(id, lastModified);
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
			if (mustReprocessScreen(id, lastModified))
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
	 * Creates a widget based in its &lt;span&gt; tag definition.
	 * 
	 * @param element
	 * @param screen
	 * @return
	 * @throws ScreenConfigException
	 */
	private Widget createWidget(JSONObject elem, Screen screen) throws ScreenConfigException
	{
		if (!elem.has("id"))
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+screen.getId()+"], there is an widget of type ["+elem.optString("_type")+"] without id.");
		}
		String widgetId;
        try
        {
	        widgetId = elem.getString("id");
        }
        catch (JSONException e)
        {
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+screen.getId()+"], there is an widget of type ["+elem.optString("_type")+"] without id.");
        }
		Widget widget = screen.getWidget(widgetId);
		if (widget != null)
		{
			throw new ScreenConfigException("Error creating widget. Duplicated identifier ["+widgetId+"].");
		}
		
		widget = newWidget(elem, widgetId);
		if (widget == null)
		{
			throw new ScreenConfigException("Can not create widget ["+widgetId+"]. Verify the widget type.");
		}
		
		screen.addWidget(widget);
		
		createWidgetChildren(elem, screen, widgetId, widget);
		
		return widget;
	}

	/**
	 * @param elem
	 * @param screen
	 * @param widgetId
	 * @param widget
	 * @throws ScreenConfigException
	 */
	private void createWidgetChildren(JSONObject elem, Screen screen, String widgetId, Widget widget) throws ScreenConfigException
    {
	    if (elem.has("_children"))
		{
			try
            {
	            JSONArray children = elem.getJSONArray("_children");
	            if (children != null)
	            {
	            	for (int i=0; i< children.length(); i++)
	            	{
	            		JSONObject childElem = children.getJSONObject(i);
	            		if (isValidWidget(childElem))
	            		{
	            			Widget child = createWidget(childElem, screen);
	            			child.setParent(widget);
	            		}
	            		else
	            		{
	            			createWidgetChildren(childElem, screen, widgetId, widget);
	            		}
	            	}
	            }
            }
			catch (JSONException e)
            {
				throw new ScreenConfigException("Can not create widget ["+widgetId+"]. Verify the widget type.", e);
            }
		}
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
	 * Test if a target json object represents a Screen definition for Crux.
	 * @param cruxObject
	 * @return
	 * @throws JSONException 
	 */
	private boolean isScreenDefinition(JSONObject cruxObject) throws JSONException
	{
		if (cruxObject.has("_type"))
		{
			String type = cruxObject.getString("_type");
			return (type != null && "screen".equals(type));
		}
		return false;
	}
	
	/**
	 * Test if a target json object represents a widget definition for Crux.
	 * @param cruxObject
	 * @return
	 * @throws JSONException
	 */
	public boolean isValidWidget(JSONObject cruxObject) throws JSONException
	{
		if (cruxObject.has("_type"))
		{
			String type = cruxObject.getString("_type");
			return (type != null && !"screen".equals(type));
		}
		return false;
	}
	
	
	/**
	 * Builds a new widget, based on its &lt;span&gt; tag definition.
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws ScreenConfigException
	 */
	private Widget newWidget(JSONObject elem, String widgetId) throws ScreenConfigException
	{
		try 
		{
			String type = elem.getString("_type");
			Widget widget = new Widget(elem);
			widget.setId(widgetId);
			widget.setType(type);
			return widget;
		} 
		catch (Throwable e) 
		{
			throw new ScreenConfigException("Can not create widget ["+widgetId+"]. Verify the widget type.", e);
		} 
	}
	
	/**
	 * Parse the HTML page and build the Crux Screen. 
	 * @param id
	 * @param stream
	 * @return
	 * @throws IOException
	 * @throws ScreenConfigException 
	 */
	private Screen parseScreen(String id, InputStream stream) throws IOException, ScreenConfigException
	{
		Screen screen = null;
		Document source = null;
		
		try
		{
			source = XMLUtils.createNSUnawareDocument(stream);
		}
		catch (XMLException e)
		{
			logger.error(e.getMessage(), e);
			throw new ScreenConfigException("Error parsing screen ["+id+"].", e);
		}

		String screenModule = getScreenModule(source);

		if(screenModule != null)
		{
			try
			{
				JSONObject metadata = getMetaData(source, id);
				JSONArray elementsMetaData = metadata.getJSONArray("elements");
				JSONObject lazyDependencies = metadata.getJSONObject("lazyDeps");

				screen = new Screen(id, getRelativeScreenId(id, screenModule), screenModule, elementsMetaData, lazyDependencies);

				int length = elementsMetaData.length();
				for (int i = 0; i < length; i++) 
				{
					JSONObject compCandidate = elementsMetaData.getJSONObject(i);

					if (isScreenDefinition(compCandidate))
					{
						parseScreenElement(screen,compCandidate);
					}
					else if (isValidWidget(compCandidate))
					{
						try 
						{
							createWidget(compCandidate, screen);
						} 
						catch (ScreenConfigException e) 
						{
							logger.error("Error creating widget ["+id+"].", e);
						}
					}
				}
			}
			catch (JSONException e)
			{
				throw new ScreenConfigException("Error parsing screen ["+id+"].", e);
			}
		}
		else
		{
			throw new ScreenConfigException("No module declared on screen ["+id+"].");
		}
		
		return screen;
	}

	/**
	 * @param source
	 * @return
	 * @throws JSONException
	 * @throws ScreenConfigException 
	 */
	private JSONObject getMetaData(Document source, String id) throws JSONException, ScreenConfigException
    {
		
		Element cruxMetaData = getCruxMetaDataElement(source);
		
	    String metaData = cruxMetaData.getTextContent();

	    if (metaData != null)
	    {
	    	int indexReturnFunction = metaData.indexOf("return ");
	    	int indexCloseFunction = metaData.lastIndexOf('}');
	    	
	    	metaData = metaData.substring(indexReturnFunction+7, indexCloseFunction).trim();
	    	
	    	JSONObject meta = new JSONObject(metaData);
	    	return meta;
	    }
	    
        throw new ScreenConfigException("Error parsing screen metaData. Screen ["+id+"].");
    }

	/**
	 * @param source
	 * @return
	 */
	private Element getCruxMetaDataElement(Document source)
    {
		NodeList nodeList = source.getElementsByTagName("script");
		for (int i=0; i< nodeList.getLength(); i++)
		{
			Element item = (Element) nodeList.item(i);
			String metaDataId = item.getAttribute("id");
			if (metaDataId != null && metaDataId.equals("__CruxMetaDataTag_"))
			{
				return item;
			}
		}
	    return null;
    }

	/**
	 * Parse screen element
	 * @param screen
	 * @param compCandidate
	 * @throws ScreenConfigException 
	 */
	private void parseScreenElement(Screen screen, JSONObject elem) throws ScreenConfigException 
	{
		try
        {
	        String[] attributes = JSONObject.getNames(elem);
	        int length = attributes.length;
	        
	        for (int i = 0; i < length; i++) 
	        {
	        	String attrName = attributes[i];
	        	
	        	if(attrName.equals("useController"))
	        	{
	        		parseScreenUseControllerAttribute(screen, elem);
	        	}
	        	else if(attrName.equals("useSerializable"))
	        	{
	        		parseScreenUseSerializableAttribute(screen, elem);
	        	}
	        	else if(attrName.equals("useFormatter"))
	        	{
	        		parseScreenUseFormatterAttribute(screen, elem);
	        	}
	        	else if(attrName.equals("useDataSource"))
	        	{
	        		parseScreenUseDatasourceAttribute(screen, elem);
	        	}
	        	else if (attrName.startsWith("on"))
	        	{
	        		Event event = EventFactory.getEvent(attrName, elem.getString(attrName));
	        		if (event != null)
	        		{
	        			screen.addEvent(event);
	        		}
	        	}
	        	else if (attrName.equals("title"))
	        	{
	        		String title = elem.getString(attrName);
	        		if (title != null && title.length() > 0)
	        		{
	        			screen.setTitle(title);
	        		}
	        	}
	        	else if (attrName.equals("fragment"))
	        	{
	        		String fragment = elem.getString(attrName);
	        		if (fragment != null && fragment.length() > 0)
	        		{
	        			screen.setFragment(fragment);
	        		}
	        	}
	        	else if (attrName.equals("enableTouchEventAdapters"))
	        	{
	        		String touchAdapters = elem.getString(attrName);
	        		if (touchAdapters != null && touchAdapters.length() > 0)
	        		{
	        			screen.setToucheEventAdaptersEnabled(Boolean.parseBoolean(touchAdapters));
	        		}
	        	}
	        	else if (attrName.equals("normalizeDeviceAspectRatio"))
	        	{
	        		String normalize = elem.getString(attrName);
	        		if (normalize != null && normalize.length() > 0)
	        		{
	        			screen.setNormalizeDeviceAspectRatio(Boolean.parseBoolean(normalize));
	        		}
	        	}
	        	else if (!attrName.equals("id") && !attrName.equals("_type"))
	        	{
	        		if (logger.isDebugEnabled()) logger.debug("Error setting property ["+attrName+"] for screen ["+screen.getId()+"].");
	        	}
	        }
        }
        catch (JSONException e)
        {
	        throw new ScreenConfigException("Error parsing screen metaData. Screen ["+screen.getId()+"].");
        }
	}

	/**
	 * @param screen
	 * @param attr
	 * @throws ScreenConfigException 
	 */
	private void parseScreenUseDatasourceAttribute(Screen screen, JSONObject elem) throws ScreenConfigException
    {
	    String datasourceStr;
        try
        {
        	datasourceStr = elem.getString("useDataSource");
        }
        catch (JSONException e)
        {
			throw new ScreenConfigException(e);
        }
	    if (datasourceStr != null)
	    {
	    	String[] datasources = RegexpPatterns.REGEXP_COMMA.split(datasourceStr);
	    	for (String datasource : datasources)
	    	{
	    		datasource = datasource.trim();
	    		if (!StringUtils.isEmpty(datasource))
	    		{
	    			if (DataSources.getDataSource(datasource) == null)
	    			{
	    				throw new ScreenConfigException("Datasource ["+datasource+"], declared on screen ["+screen.getId()+"], not found!");
	    			}
	    			screen.addDataSource(datasource);
	    		}
	    	}
	    }
    }

	/**
	 * @param screen
	 * @param attr
	 * @throws ScreenConfigException 
	 */
	private void parseScreenUseFormatterAttribute(Screen screen, JSONObject elem) throws ScreenConfigException
    {
	    String formatterStr;
        try
        {
        	formatterStr = elem.getString("useFormatter");
        }
        catch (JSONException e)
        {
			throw new ScreenConfigException(e);
        }
	    if (formatterStr != null)
	    {
	    	String[] formatters = RegexpPatterns.REGEXP_COMMA.split(formatterStr);
	    	for (String formatter : formatters)
	    	{
	    		formatter = formatter.trim();
	    		if (!StringUtils.isEmpty(formatter))
	    		{
	    			if (Formatters.getFormatter(formatter) == null)
	    			{
	    				throw new ScreenConfigException("Formatter ["+formatter+"], declared on screen ["+screen.getId()+"], not found!");
	    			}
	    			screen.addFormatter(formatter);
	    		}
	    	}
	    }
    }

	/**
	 * @param screen
	 * @param attr
	 * @throws ScreenConfigException 
	 */
	@SuppressWarnings("deprecation")
    private void parseScreenUseSerializableAttribute(Screen screen, JSONObject elem) throws ScreenConfigException
    {
	    String serializerStr;
        try
        {
	        serializerStr = elem.getString("useSerializable");
        }
        catch (JSONException e)
        {
			throw new ScreenConfigException(e);
        }
	    if (serializerStr != null)
	    {
	    	String[] serializers = RegexpPatterns.REGEXP_COMMA.split(serializerStr);
	    	for (String serializer : serializers)
	    	{
	    		serializer = serializer.trim();
	    		if (!StringUtils.isEmpty(serializer))
	    		{
	    			if (org.cruxframework.crux.core.rebind.serializable.Serializers.getCruxSerializable(serializer) == null)
	    			{
	    				throw new ScreenConfigException("Serializable ["+serializer+"], declared on screen ["+screen.getId()+"], not found!");
	    			}
	    			screen.addSerializer(serializer);
	    		}
	    	}
	    }
    }

	/**
	 * @param screen
	 * @param attr
	 * @throws ScreenConfigException 
	 */
	private void parseScreenUseControllerAttribute(Screen screen, JSONObject elem) throws ScreenConfigException
    {
	    String handlerStr;
        try
        {
	        handlerStr = elem.getString("useController");
        }
        catch (JSONException e)
        {
        	throw new ScreenConfigException(e);
        }
	    if (handlerStr != null)
	    {
	    	String[] handlers = RegexpPatterns.REGEXP_COMMA.split(handlerStr);
	    	for (String handler : handlers)
	    	{
	    		handler = handler.trim();
	    		if (!StringUtils.isEmpty(handler))
	    		{
	    			if (ClientControllers.getController(handler) == null)
	    			{
	    				throw new ScreenConfigException("Controller ["+handler+"], declared on screen ["+screen.getId()+"], not found!");
	    			}
	    			screen.addController(handler);
	    		}
	    	}
	    }
    }
}
