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
package br.com.sysmap.crux.core.rebind.screen;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a Crux Screen at the application's server side. Used for GWT Generators.
 * 
 * @author Thiago Bustamante
 */
public class Screen 
{
	protected String id;
	protected Map<String, Widget> widgets = new HashMap<String, Widget>();
	protected Map<String, Event> events = new HashMap<String, Event>();
	protected List<String> controllers = new ArrayList<String>();
	protected List<String> serializers = new ArrayList<String>();
	protected List<String> formatters = new ArrayList<String>();
	protected List<String> dataSources = new ArrayList<String>();
	
	protected String module;
	
	public Screen(String id, String module) 
	{
		this.id = id;
		this.module = module;
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
		}
	}
	
	/**
	 * Return screen identifier
	 * @return
	 */
	public String getId() 
	{
		return id;
	}
	
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
		if (controller != null)
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
		if (serializer != null)
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
		if (formatter != null)
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
		if (dataSource != null)
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
}
