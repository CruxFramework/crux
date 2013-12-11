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
package org.cruxframework.crux.widgets.client.dynatabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.core.client.screen.MultiFrameParametersHandler;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusHandler;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseEvent;
import org.cruxframework.crux.widgets.client.rollingtabs.RollingTabPanel;


import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;

/**
 * TODO - Gesse - Comment
 * 
 * @author Gesse S. F. Dafe
 */
@Legacy
@Deprecated
public class DynaTabs extends Composite
{
	public static final String DEFAULT_STYLE_NAME = "crux-DynaTabs";
	private RollingTabPanel tabPanel;
	private LinkedHashMap<String, Tab> tabs = new LinkedHashMap<String, Tab>();

	/**
	 * Empty constructor
	 */
	public DynaTabs()
	{
		this.tabPanel = new RollingTabPanel();
		this.tabPanel.setStyleName(DEFAULT_STYLE_NAME);
		this.tabPanel.addBeforeSelectionHandler(createBeforeSelectionHandler());
		initWidget(tabPanel);
	}

	/**
	 * @param tabId the tab identifier
	 * @return the tab window object
	 */
	public static JSWindow getSiblingTabWindow(String tabId)
	{
		return DynaTabsControllerInvoker.getSiblingTabWindow(tabId);
	}
	
	/**
	 * @param tabId the tab identifier
	 * @return the tab window object
	 */
	public static JSWindow getTabWindow(String tabId)
	{
		return DynaTabsControllerInvoker.getTabWindow(tabId);
	}

	/**
	 * Invokes a controller method on a tab. The tab must belong to a DynaTabs object residing in the current document.
	 * @param tabId
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeOnTab(String tabId, String call, Object param) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
	{
		DynaTabsControllerInvoker.invokeOnTab(tabId, call, param);
	}

	/**
	 * Invokes a controller method on a tab. The tab must belong to a DynaTabs object residing in the current document.
	 * @param <T>
	 * @param tabId
	 * @param call
	 * @param param
	 * @param resultType
	 * @return
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static <T> T invokeOnTab(String tabId, String call, Object param, Class<T> resultType) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
	{
		return DynaTabsControllerInvoker.invokeOnTab(tabId, call, param, resultType);
	}
	
	/**
	 * Closes the tab, skipping any BeforeCloseHandler registered
	 * @param tabId
	 */
	public void closeTab(String tabId)
	{
		closeTab(tabId, true);
	}

	/**
	 * @param tabId
	 * @param skipBeforeCloseHandlers
	 */
	public void closeTab(final String tabId, final boolean skipBeforeCloseHandlers)
	{
		final Tab tab = getTab(tabId);
		if (tab.canClose())
		{
			doCloseTab(tabId, skipBeforeCloseHandlers, tab);
		}
		else
		{
			tab.getFlapPanel().getFlapController().setCloseButtonEnabled(false);
			tab.executeWhenLoaded(new Command(){
				public void execute()
				{
					tab.getFlapPanel().getFlapController().setCloseButtonEnabled(true);
					doCloseTab(tabId, skipBeforeCloseHandlers, tab);
				}
			});
		}
	}

	/**
	 * @param tabId
	 */
	public void focusTab(String tabId)
	{
		this.tabPanel.selectTab(getTabIndex(tabId));
	}

	/**
	 * @return
	 */
	public Tab getFocusedTab()
	{
		int index = tabPanel.getSelectedTab();

		if (index >= 0)
		{
			return (Tab) tabPanel.getWidget(index);
		}

		return null;
	}

	/**
	 * @return
	 */
	public int getFocusedTabIndex()
	{
		return tabPanel.getSelectedTab();
	}	
	
	/**
	 * @param tabId
	 * @return
	 */
	public Tab getTab(String tabId)
	{
		return tabs.get(tabId);
	}

	/**
	 * @param tabId
	 * @return
	 */
	public int getTabIndex(String tabId)
	{
		return tabPanel.getWidgetIndex(getTab(tabId));
	}
	
	/**
	 * @param tab
	 * @return
	 */
	public int getTabIndex(Tab tab)
	{
		return tabPanel.getWidgetIndex(tab);
	}
	
	/**
	 * @return
	 */
	public List<Tab> getTabs()
	{
		List<Tab> result = new ArrayList<Tab>(this.tabs.size());
		for (Tab tab : this.tabs.values())
		{
			result.add(tab);
		}
		return result;
	}
	
	/**
	 * @param tabId
	 * @param label
	 * @param url
	 * @param closeable
	 * @param reloadIfAlreadyOpen
	 */
	private Tab openTab(final String tabId, String label, String url, boolean closeable, boolean reloadIfAlreadyOpen, boolean focus, boolean lazy)
	{
		Tab tab = null;
		url = MultiFrameParametersHandler.handleUrl(url);
		if (!this.tabs.containsKey(tabId))
		{
			FlapPanel flapPanel = new FlapPanel(this, tabId, label, closeable);
			
			if(lazy)
			{
				final String lazyURL = url;
				url = "";
				final Tab newTab = new Tab(tabId, label, url, closeable, tabPanel.getWidgetCount(), flapPanel);
				newTab.setLoaded(false);
				tab = newTab;
				
				newTab.addBeforeFocusHandler(new BeforeFocusHandler()
				{
					public void onBeforeFocus(BeforeFocusEvent event)
					{
						if(!newTab.isLoaded())
						{
							newTab.setLoaded(true);
							focusTab(tabId);
							new Timer() {							
								public void run() {
									newTab.changeURL(Screen.rewriteUrl(lazyURL));
								}
							}.schedule(100);
						}
					}
				});
			}
			else
			{
				tab = new Tab(tabId, label, url, closeable, tabPanel.getWidgetCount(), flapPanel);
			}
			
			this.tabs.put(tabId, tab);			
			tabPanel.add(tab, flapPanel);
		}
		else
		{
			tab = this.tabs.get(tabId);
			
			if(reloadIfAlreadyOpen)
			{
				tab.setLoaded(true);
				tab.changeURL(Screen.rewriteUrl(url));
			}
		}			

		if(focus)
		{
			focusTab(tabId);
		}

		return tab;
	}
	
	
	/**
	 * @param tabId
	 * @param label
	 * @param url
	 * @param closeable
	 * @param reloadIfAlreadyOpen
	 */
	public Tab openTab(String tabId, String label, String url, boolean closeable, boolean reloadIfAlreadyOpen)
	{
		return openTab(tabId, label, url, closeable, reloadIfAlreadyOpen, true, false);
	}
	
	/**
	 * @param tabId
	 * @param label
	 * @param url
	 * @param closeable
	 * @param reloadIfAlreadyOpen
	 * @return
	 */
	public Tab openLazyTab(String tabId, String label, String url, boolean closeable, boolean reloadIfAlreadyOpen)
	{
		return openTab(tabId, label, url, closeable, reloadIfAlreadyOpen, false, true);
	}
	
	/**
	 * @param tabId
	 */
	void doCloseTab(String tabId)
	{
		try
		{
			Tab removed = getTab(tabId);
			removed.changeURL("about:blank");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		int index = getTabIndex(tabId);
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
				String tabId = getTabId(event.getItem());

				if (selectedTab == null || !selectedTab.getId().equals(tabId))
				{
					boolean canceled = false;

					for (Tab tab : tabs.values())
					{
						if (!tab.getId().equals(tabId))
						{
							BeforeBlurEvent evt = BeforeBlurEvent.fire(tab.getFlapPanel());
							canceled = canceled || evt.isCanceled();
						}
						else
						{
							BeforeFocusEvent evt = BeforeFocusEvent.fire(tab);
							canceled = canceled || evt.isCanceled();
						}
					}

					if (canceled)
					{
						event.cancel();
					}
				}
			}			
		};
	}
	
	/**
	 * @param tabId
	 * @param skipBeforeCloseHandlers
	 * @param tab
	 */
	private void doCloseTab(String tabId, boolean skipBeforeCloseHandlers, Tab tab)
	{
		if(skipBeforeCloseHandlers)
		{
			doCloseTab(tabId);
		}
		else
		{
			BeforeCloseEvent evt = BeforeCloseEvent.fire(tab);

			if (!evt.isCanceled())
			{
				doCloseTab(tabId);
			}
		}
	}
	
	/**
	 * @param tabIndex
	 * @return
	 */
	private String getTabId(int tabIndex)
	{
		return ((Tab) tabPanel.getWidget(tabIndex)).getId();
	}
}