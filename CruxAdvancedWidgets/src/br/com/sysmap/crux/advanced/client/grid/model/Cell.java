package br.com.sysmap.crux.advanced.client.grid.model;

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

	@SuppressWarnings("unchecked")
	private AbstractGrid grid;
	private boolean selectRowOnClick;

	protected Cell(boolean fireEvents, boolean selectRowOnClick)
	{
		this.fireEvents = fireEvents;
		this.selectRowOnClick = selectRowOnClick;

		basePanel = new SimplePanel();
		initWidget(basePanel);
		basePanel.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		basePanel.getElement().getStyle().setProperty("overflow", "hidden");
		sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONDBLCLICK);
	}

	protected Cell(Widget widget, boolean fireEvents, boolean selectRowOnClick)
	{
		this(fireEvents, selectRowOnClick);
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

	@SuppressWarnings("unchecked")
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
				grid.onSelectRow(!status, row);
			}

			if(fireEvents && row.isEnabled())
			{
				grid.fireRowClickEvent(row);
			}
		}

		if (type == Event.ONDBLCLICK)
		{
			if(fireEvents && row.isEnabled())
			{
				grid.fireRowDoubleClickEvent(row);
			}
		}

		super.onBrowserEvent(event);
	}
}