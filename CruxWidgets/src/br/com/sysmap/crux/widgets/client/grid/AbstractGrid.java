/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.grid;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.utils.StyleUtils;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.event.row.HasRowClickHandlers;
import br.com.sysmap.crux.widgets.client.event.row.HasRowDoubleClickHandlers;
import br.com.sysmap.crux.widgets.client.event.row.HasRowRenderHandlers;
import br.com.sysmap.crux.widgets.client.event.row.RowClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowClickHandler;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickHandler;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for implementing grids. 
 * All subclasses must invoke the method <code>render()</code> in their constructors.
 * @author Gesse S. F. Dafe
 */
public abstract class AbstractGrid<R extends Row> extends Composite implements HasRowClickHandlers, HasRowDoubleClickHandlers, HasRowRenderHandlers {	
	
	private static final String DEFAULT_STYLE_NAME = "crux-Grid";
	
	private GridHtmlTable table;
	private ColumnDefinitions definitions;
	private String generatedId =  "cruxGrid_" + new Date().getTime();
	private FastList<R> rows = new FastList<R>();
	private Map<Widget, R> widgetsPerRow = new HashMap<Widget, R>();
	private RowSelectionModel rowSelection;
	private ScrollPanel scrollingArea;
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
	 * @param fixedCellSize 
	 */
	public AbstractGrid(ColumnDefinitions columnDefinitions, RowSelectionModel rowSelection, int cellSpacing, boolean stretchColumns, boolean highlightRowOnMouseOver, boolean fixedCellSize)
	{
		this.definitions = columnDefinitions;
		this.rowSelection = rowSelection;
		this.stretchColumns = stretchColumns;
		this.highlightRowOnMouseOver = highlightRowOnMouseOver;
		
		scrollingArea = new ScrollPanel();
		scrollingArea.setStyleName(DEFAULT_STYLE_NAME);
		initWidget(scrollingArea);
		
		table = new GridHtmlTable();
		table.setCellSpacing(cellSpacing);
		table.setCellPadding(0);
		StyleUtils.addStyleProperty(table.getBodyElement(), "width", "100%");
		StyleUtils.addStyleProperty(table.getBodyElement(), "height", "100%");
		
		if(this.stretchColumns)
		{
			table.setWidth("100%");
		}

		if(fixedCellSize)
		{
			table.getElement().getStyle().setProperty("tableLayout", "fixed");
		}
		
		scrollingArea.add(table);
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
	 * Clears all grid rows
	 */
	public void clear()
	{
		table.resizeRows(0);
		rows = new FastList<R>();
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
		Widget widget = w;
		
		while(!widgetsPerRow.containsKey(widget) && widget.getParent() != null)
		{
			widget = widget.getParent();
		}
		
		return widgetsPerRow.get(widget);
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
	 * @param key
	 * @return
	 */
	public ColumnDefinition getColumnDefinition(String key)
	{
		return this.definitions.getDefinition(key);
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
				throw new UnsupportedOperationException(WidgetMsgFactory.getMessages().errorItsNotPossibleToRemoveARowmFromAGrid());				
			}			
		};
		
		return it;
	}
	
	/**
	 * Creates a cell
	 * @param widget the content of the cell
	 * @return
	 */
	protected Cell createCell(Widget widget, boolean wrapLine, boolean truncate)
	{
		Cell cell = createBaseCell(widget, true, selectRowOnClickCell(), highlightRowOnMouseOver, wrapLine, truncate);
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
	protected Cell createBaseCell(Widget widget, boolean fireEvents, boolean selectRowOnclick, boolean highlightRowOnMouseOver, boolean wrapLine, boolean truncate)
	{
		Cell cell = null;
		
		if(widget != null)
		{
			cell = new Cell(widget, fireEvents, selectRowOnclick, highlightRowOnMouseOver, wrapLine, truncate);
		}
		else
		{
			cell = new Cell(fireEvents, selectRowOnclick, highlightRowOnMouseOver, wrapLine, truncate);
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
		Cell cell = createBaseCell(widget, false, false, false, true, false);
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
		return RowSelectionModel.multipleCheckBox.equals(rowSelection) || RowSelectionModel.multipleCheckBoxSelectAll.equals(rowSelection) || RowSelectionModel.singleRadioButton.equals(rowSelection);
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
		this.definitions.reset();
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
		table.resize(rowCount, definitions.getVisibleColumnCount() + (hasSelectionColumn() ? 1 : 0));
		this.rows = new FastList<R>();
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
		
		if(RowSelectionModel.multipleCheckBox.equals(rowSelection) || RowSelectionModel.multipleCheckBoxSelectAll.equals(rowSelection))
		{
			CheckBox checkBox = new CheckBox();
			checkBox.addClickHandler(new RowSelectionHandler<R>(this, row));
			w = checkBox;
		}
		else if(RowSelectionModel.singleRadioButton.equals(rowSelection))
		{
			RadioButton radio = new RadioButton(generatedId + "_selector");
			radio.addClickHandler(new RowSelectionHandler<R>(this, row));
			w = radio;
		}
		
		Cell cell = createCell(w, false, false);
		cell.addStyleName("rowSelector");
		
		return cell;
	}
	
	/**
	 * Creates a cell to be used as first header cell. 
	 * If the row selection model is <code>multipleCheckBoxSelectAll</code>, 
	 * 	this cell will contain a check box which when clicked selects or deselects all enabled rows.  
	 * @param rowCount
	 * @return the created cell
	 */
	private Cell createHeaderFristColumnCell(int rowCount)
	{
		Widget w = null;
		
		if(hasSelectionColumn())
		{
			if(RowSelectionModel.multipleCheckBoxSelectAll.equals(rowSelection))
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
		
		if(hasSelectionColumn())
		{
			StyleUtils.addStyleDependentName(table.getCellElement(0, 0), "rowSelectionColumn");
		}
		else
		{
			StyleUtils.removeStyleDependentName(table.getCellElement(0, 0), "rowSelectionColumn");
		}
				
		FastList<ColumnDefinition> columns = definitions.getDefinitions();
		for (int i=0; i<columns.size(); i++)
		{
			ColumnDefinition columnDefinition = columns.get(i);
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
				if(RowSelectionModel.multipleCheckBox.equals(rowSelection) || RowSelectionModel.multipleCheckBoxSelectAll.equals(rowSelection))
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