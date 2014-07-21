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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.event.row.BeforeCancelRowEditionEvent;
import org.cruxframework.crux.widgets.client.event.row.BeforeCancelRowEditionHandler;
import org.cruxframework.crux.widgets.client.event.row.BeforeRowEditEvent;
import org.cruxframework.crux.widgets.client.event.row.BeforeRowEditHandler;
import org.cruxframework.crux.widgets.client.event.row.BeforeSaveRowEditionEvent;
import org.cruxframework.crux.widgets.client.event.row.BeforeSaveRowEditionHandler;
import org.cruxframework.crux.widgets.client.event.row.BeforeShowRowDetailsEvent;
import org.cruxframework.crux.widgets.client.event.row.BeforeShowRowDetailsHandler;
import org.cruxframework.crux.widgets.client.event.row.CancelRowEditionEvent;
import org.cruxframework.crux.widgets.client.event.row.CancelRowEditionHandler;
import org.cruxframework.crux.widgets.client.event.row.HasBeforeCancelRowEditionHandler;
import org.cruxframework.crux.widgets.client.event.row.HasBeforeRowEditHandlers;
import org.cruxframework.crux.widgets.client.event.row.HasBeforeSaveRowEditionHandler;
import org.cruxframework.crux.widgets.client.event.row.HasBeforeShowDetailsHandlers;
import org.cruxframework.crux.widgets.client.event.row.HasCancelRowEditionHandler;
import org.cruxframework.crux.widgets.client.event.row.HasLoadRowDetailsHandlers;
import org.cruxframework.crux.widgets.client.event.row.HasRowClickHandlers;
import org.cruxframework.crux.widgets.client.event.row.HasRowDoubleClickHandlers;
import org.cruxframework.crux.widgets.client.event.row.HasRowEditHandlers;
import org.cruxframework.crux.widgets.client.event.row.HasRowRenderHandlers;
import org.cruxframework.crux.widgets.client.event.row.HasShowRowDetailsHandlers;
import org.cruxframework.crux.widgets.client.event.row.LoadRowDetailsEvent;
import org.cruxframework.crux.widgets.client.event.row.LoadRowDetailsHandler;
import org.cruxframework.crux.widgets.client.event.row.RowClickEvent;
import org.cruxframework.crux.widgets.client.event.row.RowClickHandler;
import org.cruxframework.crux.widgets.client.event.row.RowDoubleClickEvent;
import org.cruxframework.crux.widgets.client.event.row.RowDoubleClickHandler;
import org.cruxframework.crux.widgets.client.event.row.RowEditEvent;
import org.cruxframework.crux.widgets.client.event.row.RowEditHandler;
import org.cruxframework.crux.widgets.client.event.row.RowRenderEvent;
import org.cruxframework.crux.widgets.client.event.row.RowRenderHandler;
import org.cruxframework.crux.widgets.client.event.row.ShowRowDetailsEvent;
import org.cruxframework.crux.widgets.client.event.row.ShowRowDetailsHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for implementing grids. 
 * All subclasses must invoke the method <code>render()</code> in their constructors.
 * @author Gesse Dafe
 */
public abstract class AbstractGrid<R extends Row> extends Composite implements HasRowClickHandlers, HasRowDoubleClickHandlers, HasRowRenderHandlers, HasBeforeShowDetailsHandlers, HasShowRowDetailsHandlers, HasLoadRowDetailsHandlers, HasBeforeRowEditHandlers, HasRowEditHandlers,HasBeforeSaveRowEditionHandler,HasBeforeCancelRowEditionHandler,HasCancelRowEditionHandler  {	
	
	private static final String DEFAULT_STYLE_NAME = "crux-Grid";
	
	private GridBaseTable table;
	private ColumnDefinitions definitions;
	private String generatedId =  "cruxGrid_" + new Date().getTime();
	private FastList<R> rows = new FastList<R>();
	private Map<Widget, R> widgetsPerRow = new HashMap<Widget, R>();
	private RowSelectionModel rowSelection;
	private ScrollPanel scrollingArea;
	private boolean stretchColumns;
	private boolean highlightRowOnMouseOver;
	private RowDetailWidgetCreator rowDetailWidgetCreator;
	private boolean showRowDetailsIcon;
	private boolean freezeHeaders;
	private Boolean hasFrozenColumns;
	protected FastList<BeforeSaveRowEditionHandler> beforeSaveRowEditionHandlers = new FastList<BeforeSaveRowEditionHandler>();
	protected FastList<BeforeCancelRowEditionHandler> beforeCancelRowEditionHandlers = new FastList<BeforeCancelRowEditionHandler>();
	
	
	/**
	 * Handles the event fired when the details of some row becomes visible.
	 */
	static class RowDetailsCommandHandler<R extends Row> implements SelectHandler
	{
		private AbstractGrid<R> grid;
		private R row;
		private Button showDetailsButton;
		
		public RowDetailsCommandHandler(AbstractGrid<R> grid, R row, Button showDetailsButton)
		{
			this.grid = grid;
			this.row = row;
			this.showDetailsButton = showDetailsButton;
		}
		
		public void onSelect(SelectEvent event)
		{
			event.stopPropagation();
			boolean show = !row.isDetailsShown();
			grid.showRowDetails(show, row, showDetailsButton);
		}	
	}
	
	/**
	 * Shows or hides the row details
	 * @param show
	 * @param row
	 * @param showDetailsButton
	 */
	private void showRowDetails(boolean show, Row row, Button showDetailsButton) 
	{
		if(onShowDetails(show, row, true))
		{
			if (show)
			{
				StyleUtils.addStyleDependentName(showDetailsButton.getElement(), "opened");
			}
			else
			{
				StyleUtils.removeStyleDependentName(showDetailsButton.getElement(), "opened");
			}
		}
	}
	
	/**
	 * Executes the implementation-specific logic of showing the details of a row. 
	 * @param show
	 * @param row
	 * @param fireEvents
	 */
	protected abstract boolean onShowDetails(boolean show, Row row, boolean fireEvents);
	
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
		this(columnDefinitions, rowSelection, cellSpacing, stretchColumns, highlightRowOnMouseOver, fixedCellSize, null, false, false);
	}
	
	/**
	 * Full constructor
	 * @param columnDefinitions the columns to be rendered
	 * @param rowSelection the behavior of the grid about line selection 
	 * @param cellSpacing the space between the cells
	 * @param stretchColumns 
	 * @param fixedCellSize 
	 * @param rowDetailsDefinition
	 * @param showRowDetailsIcon
	 */
	public AbstractGrid(ColumnDefinitions columnDefinitions, RowSelectionModel rowSelection, int cellSpacing, boolean stretchColumns, boolean highlightRowOnMouseOver, boolean fixedCellSize, RowDetailWidgetCreator rowDetailWidgetCreator, boolean showRowDetailsIcon, boolean freezeHeaders)
	{
		this.definitions = columnDefinitions;
		this.rowSelection = rowSelection;
		this.stretchColumns = stretchColumns;
		this.highlightRowOnMouseOver = highlightRowOnMouseOver;
		this.rowDetailWidgetCreator = rowDetailWidgetCreator;
		this.freezeHeaders = freezeHeaders;
		this.showRowDetailsIcon = this.rowDetailWidgetCreator != null && showRowDetailsIcon;
		
		scrollingArea = new ScrollPanel();
		scrollingArea.setStyleName(DEFAULT_STYLE_NAME);
		initWidget(scrollingArea);
		
		if(hasFrozenCells())
		{
			if(hasRowDetails())
			{
				table = new FlexTablelessGridStructure(this);
			}
			else
			{
				table = new TablelessGridStructure(this);
			}
		}
		else if(hasRowDetails())
		{
			table = new GridFlexTable();
		}
		else
		{
			table = new GridHtmlTable();
		}
		
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
		
		scrollingArea.add(table.asWidget());
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasRowClickHandlers#addRowClickHandler(org.cruxframework.crux.widgets.client.event.row.RowClickHandler)
	 */
	public HandlerRegistration addRowClickHandler(RowClickHandler handler)
	{
		return addHandler(handler, RowClickEvent.getType());		
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasRowDoubleClickHandlers#addRowDoubleClickHandler(org.cruxframework.crux.widgets.client.event.row.RowDoubleClickHandler)
	 */
	public HandlerRegistration addRowDoubleClickHandler(RowDoubleClickHandler handler)
	{
		return addHandler(handler, RowDoubleClickEvent.getType());
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasRowRenderHandlers#addRowRenderHandler(org.cruxframework.crux.widgets.client.event.row.RowRenderHandler)
	 */
	public HandlerRegistration addRowRenderHandler(RowRenderHandler handler)
	{
		return addHandler(handler, RowRenderEvent.getType());
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.row.HasBeforeShowDetailsHandlers#addBeforeShowRowDetailsHandler(br.com.sysmap.crux.widgets.client.event.row.BeforeShowDetailsHandler)
	 */
	public HandlerRegistration addBeforeShowRowDetailsHandler(BeforeShowRowDetailsHandler handler) 
	{
		return addHandler(handler, BeforeShowRowDetailsEvent.getType());
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.row.HasShowRowDetailsHandlers#addShowRowDetailsHandler(br.com.sysmap.crux.widgets.client.event.row.ShowRowDetailsHandler)
	 */
	public HandlerRegistration addShowRowDetailsHandler(ShowRowDetailsHandler handler) 
	{
		return addHandler(handler, ShowRowDetailsEvent.getType());
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasBeforeRowEditHandlers#addBeforeRowEditHandler(org.cruxframework.crux.widgets.client.event.row.BeforeRowEditHandler)
	 */
	public HandlerRegistration addBeforeRowEditHandler(BeforeRowEditHandler handler) 
	{
		return addHandler(handler, BeforeRowEditEvent.getType());
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasCancelRowEditionHandler#addCancelRowEditionHandler(org.cruxframework.crux.widgets.client.event.row.CancelRowEditionHandler)
	 */
	public HandlerRegistration addCancelRowEditionHandler(CancelRowEditionHandler handler) 
	{
		return addHandler(handler, CancelRowEditionEvent.getType());
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasBeforeCancelRowEditionHandler#addBeforeCancelRowEditionHandler(org.cruxframework.crux.widgets.client.event.row.BeforeCancelRowEditionHandler)
	 */
	public HandlerRegistration addBeforeCancelRowEditionHandler(BeforeCancelRowEditionHandler handler) 
	{
		beforeCancelRowEditionHandlers.add(handler);
		return addHandler(handler, BeforeCancelRowEditionEvent.getType());
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasRowEditHandlers#addRowEditHandler(org.cruxframework.crux.widgets.client.event.row.RowEditHandler)
	 */
	public HandlerRegistration addRowEditHandler(RowEditHandler handler) 
	{
		return addHandler(handler, RowEditEvent.getType());
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.row.HasLoadRowDetailsHandlers#addLoadRowDetailsHandler(br.com.sysmap.crux.widgets.client.event.row.LoadRowDetailsHandler)
	 */
	public HandlerRegistration addLoadRowDetailsHandler(LoadRowDetailsHandler handler) 
	{
		return addHandler(handler, LoadRowDetailsEvent.getType());
	}
	
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasBeforeSaveRowEditionHandler#addBeforeSaveRowEditionHandler(org.cruxframework.crux.widgets.client.event.row.BeforeSaveRowEditionHandler)
	 */
	public HandlerRegistration addBeforeSaveRowEditionHandler(BeforeSaveRowEditionHandler handler)
	{
		this.beforeSaveRowEditionHandlers.add(handler);
		return addHandler(handler, BeforeSaveRowEditionEvent.getType());
	}
	
	/**
	 * Clears all grid rows
	 */
	public void clear()
	{
		table.removeAllRows();
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
	 * Gets the selected rows from the current visible page.
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
		return RowSelectionModel.singleCheckBox.equals(rowSelection) || RowSelectionModel.multipleCheckBox.equals(rowSelection) || RowSelectionModel.multipleCheckBoxSelectAll.equals(rowSelection) || RowSelectionModel.singleRadioButton.equals(rowSelection);
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
		onBeforeRender();
		
		this.definitions.reset();
		
		int rowCount = (hasRowDetails() ? 2 * getRowsToBeRendered() : getRowsToBeRendered()) + 1;
		
		clearRendering();
			
		boolean hasRowDetails = hasRowDetails();
		
		for (int i = 0; i < rowCount; i++)
		{
			
			if(!hasRowDetails || i == 0 || i % 2 == 1)
			{
				Element rowElement = table.getRowElement(i);
				R row = createRow(i, rowElement);
				row.setStyle("row");
				rows.add(row);
			}
			else
			{
				((GridBaseFlexTable) table).joinCells(i);
				Element cellElem = table.getRowElement(i);
				cellElem.getStyle().setOverflow(Overflow.HIDDEN);
				cellElem.getStyle().setHeight(1, Unit.PX);
				cellElem.getStyle().setOpacity(0.01);
			}
		}

		renderHeaders(rowCount);
		renderRows();
		table.onAfterRender();
	}
	
	/**
	 * Return true if the grid can be rendered
	 * @return
	 */
	protected abstract void onBeforeRender();
	
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
	
	
	/** Fires a row edit event
	 * @param row
	 */
	protected abstract void fireRowEditEvent(R row);
	
	
	/** Fires a before row edit event
	 * @param row
	 */
	protected abstract void fireBeforeRowEditEvent(R row);
	
		
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
	 * Fires a row saving event
	 * @param row
	 */
	protected abstract boolean fireBeforeSaveRowEditionEvent(R row);
	
	/**
	 * @param row
	 * @return
	 */
	protected abstract boolean fireBeforeCancelRowEditionEvent(R row);
	
	/**
	 * @param row
	 * @return
	 */
	protected abstract void fireCancelRowEditionEvent(R row);
		
	/**
	 * Access for the grid table element
	 * @return
	 */
	GridBaseTable getTable()
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
		int rowCount = (hasRowDetails() ? 2 * getRowsToBeRendered() : getRowsToBeRendered()) + 1;
		table.resize(rowCount, definitions.getVisibleColumnCount() + (hasSelectionColumn() ? 1 : 0) + (hasRowDetailsIconColumn() ? 1 : 0));
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
		}else if(RowSelectionModel.singleCheckBox.equals(rowSelection))
		{
			
			CheckBox checkBox = new CheckBox();
			checkBox.addClickHandler(new RowSelectionHandler<R>(this, row));
			w = checkBox;
			
			final int indexRowSelected = row.getIndex();
			checkBox.addClickHandler(new ClickHandler(){
				
				@Override
				public void onClick(ClickEvent event)
				{
					Iterator<R> iterator = getRowIterator();
					
					while(iterator.hasNext())
					{
						R r = iterator.next();
						
						if(indexRowSelected != r.getIndex())
						{
							r.setSelected(false);
						}
						
					}
				}
			});
		}
		
		Cell cell = createCell(w, false, false);
		cell.addStyleName("rowSelector");
		
		return cell;
	}
	
	/**
	 * Creates the cell that contains the button for showing or hiding the row's details.
	 * @param row
	 * @return the created cell
	 */
	private Cell createRowDetailsCommandCell(R row)
	{
		Button button = new Button();
		button.setText(" ");
		button.setStyleName("rowDetailsCommandButton");
		button.addSelectHandler(new RowDetailsCommandHandler<R>(this, row, button));
		Cell cell = createCell(button, false, false);
		cell.addStyleName("rowDetailsCommandCell");		
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
		
		if(hasRowDetailsIconColumn())
		{
			int detailsCommandColIndex = hasSelectionColumn() ? 1 : 0;
			row.setCell(createHeaderCell(new Label(" ")), detailsCommandColIndex);
			StyleUtils.addStyleDependentName(table.getCellElement(0, 0), "detailsCommandColumnHeader");
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

		boolean hasRowDetailsIconColumn = hasRowDetailsIconColumn();
		
		while(it.hasNext())
		{
			R row = it.next();
			
			if(hasSelectionColumn())
			{
				row.setCell(createSelectionCell(row), 0);
			}
			
			if(hasRowDetailsIconColumn)
			{
				row.setCell(createRowDetailsCommandCell(row), hasSelectionColumn() ? 1 : 0);
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
	
	/**
	 * Returns true if the grid has the row-details feature
	 * @return
	 */
	protected boolean hasRowDetails()
	{
		return this.rowDetailWidgetCreator != null;
	}
	
	/**
	 * Returns <code>true</code> if the grid has a column containing icons that expand or collapse row's details
	 * @return
	 */
	protected boolean hasRowDetailsIconColumn()
	{
		return this.showRowDetailsIcon;
	}
	
	/**
	 * @param uiObject
	 */
	protected void ensureVisible(UIObject uiObject)
	{
		this.scrollingArea.ensureVisible(uiObject);
	}

	/**
	 * @return the rowDetailWidgetCreator
	 */
	public RowDetailWidgetCreator getRowDetailWidgetCreator() 
	{
		return rowDetailWidgetCreator;
	}
	
	ScrollPanel getScrollingArea()
	{
		return this.scrollingArea;
	}

	public boolean hasFrozenHeaders() 
	{
		return freezeHeaders;
	}

	public boolean hasFrozenColumns() 
	{
		if(this.hasFrozenColumns == null)
		{
			this.hasFrozenColumns = false;
			
			FastList<ColumnDefinition> defs = definitions.getDefinitions();
			for(int i = 0; i < defs.size(); i++)
			{
				if(defs.get(i).isFrozen())
				{
					this.hasFrozenColumns = true;
					break;
				}
			}
		}
		
		return this.hasFrozenColumns;
	}
	
	public boolean hasFrozenCells() 
	{
		return freezeHeaders || hasFrozenColumns();
	}

	public RowSelectionModel getRowSelectionModel() 
	{
		return rowSelection;
	}
	
	public void setRowSelectionModel(RowSelectionModel rowSelectionModel) 
	{
		if(rowSelectionModel != null)
		{
			List<R> selectedRows = getSelectedRows();

			this.rowSelection = rowSelectionModel;
			
			int numSelectedRows = selectedRows.size();
			if(numSelectedRows > 0)
			{
				boolean selectNone = RowSelectionModel.unselectable.equals(this.rowSelection);
				boolean selectSingle = RowSelectionModel.single.equals(this.rowSelection) || RowSelectionModel.singleRadioButton.equals(this.rowSelection);

				for (int i = 0; i < numSelectedRows; i++) 
				{
					R row = selectedRows.get(i);

					if(selectNone || (selectSingle && i > 0))
					{
						row.setSelected(false);
					}					
				}
			}
		}
	}
	
}