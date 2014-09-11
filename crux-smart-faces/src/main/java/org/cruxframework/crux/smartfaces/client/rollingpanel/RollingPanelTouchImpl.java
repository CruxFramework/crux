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
package org.cruxframework.crux.smartfaces.client.rollingpanel;

import org.cruxframework.crux.smartfaces.client.css.FacesResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class RollingPanelTouchImpl extends Composite implements RollingPanel.PanelImplementation
{
	private ScrollPanel itemsScrollPanel;
	protected FlowPanel itemsPanel;
	
	private boolean scrollToAddedWidgets = false;

	public RollingPanelTouchImpl()
    {
		itemsScrollPanel = new ScrollPanel();
		itemsPanel = new FlowPanel();
		itemsPanel.setStyleName(FacesResources.INSTANCE.css().flexBoxHorizontalContainer());
		itemsScrollPanel.add(this.itemsPanel);
		
		initWidget(itemsScrollPanel);
    }
	
	@Override
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

	@Override
	public void clear()
	{
		this.itemsPanel.clear();
	}

	@Override
	public int getScrollPosition()
	{
		return itemsScrollPanel.getElement().getScrollLeft();
	}

	@Override
	public Widget getWidget(int i)
    {
	    return itemsPanel.getWidget(i);
    }

	@Override
	public int getWidgetCount()
    {
	    return itemsPanel.getWidgetCount();
    }

	@Override
	public int getWidgetIndex(Widget child)
    {
	    return itemsPanel.getWidgetIndex(child);
    }

	@Override
	public void insert(final Widget widget, int i)
    {
	    itemsPanel.insert(widget, i);
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
	
	@Override
	public boolean isScrollToAddedWidgets()
    {
    	return scrollToAddedWidgets;
    }

	@Override
	public boolean remove(int index)
    {
	    boolean ret = itemsPanel.remove(index);
		return ret;
    }
	
	@Override
	public void remove(Widget toRemove)
    {
		itemsPanel.remove(toRemove);
    }
	
	@Override
	public void scrollToWidget(Widget widget)
	{
		itemsScrollPanel.ensureVisible(widget);
	}
	
	@Override
	public void setScrollPosition(int position)
	{
		itemsScrollPanel.setHorizontalScrollPosition(position);
	}

	@Override
	public void setScrollToAddedWidgets(boolean scrollToAddedWidgets)
    {
    	this.scrollToAddedWidgets = scrollToAddedWidgets;
    }
}
