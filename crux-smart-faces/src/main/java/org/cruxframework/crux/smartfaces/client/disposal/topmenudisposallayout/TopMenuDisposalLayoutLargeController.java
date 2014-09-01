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
package org.cruxframework.crux.smartfaces.client.disposal.topmenudisposallayout;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.smartfaces.client.disposal.BaseDisposalLayoutController;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author wesley.diniz
 * 
 */
@Controller("topDisposalLayoutLargeController")
public class TopMenuDisposalLayoutLargeController extends BaseDisposalLayoutController
{
	private final String MENU_PANEL_STYLE = "top-disposal-menu-panel-large";
	private final String HEADER_PANEL_STYLE = "top-disposal-header-panel-large";
	private final String FOOTER_PANEL_STYLE = "top-disposal-footer-panel-large";
	private final String CONTENT_MENU_STYLE = "top-disposal-content-panel-large";

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

	@Override
	public void addSmallHeaderContent(Widget param){}
	
	public enum TopDisposalMenuType
	{
		HORIZONTAL_ACCORDION,
		HORIZONTAL_DROPDOWN;
	}
}
