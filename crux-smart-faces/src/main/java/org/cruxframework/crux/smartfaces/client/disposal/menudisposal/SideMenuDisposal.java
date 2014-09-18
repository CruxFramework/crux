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

import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.button.Button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author wesley.diniz
 * 
 */
public class SideMenuDisposal extends BaseMenuDisposal
{
	private final String MENU_PANEL_STYLE = "faces-SideMenuDisposal-menuPanel";
	private final String HEADER_PANEL_STYLE = "faces-SideMenuDisposal-headerPanel";
	private final String FOOTER_PANEL_STYLE = "faces-SideMenuDisposal-footerPanel";
	private final String CONTENT_MENU_STYLE = "faces-SideMenuDisposal-contentPanel";
	private final String SIDE_MENU_DISPOSAL_SMALL_HEADER_PANEL = "faces-SideMenuDisposal-smallHeaderPanel";
	private final String RIGHT_MENU_POSITION = "faces-SideMenuDisposal-rightMenuPosition";
	
	private final String DEFAULT_STYLE_NAME = "faces-SideMenuDisposal";
	
	@Override
	protected void buildLayout()
	{
		LayoutBuilder builder = GWT.create(LayoutBuilder.class);
		builder.buildLayout(this);
		setSizeDisposal(builder.getDeviceSize());
		setStyleName(DEFAULT_STYLE_NAME);
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
	protected String getSmallHeaderStyleName()
	{
		return SIDE_MENU_DISPOSAL_SMALL_HEADER_PANEL;
	}
	
	
	static class SmallLayoutBuilder implements LayoutBuilder
	{
		private static final String SIDE_MENU_DISPOSAL_MENU_BUTTON = "faces-SideMenuDisposal-menuButton";
		private static final String SIDE_MENU_DISPOSAL_MENU_BUTTON_CONTAINER = "faces-SideMenuDisposal-menuButtonContainer";
		private static final String SIDE_MENU_DISPOSAL_SMALL_HEADER_WRAPPER = "faces-SideMenuDisposal-smallHeaderWrapper";

		@Override
		public void buildLayout(final BaseMenuDisposal disposal)
		{
			FlowPanel menuButtonPanel = new FlowPanel();
			menuButtonPanel.setStyleName(SIDE_MENU_DISPOSAL_MENU_BUTTON_CONTAINER);
			Button menuButton = new Button();
			menuButton.setStyleName(SIDE_MENU_DISPOSAL_MENU_BUTTON);
			menuButtonPanel.add(menuButton);
			menuButton.addSelectHandler(new SelectHandler(){
				
				@Override
				public void onSelect(SelectEvent event)
				{
					disposal.showSmallMenu();
				}
			});
			
			FlowPanel headerWrapper = new FlowPanel();
			headerWrapper.setStyleName(SIDE_MENU_DISPOSAL_SMALL_HEADER_WRAPPER);
			
			headerWrapper.add(menuButtonPanel);
			headerWrapper.add(disposal.smallHeaderPanel);
			
			disposal.bodyPanel.add(headerWrapper);
			disposal.bodyPanel.add(disposal.menuPanel);
			disposal.bodyPanel.add(disposal.viewContentPanel);
			disposal.bodyPanel.add(disposal.footerPanel);
		}
		

		@Override
		public SizeDisposal getDeviceSize()
		{
			return SizeDisposal.SMALL;
		}
	}
	
	static class LargeLayoutBuilder implements LayoutBuilder
	{
		private static final String CF = "cf";
		private static final String FACES_SIDE_MENU_DISPOSAL_SPLIT_PANEL = "faces-SideMenuDisposal-splitPanel";
		private static final String FACES_SIDE_MENU_DISPOSAL_LAYOUT_WRAPPER_PANEL = "faces-SideMenuDisposal-layoutWrapperPanel";

		@Override
		public void buildLayout(final BaseMenuDisposal disposal)
		{ 
			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName(FACES_SIDE_MENU_DISPOSAL_LAYOUT_WRAPPER_PANEL);
			disposal.headerPanel = new SimplePanel();
			FlowPanel splitPanel = new FlowPanel();
			splitPanel.addStyleName(CF);
			splitPanel.addStyleName(FACES_SIDE_MENU_DISPOSAL_SPLIT_PANEL);
			splitPanel.add(disposal.menuPanel);
			splitPanel.add(disposal.viewContentPanel);
			
			mainPanel.add(disposal.headerPanel);
			mainPanel.add(splitPanel);
			mainPanel.add(disposal.footerPanel);
			disposal.bodyPanel.add(mainPanel);
		}
		
		@Override
		public SizeDisposal getDeviceSize()
		{
			return SizeDisposal.LARGE;
		}
	}
	
	public enum SideDisposalMenuType
	{
		VERTICAL_TREE, VERTICAL_SLIDE, VERTICAL_ACCORDION, VERTICAL_DROPDOWN;
	}
	
	public enum MenuPosition
	{
		LEFT,RIGHT;
	}
	
	static interface LayoutBuilder
	{
		void buildLayout(BaseMenuDisposal disposal);
		SizeDisposal getDeviceSize();
	}
	
	public void setMenuPositioning(MenuPosition position)
	{
		if(MenuPosition.RIGHT.equals(position))
		{
			addStyleName(RIGHT_MENU_POSITION);
		}
	}
}
