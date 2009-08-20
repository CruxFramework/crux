package br.com.sysmap.crux.advanced.client.grid.model;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasValue;

public class Row
{
	private AbstractGrid<?, ?> grid;
	private int index;
	private Element elem;
	private boolean hasSelectionCell;
		
	protected Row(int index, Element elem, AbstractGrid<?, ?> grid, boolean hasSelectionCell)
	{
		this.index = index;
		this.elem = elem;
		this.hasSelectionCell = hasSelectionCell;
		this.grid = grid;
	}
	
	public void setCell(Cell cell, String column)
	{
		int colIndex = getColumnIndex(column);
		setCell(cell, colIndex);
	}

	void setCell(Cell cell, int column)
	{
		grid.getTable().setWidget(index, column, cell);
	}
	
	Cell getCell(int column)
	{
		return (Cell) grid.getTable().getWidget(index, column);
	}
	
	public Cell getCell(String column)
	{
		int colIndex = getColumnIndex(column);
		return (Cell) grid.getTable().getWidget(index, colIndex);
	}
	
	/**
	 * Sets the style name of the row
	 * @param rowIndex
	 */
	void setStyle(String styleName)
	{
		elem.setClassName(styleName);
	}
	
	@SuppressWarnings("unchecked")
	public void markAsSelected(boolean selected)
	{
		if(hasSelectionCell)
		{
			HasValue<Boolean> selector = (HasValue<Boolean>) getCell(0).getCellWidget();
			selector.setValue(selected);
		}
		
		setStyle("row-selected");
	}
	
	private int getColumnIndex(String column)
	{
		int colIndex = grid.getColumnDefinitions().getColumnIndex(column);
		
		if(hasSelectionCell)
		{
			colIndex++;
		}
		return colIndex;
	}
}
