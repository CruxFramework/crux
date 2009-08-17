package br.com.sysmap.crux.advanced.client.grid.model;

import java.util.Date;
import java.util.List;

import br.com.sysmap.crux.advanced.client.grid.datagrid.DataColumnDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for implementing grid widgets. All subclasses of this class must invoke the method <code>render()</code> in their constructors.
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractGrid<T extends DataColumnDefinition> extends Composite {	
	
	private ScrollPanel scrollingArea;
	private GridHtmlTable rows;
	private ColumnDefinitions<T> definitions;
	private Long generatedId =  new Date().getTime();
	private GridLayout gridLayout = GWT.create(GridLayout.class);
	private int headerRowCount = 1;
	
	/**
	 * Constructor
	 * @param columnDefinitions
	 */
	public AbstractGrid(ColumnDefinitions<T> columnDefinitions)
	{
		this.definitions = columnDefinitions;
		
		scrollingArea = new ScrollPanel();
		scrollingArea.setStyleName("crux-Grid");
		
		initWidget(scrollingArea);
		
		rows = new GridHtmlTable();
		rows.setCellSpacing(1);
		rows.setCellPadding(0);		
		gridLayout.setTableLayout(rows);
		
		DeferredCommand.addCommand(new Command()
		{
			public void execute()
			{	
				scrollingArea.add(rows);
			}
		});
	}
	
	/**
	 * Inserts a widget in the given position
	 * @param widget
	 * @param rowIndex
	 * @param column
	 */
	public void setWidget(Widget widget, int rowIndex, String column)
	{
		int colIndex = calculateColumnIndex(column);
		setWidget(widget, rowIndex, colIndex, definitions.getDefinition(column).getWidth());
	}
	
	/**
	 * Gets the widget contained in the given position.
	 * @param rowIndex
	 * @param column
	 * @return the widget
	 */
	public Widget getWidget(int rowIndex, String column)
	{
		int colIndex = calculateColumnIndex(column);
		return getWidget(rowIndex, colIndex);
	}
	
	/**
	 * Creates a new row in the grid
	 * @param beforeIndex
	 * @return the newly created row index
	 */
	public int addRow(int beforeIndex)
	{
		int index = beforeIndex + getHeaderRowCount();
		
		if(getRowsToBeRendered() == 0 || index > rows.getRowCount())
		{
			index = rows.insertRow(index);			
		}
				
		return index;
	}

	protected int getHeaderRowCount()
	{
		return headerRowCount;
	}
	
	/**
	 * Empties the grid
	 */
	public void clear()
	{
		rows.resizeRows(0);
		clearRows();
	}
	
	/**
	 * Sets the style name of the row with given index
	 * @param rowIndex
	 * @param styleName
	 */
	public void setRowStyle(int rowIndex, String styleName)
	{
		rows.getRowElement(rowIndex).setClassName(styleName);
	}
	
	protected abstract int getFirstDataColumnIndex();
	
	/**
	 * When a new header row is created, subclasses may want manipulate it
	 * @param rowIndex
	 */
	protected abstract void preProcessHeaderRow(int rowIndex);

	/**
	 * Gives the subclasses a chance for rendering their data
	 */
	protected abstract void renderRows();
	
	/**
	 * Gives the subclasses a chance for releasing resources when the rows are removed 
	 */
	protected abstract void clearRows();
	
	/**
	 * Asks the subclasses how many rows are going to be rendered.
	 * @return the number of rows to be rendered. 0 if unknown
	 */
	protected abstract int getRowsToBeRendered();
	
	/**
	 * Gives the subclasses a chance for inserting header rows, before the default headers
	 * @return the number of inserted header rows. 0 if no row was inserted
	 */
	protected abstract int preProcessHeaders();
	
	/**
	 * Gives the subclasses a chance for inserting header rows, after the default headers
	 * @return the number of inserted header rows
	 */	
	protected abstract int postProcessHeaders();
	
	/**
	 * Renders the grid. All subclasses should invoke this method as the last statement of their constructors.
	 */
	protected void render()
	{
		rows.resizeColumns(countRenderingColumns());
		
		int rowsToBeRendered = getRowsToBeRendered();
		int headerRowCount = getHeaderRowCount();
		
		rows.resizeRows(rowsToBeRendered + headerRowCount);
		
		renderHeaders();
		renderRows();
	}
	
	protected Widget getWidget(int rowIndex, int columnIndex)
	{
		return ((Cell) rows.getWidget(rowIndex, columnIndex)).getCellWidget();
	}
	
	protected void setWidget(Widget widget, int rowIndex, int columnIndex)
	{
		setWidget(widget, rowIndex, columnIndex, "");
	}
	
	protected void setWidget(Widget widget, int rowIndex, int columnIndex, String cellWidth)
	{
		if(!(widget instanceof Cell))
		{
			widget = createSimpleCell(cellWidth, widget);
		}

		rows.setWidget(rowIndex, columnIndex, widget);
	}
	
	protected String getGridGeneratedId()
	{
		return "gridGeneratedId_" + generatedId;
	}
	
	protected List<T> getColumnDefinitions()
	{
		return this.definitions.getDefinitions();
	}	
	
	protected Cell createSimpleCell(String width, Widget widget)
	{
		Cell cell = createCell(width, widget);
		cell.addStyleName("cell");
		return cell;
	}
	
	protected Cell createHeaderCell(String width, Widget widget)
	{
		Cell cell = createCell(width, widget);
		cell.addStyleName("columnHeader");
		return cell;
	}
	
	private int countRenderingColumns()
	{
		int dataColumnCount = definitions.getDefinitions().size();
		int controllColumns = getFirstDataColumnIndex();
		return dataColumnCount + controllColumns;
	}
	
	private int calculateColumnIndex(String column)
	{
		return definitions.getColumnIndex(column) + getFirstDataColumnIndex();
	}

	private Cell createCell(String width, Widget widget)
	{
		Cell cell = null;
		
		if(widget != null)
		{
			cell = new Cell(widget);
		}
		else
		{
			cell = new Cell();
		}
		
		cell.setWidth(width);
			
		return cell;
	}
	
	private void renderHeaders()
	{
		int addedBefore = preProcessHeaders();
		this.headerRowCount += addedBefore;
		
		Element rowElem = rows.getRowElement(addedBefore);
		rowElem.setClassName("columnHeadersRow row");
		
		preProcessHeaderRow(addedBefore);
				
		List<T> columns = definitions.getDefinitions();
		for (T columnDefinition : columns)
		{
			if(columnDefinition.isVisible())
			{
				String label = columnDefinition.getLabel();
				Label columnHeader = new Label(label);
				Cell cell = createHeaderCell(columnDefinition.getWidth(), columnHeader);
				rows.setWidget(addedBefore, calculateColumnIndex(columnDefinition.getKey()), cell);
			}
		}
		
		postProcessHeaders();
	}
}