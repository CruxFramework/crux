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
package org.cruxframework.crux.core.client.screen.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.core.client.controller.RegisteredControllers;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.ioc.IocContainer;
import org.cruxframework.crux.core.client.resources.Resource;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class View implements HasViewResizeHandlers, HasWindowCloseHandlers, 
									  HasViewActivateHandlers, HasOrientationChangeHandler, 
									  HasViewLoadHandlers
{
	private static Logger logger = Logger.getLogger(View.class.getName());
	private static FastMap<View> loadedViews = new FastMap<View>();
	private static FastMap<ClientBundle> resources = new FastMap<ClientBundle>();
	private static int prefixCounter = 0;
	
	private String id;
	private String title;
	protected Map<String> lazyWidgets = null;
	protected FastMap<Widget> widgets = null;
	protected FastList<ResizeHandler> resizeHandlers = null;
	protected FastList<CloseHandler<Window>> windowCloseHandlers = null;
	protected FastList<ClosingHandler> windowClosingHandlers = null;
	protected FastList<ViewActivateHandler> attachHandlers = null;
	protected FastList<ViewDeactivateHandler> detachHandlers = null;
	protected FastList<ValueChangeHandler<String>> historyHandlers = null;
	protected FastList<OrientationChangeHandler> orientationHandlers = null;
	protected FastList<ViewLoadHandler> loadHandlers = null;
	protected FastList<ViewUnloadHandler> unloadHandlers = null;
	protected DataBindingHandler dataBindingHandler = null;
	protected boolean active = false;
	protected boolean loaded = false;
	protected String width;
	protected String height;
	
	
	private ViewContainer viewContainer;
	private String prefix;
	
	/**
	 * Constructor
	 * @param id
	 */
	public View(String id)
    {
		this.id = id;
		this.prefix = Integer.toString(prefixCounter++);
    }
	
	/**
	 * Retrieve the view identifier
	 * @return
	 */
	public String getId()
    {
    	return id;
    }
	
	/**
	 * Retrieve the view title
	 * @return
	 */
	public String getTitle()
    {
    	return title;
    }
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * Return true if the view was loaded into a container. 
	 * @return
	 */
	public boolean isLoaded()
	{
		return loaded;
	}
	
	/**
	 * Retrieve the view width;
	 * @return
	 */
	public String getWidth()
    {
    	return width;
    }

	/**
	 * Set the view width;
	 * @return
	 */
	public void setWidth(String width)
    {
    	this.width = width;
    	if (isActive() && !StringUtils.isEmpty(width))
    	{
    		updateViewWidth(width);
    	}
    }

	/**
	 * Retrieve the view height;
	 * @return
	 */
	public String getHeight()
    {
    	return height;
    }

	/**
	 * Set the views height;
	 * @return
	 */
	public void setHeight(String height)
    {
    	this.height = height;
    	if (isActive() && !StringUtils.isEmpty(height))
    	{
    		updateViewHeight(height);
    	}
    }

	protected abstract void updateViewHeight(String height);
	protected abstract void updateViewWidth(String width);

	/**
	 * Create a new DataSource instance
	 * @param dataSource dataSource name, declared with <code>@DataSource</code> annotation
	 * @return new dataSource instance
	 */
	public abstract DataSource<?> createDataSource(String dataSource);
	
	/**
	 * Add a new widget into this view
	 * @param id widget identifier
	 * @param widget the widget
	 */
	public void addWidget(String id, Widget widget)
	{
		widgets.put(id, widget);
	}

	/**
	 * Add a new widget into this view
	 * @param id widget identifier
	 * @param widget the widget
	 */
	public void addWidget(String id, IsWidget widget)
	{
		widgets.put(id, widget.asWidget());
	}
	
	/**
	 * Verify if the view contains an widget associated with the given identifier
	 * @param id widget identifier
	 * @return true if widget is present 
	 */
	public boolean containsWidget(String id)
	{
		return widgets.containsKey(id);
	}		

	/**
	 * Adds a new token for history control.
	 * @param token
	 */
	public static void addToHistory(String token)
	{
		History.newItem(token, false);
	}
	
	/**
	 * Adds a new token for history control.
	 * @param token
	 * @param issueEvent
	 */
	public static void addToHistory(String token, boolean issueEvent)
	{
		History.newItem(token, issueEvent);
	}

	/**
	 * Retrieve a widget contained on this view. If the the requested widget does not exists, we check if
	 * a request for a lazy creation of this widget was previously done. If so, we initialize the wrapper 
	 * required panel (according with {@code lazyWidgets} map) and try again.
	 * 
	 * @param id widget identifier
	 * @return the widget
	 */
	public Widget getWidget(String id)
	{
		assert(loaded):Crux.getMessages().viewNotInitialized(getId(), id);
		Widget widget = widgets.get(id);
		if (widget == null)
		{
			String lazyPanelId = lazyWidgets.get(id);
			if (lazyPanelId != null)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Found a lazy dependency. Widget["+id+"] depends on ["+lazyPanelId+"].");
				}
				if (initializeLazyDependentWidget(lazyPanelId))
				{
					widget = widgets.get(id);
					if (widget == null)
					{
						/*
						 * If a lazyPanel contains as child a panel that is not visible, the enclosing
						 * lazy panel of the child is only created when the external lazyPanel ensureWidget 
						 * method is called. It means that a new dependency was created during the initialization
						 * of the first panel. We must check for this situation and add this new dependency here.
						 */
						widget = getRuntimeDependencyWidget(id, lazyPanelId);
					}
				}
			}
		}
		return widget;
	}
	
	/**
	 * Retrieve the requested controller from this view
	 * @param <T> Controller type 
	 * @param controller Controller name
	 * @return
	 */
	public <T> T getController(String controller)
	{
		return getRegisteredControllers().getController(controller);
	}

	/**
	 * Retrieve the container that holds this view 
	 * @return Parent container or null if this view does not belong to any container.
	 */
	public ViewContainer getContainer()
	{
		return viewContainer;
	}
	
	/**
	 * Retrieve a widget contained on this screen. 
	 * 
	 * @param id widget identifier
	 * @param checkLazyDependencies if false, lazy dependencies will not be loaded
	 * @return the widget
	 */
	public Widget getWidget(String id, boolean checkLazyDependencies)
	{
		assert(loaded):Crux.getMessages().viewNotInitialized(getId(), id);
		if (checkLazyDependencies)
		{
			return getWidget(id);
		}
		else
		{
			return widgets.get(id);
		}
	}

	/**
	 * Retrieve a widget contained on this screen, casting it to the given class 
	 * 
	 * @param <T>
	 * @param id widget identifier
	 * @param clazz The class to be used to cast the widget
	 * @return the widget
	 */
	@SuppressWarnings("unchecked")
	public <T extends IsWidget> T getWidget(String id, Class<T> clazz)
	{
		assert(loaded):Crux.getMessages().viewNotInitialized(getId(), id);
		Widget w = getWidget(id);
		return (T) w;
	}
	
	/**
	 * Removes the given widget from this view.
	 * @param id widget identifier
	 */
	public void removeWidget(String id)
	{
		removeWidget(id, true);
	}

	/**
	 * Removes the given widget from this view.
	 * @param id widget identifier
	 * @param removeFromDOM if true, also removes the widget from this parent widget
	 */
	public void removeWidget(String id, boolean removeFromDOM)
	{
		Widget widget = widgets.remove(id);
		if (widget != null && removeFromDOM)
		{
			widget.removeFromParent();
		}
		dataBindingHandler.remove(id);
	}

	/**
	 * @return
	 */
	public FastList<String> listWidgetsId()
	{
		return widgets.keys();
	}		
	
	/**
	 * @return
	 */
	public FastList<Widget> listWidgets()
	{
		FastList<String> keys = widgets.keys();
		FastList<Widget> values = new FastList<Widget>();
		for (int i=0; i<keys.size(); i++)
		{
			values.add(widgets.get(keys.get(i)));
		}
		
		return values;
	}
	
	/**
	 * Reads the active property.
	 * @return
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	@Override
	public HandlerRegistration addResizeHandler(final ResizeHandler handler)
	{
		resizeHandlers.add(handler);
		if (isActive())
		{	
			ViewHandlers.ensureViewContainerResizeHandler(getContainer());
		}
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = resizeHandlers.indexOf(handler);
				if (index >= 0)
				{
					resizeHandlers.remove(index);
				}
			}
		};
	}
	
	/**
	 * @return true is this page supports orientationChange and false otherwise.
	 */
	public static native boolean isOrientationChangeSupported()/*-{
		return "onorientationchange" in $wnd || 
			   "orientationchange"   in $wnd ||
			   "ondeviceorientation" in $wnd ||
			   "deviceorientation"   in $wnd;
	}-*/;
	
	@Override
	@PartialSupport
	public HandlerRegistration addWindowOrientationChangeHandler(final OrientationChangeHandler handler)
	{
		if(!isOrientationChangeSupported())
		{
			return null;
		}
		
		orientationHandlers.add(handler);
		if (isActive())
		{	
			ViewHandlers.ensureViewContainerOrientationChangeHandler(getContainer());
		}
		
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = orientationHandlers.indexOf(handler);
				if (index >= 0)
				{
					orientationHandlers.remove(index);
				}
			}
		};
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	@Override
	public HandlerRegistration addWindowCloseHandler(final CloseHandler<Window> handler)
	{
		windowCloseHandlers.add(handler);
		if (isActive())
		{	
			ViewHandlers.ensureViewContainerCloseHandler(getContainer());
		}
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = windowCloseHandlers.indexOf(handler);
				if (index >= 0)
				{
					windowCloseHandlers.remove(index);
				}
			}
		};
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public HandlerRegistration addWindowClosingHandler(final ClosingHandler handler)
	{
		windowClosingHandlers.add(handler);
		if (isActive())
		{	
			ViewHandlers.ensureViewContainerClosingHandler(getContainer());
		}
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = windowClosingHandlers.indexOf(handler);
				if (index >= 0)
				{
					windowClosingHandlers.remove(index);
				}
			}
		};
	}
	
	/**
	 * 
	 */
	@Override
	public HandlerRegistration addViewActivateHandler(final ViewActivateHandler handler)
	{
		attachHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = attachHandlers.indexOf(handler);
				if (index >= 0)
				{
					attachHandlers.remove(index);
				}
			}
		};
	}
	
	/**
	 * 
	 */
	@Override
	public HandlerRegistration addViewDeactivateHandler(final ViewDeactivateHandler handler)
	{
		detachHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = detachHandlers.indexOf(handler);
				if (index >= 0)
				{
					detachHandlers.remove(index);
				}
			}
		};
	}

	/**
	 * 
	 */
	@Override
	public HandlerRegistration addViewLoadHandler(final ViewLoadHandler handler)
	{
		loadHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = loadHandlers.indexOf(handler);
				if (index >= 0)
				{
					loadHandlers.remove(index);
				}
			}
		};
	}
	
	/**
	 * 
	 */
	@Override
	public HandlerRegistration addViewUnloadHandler(final ViewUnloadHandler handler)
	{
		unloadHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = unloadHandlers.indexOf(handler);
				if (index >= 0)
				{
					unloadHandlers.remove(index);
				}
			}
		};
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public HandlerRegistration addWindowHistoryChangedHandler(final ValueChangeHandler<String> handler)
	{
		historyHandlers.add(handler);
		if (isActive())
		{	
			ViewHandlers.ensureViewContainerHistoryHandler(getContainer());
		}
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = historyHandlers.indexOf(handler);
				if (index >= 0)
				{
					historyHandlers.remove(index);
				}
			}
		};
	}

	/**
	 * Retrieve a list with all widgets identifiers present into this view
	 * @return
	 */
	public FastList<String> widgetsIdList()
	{
		return widgets.keys();
	}		
	
	/**
	 * Retrieve a list with all widgets present into this view
	 * @return
	 */
	public FastList<Widget> widgetsList()
	{
		FastList<String> keys = widgets.keys();
		FastList<Widget> values = new FastList<Widget>();
		for (int i=0; i<keys.size(); i++)
		{
			values.add(widgets.get(keys.get(i)));
		}
		
		return values;
	}
	
	/**
	 * Remove the current view from its container, if the view is loaded into a container
	 * @return true if the view is unloaded
	 */
	public boolean removeFromContainer()
	{
		if (viewContainer != null)
		{
			return viewContainer.remove(this);
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	protected boolean hasResizeHandlers()
	{
		return resizeHandlers.size() > 0;
	}
	
	/**
	 * 
	 * @return
	 */
	protected boolean hasOrientationChangeHandlers()
	{
		return orientationHandlers.size() > 0;
	}
	
	/**
	 * 
	 * @return
	 */
	protected boolean hasWindowCloseHandlers()
	{
		return windowCloseHandlers.size() > 0;
	}
	
	/**
	 * 
	 * @return
	 */
	protected boolean hasWindowClosingHandlers()
	{
		return windowClosingHandlers.size() > 0;
	}

	/**
	 * 
	 * @return
	 */
	protected boolean hasHistoryHandlers()
	{
		return historyHandlers.size() > 0;
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void fireResizeEvent(ResizeEvent event)
	{
		for (int i = 0; i < resizeHandlers.size(); i++)
        {
			ResizeHandler handler = resizeHandlers.get(i);
	        handler.onResize(event);
        }
	}

	/**
	 * 
	 */
	protected void fireOrientationEvent()
	{
		for (int i = 0; i < orientationHandlers.size(); i++)
        {
			OrientationChangeHandler handler = orientationHandlers.get(i);
	        handler.onOrientationChange();
        }
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void fireWindowCloseEvent(CloseEvent<Window> event)
	{
		for (int i = 0; i < windowCloseHandlers.size(); i++)
        {
			CloseHandler<Window> handler = windowCloseHandlers.get(i);
	        handler.onClose(event);
        }
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void fireWindowClosingEvent(ClosingEvent event)
	{
		fireUnloadEvent();
		for (int i = 0; i < windowClosingHandlers.size(); i++)
		{
			ClosingHandler handler = windowClosingHandlers.get(i);
			handler.onWindowClosing(event);
		}
	}

	/**
	 * 
	 * @param event
	 */
	protected void fireHistoryChangeEvent(ValueChangeEvent<String> event)
	{
		for (int i = 0; i < historyHandlers.size(); i++)
        {
			ValueChangeHandler<String> handler = historyHandlers.get(i);
	        handler.onValueChange(event);
        }
	}
	
	/**
	 * Fires the load event
	 */
	protected void fireLoadEvent(Object paramenter)
	{
		ViewLoadEvent event = new ViewLoadEvent(this, paramenter);
		for (int i = 0; i < loadHandlers.size(); i++)
        {
			ViewLoadHandler handler = loadHandlers.get(i);
			handler.onLoad(event);
        }
	}

	/**
	 * Fires the unload event.
	 * @return true if the view can be unloaded. If any event handler cancel the event, 
	 * the view is not unloaded
	 */
	protected boolean fireUnloadEvent()
	{
		boolean canceled = false;
		ViewUnloadEvent event = new ViewUnloadEvent(this);
		for (int i = 0; i < unloadHandlers.size(); i++)
        {
			ViewUnloadHandler handler = unloadHandlers.get(i);
			handler.onUnload(event);
			if (event.isCanceled())
			{
				canceled = true;
			}
        }
		
		return !canceled;
	}

	/**
	 * Mark this view as active
	 * @param parameter to be passed to activate event
	 */
	protected void setActive(Object parameter)
	{
		if (!active)
		{
			active = true;
			final ViewActivateEvent event = new ViewActivateEvent(this, this.getId(), parameter);
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				//IE Bug. It does not found attachHandlers reference when running on a setTimeout function
				private FastList<ViewActivateHandler> handlers = attachHandlers;
				
				@Override
				public void execute()
				{
					for (int i = 0; i < handlers.size(); i++)
					{
						ViewActivateHandler handler = handlers.get(i);
						handler.onActivate(event);
					}
				}
			});
		}
	}
	
	/**
	 * Mark this view as active
	 * @param skipEvent 
	 */
	protected boolean setDeactivated(boolean skipEvent)
	{
		if (active)
		{
			if (!skipEvent)
			{
				ViewDeactivateEvent event = new ViewDeactivateEvent(this, this.getId());
				for (int i = 0; i < detachHandlers.size(); i++)
				{
					ViewDeactivateHandler handler = detachHandlers.get(i);
					handler.onDeactivate(event);
				}
				active = event.isCanceled();
				return !active;
			}
			else
			{
				active = false;
			}
		}
		return true;
	}
	
	/**
	 * Called by the {@link ViewContainer} when the view is added to the container. 
	 * This method creates the view widgets
	 * @param parameter parameter sent to view and accessible through ViewLoadEvent event
	 */
	protected void load(Object paramenter)
	{
		if (!loaded)
		{
			prepareViewObjects();
			registerDataObjectBinders();
			registerLoadedView();
			createWidgets();
			loaded = true;
			fireLoadEvent(paramenter);
		}
	}
	
	/**
	 * Called by the {@link ViewContainer} when the view is removed from the container. 
	 * @return true if the view is not loaded
	 */
	protected boolean unload()
	{
		boolean unloaded = true;
		if (this.loaded)
		{
			unloaded = fireUnloadEvent();
			if (unloaded)
			{
				unregisterLoadedView();
				clearViewObjects();
				loaded = false;
			}
		}
		return unloaded;
	}
	
	/**
	 * Register current view into the loaded views list
	 */
	protected void registerLoadedView()
    {
		loadedViews.put(getId(), this);
    }

	/**
	 * Remove current view from the loaded views list
	 */
	protected void unregisterLoadedView()
    {
		loadedViews.remove(getId());
    }

	protected void prepareViewObjects()
	{
	    lazyWidgets = initializeLazyDependencies();
	    widgets = new FastMap<Widget>();
	    resizeHandlers = new FastList<ResizeHandler>();
	    windowCloseHandlers = new FastList<CloseHandler<Window>>();
	    windowClosingHandlers = new FastList<ClosingHandler>();
	    attachHandlers = new FastList<ViewActivateHandler>();
	    detachHandlers = new FastList<ViewDeactivateHandler>();
	    historyHandlers = new FastList<ValueChangeHandler<String>>();
	    orientationHandlers = new FastList<OrientationChangeHandler>();
	    loadHandlers = new FastList<ViewLoadHandler>();
	    unloadHandlers = new FastList<ViewUnloadHandler>();
	    dataBindingHandler = new DataBindingHandler(this);
	}
	
	/**
	 * When view is unloaded, we must free its allocated memory. 
	 */
	protected void clearViewObjects()
    {
		lazyWidgets = null;
	    widgets = null;
	    windowClosingHandlers = null;
	    windowCloseHandlers = null;
	    resizeHandlers = null;
	    attachHandlers = null;
	    detachHandlers = null;
	    historyHandlers = null;
	    orientationHandlers = null;
	    loadHandlers = null;
	    unloadHandlers = null;
	    dataBindingHandler = null;
    }
	
	/**
	 * @param widgetId
	 */
	protected void cleanLazyDependentWidgets(String widgetId)
	{
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Cleaning lazy dependencies of lazyPanel ["+widgetId+"]...");
		}
		FastList<String> dependentWidgets = getDependentWidgets(widgetId);
		for (int i=0; i<dependentWidgets.size(); i++)
		{
			lazyWidgets.remove(dependentWidgets.get(i));
		}
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Lazy dependencies of lazyPanel ["+widgetId+"] removed.");
		}
	}

	/**
	 * When we have multi-level inner lazy panels, the most inside panel is dependent from the most outside one.
	 * If the most outside is loaded, a new dependency must be created for the inner lazy panels not yet 
	 * loaded.
	 * @param id
	 * @param lazyPanelId
	 */
	protected void checkRuntimeLazyDependency(String id, String lazyPanelId)
	{
		if (!lazyWidgets.containsKey(id))
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.FINE, "New runtime lazy dependency found. Widget["+id+"] depends on LazyPanel["+lazyPanelId+"]...");
			}
			lazyWidgets.put(id, lazyPanelId);
		}
	}
	
	/**
	 * Call the {@code LazyPanel.ensureWidget()} method of the given lazyPanel.
	 * This method can trigger other dependent lazyPanel initialization, through 
	 * a recursive call to {@code View.getWidget(String)}.
	 * 
	 * @param widgetId lazyPanel to be loaded
	 * @return true if some lazyPanel was really loaded for this request
	 */
	protected boolean initializeLazyDependentWidget(String widgetId)
	{
		boolean ret = false;
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Initializing lazy dependents widgets of lazyPanel ["+widgetId+"]...");
		}

		LazyPanel lazyPanel = (LazyPanel) widgets.get(widgetId);
		if (lazyPanel == null)
		{
			//TODO: stackoverflow error when trying to access widgets on onLoad from a view (inside an HTMLPanel) 
			if (getWidget(ViewFactoryUtils.getWrappedWidgetIdFromLazyPanel(widgetId)) != null)
			{
				lazyPanel = (LazyPanel) widgets.get(widgetId);
			}
		}
		
		if (lazyPanel != null)
		{
			lazyPanel.ensureWidget();
			ret = true;
		}
		else
		{
			cleanLazyDependentWidgets(widgetId);
		}

		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, " Lazy dependents widgets of lazyPanel ["+widgetId+"] are now loaded.");
		}
		return ret;
	}
	
	
	/**
	 * If a lazyPanel contains as child a panel that is not visible, the enclosing
	 * lazy panel of the child is only created when the external lazyPanel ensureWidget 
	 * method is called. It means that a new dependency was created during the initialization
	 * of the first panel. We must check for this situation and add this new dependency here.
	 * 
	 * @param id
	 * @param lazyPanelId
	 * @return
	 */
	private Widget getRuntimeDependencyWidget(String id, String lazyPanelId)
    {
		Widget widget = null;
	    if (ViewFactoryUtils.isWholeWidgetLazyWrapper(lazyPanelId))  
	    {
	    	lazyPanelId = ViewFactoryUtils.getWrappedWidgetIdFromLazyPanel(lazyPanelId); 
	    	lazyPanelId = ViewFactoryUtils.getLazyPanelId(lazyPanelId, LazyPanelWrappingType.wrapChildren);
	    	if (widgets.containsKey(lazyPanelId))  
	    	{
	    		/* When the internal lazy dependency created is derived from a LazyPanelWrappingType.wrapChildren
	    	     lazy instantiation.*/
	    		lazyWidgets.put(id, lazyPanelId);
	    		widget = getWidget(id);
	    	}
	    	else
	    	{
	    		 /* Check if the internal lazy dependency created is derived from a LazyPanelWrappingType.wrapWholeWidget
	    		  lazy instantiation.*/
		    	widget = getRuntimeDependencyWidgetFromWholeWidget(id);
	    	}
	    }
	    else
	    {
	   		 /* Check if the internal lazy dependency created is derived from a LazyPanelWrappingType.wrapWholeWidget
	  		  lazy instantiation.*/
	    	widget = getRuntimeDependencyWidgetFromWholeWidget(id);
	    }
	    return widget;
    }
	
	/**
	 * @param widgetId
	 * @return
	 */
	private FastList<String> getDependentWidgets(String widgetId)
	{
		FastList<String> dependentWidgets = new FastList<String>();
		Array<String> keys = lazyWidgets.keys();
		int size = keys.size();
		for (int i=0; i<size; i++)
		{
			String key = keys.get(i);
			if (lazyWidgets.get(key).equals(widgetId))
			{
				dependentWidgets.add(key);
			}
		}
		return dependentWidgets;
	}
	
	/**
	 * When the internal lazy dependency created is derived from a LazyPanelWrappingType.wrapWholeWidget
	 * lazy instantiation.
	 * 
	 * @param id
	 * @return
	 */
	private Widget getRuntimeDependencyWidgetFromWholeWidget(String id)
    {
		Widget widget = null;
		String lazyPanelId;
	    lazyPanelId = ViewFactoryUtils.getLazyPanelId(id, LazyPanelWrappingType.wrapWholeWidget);
	    if (widgets.containsKey(lazyPanelId))  
	    {
	    	lazyWidgets.put(id, lazyPanelId);
	    	widget = getWidget(id);
	    }
	    return widget;
    }

	/**
	 * Retrieve the list of controllers registered into this view
	 * @return
	 */
	public abstract RegisteredControllers getRegisteredControllers();

	/**
	 * Retrieve the IoCContainer instance associated with this view
	 * @return
	 */
	public abstract IocContainer getIocContainer();
	
	/**
	 * Called by View container to render the view into the screen
	 * @param rootPanel The root element where the view elements will be rendered. 
	 */
	protected abstract void render(Panel rootPanel, RenderCallback renderCallback);

	/**
	 * Called by View container to create the view widgets 
     * 
	 */
	protected abstract void createWidgets() throws InterfaceConfigException;

	/**
	 *  Called when the view are loaded to initialize the lazy dependencies map
	 */
	protected abstract Map<String> initializeLazyDependencies();

	/**
	 * Called when the view are loaded to initialize the dataObjectBinders
	 */
	protected abstract void registerDataObjectBinders();

	/**
	 * Retrieve the view prefix. It is used to isolate all elements from this view on DOM
	 * @return
	 */
	protected String getPrefix()
	{
		return prefix;
	}

	/**
	 * Bind the view to a container. Called by the {@link ViewContainer} add method. 
	 * @param viewContainer
	 */
	protected void setContainer(ViewContainer viewContainer)
    {
		this.viewContainer = viewContainer;
    }
	
	/**
	 * Retrieve the view by its identifier
	 * @param id view identifier
	 * @return the view or null if there is no view loaded into the screen with this identifier
	 */
	@SuppressWarnings("unchecked")
    public static <T extends View> T getView(String id)
	{
		return (T) loadedViews.get(id);
	}
	
	/**
	 * Retrieve the current view associated with a controller, datasource, or other ViewAware object
	 * @param viewAware
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public static <T extends View> T of(Object viewAware)
	{
		assert (viewAware instanceof ViewAware): Crux.getMessages().viewOjectIsNotAwareOfView();
		return (T) ((ViewAware)viewAware).getBoundCruxView();
	}

	/**
	 * Retrieve the client bundle associated with the given id. To map a client bundle interface to an identifier, use
	 * the {@link Resource} annotation
	 * @param id
	 * @return
	 */
	public static ClientBundle getResource(String id)
	{
		return resources.get(id);
	}
	
	/**
	 * 
	 * @param id
	 * @param resource
	 */
	protected static void addResource(String id, ClientBundle resource)
	{
		resources.put(id, resource);
	}
	
	/**
	 * Returns true if a resource associated with the given identifiers was loaded by application
	 * @param id
	 * @return
	 */
	public static boolean containsResource(String id)
	{
		return resources.containsKey(id);
	}
	
	public static interface RenderCallback
	{
		void onRendered();
	}
	
	/**
	 * Write the given dataObject into this view. Any UI element bound to any property of this object 
	 * will be updated.
	 *   
	 * @param dataObject the data object to write
	 */
	public void write(Object dataObject)
	{
		dataBindingHandler.write(dataObject);
	}
	
	/**
	 * Write the given dataObjects into this view. Any UI element bound to any property of these objects 
	 * will be updated.
	 *   
	 * @param dataObjects the data objects to write
	 */
	public void writeAll(Object... dataObjects)
    {
		dataBindingHandler.writeAll(dataObjects);
    }
	
	/**
	 * Update the given dataObject with the values contained on UI elements of this View. 
	 * @param dataObject the object that will be updated
	 */
    public void copyTo(Object dataObject)
    {
		dataBindingHandler.copyTo(dataObject);
    }
    
    /**
     * Read the dataObject bound to the given class from this View. Its state is updated according to any 
     * value binding declaration to this dataObject   
     * @param dataObjectClass dataObject class
     * @return an updated dataObject
     */
    public <T> T read(Class<T> dataObjectClass)
    {
    	return dataBindingHandler.read(dataObjectClass);
    }

    /**
     * Read the dataObject bound to the given alias from this View. Its state is updated according to any 
     * value binding declaration to this dataObject   
     * @param dataObjectAlias dataObject alias
     * @return an updated dataObject
     */
    public <T> T read(String dataObjectAlias)
    {
    	return dataBindingHandler.read(dataObjectAlias);
    }
    
    protected void addDataObjectBinder(DataObjectBinder<?> dataObjectBinder, String dataObjectAlias)
    {
    	dataBindingHandler.addDataObjectBinder(dataObjectBinder, dataObjectAlias);
    }

    protected <T> DataObjectBinder<T> getDataObjectBinder(String dataObjectAlias)
    {
    	return dataBindingHandler.getDataObjectBinder(dataObjectAlias);
    }
    
    protected <T> DataObjectBinder<T> getDataObjectBinder(Class<T> dataObjectClass)
    {
    	return dataBindingHandler.getDataObjectBinder(dataObjectClass);
    }

}
