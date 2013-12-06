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
package org.cruxframework.crux.widgets.client.styledpanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Gesse S. F. Dafe
 */
public class StyledPanel extends Composite implements HasHorizontalAlignment, HasVerticalAlignment
{
	private static final String DEFAULT_OUTER_STYLE_NAME = "crux-StyledPanelOuter";
	private static final String DEFAULT_STYLE_NAME = "crux-StyledPanel";
	
	private VerticalAlignmentConstant verticalAlignment = HasVerticalAlignment.ALIGN_MIDDLE;
	private HorizontalAlignmentConstant horizontalAlignment = HasHorizontalAlignment.ALIGN_CENTER;

	private SimplePanel externalPanel = new SimplePanel();
	private SimplePanel internalPanel = new SimplePanel();
	
	public StyledPanel()
	{
		initWidget(externalPanel);
		externalPanel.add(internalPanel);
		
		Style extrnStyle = externalPanel.getElement().getStyle();
		extrnStyle.setProperty("display", "table");
		extrnStyle.setProperty("boxSizing", "border-box");

		Style intrnStyle = internalPanel.getElement().getStyle();
		intrnStyle.setProperty("display", "table-cell");
		intrnStyle.setWidth(100, Unit.PCT);
		
		externalPanel.setStyleName(DEFAULT_OUTER_STYLE_NAME);
		internalPanel.setStyleName(DEFAULT_STYLE_NAME);

		setHorizontalAlignment(horizontalAlignment);
		setVerticalAlignment(verticalAlignment);
	}

	@Override
	public VerticalAlignmentConstant getVerticalAlignment()
	{
		return this.verticalAlignment;
	}

	@Override
	public void setVerticalAlignment(VerticalAlignmentConstant align)
	{
		String verticalAlign = "";

		if (align != null)
		{
			this.verticalAlignment = align;
			
			if (this.verticalAlignment.equals(HasVerticalAlignment.ALIGN_MIDDLE))
			{
				verticalAlign = "middle";
			}
			else if (this.verticalAlignment.equals(HasVerticalAlignment.ALIGN_BOTTOM))
			{
				verticalAlign = "bottom";
			}
		}
		
		Style style = internalPanel.getElement().getStyle();
		style.setProperty("verticalAlign", verticalAlign);
	}

	@Override
	public HorizontalAlignmentConstant getHorizontalAlignment()
	{
		return this.horizontalAlignment;
	}

	@Override
	public void setHorizontalAlignment(HorizontalAlignmentConstant align)
	{
		String marginLeft = "";
		String marginRight = "";

		if (align != null)
		{
			this.horizontalAlignment = align;
			
			if (this.horizontalAlignment.equals(HasHorizontalAlignment.ALIGN_RIGHT))
			{
				marginRight = "auto";
			}
			if (this.horizontalAlignment.equals(HasHorizontalAlignment.ALIGN_CENTER))
			{
				marginLeft = "auto";
				marginRight = "auto";
			}
		}
		
		if(internalPanel.getWidget() != null)
		{
			Style childStyle = internalPanel.getWidget().getElement().getStyle();
			childStyle.setProperty("marginLeft", marginLeft);
			childStyle.setProperty("marginRight", marginRight);
		}
	}
	
	public void add(Widget w)
	{
		internalPanel.add(w);
		setHorizontalAlignment(getHorizontalAlignment());
	}
	
	
}