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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.core.declarativeui.template.Templates;
import org.cruxframework.crux.core.declarativeui.view.Views;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.datasource.DataSources;
import org.cruxframework.crux.core.rebind.formatter.Formatters;
import org.cruxframework.crux.core.rebind.resources.Resources;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.EventFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.scannotation.URLStreamManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;


/**
 * Creates a representation for Crux views
 *  
 * @author Thiago Bustamante
 *
 */
@Legacy(value=ViewFactory.class)
public class ViewFactoryLegacy 
{
	/**
	 * Parse view element
	 * @param view
	 * @param elem
	 * @throws ScreenConfigException 
	 */
	private void parseViewElement(View view, JSONObject elem) throws ScreenConfigException 
	{
		try
        {
	        view.setViewElement(elem);
			String[] attributes = JSONObject.getNames(elem);
	        int length = attributes.length;
	        
	        for (int i = 0; i < length; i++) 
	        {
	        	String attrName = attributes[i];
	        	
	        	if(attrName.equals("useController"))
	        	{
	        		parseViewUseControllerAttribute(view, elem);
	        	}
	        	else if(attrName.equals("useResource"))
	        	{
	        		parseViewUseResourceAttribute(view, elem);
	        	}
	        	else if(attrName.equals("useSerializable"))
	        	{
	        		parseViewUseSerializableAttribute(view, elem);
	        	}
	        	else if(attrName.equals("useFormatter"))
	        	{
	        		parseViewUseFormatterAttribute(view, elem);
	        	}
	        	else if(attrName.equals("useDataSource"))
	        	{
	        		parseViewUseDatasourceAttribute(view, elem);
	        	}
	        	else if(attrName.equals("useView"))
	        	{
	        		parseViewUseViewAttribute(view, elem);
	        	}
	        	else if (attrName.equals("width"))
				{
					view.setWidth(elem.getString(attrName));
				}
	        	else if(attrName.equals("height"))
				{
					view.setHeight(elem.getString(attrName));
				}
	        	else if(attrName.equals("smallViewport"))
				{
					view.setSmallViewport(elem.getString(attrName));
				}
	        	else if(attrName.equals("largeViewport"))
				{
					view.setLargeViewport(elem.getString(attrName));
				}
	        	else if(attrName.equals("disableRefresh"))
				{
					view.setDisableRefresh(elem.getBoolean(attrName));
				}
	        	else if (attrName.startsWith("on"))
	        	{
	        		Event event = EventFactory.getEvent(attrName, elem.getString(attrName));
	        		if (event != null)
	        		{
	        			view.addEvent(event);
	        		}
	        	}
	        	else if (attrName.equals("title"))
	        	{
	        		String title = elem.getString(attrName);
	        		if (title != null && title.length() > 0)
	        		{
	        			view.setTitle(title);
	        		}
	        	}
	        	else if (attrName.equals("fragment"))
	        	{
	        		String fragment = elem.getString(attrName);
	        		if (fragment != null && fragment.length() > 0)
	        		{
	        			view.setFragment(fragment);
	        		}
	        	}
	        	else if (attrName.equals("dataObject"))
	        	{
	        		String dataObject = elem.getString(attrName);
	        		if (dataObject != null && dataObject.length() > 0)
	        		{
	        			view.setDataObject(dataObject);
	        		}
	        	}
	        	else if (!attrName.equals("id") && !attrName.equals("_type"))
	        	{
	        		if (logger.isInfoEnabled()) logger.info("Error setting property ["+attrName+"] for view ["+view.getId()+"].");
	        	}
	        }
        }
        catch (JSONException e)
        {
	        throw new ScreenConfigException("Error parsing view metaData. View ["+view.getId()+"].");
        }
	}

	@SuppressWarnings("deprecation")
    private void parseViewUseSerializableAttribute(View view, JSONObject elem) throws ScreenConfigException
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
	    				throw new ScreenConfigException("Serializable ["+serializer+"], declared on view ["+view.getId()+"], not found!");
	    			}
	    			view.addSerializer(serializer);
	    		}
	    	}
	    }
    }

}
