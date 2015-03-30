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
package org.cruxframework.crux.smartfaces.client.disposal.menudisposal;


import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.SingleCrawlableViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.client.panel.FooterPanel;
import org.cruxframework.crux.smartfaces.client.panel.HeaderPanel;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a base class to create components that defines a page layout (example: TopMenuDisposalLayout, SideMenuDisposalLayout)
 * @author wesley.diniz
 *
 */
public abstract class BaseMenuDisposal extends SingleCrawlableViewContainer
{
	private static final String BASE_MENU_DISPOSAL_MENU = "faces-BaseMenuDisposal-Menu";

	protected NavPanel menuPanel;
	protected HeaderPanel headerPanel;
	protected FooterPanel footerPanel;
	protected Panel bodyPanel;
	protected Panel viewContentPanel;
	protected Menu menu;
	private View innerView;	
	private BaseMenuHandler handler = GWT.create(BaseMenuHandler.class);
	
	protected BaseMenuDisposal()
	{
		super(new FlowPanel(), true);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();//TODO remover isso e usar heranca nos css
		setHistoryControlEnabled(true);
		bodyPanel = getMainWidget();
		viewContentPanel = new FlowPanel();
		headerPanel = new HeaderPanel();
		menuPanel = new NavPanel();
		footerPanel = new FooterPanel();
		buildLayout();
		setStyles();
	}

	public View getView()
	{
		return innerView;
	}

	public void addFooterContent(Widget footer)
	{
		this.footerPanel.add(footer);
	}
	
	public void addSmallHeaderContent(Widget header)
	{
		handler.addSmallHeaderContent(this, header);
	}

	public void addLargeHeaderContent(Widget header)
	{
		handler.addLargeHeaderContent(this, header);
	}
	
	public void setMenu(final Menu menu)
	{
		this.menu = menu;
		handler.setMenu(this, menu);
	}
	
	public Menu getMenu()
	{
		return this.menu;
	}
	
	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		Window.setTitle(title);
	}
	
	@Override
	protected Panel getContainerPanel(View view)
	{
		return viewContentPanel;
	}
	
	/** 
	 * Must be overridden to create all the internal widgets
	 */
	protected abstract void buildLayout();
	
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
	
	protected abstract String getSmallHeaderStyleName();
	
	protected void setStyles()
	{
		footerPanel.setStyleName(getFooterStyleName());
		menuPanel.setStyleName(getMenuPanelStyleName());
		viewContentPanel.setStyleName(getContentStyleName());
	}
	
	@Override
	protected boolean activate(View view, Panel containerPanel, Object parameter)
	{
		boolean activated = super.activate(view, containerPanel, parameter);
		if (activated)
		{
			Window.scrollTo(0, 0);
		}
		return activated;
	}
	
	@Override
	protected boolean doAdd(View view, boolean lazy, Object parameter)
	{
	    assert(views.isEmpty()):"Disposal can not contain more then one view";
	    innerView = view;
	    boolean added = super.doAdd(view, lazy, parameter);
	    if (!added)
	    {//During view creation, a widget can make a reference to Screen static methods... So, it is better to 
	     // set rootView reference before widgets creation...	
	    	innerView = null;
	    }
		return added;
	}
	
	@Override
	protected boolean doRemove(View view, boolean skipEvents)
	{
	    boolean removed = super.doRemove(view, skipEvents);
	    if (removed)
	    {
	    	innerView = null;
	    }
		return removed;
	}
	
	@Override
	protected void showView(String viewName, String viewId, Object parameter)
	{
	    if (getView() != null)
	    {
	    	if (getView().removeFromContainer())
	    	{
		    	super.showView(viewName, viewId, parameter);
	    	}
	    }
	    else
	    {
	    	super.showView(viewName, viewId, parameter);
	    }
	}
	
	protected void showSmallMenu()
	{
		handler.showSmallMenu(this);
	}
	
	static interface BaseMenuHandler
	{
		void addLargeHeaderContent(BaseMenuDisposal disposal, Widget header);
		void addSmallHeaderContent(BaseMenuDisposal disposal, Widget header);
		void setMenu(BaseMenuDisposal disposal, Menu menu);
		void showSmallMenu(BaseMenuDisposal disposal);
	}
	
	static class LargeBaseMenuHandler implements BaseMenuHandler
	{
		@Override
        public void addLargeHeaderContent(BaseMenuDisposal disposal, Widget header)
        {
			disposal.headerPanel.add(header);
        }

		@Override
        public void addSmallHeaderContent(BaseMenuDisposal disposal, Widget header) {}

		@Override
        public void setMenu(final BaseMenuDisposal disposal, Menu menu)
        {
			disposal.menuPanel.add(menu);
			menu.addSelectionHandler(new SelectionHandler<MenuItem>(){
				
				@Override
				public void onSelection(SelectionEvent<MenuItem> event)
				{
					String viewName = event.getSelectedItem().getValue();
					if(!StringUtils.isEmpty(viewName))
					{
						disposal.showView(viewName);
					}
				}
			});
        }

		@Override
        public void showSmallMenu(BaseMenuDisposal disposal) {}
	}
	
	static class SmallBaseMenuHandler implements BaseMenuHandler
	{
		@Override
        public void addLargeHeaderContent(BaseMenuDisposal disposal, Widget header) {}

		@Override
        public void addSmallHeaderContent(BaseMenuDisposal disposal, Widget header)
        {
			disposal.headerPanel.add(header);
        }

		@Override
        public void setMenu(final BaseMenuDisposal disposal, final Menu menu)
        {
			menu.addStyleName(BaseMenuDisposal.BASE_MENU_DISPOSAL_MENU);
			menu.addSelectionHandler(new SelectionHandler<MenuItem>(){
				
				@Override
				public void onSelection(SelectionEvent<MenuItem> event)
				{
					String viewName = event.getSelectedItem().getValue();
					if(!StringUtils.isEmpty(viewName))
					{
						disposal.showView(viewName);
					}
					
					if(!event.getSelectedItem().hasChildren())
					{
						disposal.menuPanel.remove(menu);
					}
				}
			});
        }

		@Override
        public void showSmallMenu(BaseMenuDisposal disposal)
        {
			if (disposal.menu != null)
			{
				if (disposal.menu.getParent() == null)
				{
					disposal.menuPanel.add(disposal.menu);
				} 
				else
				{
					disposal.menuPanel.remove(disposal.menu);
				}
			}
        }
	}
}
