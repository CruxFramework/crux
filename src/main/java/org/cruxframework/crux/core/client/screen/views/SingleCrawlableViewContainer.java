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
public abstract class SingleCrawlableViewContainer extends CrawlableViewContainer
{
	protected View activeView = null;;

	public SingleCrawlableViewContainer(Widget mainWidget, boolean clearPanelsForDeactivatedViews)
    {
	    super(mainWidget, clearPanelsForDeactivatedViews);
    }

	/**
	 * 
	 * @return
	 */
	public View getActiveView()
	{
		return activeView;
	}
	
	@Override
	protected boolean isViewDisplayed(String viewId)
	{
		View view = getView(viewId);
		return (view != null && view.isActive());
	}
	
	@Override
	protected boolean activate(View view, Panel containerPanel, Object parameter)
	{
	    if (activeView != null)
	    {
	    	if (deactivate(activeView, getContainerPanel(activeView), false))
	    	{
	    		activeView = view;
	    		return super.activate(view, containerPanel, parameter);
	    	}
	    	return false;
	    }
	    else
	    {
	    	activeView = view;
    		return super.activate(view, containerPanel, parameter);
	    }
	}
	
	@Override
	protected boolean deactivate(View view, Panel containerPanel, boolean skipEvent)
	{
		assert(view != null):"Can not deactive a null view";
		boolean deactivated = true;
		if (activeView != null && view.getId().equals(activeView.getId()))
		{
			deactivated = super.deactivate(view, containerPanel, skipEvent);
			if (deactivated)
			{
				activeView = null;
			}
		}
		return deactivated;
	}
	
	@Override
    protected boolean hasResizeHandlers()
    {
	    return activeView!= null && activeView.hasResizeHandlers();
    }
	
	@Override
	protected boolean hasWindowCloseHandlers()
	{
	    return activeView!= null && activeView.hasWindowCloseHandlers();
	}

	@Override
	protected boolean hasWindowClosingHandlers()
	{
	    return activeView!= null && activeView.hasWindowClosingHandlers();
	}

	@Override
	protected boolean hasOrientationChangeHandlers()
	{
	    return activeView!= null && activeView.hasOrientationChangeHandlers();
	}
	
	@Override
	protected boolean hasHistoryHandlers()
	{
	    return activeView!= null && activeView.hasHistoryHandlers();
	}
	
	@Override
    protected void notifyViewsAboutWindowResize(ResizeEvent event)
    {
		if (activeView!= null)
		{
			activeView.fireResizeEvent(event);
		}
    }
	
	@Override
    protected void notifyViewsAboutWindowClose(CloseEvent<Window> event)
    {
		if (activeView!= null)
		{
			activeView.fireWindowCloseEvent(event);
		}
    }

	@Override
    protected void notifyViewsAboutWindowClosing(ClosingEvent event)
    {
		if (activeView!= null)
		{
			activeView.fireWindowClosingEvent(event);
		}
    }
	
	@Override
	protected void notifyViewsAboutOrientationChange()
	{
		if (activeView!= null)
		{
			activeView.fireOrientationEvent();
		}
	}
	
	@Override
	protected void notifyViewsAboutHistoryChange(ValueChangeEvent<String> event)
	{
		if (activeView!= null)
		{
			activeView.fireHistoryChangeEvent(event);
		}
	}
}
