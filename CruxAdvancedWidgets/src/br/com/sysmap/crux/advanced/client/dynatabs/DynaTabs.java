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

import java.util.LinkedHashMap;

import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeBlurEvent;
import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeFocusEvent;
import br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
			tab = new Tab(tabId, label, url, closeable, reloadIfAlreadyOpen, tabPanel.getWidgetCount());
			this.tabs.put(tabId, tab);
			tabPanel.add(tab, new FlapPanel(this, tabId, label, closeable));
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
	 * @return
	 */
	public int getSelectedTabIndex()
	{
		return tabPanel.getTabBar().getSelectedTab();
	}

	/**
	 * @return
	 */
	public Tab getSelectedTab()
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
		Tab selectedTab = getSelectedTab();

		if (selectedTab == null || !selectedTab.getId().equals(tabId))
		{
			boolean canceled = false;

			for (Tab tab : tabs.values())
			{
				if (!tab.getId().equals(tabId))
				{
					BeforeBlurEvent evt = BeforeBlurEvent.fire(tab);
					canceled = canceled || evt.isCanceled();
				}
				else
				{
					BeforeFocusEvent evt = BeforeFocusEvent.fire(tab);
					canceled = canceled || evt.isCanceled();
				}
			}

			if (!canceled)
			{
				this.tabPanel.selectTab(getTabIndex(tabId));
			}
		}
	}

	/**
	 * @param tabId
	 */
	public void closeTab(String tabId)
	{
		BeforeCloseEvent evt = BeforeCloseEvent.fire(getTab(tabId));
		if (!evt.isCanceled())
		{
			int index = getTabIndex(tabId);
			this.tabPanel.remove(index);

			if (this.tabPanel.getWidgetCount() > 0)
			{
				int indexToFocus = index == 0 ? 0 : index - 1;
				this.tabPanel.selectTab(indexToFocus);
			}
		}
	}
}

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
class FlapPanel extends Composite
{
	private SimpleDecoratedPanel panel;
	
	public FlapPanel(DynaTabs tabs, String tabId, String tabLabel, boolean closeable)
	{
		panel = new SimpleDecoratedPanel();
		panel.setContentWidget(createFlapController(tabs, tabId, tabLabel, closeable));
		initWidget(panel);	
	}

	/**
	 * @return
	 */
	private Widget createFlapController(final DynaTabs tabs, final String tabId, String tabLabel, boolean closeable)
	{
		HorizontalPanel flap = new HorizontalPanel();
		flap.setSpacing(0);

		Label title = new Label(tabLabel);
		title.setStyleName("flapLabel");
		flap.add(title);

		FocusPanel closeButton = new FocusPanel();
		Label empty = new Label("");
		empty.getElement().getStyle().setProperty("fontSize", "1px");
		closeButton.add(empty);
		closeButton.setStyleName("flapCloseButton");
		closeButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				tabs.closeTab(tabId);
			}
		});

		closeButton.setVisible(closeable);
		
		flap.add(closeButton);
		
		return flap;
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