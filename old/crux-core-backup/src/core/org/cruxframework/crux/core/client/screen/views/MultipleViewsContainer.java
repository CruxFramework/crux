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

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class MultipleViewsContainer extends ViewContainer
{
	protected FastMap<View> activeViews = new FastMap<View>();;

	public MultipleViewsContainer(Widget mainWidget, boolean clearPanelsForDeactivatedViews)
    {
	    super(mainWidget, clearPanelsForDeactivatedViews);
    }

	@Override
	protected boolean activate(View view, Panel containerPanel, Object parameter)
	{
		assert(view != null):"Can not active a null view";
		boolean activated = super.activate(view, containerPanel, parameter);
	    if (activated && !activeViews.containsKey(view.getId()))
	    {
	    	activeViews.put(view.getId(), view);
	    }
	    return activated;
	}
	
	@Override
	protected boolean deactivate(View view, Panel containerPanel, boolean skipEvent)
	{
		assert(view != null):"Can not deactive a null view";
		boolean deactivated = true;
		if (activeViews.containsKey(view.getId()))
		{
			deactivated = super.deactivate(view, containerPanel, skipEvent);
			if (deactivated)
			{
				activeViews.remove(view.getId());
			}
		}
		return deactivated;
	}
	
	@Override
    protected boolean hasResizeHandlers()
    {
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			if (activeViews.get(keys.get(i)).hasResizeHandlers())
			{
				return true;
			}
		}
		
		return false;
    }
	
	@Override
	protected boolean hasWindowCloseHandlers()
	{
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			if (activeViews.get(keys.get(i)).hasWindowCloseHandlers())
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected boolean hasWindowClosingHandlers()
	{
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			if (activeViews.get(keys.get(i)).hasWindowClosingHandlers())
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected boolean hasOrientationChangeHandlers()
	{
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			if (activeViews.get(keys.get(i)).hasOrientationChangeHandlers())
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected boolean hasHistoryHandlers()
	{
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			if (activeViews.get(keys.get(i)).hasHistoryHandlers())
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
    protected void notifyViewsAboutWindowResize(ResizeEvent event)
    {
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			activeViews.get(keys.get(i)).fireResizeEvent(event);
		}
    }
	
	@Override
    protected void notifyViewsAboutWindowClose(CloseEvent<Window> event)
    {
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			activeViews.get(keys.get(i)).fireWindowCloseEvent(event);
		}
    }

	@Override
    protected void notifyViewsAboutWindowClosing(ClosingEvent event)
    {
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			activeViews.get(keys.get(i)).fireWindowClosingEvent(event);
		}
    }
	
	@Override
	protected void notifyViewsAboutOrientationChange()
	{
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			activeViews.get(keys.get(i)).fireOrientationEvent();
		}
	}
	
	@Override
	protected void notifyViewsAboutHistoryChange(ValueChangeEvent<String> event)
	{
		FastList<String> keys = activeViews.keys();
		for (int i = 0; i < keys.size(); i++)
		{
			activeViews.get(keys.get(i)).fireHistoryChangeEvent(event);
		}
	}
}
