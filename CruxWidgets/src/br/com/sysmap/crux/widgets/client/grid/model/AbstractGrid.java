package br.com.sysmap.crux.widgets.client.grid.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.event.row.HasRowClickHandlers;
import br.com.sysmap.crux.widgets.client.event.row.HasRowDoubleClickHandlers;
import br.com.sysmap.crux.widgets.client.event.row.HasRowRenderHandlers;
import br.com.sysmap.crux.widgets.client.event.row.RowClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowClickHandler;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickHandler;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for implementing grids. 
 * All subclasses must invoke the method <code>render()</code> in their constructors.
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractGrid<R extends Row> extends Composite implements HasRowClickHandlers, HasRowDoubleClickHandlers, HasRowRenderHandlers {	
	
	private static final String DEFAULT_STYLE_NAME = "crux-Grid";
	
	private SimplePanel panel;
	private GridHtmlTable table;
	private ColumnDefinitions definitions;
	private String generatedId =  "cruxGrid_" + new Date().getTime();
	private GridLayout gridLayout = GWT.create(GridLayout.class);
	private List<R> rows = new ArrayList<R>();
	private Map<Widget, R> widgetsPerRow = new HashMap<Widget, R>();
	private RowSelectionModel rowSelection;
	private ScrollPanel scrollingArea;
	private int visibleColumnCount = -1;
	private boolean stretchColumns;
	private boolean highlightRowOnMouseOver;
	
	@SuppressWarnings("unchecked")
	static class RowSelectionHandler<R extends Row> implements ClickHandler
	{
		private AbstractGrid<R> grid;
		private R row;
		
		public RowSelectionHandler(AbstractGrid<R> grid, R row)
		{
			this.grid = grid;
			this.row = row;
		}
		
		public void onClick(ClickEvent event)
		{
			boolean selected = ((HasValue<Boolean>) event.getSource()).getValue();
			
			if(!grid.onSelectRow(selected, row, true))
			{
				event.preventDefault();
			}
			
			event.stopPropagation();
		}	
	}
	
	/**
	 * Full constructor
	 * @param columnDefinitions the columns to be rendered
	 * @param rowSelection the behavior of the grid about line selection 
	 * @param cellSpacing the space between the cells
	 * @param stretchColumns 
	 */
	public AbstractGrid(ColumnDefinitions columnDefinitions, RowSelectionModel rowSelection, int cellSpacing, boolean stretchColumns, boolean highlightRowOnMouseOver)
	{
		this.definitions = columnDefinitions;
		this.rowSelection = rowSelection;
		this.stretchColumns = stretchColumns;
		this.highlightRowOnMouseOver = highlightRowOnMouseOver;
		
		panel = new SimplePanel();
		panel.setStyleName(DEFAULT_STYLE_NAME);
		panel.setWidth("100%");
		
		scrollingArea = new ScrollPanel();
		scrollingArea.setHeight("1");
		scrollingArea.setWidth("1");
		panel.add(scrollingArea);
				
		initWidget(panel);
		
		table = new GridHtmlTable();
		table.setCellSpacing(cellSpacing);
		table.setCellPadding(0);
		
		if(this.stretchColumns)
		{
			table.setWidth("100%");
		}
		
		gridLayout.adjustToBrowser(scrollingArea, table);
		
		// lazy attaches the table to avoid problems related to scrolling    
		DeferredCommand.addCommand(new Command()
		{
			public void execute()
			{	
				resizeToFit(scrollingArea, panel);
				scrollingArea.add(table);
			}
		});
		
		// when the screen is resized, the scrolling panel must be resized to fit its container
		Screen.addResizeHandler(new ResizeHandler(){
			public void onResize(ResizeEvent event)
			{
				resizeToFit(scrollingArea, panel);				
			}
		});
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.row.HasRowClickHandlers#addRowClickHandler(br.com.sysmap.crux.widgets.client.event.row.RowClickHandler)
	 */
	public HandlerRegistration addRowClickHandler(RowClickHandler handler)
	{
		return addHandler(handler, RowClickEvent.getType());		
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.event.row.HasRowDoubleClickHandlers#addRowDoubleClickHandler(br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickHandler)
	 */
	public HandlerRegistration addRowDoubleClickHandler(RowDoubleClickHandler handler)
	{
		return addHandler(handler, RowDoubleClickEvent.getType());
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.row.HasRowRenderHandlers#addRowRenderHandler(br.com.sysmap.crux.widgets.client.event.row.RowRenderHandler)
	 */
	public HandlerRegistration addRowRenderHandler(RowRenderHandler handler)
	{
		return addHandler(handler, RowRenderEvent.getType());
	}
	
	/**
	 * Clears all gird rows
	 */
	public void clear()
	{
		table.resizeRows(0);
		rows = new ArrayList<R>();
		this.widgetsPerRow = new HashMap<Widget, R>();
		onClear();
	}
	
	/**
	 * Gets the row where the given widget is rendered
	 * @param w a widget rendered inside a row  
	 * @return a <code>Row</code> that contains the given widget. Null if no such row found.;
	 */
	public R getRow(Widget w)
	{
		return widgetsPerRow.get(w);
	}
	
	/**
	 * Gets all selected grid rows
	 * @return a <code>List</code> containing the selected rows
	 */
	public abstract List<R> getSelectedRows(); 
	
	/**
	 * Gets all grid rows
	 * @return a <code>List</code> containing the rows
	 */
	public abstract List<R> getCurrentPageRows();
	
	/**
	 * Repaints the grid
	 */
	public void refresh()
	{
		render();
	}
	
	/**
	 * Gets an iterator for the grid rows
	 * @return an iterator
	 */
	protected Iterator<R> getRowIterator()
	{
		Iterator<R> it = new Iterator<R>(){

			int position = 1;
			
			/**
			 * @see java.util.Iterator#hasNext()
			 */
			public boolean hasNext()
			{
				return position < rows.size();
			}

			/**
			 * @see java.util.Iterator#next()
			 */
			public R next()
			{
				if(hasNext())
				{
					return rows.get(position++);
				}
				else
				{
					return null;
				}
			}

			/**
			 * @see java.util.Iterator#remove()
			 */
			public void remove()
			{
				throw new RuntimeException(); // TODO - Gessé - set message here				
			}			
		};
		
		return it;
	}
	
	/**
	 * Creates a cell
	 * @param widget the content of the cell
	 * @return
	 */
	protected Cell createCell(Widget widget)
	{
		Cell cell = createBaseCell(widget, true, selectRowOnClickCell(), highlightRowOnMouseOver);
		cell.addStyleName("cell");
		cell.setWidth("100%");
		return cell;
	}

	/**
	 * Creates a cell that will be used as the header for the given column
	 * @param columnDefinition
	 * @return the newly created cell
	 */
	protected Cell createColumnHeaderCell(ColumnDefinition columnDefinition)
	{
		String label = columnDefinition.getLabel();
		Label columnHeader = new Label(label);
		Cell cell = createHeaderCell(columnHeader);
		return cell;
	}
	
	/**
	 * Creates a basic (no style) cell
	 * @param widget
	 * @param fireEvents
	 * @param selectRowOnclick
	 * @return
	 */
	protected Cell createBaseCell(Widget widget, boolean fireEvents, boolean selectRowOnclick, boolean highlightRowOnMouseOver)
	{
		Cell cell = null;
		
		if(widget != null)
		{
			cell = new Cell(widget, fireEvents, selectRowOnclick, highlightRowOnMouseOver);
		}
		else
		{
			cell = new Cell(fireEvents, selectRowOnclick, highlightRowOnMouseOver);
		}
			
		return cell;
	}

	/**
	 * Creates a header styled cell
	 * @param widget the content of the cell
	 * @return the new cell
	 */
	protected Cell createHeaderCell(Widget widget)
	{
		Cell cell = createBaseCell(widget, false, false, false);
		cell.addStyleName("columnHeader");
		return cell;
	}
	
	/**
	 * Creates a row
	 * @param index the position of the row (zero based)
	 * @param element the row HTML element (<code>TR</code>)
	 * @return
	 */
	protected abstract R createRow(int index, Element element);
	
	/**
	 * Gets the grid columns (including the invisible columns)
	 * @return
	 */
	protected ColumnDefinitions getColumnDefinitions()
	{
		return definitions;
	}
	
	/**
	 * @return the number of rows that will be rendered, less the header row
	 */
	protected abstract int getRowsToBeRendered();
		
	/**
	 * @return <code>true</code> if grid rows must contain a special cell with a widget for selecting them. 
	 */
	protected boolean hasSelectionColumn()
	{
		return RowSelectionModel.multipleWithCheckBox.equals(rowSelection) || RowSelectionModel.singleWithRadioButton.equals(rowSelection);
	}
	
	/**
	 * Callback for before clearing the rows contents
	 */
	protected abstract void onClearRendering();
	
	/**
	 * Callback for before rendering the rows 
	 */
	protected abstract void onBeforeRenderRows();
	
	/**
	 * Callback for grid clearing
	 */
	protected abstract void onClear();
	
	/**
	 * Callback for row selection
	 * @param select <code>true</code> if the row was selected, <code>false</code> if deselected
	 * @param row
	 */
	protected abstract boolean onSelectRow(boolean select, R row, boolean fireEvents);
	
	/**
	 * Renders the grid. The subclasses should call this method for rendering themselves.
	 */
	protected void render()
	{
		int rowCount = getRowsToBeRendered() + 1;
		
		clearRendering();
			
		for (int i = 0; i < rowCount; i++)
		{
			R row = createRow(i, table.getRowElement(i));
			row.setStyle("row");
			rows.add(row);
		}
		
		renderHeaders(rowCount);
		renderRows();
	}
	
	/**
	 * Renders the given row cells
	 * @param row
	 */
	protected abstract void renderRow(R row);
	
	/**
	 * Fires a row click event
	 * @param row
	 */
	protected abstract void fireRowClickEvent(R row);
		
	/**
	 * Fires a row double click event
	 * @param row
	 */
	protected abstract void fireRowDoubleClickEvent(R row);
	
	/**
	 * Fires a row rendering event
	 * @param row
	 */
	protected abstract void fireRowRenderEvent(R row);
		
	/**
	 * Access for the grid table element
	 * @return
	 */
	GridHtmlTable getTable()
	{
		return table;
	}
	
	@SuppressWarnings("unchecked")
	void registerWidget(Widget w, Row row)
	{
		widgetsPerRow.put(w, (R) row);
	}	
	
	/**
	 * Resizes the grid table, according with the number of rows that will be rendered. 
	 */
	private void clearRendering()
	{
		int rowCount = getRowsToBeRendered() + 1;
		table.resize(rowCount, getVisibleColumnCount() + (hasSelectionColumn() ? 1 : 0));
		this.rows = new ArrayList<R>();
		this.widgetsPerRow = new HashMap<Widget, R>();
		onClearRendering();
	}
	
	/**
	 * Creates a cell which will contain a check box or radio button, used to select the row
	 * @param row
	 * @return the newly created cell
	 */
	private Cell createSelectionCell(R row)
	{
		Widget w = null;
		
		if(RowSelectionModel.multipleWithCheckBox.equals(rowSelection))
		{
			CheckBox checkBox = new CheckBox();
			checkBox.addClickHandler(new RowSelectionHandler<R>(this, row));
			w = checkBox;
		}
		else if(RowSelectionModel.singleWithRadioButton.equals(rowSelection))
		{
			RadioButton radio = new RadioButton(generatedId + "_selector");
			radio.addClickHandler(new RowSelectionHandler<R>(this, row));
			w = radio;
		}
		
		Cell cell = createCell(w);
		cell.addStyleName("rowSelector");
		
		return cell;
	}
	
	/**
	 * Gets the number of columns that will be rendered
	 * @return the visible column count
	 */
	private int getVisibleColumnCount()
	{
		if(this.visibleColumnCount == -1)
		{
			this.visibleColumnCount = 0;
			
			for (ColumnDefinition def : definitions.getDefinitions())
			{
				if(def.isVisible())
				{
					this.visibleColumnCount++;
				}
			}
		}
		
		return this.visibleColumnCount ;		
	}
	
	/**
	 * Creates a cell to be used as first header cell. 
	 * If the row selection model is <code>MULTIPLE</code> or <code>MULTIPLE_WITH_CHECKBOX</code>, 
	 * 	this cell will contain a check box which when clicked selects or deselects all enabled rows.  
	 * @param rowCount
	 * @return the created cell
	 */
	private Cell createHeaderFristColumnCell(int rowCount)
	{
		Widget w = null;
		
		if(hasSelectionColumn())
		{
			if(RowSelectionModel.multiple.equals(rowSelection) || RowSelectionModel.multipleWithCheckBox.equals(rowSelection))
			{
				CheckBox checkBox = new CheckBox();
				checkBox.addClickHandler(createSelectAllRowsClickHandler());
				
				if(rowCount <= 1)
				{
					checkBox.setEnabled(false);
				}
				
				w = checkBox;
			}	
		}
		
		if(w == null)
		{
			w = new Label(" ");
		}
		
		return createHeaderCell(w);
	}
	
	/**
	 * Creates a click handler capable of selecting or deselecting all enabled rows.
	 * @return a newly created click handler
	 */
	private ClickHandler createSelectAllRowsClickHandler()
	{
		return new ClickHandler()
		{
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event)
			{
				HasValue<Boolean> source = (HasValue<Boolean>) event.getSource();
				boolean select = source.getValue();
				selectCurrentPageRows(select, true);
			}
		};
	}
	
	/**
	 * Renders the columns headers
	 * @param rowCount
	 */
	private void renderHeaders(int rowCount)
	{
		R row = rows.get(0);
		
		row.setStyle("columnHeadersRow row");
		row.setCell(createHeaderFristColumnCell(rowCount), 0);
				
		List<ColumnDefinition> columns = definitions.getDefinitions();
		for (ColumnDefinition columnDefinition : columns)
		{
			if(columnDefinition.isVisible())
			{
				Cell cell = createColumnHeaderCell(columnDefinition);
				row.setCell(cell, columnDefinition.getKey());
			}
		}
	}
	
	/**
	 * Renders the grid rows
	 */
	private void renderRows()
	{
		Iterator<R> it = getRowIterator();
		
		onBeforeRenderRows();
		
		while(it.hasNext())
		{
			R row = it.next();
			
			if(hasSelectionColumn())
			{
				row.setCell(createSelectionCell(row), 0);
			}
			
			renderRow(row);
			
			fireRowRenderEvent(row);
		}
	}
	
	/**
	 * Resizes a widget so it fills all available space in its parent
	 * @param widget
	 * @param parent
	 */
	private void resizeToFit(final Widget widget, final Panel parent)
	{
		final Element elem = parent.getElement();
		
		String borderLeft = elem.getStyle().getProperty("borderLeft");
		elem.getStyle().setProperty("borderLeft", "solid 1px #FFFFFF");
		
		parent.clear();
		
		int width = 0;
		int height = 0;
		
		if(elem != null)
		{
			width = elem.getClientWidth();
			height = elem.getClientHeight();
		}
		
		final int finalWidth = width;
		final int finalHeight = height;
		
		elem.getStyle().setProperty("borderLeft", borderLeft);
		
		new Timer()
		{
			@Override
			public void run()
			{
				if(finalWidth > 0)
				{
					widget.setWidth("" + finalWidth);
				}
				else
				{
					widget.setWidth("100%");
				}
				
				if(finalHeight > 0)
				{
					widget.setHeight("" + finalHeight);
				}
				else
				{
					widget.setHeight("100%");
				}
				
				parent.clear();
				parent.add(widget);				
			}
		}.schedule(100);
	}
	
	/**
	 * Selects or deselects all enabled rows
	 * @param select <code>true</code> for select, <code>false</code> for deselect
	 * @param fireEvents if <code>true</code>, fires a <code>BeforeRowSelectEvent</code> for each enabled row 
	 */
	public void selectCurrentPageRows(boolean select, boolean fireEvents) 
	{
		Iterator<R> it = getRowIterator();
		
		while(it.hasNext())
		{
			R row = it.next();
			
			if(row.isEnabled())
			{
				if(RowSelectionModel.multipleWithCheckBox.equals(rowSelection))
				{
					((CheckBox) row.getCell(0).getCellWidget()).setValue(select);
				}
				
				if(!RowSelectionModel.unselectable.equals(rowSelection))
				{
					onSelectRow(select, row, fireEvents);
				}
			}
		}
	}
	
	/**
	 * @return <code>true</code> if a row should be selected when one of its cells is clicked by the user. 
	 */
	private boolean selectRowOnClickCell()
	{
		return RowSelectionModel.single.equals(rowSelection) || RowSelectionModel.multiple.equals(rowSelection);
	}
}