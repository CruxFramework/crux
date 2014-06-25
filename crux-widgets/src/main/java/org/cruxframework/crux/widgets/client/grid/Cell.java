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
package org.cruxframework.crux.widgets.client.grid;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Cell extends Composite
{

	private SimplePanel basePanel;
	private Row row;
	private boolean fireEvents;

	@SuppressWarnings("rawtypes")

	private AbstractGrid grid;
	private boolean selectRowOnClick;
	private boolean highlightRowOnMouseOver;
	
	protected Cell(boolean fireEvents, boolean selectRowOnClick, boolean highlightRowOnMouseOver, boolean wrapLine, boolean truncate)
	{
		this.fireEvents = fireEvents;
		this.selectRowOnClick = selectRowOnClick;
		this.highlightRowOnMouseOver = highlightRowOnMouseOver;

		basePanel = new SimplePanel();
		initWidget(basePanel);
		setStyleName("crux-Cell");
		
		if(!wrapLine)
		{
			basePanel.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		}
		
		if(truncate)
		{
			basePanel.getElement().getStyle().setProperty("overflow", "hidden");
		}
		
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONDBLCLICK);
	}

	protected Cell(Widget widget, boolean fireEvents, boolean selectRowOnClick, boolean highlightRowOnMouseOver, boolean wrapLine, boolean truncate)
	{
		this(fireEvents, selectRowOnClick, highlightRowOnMouseOver, wrapLine, truncate);
		this.basePanel.add(widget);
	}

	protected void setCellWidget(Widget widget)
	{
		basePanel.add(widget);
	}

	protected Widget getCellWidget()
	{
		return basePanel.getWidget();
	}

	public void setRow(Row row)
	{
		this.row = row;
	}

	@SuppressWarnings("rawtypes")
	public void setGrid(AbstractGrid grid)
	{
		this.grid = grid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onBrowserEvent(Event event)
	{
		int type = DOM.eventGetType(event);

		if (type == Event.ONCLICK)
		{
			if(selectRowOnClick && row.isEnabled())
			{
				boolean status = row.isSelected();
				grid.onSelectRow(!status, row, true);
				
			}
			
			if(fireEvents)
			{
				grid.fireRowClickEvent(row);
			}
		}

		if (type == Event.ONDBLCLICK)
		{
			if(grid instanceof Grid && ((Grid)grid).isEditable())
			{
				if(fireEvents && row.isEnabled())
				{
					grid.fireBeforeRowEditEvent(row);
					((Grid)grid).makeEditable((DataRow) row, this);
				}
			}
			
			if(fireEvents && row.isEnabled())
			{
				grid.fireRowDoubleClickEvent(row);
			}
			return;
		}
		
		if(highlightRowOnMouseOver)
		{
			if (type == Event.ONMOUSEOVER)
			{
				if(row.isEnabled())
				{
					row.addStyleDependentName("highlighted");
				}
				
				return;
			}
			
			if (type == Event.ONMOUSEOUT)
			{
				if(row.isEnabled())
				{
					row.removeStyleDependentName("highlighted");
				}
				
				return;
			}
		}

		super.onBrowserEvent(event);
	}
}