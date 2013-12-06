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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class RollingPanelTouchImpl extends Composite implements RollingPanel.PanelImplementation
{
	private ScrollPanel itemsScrollPanel;
	protected CellPanel itemsPanel;
	
	private boolean scrollToAddedWidgets = false;

	public RollingPanelTouchImpl()
    {
		this.itemsScrollPanel = new ScrollPanel();
		
		this.itemsScrollPanel.setWidth("100%");
		this.itemsPanel = new HorizontalPanel();
		this.itemsScrollPanel.add(this.itemsPanel);
		
		initWidget(itemsScrollPanel);
		setSpacing(0);
		setStyleName("crux-RollingPanel");
    }
	
	/**
	 * @param child
	 */
	public void add(final Widget child)
	{
		this.itemsPanel.add(child);
		if (scrollToAddedWidgets)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				public void execute()
				{
					scrollToWidget(child);
				}
			});
		}
	}

	/**
	 * 
	 */
	public void clear()
	{
		this.itemsPanel.clear();
	}

	/**
	 * @return
	 */
	public int getScrollPosition()
	{
		return itemsScrollPanel.getElement().getScrollLeft();
	}

	/**
	 * @return
	 */
	public int getSpacing()
	{
		return itemsPanel.getSpacing();
	}

	/**
	 * @param i
	 * @return
	 */
	public Widget getWidget(int i)
    {
	    return itemsPanel.getWidget(i);
    }

	/**
	 * @return
	 */
	public int getWidgetCount()
    {
	    return itemsPanel.getWidgetCount();
    }

	/**
	 * @see com.google.gwt.user.client.ui.IndexedPanel#getWidgetIndex(com.google.gwt.user.client.ui.Widget)
	 */
	public int getWidgetIndex(Widget child)
    {
	    return ((InsertPanel)itemsPanel).getWidgetIndex(child);
    }

	/**
	 * @param widget
	 * @param i
	 */
	public void insert(final Widget widget, int i)
    {
	    ((InsertPanel)itemsPanel).insert(widget, i);
		if (scrollToAddedWidgets)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				public void execute()
				{
					scrollToWidget(widget);
				}
			});
		}
    }
	
	/**
	 * @return
	 */
	public boolean isScrollToAddedWidgets()
    {
    	return scrollToAddedWidgets;
    }

	/**
	 * @see com.google.gwt.user.client.ui.IndexedPanel#remove(int)
	 */
	public boolean remove(int index)
    {
	    boolean ret = ((InsertPanel)itemsPanel).remove(index);
		return ret;
    }
	
	/**
	 * @param toRemove
	 */
	public void remove(Widget toRemove)
    {
		itemsPanel.remove(toRemove);
    }
	
	/**
	 * @param widget
	 */
	public void scrollToWidget(Widget widget)
	{
		itemsScrollPanel.ensureVisible(widget);
	}
	
	/**
	 * @param child
	 * @param cellHeight
	 */
	public void setCellHeight(Widget child, String cellHeight)
    {
		this.itemsPanel.setCellHeight(child, cellHeight);
    }
	
	/**
	 * @param w
	 * @param align
	 */
	public void setCellHorizontalAlignment(Widget w, HorizontalAlignmentConstant align)
	{
		this.itemsPanel.setCellHorizontalAlignment(w, align);
	}	
	
	/**
	 * @param verticalAlign
	 */
	public void setCellVerticalAlignment(Widget w, VerticalAlignmentConstant verticalAlign)
    {
		this.itemsPanel.setCellVerticalAlignment(w, verticalAlign);
    }	

	/**
	 * @param child
	 * @param cellWidth
	 */
	public void setCellWidth(Widget child, String cellWidth)
    {
		this.itemsPanel.setCellWidth(child, cellWidth);
    }
	
	/**
	 * @param position
	 */
	public void setScrollPosition(int position)
	{
		itemsScrollPanel.setHorizontalScrollPosition(position);
	}

	/**
	 * @param scrollToAddedWidgets
	 */
	public void setScrollToAddedWidgets(boolean scrollToAddedWidgets)
    {
    	this.scrollToAddedWidgets = scrollToAddedWidgets;
    }
	
	/**
	 * @param spacing
	 */
	public void setSpacing(int spacing)
	{
		itemsPanel.setSpacing(spacing);
	}	
}
