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
 * A component that defines a page layout. It is a view container and has panel to render a header, a footer and an automatically view-binded menu
 * @author wesley.diniz
 */
public class TopMenuDisposal extends BaseMenuDisposal
{

	private final String MENU_PANEL_STYLE = "faces-TopMenuDisposal-menuPanel";
	private final String HEADER_PANEL_STYLE = "faces-TopMenuDisposal-headerPanel";
	private final String FOOTER_PANEL_STYLE = "faces-TopMenuDisposal-footerPanel";
	private final String CONTENT_PANEL_STYLE = "faces-TopMenuDisposal-contentPanel";
	private final String TOP_MENU_DISPOSAL_SMALL_HEADER_PANEL = "faces-TopMenuDisposal-smallHeaderPanel";
	
	public final String DEFAULT_STYLE_NAME = "faces-TopMenuDisposal";

	@Override
	protected void buildLayout()
	{
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		FacesBackboneResourcesLarge.INSTANCE.css().ensureInjected();
		FacesBackboneResourcesSmall.INSTANCE.css().ensureInjected();
		LayoutBuilder builder = GWT.create(LayoutBuilder.class);
		builder.buildLayout(this);
		setSizeDisposal(builder.getDeviceSize());
		setStyleName(DEFAULT_STYLE_NAME);
	}
	;

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
			if(getSizeDisposal().equals(sizeDisposal.LARGE))
			{
				addStyleName(FacesBackboneResourcesLarge.INSTANCE.css().facesBackboneTopMenuDisposal());
			}
			
			if(getSizeDisposal().equals(sizeDisposal.SMALL))
			{
				addStyleName(FacesBackboneResourcesSmall.INSTANCE.css().facesBackboneTopMenuDisposal());
			}
			
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneTopMenuDisposal());
		}
	}
	
	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    
	    if(getSizeDisposal().equals(sizeDisposal.LARGE))
		{
			addStyleName(FacesBackboneResourcesLarge.INSTANCE.css().facesBackboneTopMenuDisposal());
		}
		
		if(getSizeDisposal().equals(sizeDisposal.SMALL))
		{
			addStyleName(FacesBackboneResourcesSmall.INSTANCE.css().facesBackboneTopMenuDisposal());
		}
		
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneTopMenuDisposal());
	}

	public enum TopDisposalMenuType
	{
		HORIZONTAL_ACCORDION,
		HORIZONTAL_DROPDOWN;
	}
	
	static class SmallLayoutBuilder implements LayoutBuilder
	{
		private static final String TOP_MENU_DISPOSAL_SMALL_HEADER_WRAPPER = "faces-TopMenuDisposal-smallHeaderWrapper";
		private static final String TOP_MENU_DISPOSAL_MENU_BUTTON = "faces-TopMenuDisposal-menuButton";
		private static final String TOP_MENU_DISPOSAL_MENU_BUTTON_CONTAINER = "faces-TopMenuDisposal-menuButtonContainer";


		@Override
		public void buildLayout(final BaseMenuDisposal disposal)
		{
			FlowPanel menuButtonPanel = new FlowPanel();
			menuButtonPanel.setStyleName(TOP_MENU_DISPOSAL_MENU_BUTTON_CONTAINER);
			Button menuButton = new Button();
			menuButton.setStyleName(TOP_MENU_DISPOSAL_MENU_BUTTON);
			menuButtonPanel.add(menuButton);
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
		@Override
		public void buildLayout(BaseMenuDisposal disposal)
		{
			disposal.bodyPanel.add(disposal.headerPanel);
			disposal.bodyPanel.add(disposal.menuPanel);
			disposal.bodyPanel.add(disposal.viewContentPanel);
			disposal.bodyPanel.add(disposal.footerPanel);
		}

		@Override
		public SizeDisposal getDeviceSize()
		{
			return SizeDisposal.LARGE;
		}
	}
	
	static interface LayoutBuilder
	{
		void buildLayout(BaseMenuDisposal disposal);
		SizeDisposal getDeviceSize();
	}
	
	

}
