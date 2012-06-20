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
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.dev.util.collect.HashSet;

/**
 * Represents a Crux Screen at the application's server side. Used for GWT Generators.
 * 
 * @author Thiago Bustamante
 */
public class Screen 
{
	protected String id;
	protected String relativeId;
	protected String fragment;
	protected Map<String, Widget> widgets = new HashMap<String, Widget>();
	protected Set<String> widgetTypes = new HashSet<String>();
	protected Map<String, Event> events = new HashMap<String, Event>();
	protected List<String> controllers = new ArrayList<String>();
	protected List<String> serializers = new ArrayList<String>();
	protected List<String> formatters = new ArrayList<String>();
	protected List<String> dataSources = new ArrayList<String>();
	protected String title;
	protected boolean toucheEventAdaptersEnabled = false;
	protected boolean normalizeDeviceAspectRatio = false;
	
	private ViewFactoryCreator factory = null;
	
	protected String module;
	private final JSONArray metaData;
	private final JSONObject lazyDependencies;
	
	public Screen(String id, String relativeId, String module, JSONArray metaData, JSONObject lazyDependencies) 
	{
		this.id = id;
		this.relativeId = relativeId;
		this.module = module;
		this.metaData = metaData;
		this.lazyDependencies = lazyDependencies;
	}

	/**
	 * Return DeclarativeFactory associated with the given id
	 * @param widgetId
	 * @return
	 */
	public Widget getWidget(String widgetId)
	{
		if (widgetId == null) return null;
		return widgets.get(widgetId);
	}

	/**
	 * Return a Set containing all types of widgets found on this screen
	 * @return
	 */
	public Set<String> getWidgetTypesIncluded()
	{
		return widgetTypes;
	}
	
	/**
	 * Iterate over widgets
	 * @return
	 */
	public Iterator<Widget> iterateWidgets() 
	{
		return widgets.values().iterator();
	}

	/**
	 * Add a new widget to screen
	 * @param widget
	 */
	protected void addWidget(Widget widget)
	{
		if (widget != null)
		{
			widgets.put(widget.getId(), widget);
			if (!widgetTypes.contains(widget.getType()))
			{
				widgetTypes.add(widget.getType());
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTouchEventAdaptersEnabled()
	{
		return this.toucheEventAdaptersEnabled;
	}
	
	/**
	 * 
	 * @param toucheEventAdaptersEnabled
	 */
	public void setToucheEventAdaptersEnabled(boolean toucheEventAdaptersEnabled)
	{
		this.toucheEventAdaptersEnabled = toucheEventAdaptersEnabled;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isNormalizeDeviceAspectRatio()
    {
    	return normalizeDeviceAspectRatio;
    }

	/**
	 * 
	 * @param normalizeDeviceAspectRatio
	 */
	public void setNormalizeDeviceAspectRatio(boolean normalizeDeviceAspectRatio)
    {
    	this.normalizeDeviceAspectRatio = normalizeDeviceAspectRatio;
    }

	/**
	 * Return screen identifier
	 * @return
	 */
	public String getId() 
	{
		return id;
	}
	
	/**
	 * @return
	 */
	public String getRelativeId()
	{
		return relativeId;
	}
	
	/**
	 * @return
	 */
	public String getModule()
	{
		return module;
	}

	/**
	 * Add a new event to screen
	 * @param event
	 */
	protected void addEvent(Event event)
	{
		if (event != null)
		{
			events.put(event.getId(), event);
		}
	}
	
	/**
	 * Return a event associated with the given id
	 * @param evtId
	 * @return
	 */
	public Event getEvent(String evtId)
	{
		return events.get(evtId);
	}
	
	/**
	 * Iterate over screen events
	 * @return
	 */
	public Iterator<Event> iterateEvents()
	{
		return events.values().iterator();
	}

	/**
	 * Import a controller into screen
	 * @param event
	 */
	protected void addController(String controller)
	{
		if (!StringUtils.isEmpty(controller))
		{
			controllers.add(controller);
		}
	}
	
	/**
	 * Iterate over screen controllers
	 * @return
	 */
	public Iterator<String> iterateControllers()
	{
		return controllers.iterator();
	}

	/**
	 * Import a serializer for a CruxSerializable into screen
	 * @param event
	 */
	protected void addSerializer(String serializer)
	{
		if (!StringUtils.isEmpty(serializer))
		{
			serializers.add(serializer);
		}
	}
	
	/**
	 * Iterate over screen serializers
	 * @return
	 */
	public Iterator<String> iterateSerializers()
	{
		return serializers.iterator();
	}
	
	/**
	 * Import a formatter into screen
	 * @param event
	 */
	protected void addFormatter(String formatter)
	{
		if (!StringUtils.isEmpty(formatter))
		{
			formatters.add(formatter);
		}
	}
	
	/**
	 * Iterate over screen formatters
	 * @return
	 */
	public Iterator<String> iterateFormatters()
	{
		return formatters.iterator();
	}	

	/**
	 * Import a dataSource into screen
	 * @param event
	 */
	protected void addDataSource(String dataSource)
	{
		if (!StringUtils.isEmpty(dataSource))
		{
			dataSources.add(dataSource);
		}
	}
	
	/**
	 * Iterate over screen dataSources
	 * @return
	 */
	public Iterator<String> iterateDataSources()
	{
		return dataSources.iterator();
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public JSONArray getMetaData()
    {
    	return metaData;
    }

	public JSONObject getLazyDependencies()
	{
		return lazyDependencies;
	}
	
	public ViewFactoryCreator getFactory() {
		return factory;
	}

	public void setFactory(ViewFactoryCreator factory) {
		this.factory = factory;
	}

	public String getFragment()
    {
	    return this.fragment;
    }

	protected void setFragment(String fragment)
    {
    	this.fragment = fragment;
    }
	
}
