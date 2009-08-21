package br.com.sysmap.crux.advanced.client.grid.model;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Cell extends Composite {

	private SimplePanel basePanel;
	private Row row;
	private AbstractGrid<?, ?> grid;
	
	protected Cell()
	{
		basePanel = new SimplePanel();
		initWidget(basePanel);
		basePanel.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		basePanel.getElement().getStyle().setProperty("overflow", "hidden");
	}
	
	protected Cell(Widget widget)
	{
		this();
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

	public void setGrid(AbstractGrid<?, ?> grid)
	{
		this.grid = grid;		
	}
}