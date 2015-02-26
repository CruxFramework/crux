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
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.backbone.large.FacesBackboneResourcesLarge;
import org.cruxframework.crux.smartfaces.client.backbone.small.FacesBackboneResourcesSmall;
import org.cruxframework.crux.smartfaces.client.button.Button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author wesley.diniz
 * 
 */
public class SideMenuDisposal extends BaseMenuDisposal
{
	public static final String DEFAULT_STYLE_NAME = "faces-SideMenuDisposal";
	
	private static final String MENU_PANEL_STYLE = "faces-SideMenuDisposal-menuPanel";
	private static final String HEADER_PANEL_STYLE = "faces-SideMenuDisposal-headerPanel";
	private static final String FOOTER_PANEL_STYLE = "faces-SideMenuDisposal-footerPanel";
	private static final String CONTENT_MENU_STYLE = "faces-SideMenuDisposal-contentPanel";
	private static final String SIDE_MENU_DISPOSAL_SMALL_HEADER_PANEL = "faces-SideMenuDisposal-smallHeaderPanel";
	private static final String RIGHT_MENU_POSITION = "faces-SideMenuDisposal--right";
	
	private LayoutBuilder layoutBuilder = null;
	
	@Override
	protected void buildLayout()
	{
		getLayoutBuilder().buildLayout(this);
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
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		
		if (!add)
		{
			getLayoutBuilder().setStyleName(this);
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneSideMenuDisposal());
		}
	}
	
	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    getLayoutBuilder().setStyleName(this);
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneSideMenuDisposal());
	}
	
	public void setMenuPositioning(MenuPosition position)
	{
		if(MenuPosition.RIGHT.equals(position))
		{
			addStyleName(RIGHT_MENU_POSITION);
		}
		else
		{
			removeStyleName(RIGHT_MENU_POSITION);
		}
	}
	
	static class SmallLayoutBuilder implements LayoutBuilder
	{
		private static final String SIDE_MENU_DISPOSAL_MENU_BUTTON = "faces-SideMenuDisposal-menuButton";
		private static final String SIDE_MENU_DISPOSAL_MENU_BUTTON_CONTAINER = "faces-SideMenuDisposal-menuButtonContainer";
		private static final String SIDE_MENU_DISPOSAL_SMALL_HEADER_WRAPPER = "faces-SideMenuDisposal-smallHeaderWrapper";

		@Override
		public void buildLayout(final BaseMenuDisposal disposal)
		{
			FacesBackboneResourcesSmall.INSTANCE.css().ensureInjected();

			FlowPanel menuButtonPanel = new FlowPanel();
			menuButtonPanel.setStyleName(SIDE_MENU_DISPOSAL_MENU_BUTTON_CONTAINER);
			Button menuButton = new Button();
			menuButton.setStyleName(SIDE_MENU_DISPOSAL_MENU_BUTTON);
			menuButtonPanel.add(menuButton);
			disposal.headerPanel.setStyleName(disposal.getSmallHeaderStyleName());
			
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
			headerWrapper.add(disposal.headerPanel);
			
			disposal.bodyPanel.add(headerWrapper);
			disposal.bodyPanel.add(disposal.menuPanel);
			disposal.bodyPanel.add(disposal.viewContentPanel);
			disposal.bodyPanel.add(disposal.footerPanel);
		}
		
		@Override
        public void setStyleName(BaseMenuDisposal disposal)
        {
			disposal.addStyleName(FacesBackboneResourcesSmall.INSTANCE.css().facesBackboneSideMenuDisposal());
        }
	}
	
	static class LargeLayoutBuilder implements LayoutBuilder
	{
		private static final String FACES_SIDE_MENU_DISPOSAL_SPLIT_PANEL = "faces-SideMenuDisposal-splitPanel";
		private static final String FACES_SIDE_MENU_DISPOSAL_LAYOUT_WRAPPER_PANEL = "faces-SideMenuDisposal-layoutWrapperPanel";

		@Override
		public void buildLayout(final BaseMenuDisposal disposal)
		{ 
			FacesBackboneResourcesLarge.INSTANCE.css().ensureInjected();

			FlowPanel mainPanel = new FlowPanel();
			mainPanel.setStyleName(FACES_SIDE_MENU_DISPOSAL_LAYOUT_WRAPPER_PANEL);
			
			disposal.headerPanel.setStyleName(disposal.getHeaderStyleName());
			
			FlowPanel splitPanel = new FlowPanel();
			splitPanel.addStyleName(FACES_SIDE_MENU_DISPOSAL_SPLIT_PANEL);
			splitPanel.add(disposal.menuPanel);
			splitPanel.add(disposal.viewContentPanel);
			
			mainPanel.add(disposal.headerPanel);
			mainPanel.add(splitPanel);
			mainPanel.add(disposal.footerPanel);
			disposal.bodyPanel.add(mainPanel);
		}

		@Override
        public void setStyleName(BaseMenuDisposal disposal)
        {
			disposal.addStyleName(FacesBackboneResourcesLarge.INSTANCE.css().facesBackboneSideMenuDisposal());
        }
	}
	
	public static enum MenuPosition
	{
		LEFT,RIGHT;
	}
	
	static interface LayoutBuilder
	{
		void buildLayout(BaseMenuDisposal disposal);
		void setStyleName(BaseMenuDisposal disposal);
	}
	
	private LayoutBuilder getLayoutBuilder()
	{
		if(layoutBuilder == null)
		{
			layoutBuilder = GWT.create(LayoutBuilder.class);
		}
		
		return layoutBuilder;
	}
}
