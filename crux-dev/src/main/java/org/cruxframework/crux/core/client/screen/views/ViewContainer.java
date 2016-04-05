/*
 * Copyright 2014 cruxframework.org.
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

import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.views.View.RenderCallback;
import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ViewContainer extends Composite implements HasViewActivateHandlers, HasViewLoadHandlers
{
	protected static Logger logger = Logger.getLogger(ViewContainer.class.getName());	
	private static ViewFactory viewFactory;
	
	protected Array<ViewActivateHandler> attachHandlers = null;
	protected Array<ViewDeactivateHandler> detachHandlers = null;
	protected Array<ViewLoadHandler> loadHandlers = null;
	protected Array<ViewUnloadHandler> unloadHandlers = null;
	protected Map<View> views = CollectionFactory.createMap();
	private final boolean clearPanelsForDeactivatedViews;

	private final Widget mainWidget;

	/**
	 * Constructor
	 * @param mainWidget main widget on this container
	 */
	public ViewContainer(Widget mainWidget)
    {
		this(mainWidget, true);
    }

	/**
	 * Constructor
	 * @param mainWidget Main widget on this container
	 * @param clearPanelsForDeactivatedViews If true, makes the container clear the container panel for a view, when the view is deactivated.
	 */
	public ViewContainer(Widget mainWidget, boolean clearPanelsForDeactivatedViews)
    {
		this.mainWidget = mainWidget;
		if (mainWidget != null)
		{
			this.mainWidget.addAttachHandler(new Handler()
			{
				@Override
				public void onAttachOrDetach(AttachEvent event)
				{
					if (event.isAttached())
					{
						bindToDOM();
					}
					else
					{
						unbindToDOM();
					}
				}
			});
			initWidget(this.mainWidget);
		}
		
		this.clearPanelsForDeactivatedViews = clearPanelsForDeactivatedViews;
		ViewHandlers.initializeWindowContainers();
    }
	
	/**
	 * Loads a new view into the container
	 * @param view View to be added
	 * @return
	 */
	public boolean add(View view)
	{
		return add(view, false, null);
	}
	
	/**
	 * Adds a new view into the container, but does not load the view. 
	 * @param viewName Name of the View to be added
	 * @return
	 */
	public boolean addLazy(String viewName)
	{
		return addLazy(viewName, viewName);
	}

	/**
	 * Adds a new view into the container, but does not load the view. 
	 * @param viewName Name of the View to be added
	 * @param viewId ID of the View to be added
	 * @return
	 */
	public boolean addLazy(String viewName, String viewId)
	{
		createView(viewName, viewId, new CreateCallback()
		{
			@Override
            public void onViewCreated(View view)
            {
				addView(view, true, null);
            }
		});
		return true;
	}

	/**
	 * Adds a new view into the container, but does not load the view. 
	 * @param view View to be added
	 * @return
	 */
	public boolean addLazy(View view)
	{
		return addView(view, true, null);
	}
	
	@Override
	public HandlerRegistration addViewActivateHandler(final ViewActivateHandler handler)
	{
		if (attachHandlers == null)
		{
			attachHandlers = CollectionFactory.createArray();
		}
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

	@Override
	public HandlerRegistration addViewDeactivateHandler(final ViewDeactivateHandler handler)
	{
		if (detachHandlers == null)
		{
			detachHandlers = CollectionFactory.createArray();
		}
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
	
	@Override
	public HandlerRegistration addViewLoadHandler(final ViewLoadHandler handler)
	{
		if (loadHandlers == null)
		{
			loadHandlers = CollectionFactory.createArray();
		}
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
	
	
	@Override
	public HandlerRegistration addViewUnloadHandler(final ViewUnloadHandler handler)
	{
		if (unloadHandlers == null)
		{
			unloadHandlers = CollectionFactory.createArray();
		}
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
	 * Remove all view inside this container
	 */
	public void clear()
	{
		Array<String> keys = views.keys();
		for (int i=0; i< keys.size(); i++)
		{
			remove(getView(keys.get(i)), true);
		}
	}

	/**
	 * Retrieve the view associated to viewId
	 * @param viewId View identifier
	 * @return The view
	 */
    public View getView(String viewId)
    {
		if (viewId == null)
		{
			return null;
		}
	    return views.get(viewId);
    }
	
	/**
	 * Loads a view into the current container
	 * 
	 * @param viewName View name
	 * @param render If true also render the view
	 */
	public void loadView(String viewName, final boolean render)
	{
		loadView(viewName, viewName, render);
	}
	
	/**
	 * Loads a view into the current container
	 * 
	 * @param viewName View name
	 * @param viewId View identifier
	 * @param render If true also render the view
	 */
	public void loadView(final String viewName, final String viewId, final boolean render)
	{
		loadView(viewName, viewId, render, null);
	}
	
	/**
	 * Remove the view from this container
	 * @param view View to be removed
	 * @return
	 */
	public boolean remove(View view)
	{
		return remove(view, false);
	}
	
	/**
	 * Remove the view from this container
	 * @param view View to be removed
	 * @param skipEvents skip the events fired during view removal
	 * @return
	 */
	public boolean remove(View view, boolean skipEvents)
	{
		assert (view != null):"Can not remove a null view from the ViewContainer";
		if (doRemove(view, skipEvents))
		{
			view.setContainer(null);
			return true;
		}
		return false;
	}
	
	/**
     * Render the requested view into the container.
     * @param viewName View name
     */
	public void showView(String viewName)
	{
		showView(viewName, viewName);
	}

    /**
     * Render the requested view into the container.
     * @param viewId View identifier
	 * @param viewId View name
	 */
	public void showView(String viewName, String viewId)
	{
		showView(viewName, viewId, null);
	}

	/**
	 * This method must be called by subclasses when any of your views is rendered.
	 * @param view
	 * @param containerPanel
	 * @param parameter
	 */
	protected boolean activate(final View view, Panel containerPanel, final Object parameter)
	{
		if (!view.isLoaded())
		{
			if (view.load(parameter))
			{
				fireLoadEvent(view, parameter);
			}
		}
		view.render(containerPanel, new RenderCallback()
		{
			@Override
			public void onRendered()
			{
				view.setActive(parameter);
			}
		});
		return true;
	}
	
	/**
	 * Loads a new view into the container
	 * @param view View to be added
	 * @param render If true, call the render method
	 * @param parameter A parameter passed that will be bound to the view load and activate events
	 * @return true if the view is loaded into the container
	 */
    protected boolean add(View view, boolean render, Object parameter)
    {
		if (addView(view, false, parameter))
		{
			if (render)
			{
				renderView(view, parameter);
			}
			return true;
		}
		return false;
    }

	/**
     * 
     * @param view
     * @param lazy
     * @return
     */
    protected boolean addView(View view, boolean lazy, Object parameter)
    {
	    assert (view != null):"Can not add a null view to the ViewContainer";
		assert (getView(view.getId()) == null):"This container already contains a view with the given identifier ["+view.getId()+"].";
		if (doAdd(view, lazy, parameter))
		{
			adoptView(view);
			return true;
		}
		return false;
    }

	/**
     * 
     * @param view
     */
    protected void adoptView(View view)
    {
    	view.setContainer(this);
    }

	/**
	 * This method must be called by subclasses when the container is attached to DOM
	 */
	protected void bindToDOM()
	{
		ViewHandlers.bindToDOM(this);
	}

	/**
	 * This method must be called by subclasses when any of your views currently rendered is removed from view.
	 * 
	 * @param view
	 * @param containerPanel
	 * @param skipEvent
	 * @return True if view is deactivated
	 */
	protected boolean deactivate(View view, Panel containerPanel, boolean skipEvent)
    {
		if (view.isActive())
		{
			if (view.setDeactivated(skipEvent))
			{
				if (this.clearPanelsForDeactivatedViews)
				{
					containerPanel.clear();
				}
				return true;
			}
			return false;
		}
		return true;
    }

	/**
	 * Loads a new view into the container
	 * @param view View to be added
	 * @return
	 */
    protected boolean doAdd(View view, boolean lazy, Object parameter)
    {
		if (!views.containsKey(view.getId()))
		{
			views.put(view.getId(), view);
			if (!lazy)
			{
				if (view.load(parameter))
				{
					fireLoadEvent(view, parameter);
				}
			}
			return true;
		}
		return false;
    }
	
	/**
     * 
     * @param view
     * @param skipEvent
     * @return
     */
	protected boolean doRemove(View view, boolean skipEvent)
    {
	    if (views.containsKey(view.getId()))
		{
			Panel containerPanel = getContainerPanel(view);
			boolean active = view.isActive();
			if (deactivate(view, containerPanel, skipEvent) && (skipEvent || view.unload()))
			{
				views.remove(view.getId());
				if (active)
				{
					containerPanel.clear();
				}
				return true;
			}
		}
		return false;
    }
	
	/**
	 * Fire the activate event
	 * @param event
	 */
	protected void fireActivateEvent(ViewActivateEvent event)
    {
		if (attachHandlers != null)
		{
			for (int i = 0; i < attachHandlers.size(); i++)
			{
				ViewActivateHandler handler = attachHandlers.get(i);
				handler.onActivate(event);
			}
		}
    }

    /**
	 * Fire the deactivate event
	 * @param event
	 */
	protected void fireDeactivateEvent(ViewDeactivateEvent event)
    {
		if (detachHandlers != null)
		{
			for (int i = 0; i < detachHandlers.size(); i++)
			{
				ViewDeactivateHandler handler = detachHandlers.get(i);
				handler.onDeactivate(event);
			}
		}
    }
    
	/**
	 * Fires the load event
	 */
	protected void fireLoadEvent(View view, Object parameter)
	{
		if (loadHandlers != null)
		{
			ViewLoadEvent event = new ViewLoadEvent(view, parameter); 
			for (int i = 0; i < loadHandlers.size(); i++)
			{
				ViewLoadHandler handler = loadHandlers.get(i);
				handler.onLoad(event);
			}
		}
	}

	/**
	 * Fires the unload event.
	 * @return true if the view can be unloaded. If any event handler cancel the event, 
	 * the view is not unloaded
	 */
	protected boolean fireUnloadEvent(ViewUnloadEvent event)
	{
		boolean canceled = false;
		if (unloadHandlers != null)
		{
			for (int i = 0; i < unloadHandlers.size(); i++)
			{
				ViewUnloadHandler handler = unloadHandlers.get(i);
				handler.onUnload(event);
				if (event.isCanceled())
				{
					canceled = true;
				}
			}
		}
		
		return !canceled;
	}
	
	protected abstract Panel getContainerPanel(View view);
	
	@SuppressWarnings("unchecked")
    protected <T extends Widget> T getMainWidget()
	{
		return (T) mainWidget;
	}
	
	protected abstract void handleViewTitle(String title, Panel containerPanel, String viewId);

	protected void loadAndRenderView(final String viewName, final String viewId, final Object parameter)
	{
		try
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.info(Crux.getMessages().viewContainerCreatingView(viewId));
			}
			createView(viewName, viewId, new CreateCallback()
			{
				@Override
				public void onViewCreated(View view)
				{
					if (addView(view, false, parameter))
					{
						renderView(view, parameter);
					}
					else
					{
						Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId));
					}
				}
			});
		}
		catch (InterfaceConfigException e)
		{
			Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId), e);
		}
	}

	/**
	 * Loads a view into the current container
	 * 
	 * @param viewName View name
	 * @param viewId View identifier
	 * @param parameter A parameter passed that will be bound to the view load and activate events
	 * @param render If true also render the view
	 */
	protected void loadView(final String viewName, final String viewId, final boolean render, final Object parameter)
	{
		try
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.info(Crux.getMessages().viewContainerCreatingView(viewId));
			}
			createView(viewName, viewId, new CreateCallback()
			{
				@Override
				public void onViewCreated(View view)
				{
					if (!add(view, render, parameter))
					{
						Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId));
					}
				}
			});
		}
		catch (InterfaceConfigException e)
		{
			Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId), e);
		}
	}
	
	protected abstract void notifyViewsAboutHistoryChange(ValueChangeEvent<String> event);
    protected abstract void notifyViewsAboutOrientationChange(String orientation);
    protected abstract void notifyViewsAboutWindowClose(CloseEvent<Window> event);	
    protected abstract void notifyViewsAboutWindowClosing(ClosingEvent event);
    protected abstract void notifyViewsAboutWindowResize(ResizeEvent event);
    
	/**
     * Render the view into the container
     * @param view
     * @param parameter
     */
    protected boolean renderView(View view, Object parameter)
    {
		assert (view!= null && views.containsKey(view.getId())):"Can not render the view["+view.getId()+"]. It was not added to the container";
		Panel containerPanel = getContainerPanel(view);
		boolean activated = activate(view, containerPanel, parameter);
		if (activated)
		{
			String title = view.getTitle();
			if (!StringUtils.isEmpty(title))
			{
				handleViewTitle(title, containerPanel, view.getId());
			}
		}
		return activated;
    }
	/**
     * Render the requested view into the container.
	 * @param viewName View name
	 * @param parameter to be passed to activate event
     */
    protected void showView(String viewName, Object parameter)
	{
    	showView(viewName, viewName, parameter);
	}
	/**
     * Render the requested view into the container.
     * @param viewId View identifier
	 * @param viewId View name
	 * @param parameter to be passed to activate event
	 */
	protected void showView(String viewName, String viewId, Object parameter)
	{
		assert (!StringUtils.isEmpty(viewId)) : "View [" + viewName + "] must have an id.";
		View view = getView(viewId);
		if (view != null)
		{
			if (!view.isActive())
			{
				renderView(view, parameter);
			}
		}
		else
		{
			loadAndRenderView(viewName, viewId, parameter);
		}
	}
	/**
	 * This method must be called by subclasses when the container is detached from DOM
	 */
	protected void unbindToDOM()
	{
		ViewHandlers.unbindToDOM(this);
	}
	/**
	 * Creates the view referenced by the given name
	 * 
	 * @param viewName View name
	 * @param callback Called when the view creation is completed.
	 */
	public static void createView(String viewName, CreateCallback callback)
	{
		getViewFactory().createView(viewName, callback);
	}
	/**
	 * Creates the view referenced by the given name and associate a custom identifier with the view created
	 * 
	 * @param viewName View name
	 * @param viewId View identifier
	 * @param callback Called when the view creation is completed.
	 */
	public static void createView(String viewName, String viewId, CreateCallback callback)
	{
		getViewFactory().createView(viewName, viewId, callback);
	}
	/**
	 * Retrieve the views factory associated with this screen.
	 * @return
	 */
	public static ViewFactory getViewFactory()
	{
		if (viewFactory == null)
		{
			viewFactory = (ViewFactory) GWT.create(ViewFactory.class);
		}
		return viewFactory;
	}
}
