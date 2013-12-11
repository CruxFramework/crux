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
package org.cruxframework.crux.widgets.client.collapsepanel;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.views.LazyPanel;
import org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeCollapseEvent;
import org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeCollapseHandler;
import org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeCollapseOrBeforeExpandEvent;
import org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeExpandEvent;
import org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeExpandHandler;
import org.cruxframework.crux.widgets.client.event.collapseexpand.HasBeforeCollapseAndBeforeExpandHandlers;
import org.cruxframework.crux.widgets.client.titlepanel.TitlePanel;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel based on a 3x3 table, with collapse/expand feature. Similar to GWT's DisclosurePanel
 * @author Gesse S. F. Dafe
 */
@Deprecated
@Legacy
public class CollapsePanel extends TitlePanel implements HasBeforeCollapseAndBeforeExpandHandlers
{
	private static final String DEFAULT_STYLE_NAME = "crux-CollapsePanel";
	private boolean collapsible = false;
	private boolean collapsed = false;
	private String height;
	private FocusPanel button = null;
	
	public CollapsePanel()
	{
		this(null, null, null, true, false);
	}
	
	/**
	 * Constructor
	 * @param width
	 * @param height
	 * @param styleName
	 * @param collapsible
	 * @param collapsed
	 */
	public CollapsePanel(String width, String height, String styleName, boolean collapsible, boolean collapsed)
	{
		super(width, collapsible ? "" : height, styleName != null && styleName.trim().length() > 0 ? styleName : DEFAULT_STYLE_NAME);
		this.collapsed = collapsed;
		this.button = createCollapseExpandButton();
		setCollapsible(collapsible);
	}
	
	/**
	 * 
	 * @return
	 */
	private FocusPanel createCollapseExpandButton()
	{
		FocusPanel button = new FocusPanel();
		button.setStyleName("collapseExpandButton");
		button.addStyleDependentName(collapsed ? "collapsed" : "expanded");
		button.addClickHandler(new ExpandButtonClickHandler());
		setTopRightWidget(button);
		DOM.setStyleAttribute(button.getElement(), "marginRight", "4px");
		return button;
	}

	/**
	 * Enables or disables the collapse/expand feature
	 * @param collapsible the collapsible to set
	 */
	public void setCollapsible(boolean collapsible)
	{
		this.collapsible = collapsible;
		getTable().setPropertyString("height", collapsible ? "" : this.height);
		setCollapsed(this.collapsed);
	}
	
	/**
	 * Collapses or expands the panel
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
				button.removeStyleDependentName("expanded");
				button.addStyleDependentName("collapsed");
			}
			else
			{
				button.removeStyleDependentName("collapsed");
				button.addStyleDependentName("expanded");
			}
		}
		
		button.setVisible(collapsible);
	}

	@Override
	public void setTitleWidget(Widget widget)
	{
		super.setTitleWidget(widget);
		
		if (widget != null && (widget instanceof LazyPanel))
		{
			((LazyPanel)widget).ensureWidget();
		}
	}
	
	/**
	 * @return true if collapsible
	 */
	public boolean isCollapsible()
	{
		return collapsible;
	}

	/**
	 * @return true if collapsed
	 */
	public boolean isCollapsed()
	{
		return collapsed;
	}

	@Override
	public void setContentWidget(Widget widget)
	{
		super.setContentWidget(widget);
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.collapseexpand.HasBeforeCollapseHandlers#addBeforeCollapseHandler(org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeCollapseHandler)
	 */
	public HandlerRegistration addBeforeCollapseHandler(BeforeCollapseHandler handler)
	{
		return addHandler(handler, BeforeCollapseEvent.getType());
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.event.collapseexpand.HasBeforeExpandHandlers#addBeforeExpandHandler(org.cruxframework.crux.widgets.client.event.collapseexpand.BeforeExpandHandler)
	 */
	public HandlerRegistration addBeforeExpandHandler(BeforeExpandHandler handler)
	{
		return addHandler(handler, BeforeExpandEvent.getType());
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
 * Button that collapses or expands the panel
 * @author Gesse S. F. Dafe
 */
class ExpandButtonClickHandler implements ClickHandler
{
	/**
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event)
	{
		FocusPanel button = (FocusPanel) event.getSource();
		CollapsePanel panel = (CollapsePanel) button.getParent();
		boolean collapsed = panel.isCollapsed();
		BeforeCollapseOrBeforeExpandEvent preEvent = null;
		
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