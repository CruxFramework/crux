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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.json.JSONObject;

/**
 * Represents a Crux View at the application's server side. Used for GWT Generators.
 * 
 * @author Thiago Bustamante
 */
public class View 
{
	protected List<String> controllers = new ArrayList<String>();
	protected String dataObject;
	protected Map<String, DataProvider> dataProviders = new HashMap<String, DataProvider>();
	@Deprecated
	@Legacy
	protected List<String> dataSources = new ArrayList<String>();
	protected boolean disableRefresh;
	protected Map<String, Event> events = new HashMap<String, Event>();
	protected List<String> formatters = new ArrayList<String>();
	protected String fragment;
	protected String height;
	protected String id;
	protected String largeViewport;
	protected Set<NativeControllerCall> nativeControllers = new HashSet<NativeControllerCall>();
	protected Set<NativeDataBinding> nativeBindings = new HashSet<NativeDataBinding>();
	protected List<String> resources = new ArrayList<String>();
	@Deprecated
	@Legacy
	protected List<String> serializers = new ArrayList<String>();
	protected String smallViewport;
	protected String title;
	protected List<String> views = new ArrayList<String>();
	protected Map<String, Widget> widgets = new HashMap<String, Widget>();
	protected Set<String> widgetTypes = new HashSet<String>();
	protected String width;

	private final String html;
	private long lastModified;
	private final JSONObject lazyDependencies;
	private final boolean rootView;
	private JSONObject viewElement;

	public View(String id, JSONObject lazyDependencies, String html, boolean rootView) 
	{
		this.id = id;
		this.lazyDependencies = lazyDependencies;
		this.html = html;
		this.rootView = rootView;
		if (rootView)
		{
			disableRefresh = ConfigurationFactory.getConfigurations().disableRefreshByDefault().equals("true");
		}
	}

	/**
	 * DataObject bound to this view.
	 * @return
	 */
	@Deprecated
	public String getDataObject()
    {
    	return dataObject;
    }

	public DataProvider getDataProvider(String id)
	{
		if (id == null) return null;
		return dataProviders.get(id);
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
	 * Fragment name, used for code spliting
	 * @return
	 */
	public String getFragment()
    {
	    return this.fragment;
    }

	/**
	 * Retrieve the view height
	 * @return
	 */
	public String getHeight()
    {
    	return height;
    }

	/**
	 * 
	 * @return
	 */
	public String getHtml()
	{
		return html;
	}

	/**
	 * Return view identifier
	 * @return
	 */
	public String getId() 
	{
		return id;
	}
	
	/**
	 * Viewport for large devices
	 * 
	 * @return
	 */
	public String getLargeViewport()
    {
    	return largeViewport;
    }
	
	public long getLastModified()
	{
		return lastModified;
	}
	
	/**
	 * Return the lazy dependencies metadata
	 * @return
	 */
	public JSONObject getLazyDependencies()
	{
		return lazyDependencies;
	}
	
	/**
	 * Viewport for small devices
	 * 
	 * @return
	 */
	public String getSmallViewport()
    {
    	return smallViewport;
    }

	/**
	 * Return the view title
	 * @return
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * 
	 * @return
	 */
	public JSONObject getViewElement()
    {
    	return viewElement;
    }
	
	/**
	 * Return DeclarativeFactory associated to the given id
	 * @param widgetId
	 * @return
	 */
	public Widget getWidget(String widgetId)
	{
		if (widgetId == null) return null;
		return widgets.get(widgetId);
	}
	
	/**
	 * Return a Set containing all types of widgets found on this view
	 * @return
	 */
	public Set<String> getWidgetTypesIncluded()
	{
		return widgetTypes;
	}

	/**
	 * Retrieve the view width
	 * @return
	 */
	public String getWidth()
    {
    	return width;
    }
	
	/**
	 * 
	 * @return
	 */
	public boolean isDisableRefresh()
    {
    	return disableRefresh;
    }
	
	/**
	 * 
	 * @return
	 */
	public boolean isRootView()
	{
		return rootView;
	}
	
	/**
	 * Iterate over view controllers
	 * @return
	 */
	public Iterator<String> iterateControllers()
	{
		return controllers.iterator();
	}
	
	public Iterator<DataProvider> iterateDataProviders()
	{
		return dataProviders.values().iterator();
	}
	
	/**
	 * Iterate over view dataSources
	 * @return
	 */
	public Iterator<String> iterateDataSources()
	{
		return dataSources.iterator();
	}
	
	/**
	 * Iterate over view events
	 * @return
	 */
	public Iterator<Event> iterateEvents()
	{
		return events.values().iterator();
	}	

	/**
	 * Iterate over view formatters
	 * @return
	 */
	public Iterator<String> iterateFormatters()
	{
		return formatters.iterator();
	}
	
	/**
	 * Iterate over native controller method calls
	 * @return
	 */
	public Iterator<NativeControllerCall> iterateNativeControllerCalls()
	{
		return nativeControllers.iterator();
	}
	
	/**
	 * Iterate over native native binding declarations
	 * @return
	 */
	public Iterator<NativeDataBinding> iterateNativeDataBindings()
	{
		return nativeBindings.iterator();
	}

	/**
	 * Iterate over view resources
	 * @return
	 */
	public Iterator<String> iterateResources()
	{
		return resources.iterator();
	}
	
	/**
	 * Iterate over view serializers
	 * @return
	 */
	public Iterator<String> iterateSerializers()
	{
		return serializers.iterator();
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
	 * Set the view height
	 * @param height
	 */
	public void setHeight(String height)
    {
    	this.height = height;
    }

	/**
	 * Sets the view title
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Set the view width
	 * @param width
	 */
	public void setWidth(String width)
    {
    	this.width = width;
    }

	/**
	 * 
	 * @param controller
	 * @return
	 */
	public boolean useController(String controller)
	{
		return controllers.contains(controller);
	}

	/**
	 * 
	 * @param datasource
	 * @return
	 */
	public boolean useDataSource(String datasource)
	{
		return dataSources.contains(datasource);
	}

	/**
	 * 
	 * @param formatter
	 * @return
	 */
	public boolean useFormatter(String formatter)
	{
		return formatters.contains(formatter);
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	public boolean useResource(String resource)
	{
		return resources.contains(resource);
	}

	/**
	 * Import a controller into view
	 * @param event
	 */
	protected void addController(String controller)
	{
		if (!StringUtils.isEmpty(controller))
		{
			controllers.add(controller);
		}
	}

	protected void addDataProvider(DataProvider dataProvider)
	{
		if (dataProvider != null)
		{
			dataProviders.put(dataProvider.getId(), dataProvider);
		}
	}

	/**
	 * Import a dataSource into view
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
	 * Add a new event to view
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
	 * Import a formatter into view
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
	 * Register a native controller call
	 * @param method
	 * @param controllerCall
	 */
	protected void addNativeControllerCall(String method, String controllerCall)
	{
		nativeControllers.add(new NativeControllerCall(method, controllerCall));
	}

	/**
	 * Register a native databinding declaration
	 * @param elementId
	 * @param dataBinding
	 * @param attributeName
	 */
	protected void addNativeDataBinding(String elementId, String dataBinding, String attributeName)
    {
		nativeBindings.add(new NativeDataBinding(elementId, dataBinding, attributeName));
    }
	
	/**
	 * Import a resources into view
	 * @param event
	 */
	protected void addResource(String resource)
	{
		if (!StringUtils.isEmpty(resource))
		{
			resources.add(resource);
		}
	}
	
	/**
	 * Import a serializer for a CruxSerializable into view
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
	 * Add a new widget to view
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
	 * DataObject bound to this view.
	 * @param dataObject
	 */
	protected void setDataObject(String dataObject)
    {
    	this.dataObject = dataObject;
    }
	
	/**
	 * 
	 * @param disableRefresh
	 */
	protected void setDisableRefresh(boolean disableRefresh)
    {
    	this.disableRefresh = disableRefresh;
    }

	/**
	 * Fragment name, used for code spliting
	 * @param fragment
	 */
	protected void setFragment(String fragment)
    {
    	this.fragment = fragment;
    }

	/**
	 * Viewport for large devices
	 * @param largeViewport
	 */
	protected void setLargeViewport(String largeViewport)
    {
    	this.largeViewport = largeViewport;
    }

	/**
	 * Viewport for small devices
	 * @param smallViewport
	 */
	protected void setSmallViewport(String smallViewport)
    {
    	this.smallViewport = smallViewport;
    }
	
	void setLastModified(long lastModified)
    {
		this.lastModified = lastModified;
    }
	
	/**
	 * 
	 * @param viewElement
	 */
	void setViewElement(JSONObject viewElement)
    {
    	this.viewElement = viewElement;
    }

	/**
	 * Represents a native call to a view controller method
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class NativeControllerCall
	{
		private final String controllerCall;
		private final String method;
		
		public NativeControllerCall(String method, String controllerCall)
		{
			this.method = method;
			this.controllerCall = controllerCall;
		}

		public String getControllerCall()
		{
			return controllerCall;
		}

		public String getMethod()
		{
			return method;
		}
	}
	
	public static class NativeDataBinding
	{
		private final String elementId;
		private final String binding;
		private final String attributeName;

		public NativeDataBinding(String elementId, String dataBinding, String attributeName)
        {
			this.elementId = elementId;
			this.binding = dataBinding;
			this.attributeName = attributeName;
        }

		public String getElementId()
		{
			return elementId;
		}

		public String getBinding()
		{
			return binding;
		}

		public String getAttributeName()
		{
			return attributeName;
		}
	}
	
}
