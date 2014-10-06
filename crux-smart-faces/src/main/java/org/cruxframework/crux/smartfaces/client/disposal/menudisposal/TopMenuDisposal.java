/*
2 * Copyright 2011 cruxframework.org.
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
 * A component that defines a page layout. It is a view container and has panel to render a header, a footer and an automatically view-binded menu
 * @author wesley.diniz
 */
public class TopMenuDisposal extends BaseMenuDisposal
{
	public static final String DEFAULT_STYLE_NAME = "faces-TopMenuDisposal";

	private static final String MENU_PANEL_STYLE = "faces-TopMenuDisposal-menuPanel";
	private static final String HEADER_PANEL_STYLE = "faces-TopMenuDisposal-headerPanel";
	private static final String FOOTER_PANEL_STYLE = "faces-TopMenuDisposal-footerPanel";
	private static final String CONTENT_PANEL_STYLE = "faces-TopMenuDisposal-contentPanel";
	private static final String TOP_MENU_DISPOSAL_SMALL_HEADER_PANEL = "faces-TopMenuDisposal-smallHeaderPanel";

	private LayoutBuilder layoutBuilder = GWT.create(LayoutBuilder.class);

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
		return CONTENT_PANEL_STYLE;
	}
	
	@Override
	protected String getSmallHeaderStyleName()
	{
		return TOP_MENU_DISPOSAL_SMALL_HEADER_PANEL;
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		
		if (!add)
		{
			getLayoutBuilder().setStyleName(this);
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneTopMenuDisposal());
		}
	}
	
	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);

	    getLayoutBuilder().setStyleName(this);
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneTopMenuDisposal());
	}
	
	static class SmallLayoutBuilder implements LayoutBuilder
	{
		private static final String TOP_MENU_DISPOSAL_SMALL_HEADER_WRAPPER = "faces-TopMenuDisposal-smallHeaderWrapper";
		private static final String TOP_MENU_DISPOSAL_MENU_BUTTON = "faces-TopMenuDisposal-menuButton";
		private static final String TOP_MENU_DISPOSAL_MENU_BUTTON_CONTAINER = "faces-TopMenuDisposal-menuButtonContainer";


		@Override
		public void buildLayout(final BaseMenuDisposal disposal)
		{
			FacesBackboneResourcesSmall.INSTANCE.css().ensureInjected();

			FlowPanel menuButtonPanel = new FlowPanel();
			menuButtonPanel.setStyleName(TOP_MENU_DISPOSAL_MENU_BUTTON_CONTAINER);
			Button menuButton = new Button();
			menuButton.setStyleName(TOP_MENU_DISPOSAL_MENU_BUTTON);
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
			headerWrapper.setStyleName(TOP_MENU_DISPOSAL_SMALL_HEADER_WRAPPER);
			
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
			disposal.addStyleName(FacesBackboneResourcesSmall.INSTANCE.css().facesBackboneTopMenuDisposal());
        }
	}
	
	static class LargeLayoutBuilder implements LayoutBuilder
	{
		@Override
		public void buildLayout(BaseMenuDisposal disposal)
		{
			FacesBackboneResourcesLarge.INSTANCE.css().ensureInjected();

			disposal.headerPanel.setStyleName(disposal.getHeaderStyleName());
			disposal.bodyPanel.add(disposal.headerPanel);
			disposal.bodyPanel.add(disposal.menuPanel);
			disposal.bodyPanel.add(disposal.viewContentPanel);
			disposal.bodyPanel.add(disposal.footerPanel);
		}

		@Override
        public void setStyleName(BaseMenuDisposal disposal)
        {
			disposal.addStyleName(FacesBackboneResourcesLarge.INSTANCE.css().facesBackboneTopMenuDisposal());
        }
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
