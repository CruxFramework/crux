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
package org.cruxframework.crux.smartfaces.client.disposal.leftmenudisposallayout;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.smartfaces.client.disposal.BaseDisposalLayoutController;
import org.cruxframework.crux.smartfaces.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;

import com.google.gwt.user.client.ui.Widget;

@Controller("leftMenuDisposalSmallController")
public class LeftMenuDisposalLayoutSmallController extends BaseDisposalLayoutController
{

	private final String MENU_PANEL_STYLE = "top-disposal-menu-panel-small";
	private final String HEADER_PANEL_STYLE = "top-disposal-header-panel-small";
	private final String FOOTER_PANEL_STYLE = "top-disposal-footer-panel-small";
	private final String CONTENT_MENU_STYLE = "top-disposal-content-panel-small";

	@Override
	protected void initContentPanel()
	{
		contentPanel = getChildWidget("contentPanel");
	}

	@Override
	protected void initHeaderPanel()
	{
		headerPanel = getChildWidget("headerPanel");
	}

	@Override
	protected void initFooterPanel()
	{
		footerPanel = getChildWidget("footerPanel");
	}

	@Override
	protected void initMenuPanel()
	{
		menuPanel = getChildWidget("menuPanel");
	}

	@Override
	protected String getFooterStyleName()
	{
		return FOOTER_PANEL_STYLE;
	}

	@Override
	protected String getHeaderStyleName()
	{
		return HEADER_PANEL_STYLE;
	}

	@Override
	protected String getMenuPanelStyleName()
	{
		return MENU_PANEL_STYLE;
	}

	@Override
	protected String getContentStyleName()
	{
		return CONTENT_MENU_STYLE;
	}

	@Expose
	public void onShowMenu(SelectEvent evt)
	{
		if (this.menu.getParent() == null)
		{
			this.contentPanel.add(menu);
		} else
		{
			this.contentPanel.remove(menu);
		}
	}

	@Override
	public void setMenu(Menu param)
	{
		this.menu = param;
		param.addStyleName("menu-disposal-small");
		int count = param.getItemCount();

		SelectHandler handler = new SelectHandler(){

			@Override
			public void onSelect(SelectEvent event)
			{
				contentPanel.remove(menu);
			}
		};

		for (int i = 0; i <= count - 1; i++)
		{
			MenuItem item = param.getItem(i);

			if (!item.hasChildren())
			{
				item.addSelectHandler(handler);
			} else
			{
				for (int j = 0; j < item.getItemCount(); j++)
				{
					MenuItem childItem = item.getItem(j);
					childItem.addSelectHandler(handler);
				}
			}
		}
	}
	
	@Override
	public void addSmallHeaderContent(Widget w)
	{
		this.headerPanel.clear();
		this.headerPanel.add(w);
	}
	
	@Override
	public void addHeaderContent(Widget header)
	{
		if(headerPanel.getElement().getChildCount() == 1)
		{
			super.addHeaderContent(header);
		}
	}

}
