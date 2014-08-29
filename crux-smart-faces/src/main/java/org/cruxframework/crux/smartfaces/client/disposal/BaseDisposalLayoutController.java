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
package org.cruxframework.crux.smartfaces.client.disposal;


import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.client.viewcontainer.SimpleViewContainer;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a base class to create components that defines a page layout (example: TopMenuDisposalLayout, LeftMenuDisposalLayout)
 * @author wesley.diniz
 *
 */
public abstract class BaseDisposalLayoutController extends DeviceAdaptiveController implements DisposalLayout
{
	private static final String HISTORY_PREFIX = "!disposalLayout=";

	protected String viewName;
	protected Panel menuPanel;
	protected Panel headerPanel;
	protected Panel footerPanel;
	protected Panel contentPanel;
	protected Menu menu;
	protected SimpleViewContainer viewContainer;
	
	/** 
	 * Must be overridden to create an object instance that will be set as the contentPanel (FlowPanel, SimplePanel etc.)
	 */
	protected abstract void initContentPanel();
	/** 
	 * Must be overridden to create an object instance that will be set as the headerPanel (FlowPanel, SimplePanel etc.)
	 */
	protected abstract void initHeaderPanel();
	/** 
	 * Must be overridden to create an object instance that will be set as the footerPanel (FlowPanel, SimplePanel etc.)
	 */
	protected abstract void initFooterPanel();
	
	/** 
	 * Must be overridden to create an object instance that will be set as the menuPanel (FlowPanel, SimplePanel etc.)
	 */
	protected abstract void initMenuPanel();
	
	
	/**
	 * Must be overridden to specify footer's stylename
	 * @return styteName
	 */
	protected abstract String getFooterStyleName();
	/**
	 * Must be overridden to specify header's style name
	 * @return styteName
	 */
	protected abstract String getHeaderStyleName();
	/**
	 * Must be overridden to specify menu's style name
	 * @return styteName
	 */
	protected abstract String getMenuPanelStyleName();
	
	/**
	 * Must be overridden to specify content's style name
	 * @return styteName
	 */
	protected abstract String getContentStyleName();

	private void initPanels()
	{
		initContentPanel();
		initHeaderPanel();
		initFooterPanel();
		initMenuPanel();
	}
	
	private void setStyles()
	{
		footerPanel.setStyleName(getFooterStyleName());
		headerPanel.setStyleName(getHeaderStyleName());
		menuPanel.setStyleName(getMenuPanelStyleName());
		contentPanel.setStyleName(getContentStyleName());
	}
	
	/* (non-Javadoc)
	 * @see org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController#init()
	 */
	@Override
	protected void init()
	{
		initPanels();
		
		if(!isComponentBuiltOk())
		{
			throw new RuntimeException("MenuPanel and ContentPanel cannot be null.");
		}
		
		viewContainer = new SimpleViewContainer();
		contentPanel.add(viewContainer);
		setStyles();
		addHistoryHandlers();
	}
	
	private boolean isComponentBuiltOk()
	{
		if(menuPanel == null || contentPanel == null)
		{
			return false;
		}
		
		return true;
	}
	
	private void addHistoryHandlers()
	{
		Screen.addHistoryChangedHandler(new ValueChangeHandler<String>(){
			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				String token = event.getValue();
				if (token != null && token.startsWith(HISTORY_PREFIX))
				{
					showView(token.replace(HISTORY_PREFIX, ""), false);
				}
			}
		});

		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			@Override
			public void execute()
			{
				// Favorites
				String hash = com.google.gwt.user.client.Window.Location.getHash();

				if (hash != null && hash.startsWith(HASH + HISTORY_PREFIX))
				{
					String currentViewName = hash.replace(HASH + HISTORY_PREFIX, "");
					showView(currentViewName, false);
					History.newItem(HISTORY_PREFIX + currentViewName);
					return;
				}

				// DefaultView
				if (viewName != null)
				{
					showView(viewName.replace(HISTORY_PREFIX, ""), false);
					History.newItem(HISTORY_PREFIX + viewName);
					return;
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.cruxframework.crux.smartfaces.client.disposal.DisposalLayout#showView(java.lang.String, boolean)
	 */
	@Override
	public void showView(String viewName, boolean saveHistory)
	{
		if (saveHistory)
		{
			History.newItem(HISTORY_PREFIX + viewName);
		} else
		{
			viewContainer.showView(viewName);
			Window.scrollTo(0, 0);
		}
	}

	/* (non-Javadoc)
	 * @see org.cruxframework.crux.smartfaces.client.disposal.DisposalLayout#setDefaultView(java.lang.String)
	 */
	@Override
	public void setDefaultView(String viewName)
	{
		this.viewName = viewName;
	}

	/* (non-Javadoc)
	 * @see org.cruxframework.crux.smartfaces.client.disposal.DisposalLayout#setHeader(com.google.gwt.user.client.ui.Panel)
	 */
	@Override
	public void addHeaderContent(Widget header)
	{
		this.headerPanel.add(header);
	}

	/* (non-Javadoc)
	 * @see org.cruxframework.crux.smartfaces.client.disposal.DisposalLayout#setFooter(com.google.gwt.user.client.ui.Panel)
	 */
	@Override
	public void addFooterContent(Widget footer)
	{
		this.footerPanel.add(footer);
	}
	
	/* (non-Javadoc)
	 * @see org.cruxframework.crux.smartfaces.client.disposal.DisposalLayout#setMenu(org.cruxframework.crux.smartfaces.client.menu.Menu)
	 */
	@Override
	public void setMenu(Menu menu)
	{
		this.menu = menu;
		this.menuPanel.add(menu);
	}
	
	/*
	 * @see org.cruxframework.crux.smartfaces.client.disposal.DisposalLayout#addMenuItem(org.cruxframework.crux.smartfaces.client.menu.MenuItem, java.lang.String)
	 */
	@Override
	public void addMenuItem(MenuItem item,String label)
	{
		this.menu.addItem(item,label);
	}
	
	/**
	 * @return menu's instance
	 */
	public Menu getMenu()
	{
		return this.menu;
	}
}
