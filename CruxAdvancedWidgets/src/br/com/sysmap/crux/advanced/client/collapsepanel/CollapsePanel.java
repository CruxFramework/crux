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
package br.com.sysmap.crux.advanced.client.collapsepanel;

import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeCollapseEvent;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeCollapseHandler;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeExpandEvent;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeExpandHandler;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.CollapseOrExpandEvent;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.HasBeforeCollapseAndBeforeExpandHandlers;
import br.com.sysmap.crux.advanced.client.titlepanel.TitlePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * Panel based on a 3x3 table, with collapse/expand feature. Similar to GWT's DisclosurePanel
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CollapsePanel extends TitlePanel implements HasBeforeCollapseAndBeforeExpandHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-CollapsePanel" ;
	private boolean collapsible = false;
	private boolean collapsed = false;
	private String height;
	private CollapsePanelImages images;
	private Image image = null;
	
	/**
	 * @param width
	 * @param height
	 * @param styleName
	 * @param collapsible
	 * @param expanded
	 */
	public CollapsePanel(String width, String height, String styleName, boolean collapsible, boolean collapsed)
	{
		this(width, height, styleName, collapsible, collapsed, (CollapsePanelImages) GWT.create(CollapsePanelImages.class));
	}	
	
	/**
	 * @param b
	 * @return
	 */
	private Image createCollapseExpandImage()
	{
		AbstractImagePrototype proto = collapsed ? images.expand() : images.collapse();
		Image image = proto.createImage();
		image.addClickHandler(new ExpandButtonClickHandler());
		add(image, getTopCenterRightCell());
		DOM.setStyleAttribute(image.getElement(), "marginRight", "4px");
		return image;
	}

	/**
	 * @param width
	 * @param height
	 * @param styleName
	 * @param collapsible
	 * @param expanded
	 * @param images
	 */
	public CollapsePanel(String width, String height, String styleName, boolean collapsible, boolean collapsed, CollapsePanelImages images)
	{
		super(width, collapsible ? "" : height, styleName != null && styleName.trim().length() > 0 ? styleName : DEFAULT_STYLE_NAME);
		this.images = images;
		this.collapsed = collapsed;
		this.image = createCollapseExpandImage();
		
		setCollapsible(collapsible);
	}
	
	/**
	 * @param collapsible the collapsible to set
	 */
	public void setCollapsible(boolean collapsible)
	{
		this.collapsible = collapsible;
		getTable().setPropertyString("height", collapsible ? "" : this.height);
		setCollapsed(this.collapsed);
	}
	
	/**
	 * @param collapsed the collapsed to set
	 */
	public void setCollapsed(boolean collapsed)
	{
		this.collapsed = collapsed;
		
		String display = collapsed ? "none" : "";
		getMiddleLine().getStyle().setProperty("display", display);
		getBottomLine().getStyle().setProperty("display", display);
		
		if(collapsible)
		{
			if(collapsed)
			{
				images.expand().applyTo(image);
			}
			else
			{
				images.collapse().applyTo(image);
			}
		}
		
		image.setVisible(collapsible);
	}

	/**
	 * @return the collapsible
	 */
	public boolean isCollapsible()
	{
		return collapsible;
	}

	/**
	 * @return the collapsed
	 */
	public boolean isCollapsed()
	{
		return collapsed;
	}

	public HandlerRegistration addBeforeCollapseHandler(BeforeCollapseHandler handler)
	{
		return addHandler(handler, BeforeCollapseEvent.getType());
	}

	public HandlerRegistration addBeforeExpandHandler(BeforeExpandHandler handler)
	{
		return addHandler(handler, BeforeExpandEvent.getType());
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(CollapsePanelImages images)
	{
		this.images = images;
	}
	
	@Override
	public void setHeight(String height)
	{
		if(!this.collapsible)
		{
			super.setHeight(height);
		}
		else
		{
			super.setHeight("");
		}
	}
}

/**
 * Collapses or expands the panel
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
class ExpandButtonClickHandler implements ClickHandler
{
	public void onClick(ClickEvent event)
	{
		Image img = (Image) event.getSource();
		CollapsePanel panel = (CollapsePanel) img.getParent();
		boolean collapsed = panel.isCollapsed();
		CollapseOrExpandEvent preEvent = null;
		
		if(!collapsed)
		{
			preEvent = BeforeCollapseEvent.fire(panel);
		}
		else
		{
			preEvent = BeforeExpandEvent.fire(panel);
		}
		
		if(!preEvent.isCanceled())
		{
			panel.setCollapsed(!panel.isCollapsed());
		}
	}
}