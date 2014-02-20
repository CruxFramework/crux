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
package org.cruxframework.crux.widgets.client.rollingpanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class RollingPanelNoTouchImpl extends Composite implements RollingPanel.PanelImplementation
{
	private CustomRollingPanel panel;

	public RollingPanelNoTouchImpl()
    {
        panel = new CustomRollingPanel();
		initWidget(panel);
    }

	@Override
    public Widget getWidget(int index)
    {
	    return panel.getWidget(index);
    }

	@Override
    public int getWidgetCount()
    {
	    return panel.getWidgetCount();
    }

	@Override
    public int getWidgetIndex(Widget child)
    {
	    return panel.getWidgetIndex(child);
    }

	@Override
    public boolean remove(int index)
    {
	    return panel.remove(index);
    }

	@Override
    public void add(Widget w)
    {
		panel.add(w);
    }

	@Override
    public void insert(Widget w, int beforeIndex)
    {
		panel.insert(w, beforeIndex);
    }

	@Override
    public void clear()
    {
		panel.clear();
    }

	@Override
    public int getScrollPosition()
    {
        return panel.getScrollPosition();
    }

	@Override
    public int getSpacing()
    {
        return panel.getSpacing();
    }

	@Override
    public boolean isScrollToAddedWidgets()
    {
        return panel.isScrollToAddedWidgets();
    }

	@Override
    public void remove(Widget toRemove)
    {
		panel.remove(toRemove);
    }

	@Override
    public void scrollToWidget(Widget widget)
    {
		panel.scrollToWidget(widget);
    }

	@Override
    public void setCellHeight(Widget child, String cellHeight)
    {
		panel.setCellHeight(child, cellHeight);
    }

	@Override
    public void setCellHorizontalAlignment(Widget w, HorizontalAlignmentConstant align)
    {
		panel.setCellHorizontalAlignment(w, align);
    }

	@Override
    public void setCellVerticalAlignment(Widget w, VerticalAlignmentConstant verticalAlign)
    {
		panel.setCellVerticalAlignment(w, verticalAlign);
    }

	@Override
    public void setCellWidth(Widget child, String cellWidth)
    {
		panel.setCellWidth(child, cellWidth);
    }

	@Override
    public void setScrollPosition(int position)
    {
		panel.setScrollPosition(position);			
    }

	@Override
    public void setScrollToAddedWidgets(boolean scrollToAddedWidgets)
    {
		panel.setScrollToAddedWidgets(scrollToAddedWidgets);
    }

	@Override
    public void setSpacing(int spacing)
    {
		panel.setSpacing(spacing);
    }
}
