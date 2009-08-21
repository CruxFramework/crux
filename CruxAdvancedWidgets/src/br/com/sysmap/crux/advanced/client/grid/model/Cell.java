package br.com.sysmap.crux.advanced.client.grid.model;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Cell extends Composite {

	private SimplePanel basePanel;
	private Row row;
	private boolean fireEvents;
	private AbstractGrid grid;
	
	protected Cell(boolean fireEvents)
	{
		this.fireEvents = fireEvents;
		
		basePanel = new SimplePanel();
		initWidget(basePanel);
		basePanel.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		basePanel.getElement().getStyle().setProperty("overflow", "hidden");
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONDBLCLICK);
	}
	
	protected Cell(Widget widget, boolean fireEvents)
	{
		this(fireEvents);
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

	public void setGrid(AbstractGrid grid)
	{
		this.grid = grid;		
	}
	
	@Override
	public void onBrowserEvent(Event event)
	{
		if(fireEvents)
		{		
			int type = DOM.eventGetType(event);
		
			if(type == Event.ONCLICK)
			{
				boolean status = row.isSelected();
				row.markAsSelected(!status);
				grid.onSelectRow(!status, row);
				
				grid.fireRowClickEvent(row);
			}
				
			if(type == Event.ONDBLCLICK)
			{
				grid.fireRowDoubleClickEvent(row);
			}
		}

		super.onBrowserEvent(event);
	}
}