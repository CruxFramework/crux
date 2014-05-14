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
package org.cruxframework.crux.widgets.client.tabcontainer;

import java.util.LinkedHashMap;

import org.cruxframework.crux.core.client.screen.views.MultipleViewsContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusEvent;
import org.cruxframework.crux.widgets.client.rollingtabs.RollingTabPanel;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
//TODO refatorar. Fazer sem tables
public class TabContainer extends MultipleViewsContainer
{
	public static final String DEFAULT_STYLE_NAME = "crux-TabContainer";
	private RollingTabPanel tabPanel;
	private LinkedHashMap<String, Tab> tabs = new LinkedHashMap<String, Tab>();

	/**
	 * Constructor
	 */
	public TabContainer()
	{
		super(new RollingTabPanel(), true);
		tabPanel = getMainWidget();
		tabPanel.addBeforeSelectionHandler(createBeforeSelectionHandler());
		tabPanel.setStyleName(DEFAULT_STYLE_NAME);
	}
	
	/**
	 * 
	 * @param view
	 * @param lazy
	 * @param closeable
	 * @return
	 */
	public boolean add(View view, boolean lazy, boolean closeable)
	{
		if (doAdd(view, lazy, closeable))
		{
			adoptView(view);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param view
	 * @param lazy
	 * @param closeable
	 * @param render
	 * @return
	 */
    public boolean add(View view, boolean lazy, boolean closeable, boolean render)
    {
		if (add(view, lazy, closeable))
		{
			if (render)
			{
				renderView(view, null);
			}
			return true;
		}
		return false;
    }
	
	/**
	 * Closes the tab, skipping any Unload event
	 * @param view
	 */
	public void closeView(String viewId)
	{
		closeView(viewId, true);
	}

	/**
	 * @param view
	 * @param skipEvents
	 */
	public void closeView(String viewId, final boolean skipEvents)
	{
		View view = getView(viewId);
		if (view != null)
		{
			remove(view, skipEvents);
		}
	}
	
	/**
	 * @param viewId
	 */
	public void focusView(String viewId)
	{
		this.tabPanel.selectTab(getIndex(viewId));
	}

	/**
	 * @return
	 */
	public int getFocusedViewIndex()
	{
		return tabPanel.getSelectedTab();
	}	

	/**
	 * @param viewId
	 * @return
	 */
	public int getIndex(String viewId)
	{
		return tabPanel.getWidgetIndex(getTab(viewId));
	}
	
	/**
	 * @param tab
	 * @return
	 */
	public int getIndex(View view)
	{
		return getIndex(view.getId());
	}
	
	/**
	 * @param tabIndex
	 * @return
	 */
	public String getViewId(int tabIndex)
	{
		return ((Tab) tabPanel.getWidget(tabIndex)).getViewId();
	}
	
	/**
	 * @param viewId
	 * @return
	 */
	public Tab getTab(String viewId)
	{
		return tabs.get(viewId);
	}

	@Override
	protected boolean doAdd(View view, boolean lazy, Object parameter)
	{
		return doAdd(view, lazy, true, parameter);
	}

	protected boolean doAdd(View view, boolean lazy, boolean closeable, Object parameter)
    {
	    String tabId = view.getId();
	    if (!views.containsKey(view.getId()))
	    {
	    	boolean doAdd = super.doAdd(view, lazy, parameter);
	    	if (doAdd)
	    	{
	    		Flap flap = new Flap(this, view, closeable);
	    		Tab tab = new Tab(flap, tabId);
	    		this.tabs.put(tabId, tab);			
	    		tabPanel.add(tab, flap);
	    		focusView(tabId);
	    	}
	    	return doAdd;
	    }
	    else
	    {
	    	focusView(tabId);
	    }
	    return false;
    }
	
	@Override
	protected boolean doRemove(View view, boolean skipEvent)
	{
	    boolean doRemove = super.doRemove(view, skipEvent);
	    if (doRemove)
	    {
	    	doCloseTab(view.getId());
	    }
		return doRemove;
	}
	
	/**
	 * @return
	 */
	protected Tab getFocusedTab()
	{
		int index = tabPanel.getSelectedTab();

		if (index >= 0)
		{
			return (Tab) tabPanel.getWidget(index);
		}

		return null;
	}

	@Override
    protected Panel getContainerPanel(View view)
    {
		Tab tab = getTab(view.getId());
	    return tab.getContainerPanel();
    }

	@Override
    protected void handleViewTitle(String title, Panel containerPanel, String viewId)
    {
		Tab tab = getTab(viewId);
		if (tab != null)
		{
			tab.setLabel(title);
		}
    }

	/**
	 * @param tabId
	 */
	private void doCloseTab(String tabId)
	{
		int index = getIndex(tabId);
		this.tabPanel.remove(index);
		this.tabs.remove(tabId);
		
		if (this.tabPanel.getWidgetCount() > 0)
		{
			int indexToFocus = index == 0 ? 0 : index - 1;
			this.tabPanel.selectTab(indexToFocus);
		}
	}	
	
	/**
	 * @param tabId
	 */
	private BeforeSelectionHandler<Integer> createBeforeSelectionHandler()
	{	
		return new BeforeSelectionHandler<Integer>()
		{
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event)
			{
				Tab selectedTab = getFocusedTab();
				String tabId = getViewId(event.getItem());
				boolean canceled = false;
				if (selectedTab != null && !selectedTab.getViewId().equals(tabId))
				{
					BeforeBlurEvent evt = BeforeBlurEvent.fire(selectedTab.getFlap());
					canceled = evt.isCanceled();
				}
				if ((!canceled) && (selectedTab == null || !selectedTab.getViewId().equals(tabId)))
				{
					BeforeFocusEvent evt = BeforeFocusEvent.fire(getTab(tabId));
					canceled = canceled || evt.isCanceled();
				}

				if (canceled)
				{
					event.cancel();
				}
			}			
		};
	}
}