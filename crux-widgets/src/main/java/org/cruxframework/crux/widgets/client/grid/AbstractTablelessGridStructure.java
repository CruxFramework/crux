package org.cruxframework.crux.widgets.client.grid;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.widgets.client.layout.AutoResizableComposite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tableless implementation to be used as grid's substructure element.
 * 
 * @author Gesse Dafe
 */
abstract class AbstractTablelessGridStructure extends AutoResizableComposite implements GridBaseTable
{
	private Lines lines = new Lines();
	private int cellSpacing = 0; // TODO isso nao pode existir
	private int cellPadding;
	private AbstractGrid<?> grid;
	private CellRenderer cellRenderer = GWT.create(CellRenderer.class);
	
	/**
	 * Constructor
	 * @param grid
	 */
	public AbstractTablelessGridStructure(AbstractGrid<?> grid) 
	{
		this.grid = grid;
		initWidget(lines);
		setStyleName("crux-AbstractTablelessGridStructure");
		if(grid.hasFrozenCells())
		{
			this.grid.getScrollingArea().addScrollHandler(new FrozenCellsScrollHandler(this));
		}
	}
	
	@Override
	protected void onResize(int containerHeight, int containerWidth) 
	{
		int numLines = lines.getWidgetCount();
		
		if(numLines > 0)
		{
			boolean isPercentual = hasPercentualColumnWidths();
			
			FastList<Integer> columnWidths = calculateColumnWidths(containerWidth);
			String lineWidth = calculateRowWidth(isPercentual, columnWidths);
		
			for(int i = 0; i < numLines; i++)
			{
				Line line = getLines().getLine(i);
				line.setWidth(lineWidth);
				updateCellsDimensions(i, columnWidths);
			}
		}
	}

	/**
	 * Calculates the widht of the row based on the columns widths
	 * @param isPercentual
	 * @param columnWidths
	 * @return
	 */
	private String calculateRowWidth(boolean isPercentual, FastList<Integer> columnWidths) 
	{
		String lineWidth = "100%";
		
		if(!isPercentual)
		{
			int sumWidths = 0;
			for(int i = 0; i < columnWidths.size(); i++)
			{
				sumWidths += columnWidths.get(i);
			}
			
			lineWidth = sumWidths + "px";
		}
		return lineWidth;
	}

	/**
	 * Calculates the absolute width of each grid column
	 * @param containerWidth
	 * @return
	 */
	private FastList<Integer> calculateColumnWidths(int containerWidth) 
	{
		FastList<Integer> columnWidths = new FastList<Integer>();

		ColumnDefinitions defs = grid.getColumnDefinitions();
		int numCols = lines.getLine(0).getWidgetCount();
		int offset = (grid.hasRowDetailsIconColumn() ? 1 : 0) + (grid.hasSelectionColumn() ? 1 : 0);
		for(int i = 0; i < numCols; i++)
		{
			if(i < offset)
			{
				columnWidths.add(30);
			}
			
			if(i >= offset)
			{
				ColumnDefinition def = defs.getVisibleColumnDefinition(i - offset);
				String width = def.getWidth();
				int absoluteWidth = 0;
				if(hasPercentualColumnWidths())
				{
					absoluteWidth = calculateAbsoluteColumnWidth(width, containerWidth);
				}
				else
				{
					absoluteWidth = getIntegerPixelMeasure(width);
				}
				
				columnWidths.add(absoluteWidth);
			}
		}
		
		return columnWidths;
	}
	
	/**
	 * Parses a px value and returns an integer
	 * @param measure
	 * @return
	 */
	protected int getIntegerPixelMeasure(String measure) 
	{
		measure = measure.replace("px", "").trim();
		if(measure.length() > 0)
		{
			return (int) Math.round(Double.parseDouble(measure));
		}
		return 0;
	}

	/**
	 * Simulates the percentual widths by calculating an absolute for each column, based on the container's width
	 * @param columnWidths
	 * @param containerWidth
	 * @return true if at least one column has a percentual width declaration
	 */
	private int calculateAbsoluteColumnWidth(String percentualWidth, int containerWidth) 
	{
		percentualWidth = percentualWidth.replace("%", "").trim();
		double value = Double.valueOf(percentualWidth);
		value = Math.floor(value / 100.0 * containerWidth);
		value = value - cellSpacing;
		return (int) value;
	}
	
	/**
	 * Checks if the columns have percentual widths
	 * @return true if at least one column has a percentual width declaration
	 */
	private boolean hasPercentualColumnWidths() 
	{
		FastList<ColumnDefinition> defs = grid.getColumnDefinitions().getDefinitions();
		
		int numCols = defs.size();
		for (int i = 0; i < numCols; i++) 
		{
			ColumnDefinition def = defs.get(i);
			String width = def.getWidth();
			if(isPercentual(width))
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @param lineIndex
	 * @param columnWidths
	 */
	protected void updateCellsDimensions(int lineIndex, FastList<Integer> columnWidths) 
	{
		int numCells = columnWidths.size();
		
		for (int i = 0; i < numCells; i++) 
		{
			int width = columnWidths.get(i);
			Line line = lines.getLine(lineIndex);
			Cell cell = line.getCell(i);
			cell.setWidth(width + "px");
			cell.setHeight("100%");
		}
	}

	public void setCellAlignment(int index, int colIndex, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign) 
	{
		Widget innerWidget = lines.getLine(index).getCell(colIndex).getWidget();
		if(innerWidget != null)
		{
			innerWidget.getElement().getParentElement().getStyle().setProperty("textAlign", horizontalAlign.getTextAlignString());
			innerWidget.getElement().getParentElement().getStyle().setProperty("verticalAlign", verticalAlign.getVerticalAlignString());
			
			if(innerWidget instanceof Label)
			{
				((Label) innerWidget).setHorizontalAlignment(horizontalAlign);
			}
		}
	}

	public void setCellWidth(int index, int colIndex, String width) 
	{
		Element cellElement = lines.getLine(index).getCell(colIndex).getElement();
		cellElement.getStyle().setProperty("width", width);
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#removeAllRows()
	 */
	public void removeAllRows() 
	{
		lines.clear();
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#resize(int, int)
	 */
	public void resize(int rowCount, int columnCount) 
	{
		while(lines.getWidgetCount() > rowCount)
		{
			lines.remove(lines.getWidgetCount() - 1);
		}
		
		while(lines.getWidgetCount() < rowCount)
		{
			lines.add(createRow());
		}
		
		for(int i = 0, numLines = lines.getWidgetCount(); i < numLines; i++)
		{
			Line line = lines.getLine(i);
			
			while(line.getWidgetCount() > columnCount)
			{
				line.remove(line.getWidgetCount() - 1);
			}
			
			while(line.getWidgetCount() < columnCount)
			{
				line.add(createCell());
			}
		}
	}
	
	/**
	 * Creates a grid row
	 * @return
	 */
	private Widget createRow() 
	{
		Line line = new Line();
		line.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		line.getElement().getStyle().setFloat(Float.LEFT);
		line.getElement().getStyle().setProperty("clear", "both");
		line.getElement().getStyle().setMarginBottom(cellSpacing, Unit.PX);
		line.getElement().getStyle().setPosition(Position.RELATIVE);
		return line;
	}
	
	/**
	 * Creates a grid cell
	 * @return
	 */
	private Widget createCell() 
	{
		Cell cell = new Cell(this);
		cellRenderer.updateCellStyle(cell, cellSpacing, cellPadding);
		return cell;
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#setWidget(int, int, com.google.gwt.user.client.ui.Widget)
	 */
	public void setWidget(int index, int column, Widget widget) 
	{
		Cell cell = lines.getLine(index).getCell(column);
		if(cell.getWidget() != null)
		{
			cell.clear();
		}
		cell.add(widget);
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#getWidget(int, int)
	 */
	public Widget getWidget(int index, int column) 
	{
		return lines.getLine(index).getCell(column).getWidget();
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#getCellElement(int, int)
	 */
	public Element getCellElement(int row, int col) 
	{
		Line line = lines.getLine(row);
		return line.getCell(col).getElement();
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#getRowElement(int)
	 */
	public Element getRowElement(int row) 
	{
		return lines.getLine(row).getElement();
	}

	/**
	 * Dummy implementation. There is no table body in this widget.
	 */
	public Element getBodyElement() 
	{
		return new Line().getElement();
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#setCellSpacing(int)
	 */
	public void setCellSpacing(int cellSpacing) 
	{
		this.cellSpacing = 0;
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#setCellPadding(int)
	 */
	public void setCellPadding(int cellPadding) 
	{
		this.cellPadding = cellPadding;
	}
	
	/**
	 * Checks if a measure is percentual
	 * @param measure
	 * @return
	 */
	private boolean isPercentual(String measure) 
	{
		return measure != null && measure.trim().endsWith("%");
	}
	
	/**
	 * Represents a line
	 * @author Gesse Dafe
	 */
	static class Line extends com.google.gwt.user.client.ui.FlowPanel
	{
		Cell getCell(int index)
		{
			return (Cell) getWidget(index);
		}
	}
	
	/**
	 * Represents the grid lines
	 * @author Gesse Dafe
	 */
	static class Lines extends com.google.gwt.user.client.ui.FlowPanel
	{
		Line getLine(int index)
		{
			return (Line) getWidget(index);
		}
	}
	
	/**
	 * Represents a cell
	 * @author Gesse Dafe
	 */
	static class Cell extends com.google.gwt.user.client.ui.HorizontalPanel
	{	
		private AbstractTablelessGridStructure struct;

		public Cell(AbstractTablelessGridStructure struct)
		{
			this.struct = struct;
			this.getElement().getStyle().setTableLayout(TableLayout.FIXED);
		}
		
		public Widget getWidget() 
		{
			if(getWidgetCount() > 0)
			{
				return super.getWidget(0);
			}
			
			return null;
		}
		
		@Override
		public void add(Widget w) 
		{
			super.add(w);
			setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
			w.getElement().getParentElement().getStyle().setPadding(struct.getCellPadding(), Unit.PX);
		}
	}

	/**
	 * This one and its subclasses handle drawing differences among various browsers.
	 * @author Gesse Dafe
	 */
	public static class CellRenderer
	{
		final void updateCellStyle(Widget cell, int cellSpacing, int cellPadding)
		{
			cell.getElement().getStyle().setMarginRight(cellSpacing, Unit.PX);		
			cell.getElement().getStyle().setPadding(cellPadding, Unit.PX);
			cell.getElement().getStyle().setOverflow(Overflow.HIDDEN);
			cell.getElement().getStyle().setPosition(Position.RELATIVE);
			setDisplay(cell);
		}
		
		protected void setDisplay(Widget cell)
		{
			cell.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}
	}
	
	/**
	 * Internet Explorer
	 * @author Gesse Dafe
	 */
	public static class CellRendererIE extends CellRenderer
	{
		@Override
		protected void setDisplay(Widget cell)
		{
			cell.getElement().getStyle().setDisplay(Display.INLINE);
		}
	}

	/**
	 * @return the lines
	 */
	protected Lines getLines() 
	{
		return lines;
	}

	/**
	 * @return the cellSpacing
	 */
	protected int getCellSpacing() 
	{
		return cellSpacing;
	}

	/**
	 * @return the cellPadding
	 */
	protected int getCellPadding() 
	{
		return cellPadding;
	}

	/**
	 * @return the grid
	 */
	protected AbstractGrid<?> getGrid() 
	{
		return grid;
	}

	protected abstract boolean canFreezeColumns(int lineIndex);
	
	public void onAfterRender() 
	{
		Element container = this.getElement().getParentElement().cast();
		onResize(container.getClientHeight(), container.getClientWidth());
	}
}
