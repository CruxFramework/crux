package org.cruxframework.crux.widgets.client.grid;

import org.cruxframework.crux.core.client.collection.FastList;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;


/**
 * Tableless implementation to be used as grid's substructure element.
 * 
 * @author Gesse Dafe
 */
public class FlexTablelessGridStructure extends AbstractTablelessGridStructure implements GridBaseFlexTable
{
	/**
	 * @param grid
	 */
	public FlexTablelessGridStructure(AbstractGrid<?> grid) 
	{
		super(grid);
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseFlexTable#joinCells(int)
	 */
	public void joinCells(int row) 
	{
		Line line = getLines().getLine(row);
		
		if(line.getWidgetCount() > 0)
		{
			line.clear();
		}
		
		Cell cell = new Cell(this);
		cell.setWidth("100%");
		cell.setHeight("auto");
		cell.getElement().getStyle().setMarginRight(getCellSpacing(), Unit.PX);		
		cell.getElement().getStyle().setPadding(getCellPadding(), Unit.PX);
		cell.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		cell.getElement().getStyle().setPosition(Position.RELATIVE);
		
		line.add(cell);
	}

	@Override
	protected void updateCellsDimensions(int lineIndex, FastList<Integer> columnWidths) 
	{
		if(!isDetailLine(lineIndex))
		{
			super.updateCellsDimensions(lineIndex, columnWidths);
		}
	}

	/**
	 * Checks is a line is a detail container
	 * @param lineIndex
	 * @return
	 */
	private boolean isDetailLine(int lineIndex) 
	{
		return lineIndex != 0 && lineIndex % 2 == 0;
	}

	@Override
	protected boolean canFreezeColumns(int lineIndex) 
	{
		return !isDetailLine(lineIndex);
	}
}
