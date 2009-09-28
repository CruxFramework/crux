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
package br.com.sysmap.crux.advanced.client.dynatabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeBlurEvent;
import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeFocusEvent;
import br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseEvent;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO - Gessé - Comment
 * 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DynaTabs extends Composite
{
	public static final String DEFAULT_STYLE_NAME = "crux-DynaTabs";
	private LinkedHashMap<String, Tab> tabs = new LinkedHashMap<String, Tab>();
	private TabPanel tabPanel;

	/**
	 * Empty constructor
	 */
	public DynaTabs()
	{
		this.tabPanel = new TabPanel();
		this.tabPanel.setStyleName(DEFAULT_STYLE_NAME);
		this.tabPanel.addBeforeSelectionHandler(createBeforeSelectionHandler());
		initWidget(tabPanel);
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
		Tab tab = null;

		if (!this.tabs.containsKey(tabId))
		{
			FlapPanel flapPanel = new FlapPanel(this, tabId, label, closeable);
			tab = new Tab(tabId, label, url, closeable, reloadIfAlreadyOpen, tabPanel.getWidgetCount(), flapPanel);
			this.tabs.put(tabId, tab);			
			tabPanel.add(tab, flapPanel);
		}

		focusTab(tabId);

		return tab;
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
	 * @param tabId
	 * @return
	 */
	public int getTabIndex(String tabId)
	{
		return tabPanel.getWidgetIndex(getTab(tabId));
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
	 * @param tabIndex
	 * @return
	 */
	private String getTabId(int tabIndex)
	{
		return ((Tab) tabPanel.getWidget(tabIndex)).getId();
	}

	/**
	 * @return
	 */
	public int getFocusedTabIndex()
	{
		return tabPanel.getTabBar().getSelectedTab();
	}

	/**
	 * @return
	 */
	public Tab getFocusedTab()
	{
		int index = tabPanel.getTabBar().getSelectedTab();

		if (index >= 0)
		{
			return (Tab) tabPanel.getWidget(index);
		}

		return null;
	}

	/**
	 * @param tabId
	 */
	public void focusTab(String tabId)
	{
		this.tabPanel.selectTab(getTabIndex(tabId));
	}

	
	/**
	 * @param tabId
	 * @param skipBeforeCloseHandlers
	 */
	public void closeTab(String tabId, boolean skipBeforeCloseHandlers)
	{
		if(skipBeforeCloseHandlers)
		{
			doCloseTab(tabId);
		}
		else
		{
			BeforeCloseEvent evt = BeforeCloseEvent.fire(getTab(tabId));
			
			if (!evt.isCanceled())
			{
				doCloseTab(tabId);
			}
		}		
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
	 */
	private void doCloseTab(String tabId)
	{
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
							BeforeFocusEvent evt = BeforeFocusEvent.fire(tab.getFlapPanel());
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
}

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
class SimpleDecoratedPanel extends CellPanel
{
	private Element line;
	private Element leftCell;
	private Element centerCell;
	private Element rightCell;
	
	public SimpleDecoratedPanel()
	{
		getTable().setClassName("");
	
		line = DOM.createTR();
		leftCell = createTd("flapLeft", true);
		centerCell = createTd("flapCenter", false);
		centerCell.setPropertyInt("colSpan", 2);
		rightCell = createTd("flapRight", false);
		
	    DOM.appendChild(line, leftCell);
	    DOM.appendChild(line, centerCell);
	    DOM.appendChild(line, rightCell);
	    DOM.appendChild(getBody(), line);
	    	    
	    setSpacing(0);
	}
	
	/**
	 * Adds a widget to the body of the panel (middle center cell)
	 * @param w
	 */
	public void setContentWidget(Widget w)
	{
		cleanEmptySpaces(centerCell);
		add(w, centerCell);
	}
	
	/**
	 * @param middleCenterCell2
	 */
	private void cleanEmptySpaces(Element cell)
	{
		String text = cell.getInnerText();
		
		if(text != null && text.trim().length() == 0)
		{
			cell.setInnerText("");
		}
	}

	/**
	 * Creates a TD with the given style name 
	 * @param styleName
	 * @return
	 */
	private Element createTd(String styleName, boolean fillWithBlank)
	{
		Element td = DOM.createTD();
		td.setClassName(styleName);
		
		if(fillWithBlank)
		{
			td.setInnerHTML("&nbsp;");
		}
		
		td.setPropertyString("align", "center");
		td.setPropertyString("valign", "middle");		
		return td;
	}
}