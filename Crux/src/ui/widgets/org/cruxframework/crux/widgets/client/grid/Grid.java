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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.datasource.DataSourceRecord;
import org.cruxframework.crux.core.client.datasource.HasDataSource;
import org.cruxframework.crux.core.client.datasource.LocalDataSource;
import org.cruxframework.crux.core.client.datasource.LocalDataSourceCallback;
import org.cruxframework.crux.core.client.datasource.MeasurableDataSource;
import org.cruxframework.crux.core.client.datasource.MeasurablePagedDataSource;
import org.cruxframework.crux.core.client.datasource.MeasurableRemoteDataSource;
import org.cruxframework.crux.core.client.datasource.PagedDataSource;
import org.cruxframework.crux.core.client.datasource.RemoteDataSource;
import org.cruxframework.crux.core.client.datasource.RemoteDataSourceCallback;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.HasFormatter;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.event.row.BeforeRowSelectEvent;
import org.cruxframework.crux.widgets.client.event.row.BeforeRowSelectHandler;
import org.cruxframework.crux.widgets.client.event.row.BeforeShowRowDetailsEvent;
import org.cruxframework.crux.widgets.client.event.row.HasBeforeRowSelectHandlers;
import org.cruxframework.crux.widgets.client.event.row.LoadRowDetailsEvent;
import org.cruxframework.crux.widgets.client.event.row.RowClickEvent;
import org.cruxframework.crux.widgets.client.event.row.RowDoubleClickEvent;
import org.cruxframework.crux.widgets.client.event.row.RowRenderEvent;
import org.cruxframework.crux.widgets.client.event.row.ShowRowDetailsEvent;
import org.cruxframework.crux.widgets.client.grid.DataColumnEditorCreators.DataColumnEditorCreator;
import org.cruxframework.crux.widgets.client.paging.Pageable;
import org.cruxframework.crux.widgets.client.paging.Pager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A paged sortable data grid
 * @author Gesse S. F. Dafe
 */
public class Grid extends AbstractGrid<DataRow> implements Pageable, HasDataSource<PagedDataSource<?>>, HasBeforeRowSelectHandlers
{	
	private int pageSize;
	private PagedDataSource<?> dataSource;
	private FastList<ColumnHeader> headers = new FastList<ColumnHeader>();
	private boolean autoLoadData;
	private boolean loaded;
	private String currentSortingColumn;
	private boolean ascendingSort;
	private Pager pager; 
	private String emptyDataFilling;
	private String defaultSortingColumn;
	private SortingType defaultSortingType;
	private RowDetailsManager rowDetailsManager;
	private DataRow currentEditingRow;
	
	/**
	 * @param columnDefinitions the columns to be rendered
	 * @param pageSize the number of rows per page
	 * @param rowSelection the behavior of the grid about line selection
	 * @param cellSpacing the space between the cells
	 * @param autoLoadData if <code>true</code>, when a data source is set, its first page records are fetched and rendered.
	 * @param stretchColumns if <code>true</code>, the width of the columns are auto adjusted to fit the grid width. Prevents horizontal scrolling.   
	 * @param highlightRowOnMouseOver if <code>true</code>, rows change their styles when mouse passed over them
	 * @param emptyDataFilling an alternative text to be shown when there is no data for some data cell
	 * @param fixedCellSize equivalent of setting CSS attribute <code>table-layout</code> to <code>fixed</code>
	 * @param defaultSortingColumn the column to be used to automatically sort the grid's data when it is rendered for the first time 
	 * @param defaultSortingType tells the grid if <code>defaultSortingColumn</code> should be used ascending or descending
	 */
	public Grid(ColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection, int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling, boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType)
	{
		this(columnDefinitions, pageSize, rowSelection, cellSpacing, autoLoadData, stretchColumns, highlightRowOnMouseOver, emptyDataFilling, fixedCellSize, defaultSortingColumn, defaultSortingType, null, false, false);
	}
	
	/**
	 * Full constructor
	 * @param columnDefinitions the columns to be rendered
	 * @param pageSize the number of rows per page
	 * @param rowSelection the behavior of the grid about line selection
	 * @param cellSpacing the space between the cells
	 * @param autoLoadData if <code>true</code>, when a data source is set, its first page records are fetched and rendered.
	 * @param stretchColumns if <code>true</code>, the width of the columns are auto adjusted to fit the grid width. Prevents horizontal scrolling.   
	 * @param highlightRowOnMouseOver if <code>true</code>, rows change their styles when mouse passed over them
	 * @param emptyDataFilling an alternative text to be shown when there is no data for some data cell
	 * @param fixedCellSize equivalent of setting CSS attribute <code>table-layout</code> to <code>fixed</code>
	 * @param defaultSortingColumn the column to be used to automatically sort the grid's data when it is rendered for the first time 
	 * @param defaultSortingType tells the grid if <code>defaultSortingColumn</code> should be used ascending or descending
	 * @param rowDetailsWidgetCreator used to create on-demand row details
	 * @param showRowDetailsIcon if <code>true</code>, the second column of the grid will contain icons for expanding or collapsing the row's details 
	 */
	public Grid(ColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection, int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling, boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType, RowDetailWidgetCreator rowDetailsWidgetCreator, boolean showRowDetailsIcon, boolean freezeHeaders)
	{
		super(columnDefinitions, rowSelection, cellSpacing, stretchColumns, highlightRowOnMouseOver, fixedCellSize, rowDetailsWidgetCreator, showRowDetailsIcon, freezeHeaders);
		getColumnDefinitions().setGrid(this);
		this.emptyDataFilling = emptyDataFilling != null ? emptyDataFilling : " ";
		this.pageSize = pageSize;
		this.autoLoadData = autoLoadData;
		this.defaultSortingColumn = defaultSortingColumn;
		this.defaultSortingType = defaultSortingType;
		if(hasRowDetails())
		{
			this.rowDetailsManager = new RowDetailsManager(rowDetailsWidgetCreator);
		}
		super.render();
	}
	
	/**
	 * Sets the data source and re-renders the grid
	 * @param dataSource
	 */
	public void setDataSource(PagedDataSource<?> dataSource)
	{
		this.dataSource = dataSource;
		this.dataSource.setPageSize(this.pageSize);
		
		if(hasRowDetails())
		{
			this.rowDetailsManager.reset();
		}
		
		if(this.dataSource instanceof RemoteDataSource<?>)
		{
			RemoteDataSource<?> remote = (RemoteDataSource<?>) this.dataSource;
			
			remote.setCallback(new RemoteDataSourceCallback()
			{
				public void execute(int startRecord, int endRecord)
				{
					loaded = true;
					if(!autoSort())
					{
						render();
					}
				}
				
				public void cancelFetching()
				{
					render();			
				}
			});
			
			if(autoLoadData)
			{
				loadData();
			}
		}
		else if(this.dataSource instanceof LocalDataSource<?>)
		{
			LocalDataSource<?> local = (LocalDataSource<?>) this.dataSource;
			
			local.setCallback(new LocalDataSourceCallback()
			{
				public void execute()
				{
					loaded = true;
					if(!autoSort())
					{
						render();
					}
				}
			});
			
			if(autoLoadData)
			{
				loadData();
			}
		}
	}
	
	public void loadData()
	{
		if(!this.loaded)
		{
			if(this.dataSource instanceof RemoteDataSource)
			{
				if(this.dataSource instanceof MeasurableDataSource)
				{
					((MeasurableRemoteDataSource<?>) this.dataSource).load();
				}
				else
				{
					this.dataSource.nextPage();
				}
			}
			else if(this.dataSource instanceof LocalDataSource)
			{
				LocalDataSource<?> local = (LocalDataSource<?>) this.dataSource;
				local.load();
			}
		}
	}

	@Override
	protected DataRow createRow(int index, Element element)
	{	
		return new DataRow(index, element, this, hasSelectionColumn(), hasRowDetails(), hasRowDetailsIconColumn());
	}

	@Override
	protected int getRowsToBeRendered()
	{
		if(isDataLoaded())
		{
			if(this.dataSource.getCurrentPage() == 0)
			{
				this.dataSource.nextPage();
			}
			
			return this.dataSource.getCurrentPageSize();
		}
		
		return 0;
	}
	
	/**
	 * Gets the current page row count
	 * @return
	 */
	public int getCurrentPageSize()
	{
		return getRowsToBeRendered();
	}

	@Override
	protected void onClear()
	{
		if(this.dataSource != null)
		{
			this.dataSource.reset();
		}
		
		this.currentSortingColumn = null;
		this.ascendingSort = false;
		this.loaded = false;
		
		if(this.pager != null)
		{
			this.pager.update(0, false);
		}
		
		if(hasRowDetails())
		{
			this.rowDetailsManager.reset();
		}
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.AbstractGrid#onShowDetails(boolean, br.com.sysmap.crux.widgets.client.grid.Row, boolean)
	 */
	protected boolean onShowDetails(boolean show, Row row, boolean fireEvents)
	{
		boolean proceed = true;

		if (hasRowDetails()) 
		{
			DataRow dataRow = (DataRow) row;

			if(show)
			{
				if(fireEvents)
				{
					BeforeShowRowDetailsEvent event = BeforeShowRowDetailsEvent.fire(this, dataRow);
					proceed = !event.isCanceled();
				}
			}
			
			if(proceed)
			{
				dataRow.showDetailsArea(show);
				
				if(show)
				{	
					boolean detailsPanelCreated = dataRow.getDetailsPanel() != null;
					
					if(!detailsPanelCreated)
					{
						DataSourceRecord<?> record = dataRow.getDataSourceRecord();
						boolean detailLoaded = this.rowDetailsManager.isDetailLoaded(record);
						createAndAttachDetails(dataRow, record);
	
						if(fireEvents)
						{
							if(detailLoaded)
							{
								ShowRowDetailsEvent.fire(this, dataRow);
							}
							else
							{
								LoadRowDetailsEvent.fire(this, dataRow);
							}
						}
					}
					else
					{
						if(fireEvents)
						{
							ShowRowDetailsEvent.fire(this, dataRow);
						}
					}
				}
				
				dataRow.getDetailsPanel().setVisible(show);
			}
		}
		
		return proceed;
	}
	
	/**
	 * Creates and attaches the details widget to the row
	 * @param dataRow
	 * @param record
	 */
	private void createAndAttachDetails(DataRow dataRow, DataSourceRecord<?> record) 
	{
		Widget w = this.rowDetailsManager.createWidget(dataRow);
		RowDetailsPanel details = new RowDetailsPanel(dataRow, getRowDetailWidgetCreator());
		details.add(w);
		dataRow.attachDetails(details);
		ensureVisible(dataRow.getDetailsPanel());
		this.rowDetailsManager.setDetailLoaded(record);
	}
	
	@Override
	protected boolean onSelectRow(boolean select, DataRow row, boolean fireEvents)
	{
		boolean proceed = true;
		
		if(fireEvents)
		{
			BeforeRowSelectEvent event = BeforeRowSelectEvent.fire(this, row);
			proceed = !event.isCanceled();
		}
		
		if(proceed)
		{
			if(select && (RowSelectionModel.single.equals(getRowSelectionModel()) || RowSelectionModel.singleRadioButton.equals(getRowSelectionModel())))
			{
				DataSourceRecord<?>[] records = dataSource.getSelectedRecords();
				if(records != null)
				{
					for (int i = 0; i < records.length; i++)
					{
						DataSourceRecord<?> editableDataSourceRecord = records[i];
						editableDataSourceRecord.setSelected(false);
					}
				}
				
				Iterator<DataRow> it = getRowIterator();
				
				while(it.hasNext())
				{
					DataRow dataRow = it.next();
					dataRow.setSelected(false);
				}
			}
			
			row.setSelected(select);
		}
		
		return proceed;
	}

	@Override
	protected void renderRow(DataRow row)
	{
		renderRow(row, dataSource.getRecord(), false, null);
	}
	
	protected void renderRow(DataRow row,  DataSourceRecord<?> record, boolean editMode,String focusCellKey)
	{
		row.setDataSourceRecord(record);
		ColumnDefinitions defs = getColumnDefinitions();
		Iterator<ColumnDefinition> it = defs.getIterator();
		FastList<Widget> editors = new FastList<Widget>();
		FastList<String> editableColumns = new FastList<String>();
		
		while (it.hasNext())
		{
			ColumnDefinition column = it.next();
			
			if(column.isVisible())
			{
				Widget widget = null;
				String key = column.getKey();
				boolean wrapLine = true;
				boolean truncate = true;
				
				// Access a field is much more efficient than call the instanceof operator.
				// As this decision is executed for every column of every row when rendering the
				// grid, we decided to avoid the instanceof operator.
				if(column.isDataColumn)
				{
					DataColumnDefinition dataColumnDefinition = (DataColumnDefinition) column;
					
					wrapLine = true;
					truncate = false;
					
					DataColumnEditorCreator<?> editorCreator = dataColumnDefinition.getEditorCreator();
					
					if(!editMode || editorCreator == null)
					{
						widget = createDataLabel(dataColumnDefinition, key, row.getDataSourceRecord());
					}
					else
					{
						Object editorWidget = editorCreator.createEditorWidget((DataColumnDefinition) column);
						
						if(editorWidget != null)
						{
							widget = setValueToEditorWidget(row, key, editorWidget);
						}
						
						if(widget == null)
						{
							widget = createDataLabel(dataColumnDefinition, key, row.getDataSourceRecord());
						}
						else
						{
							editors.add(widget);
							editableColumns.add(key);
						}
					}
				}
				else
				{
					wrapLine = false;
					truncate = true;
					widget = createWidgetForColumn((WidgetColumnDefinition) column);
				}
				
				row.setCell(createCell(widget, wrapLine, truncate), key);
			}
		}
		
		if(editMode)
		{
			chooseFocusedEditor(editors, editableColumns, focusCellKey);
		}
		
		row.setSelected(row.getDataSourceRecord().isSelected());
		row.setEnabled(!row.getDataSourceRecord().isReadOnly());
		
		if(dataSource.hasNextRecord())
		{
			dataSource.nextRecord();
		}
	}

	/**
	 * Makes sure that only one row is in edit mode at a time 
	 * @param row
	 * @param editors
	 * @param editableColumns
	 */
	void swapCurrentEditingRow(DataRow row)
	{
		DataRow lastEditingRow = currentEditingRow;
		currentEditingRow = row;
		
		if(lastEditingRow != null)
		{
			FastList<ColumnDefinition> defs = getColumnDefinitions().getDefinitions();
			
			FastList<String> editableColumns = new FastList<String>();
			for(int i = 0; i < defs.size(); i++)
			{
				ColumnDefinition def = defs.get(i);
				if(def instanceof DataColumnDefinition)
				{
					DataColumnEditorCreator<?> editorCreator = ((DataColumnDefinition) def).getEditorCreator();
					if(def.isVisible() && editorCreator != null)
					{
						editableColumns.add(def.getKey());
					}
				}
			}
			
			confirmLastEditedRowValues(lastEditingRow, editableColumns);
		}
	}
	
	/**
	 * If there is any row in edit mode, applies its editors' values to the underlying data row.
	 * @param row
	 */
	private void confirmLastEditedRowValues(DataRow row, FastList<String> editableColumns) 
	{
		for (int i = 0; i < editableColumns.size(); i++) 
		{
			String key = editableColumns.get(i);
			Widget widget = row.getWidget(key);
			Object value = null;
			
			if(widget instanceof HasFormatter)
			{
				value = ((HasFormatter) widget).getUnformattedValue();
			}
			else if(widget instanceof HasValue)
			{
				value = ((HasValue<?>) widget).getValue();
			}
			
			dataSource.setValue(value, key, row.getDataSourceRecord());
		}
		
		renderRow(row, row.getDataSourceRecord(), false, null);
	}

	/**
	 * Sets the focus to the editor at the clicked cell or to the first focusable one at the row.
	 * @param editors
	 * @param editableColumns
	 * @param focusCellKey
	 */
	private void chooseFocusedEditor(FastList<Widget> editors, FastList<String> editableColumns, String focusCellKey) 
	{
		if(editors != null && editors.size() > 0)
		{
			for(int i = 0; i < editors.size(); i++)
			{
				Widget editorWidget = editors.get(i);
				String key = editableColumns.get(i);
				
				if(editorWidget instanceof Focusable)
				{
					if(focusCellKey == null || focusCellKey.equals(key))
					{
						final Focusable focusable = (Focusable) editorWidget;
						
						Scheduler.get().scheduleDeferred(new ScheduledCommand() 
						{
							public void execute() 
							{
								focusable.setFocus(true);
							}
						});
					
						break;
					}
				}
			}
		}
	}

	/**
	 * Applies the value present in the data cell to the related editor widget
	 * @param row
	 * @param key
	 * @param editorWidget
	 * @return
	 */
	private Widget setValueToEditorWidget(final DataRow row, final String key, Object editorWidget) 
	{
		Widget widget = null;
		
		if(editorWidget instanceof Widget)
		{
			widget = (Widget) editorWidget;
		}
		else if(editorWidget instanceof IsWidget)
		{
			widget = ((IsWidget) editorWidget).asWidget();
		}
		
		if(widget instanceof HasClickHandlers)
		{
			((HasClickHandlers) widget).addClickHandler(new ClickHandler() 
			{
				public void onClick(ClickEvent event) 
				{
					event.stopPropagation();
				}
			});
		}
		
		if((widget instanceof HasValue) && (widget instanceof HasValueChangeHandlers) && !(widget instanceof HasFormatter))
		{
			dataSource.copyValueToWidget((HasValue<?>) widget, key, row.getDataSourceRecord());
		}
		else if(widget instanceof HasFormatter)
		{
			Object value = dataSource.getValue(key, row.getDataSourceRecord());
			final HasFormatter hasFormatter = (HasFormatter) widget;
			hasFormatter.setUnformattedValue(value);
			
			if(widget instanceof HasBlurHandlers)
			{
				((HasBlurHandlers) widget).addBlurHandler(new BlurHandler() 
				{
					public void onBlur(BlurEvent event) 
					{
						dataSource.setValue(hasFormatter.getUnformattedValue(), key, row.getDataSourceRecord());
					}
				});
			}
		}
		
		return widget;
	}

	/**
	 * Creates a widget
	 * @param column
	 * @return
	 */
	private Widget createWidgetForColumn(WidgetColumnDefinition column)
	{
		try
		{
			return column.getWidgetColumnCreator().createWidgetForColumn();
		}
		catch (Exception e)
		{
			throw new RuntimeException(WidgetMsgFactory.getMessages().errorCreatingWidgetForColumn(column.getKey()),e);
		}
	}

	private Widget createDataLabel(DataColumnDefinition dataColumn, String key, DataSourceRecord<?> record)
	{
		Formatter formatter = dataColumn.getFormatter();
		Object value = dataSource.getValue(key, record);
		String str = emptyDataFilling;
		boolean useEmptyDataStyle = true;
		
		if(value != null)
		{
			if(formatter != null )
			{
				str = formatter.format(value);
				useEmptyDataStyle = false;
			}
			else
			{
				String strValue = value.toString(); 
				if(strValue.length() > 0)
				{
					str = strValue;
					useEmptyDataStyle = false;
				}
			}
		}
		
		Label label = new Label(str);
		
		if(useEmptyDataStyle)
		{
			label.addStyleName("emptyData");
		}
		
		if(!dataColumn.isWrapLine())
		{
			label.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		}		 
		else
		{
			label.getElement().getStyle().setProperty("whiteSpace", "normal");
		}
		
		return label;
	}
	
	@Override
	protected Cell createColumnHeaderCell(final ColumnDefinition columnDefinition)
	{
		ColumnHeader header = new ColumnHeader(columnDefinition, this);		
		headers.add(header);
		Cell cell = createHeaderCell(header);
		return cell;
	}

	@Override
	protected void onBeforeRenderRows()
	{
		this.currentEditingRow = null;
		
		if(isDataLoaded())
		{
			if(this.dataSource.getCurrentPage() > 0 && this.dataSource.getCurrentPageSize() == 0)
			{
				this.previousPage();
				return;
			}
			
			updatePager();
	
			for (int i=0; i<headers.size(); i++)
			{
				ColumnHeader header = headers.get(i);
				header.applySortingLayout();
			}
			
			dataSource.firstRecord();
		}
	}
	
	private void updatePager()
	{
		if(isDataLoaded() && this.pager != null)
		{
			if(this.pager != null)
			{
				this.pager.update(this.dataSource.getCurrentPage(),  !this.dataSource.hasNextPage());
			}
		}
	}

	public int getPageCount()
	{
		if(isDataLoaded() && this.dataSource instanceof MeasurablePagedDataSource<?>)
		{
			MeasurablePagedDataSource<?> ds = (MeasurablePagedDataSource<?>) this.dataSource;
			return ds.getPageCount();
		}
		else
		{
			return -1;
		}
	}

	public void nextPage()
	{
		if(isDataLoaded())
		{
			this.dataSource.nextPage();
			
			if(!(this.dataSource instanceof RemoteDataSource<?>))
			{
				render();
			}
		}
	}

	public void previousPage()
	{
		if(isDataLoaded())
		{
			this.dataSource.previousPage();
			
			if(!(this.dataSource instanceof RemoteDataSource<?>))
			{
				render();
			}
		}
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.paging.Pageable#setPager(org.cruxframework.crux.widgets.client.paging.Pager)
	 */
	public void setPager(Pager pager)
	{
		this.pager = pager;
		updatePager();
	}
	
	@Override
	protected void onClearRendering()
	{
		this.headers = new FastList<ColumnHeader>();
		if(hasRowDetails())
		{
			this.rowDetailsManager.clearRendering();
		}		
	}
	
	/**
	 * @return the dataSource
	 */
	public PagedDataSource<?> getDataSource()
	{
		return dataSource;
	}

	/**
	 * Sorts the grid's data by the given column
	 * @param columnKey
	 */
	public void sort(String columnKey, boolean ascending)
	{
		if(this.isDataLoaded())
		{
			this.dataSource.sort(columnKey, ascending);
			this.ascendingSort = ascending;
			this.currentSortingColumn = columnKey;
			this.render();
		}		
	}

	@Override
	public List<DataRow> getSelectedRows()
	{
		List<DataRow> result = new ArrayList<DataRow>();
		
		Iterator<DataRow> rows = getRowIterator();
		
		while(rows.hasNext())
		{
			DataRow row = rows.next();
			
			if(row.getDataSourceRecord().isSelected())
			{
				result.add(row);
			}
		}
		
		return result;
	}
	
	@Override
	public List<DataRow> getCurrentPageRows()
	{
		List<DataRow> result = new ArrayList<DataRow>();
		
		Iterator<DataRow> rows = getRowIterator();
		
		while(rows.hasNext())
		{
			DataRow row = rows.next();
			result.add(row);
		}
		
		return result;
	}
	
	/**
	 * Gets all selected data objects contained by the grid.
	 * @deprecated Use <code>getSelectedDataObjects()</code> instead
	 */
	@Deprecated
	public Object[] getSelectedDataRows()
	{
		return getSelectedDataObjects();
	}
	
	/**
	 * Gets all selected data objects contained by the grid.
	 * @return an array of data objects
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public Object[] getSelectedDataObjects()
	{
		if(this.dataSource != null)
		{
			DataSourceRecord[] selectedRecords = this.dataSource.getSelectedRecords();
			
			if(selectedRecords != null)
			{
				Object[] selectedObjs = new Object[selectedRecords.length]; 

				for (int i = 0; i < selectedRecords.length; i++)
				{
					Object o = this.dataSource.getBoundObject(selectedRecords[i]);
					selectedObjs[i] = o;					
				}

				return selectedObjs;
			}			
		}		
		
		return new Object[0];
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.event.row.HasBeforeRowSelectHandlers#addBeforeRowSelectHandler(org.cruxframework.crux.widgets.client.event.row.BeforeRowSelectHandler)
	 */
	public HandlerRegistration addBeforeRowSelectHandler(BeforeRowSelectHandler handler)
	{
		return addHandler(handler, BeforeRowSelectEvent.getType());
	}

	@Override
	protected void fireRowRenderEvent(DataRow row)
	{
		RowRenderEvent.fire(this, row);
	}

	@Override
	protected void fireRowClickEvent(DataRow row)
	{
		RowClickEvent.fire(this, row);
	}

	@Override
	protected void fireRowDoubleClickEvent(DataRow row)
	{
		RowDoubleClickEvent.fire(this, row);
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.paging.Pageable#goToPage(int)
	 */
	public void goToPage(int page)
	{
		if(isDataLoaded())
		{
			if(this.dataSource instanceof MeasurablePagedDataSource<?>)
			{
				((MeasurablePagedDataSource<?>) this.dataSource).setCurrentPage(page);

				if(!(this.dataSource instanceof RemoteDataSource<?>))
				{
					render();
				}
			}
			else
			{
				throw new UnsupportedOperationException(WidgetMsgFactory.getMessages().gridRandomPagingNotSupported());
			}
		}
		
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.paging.Pageable#isDataLoaded()
	 */
	public boolean isDataLoaded()
	{
		return this.dataSource != null && loaded;
	}
	
	/**
	 * Sorts the grid's data when it is loaded for the first time
	 */
	private boolean autoSort()
	{
		boolean sort = isAutoSortEnabled();
		
		if(sort)
		{
			sort(this.defaultSortingColumn, !this.defaultSortingType.equals(SortingType.descending));
		}
		
		return sort;
	}
	
	@Override
	public DataRow getRow(Widget w)
	{
		DataRow row = super.getRow(w);
		
		if (row == null && hasRowDetails()) 
		{
			while(!(w instanceof RowDetailsPanel))
			{
				w = w.getParent();
			}
			
			if(w instanceof RowDetailsPanel)
			{
				row = (DataRow) ((RowDetailsPanel) w).getRow();
			}
		}
		
		return row;
	}
	
	/**
	 * Checks if auto sorting is enabled
	 * @return
	 */
	private boolean isAutoSortEnabled()
	{
		if(!StringUtils.isEmpty(this.defaultSortingColumn))
		{
			ColumnDefinition column = getColumnDefinition(this.defaultSortingColumn);
			
			if(column != null && column instanceof DataColumnDefinition)
			{
				return true;
			}
			else
			{
				throw new IllegalArgumentException(WidgetMsgFactory.getMessages().errorGridNoDataColumnFound(this.defaultSortingColumn));
			}		
		}
		
		return false;
	}
	
	/**
	 * Grid default sorting type
	 * @author Gesse S. F. Dafe
	 */
	public enum SortingType
	{
		ascending,
		descending
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	protected static class ColumnHeader extends Composite
	{
		private FocusPanel clickable;
		private Label columnLabelArrow;
		
		private Grid grid;
		private ColumnDefinition columnDefinition;
		
		public ColumnHeader(ColumnDefinition columnDefinition, Grid grid)
		{
			this.grid = grid;
			this.columnDefinition = columnDefinition;
			
			clickable = new FocusPanel();
			
			HorizontalPanel panel = new HorizontalPanel();
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			
			Label columnLabel = new Label(columnDefinition.getLabel());
			columnLabel.setStyleName("label");
			
			columnLabelArrow = new Label(" ");
			columnLabelArrow.setStyleName("arrow");
			
			panel.add(columnLabel);
			panel.add(columnLabelArrow);
			
			clickable.add(panel);
			if (isDataColumnSortable(columnDefinition))
			{
				clickable.addClickHandler(createClickHandler());
			}
			
			initWidget(clickable);
			
			setStyleName("columnSorter");
		}
		
		/**
		 * @param columnDefinition2
		 * @return
		 */
		private boolean isDataColumnSortable(ColumnDefinition columnDefinition)
		{
			if(columnDefinition instanceof DataColumnDefinition)
			{
				return grid.isDataLoaded() 
				&& ((DataColumnDefinition) columnDefinition).isSortable()
				&& this.grid.getDataSource().getColumnDefinitions().getColumn(columnDefinition.getKey()).isSortable();
			}
			return false;			
		}

		/**
		 * @return
		 */
		private ClickHandler createClickHandler()
		{
			return new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					String columnKey = columnDefinition.getKey();
					String previousSorting = grid.currentSortingColumn;
					grid.currentSortingColumn = columnKey;
										
					boolean resorting = columnKey.equals(previousSorting);
					boolean descending = resorting && grid.ascendingSort;
										
					grid.sort(columnKey, !descending);									
				}			
			};
		}
		
		void applySortingLayout()
		{
			if(this.columnDefinition.getKey().equals(grid.currentSortingColumn))
			{
				if(grid.ascendingSort)
				{
					addStyleDependentName("asc");
				}
				else
				{
					addStyleDependentName("desc");
				}
			}
			else
			{
				removeStyleDependentName("asc");
				removeStyleDependentName("desc");
			}
		}
	}

	public boolean isEditable() 
	{
		FastList<ColumnDefinition> columnDefinitions = getColumnDefinitions().getDefinitions();
		
		for(int i = 0; i < columnDefinitions.size(); i++)
		{
			ColumnDefinition col = columnDefinitions.get(i);
			if(col instanceof DataColumnDefinition)
			{
				DataColumnDefinition dataCol = (DataColumnDefinition) col;
				if(dataCol.getEditorCreator() != null)
				{
					return true;
				}
			}
		}
		
		return false;
	}

	public void makeEditable(DataRow row, Cell focusCell) 
	{
		if(!row.isEditMode())
		{
			if(currentEditingRow != null)
			{
				currentEditingRow.setEditMode(false);
			}
			
			swapCurrentEditingRow(row);
			
			String focusCellKey = null; 
			
			if(focusCell != null)
			{
				FastList<ColumnDefinition> defs = getColumnDefinitions().getDefinitions();
				for (int i = 0; i < defs.size(); i++) 
				{
					ColumnDefinition def = defs.get(i);
					String key = def.getKey();
					Cell cell = row.getCell(key);
					if(focusCell.equals(cell))
					{
						focusCellKey = key;
						break;
					}
				}
			}
			
			renderRow(row, row.getDataSourceRecord(), true, focusCellKey);
			
			row.setEditMode(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> void addRow(T dataObject)
	{
		if(isDataLoaded())
		{
			DataSourceRecord<T> newRec = (DataSourceRecord<T>) dataSource.insertRecord(0);
			newRec.setRecordObject(dataObject);
			goToPage(1);
			makeEditable(getRowIterator().next(), null);
		}
	}
	
	public void removeSelectedRows()
	{
		removeSelectedRows(false);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void removeSelectedRows(boolean fromCurrentPageOnly)
	{
		if(isDataLoaded())
		{
			Object[] selectedDataObjects = null;
			
			if(!fromCurrentPageOnly)
			{
				selectedDataObjects = getSelectedDataObjects();
			}
			else
			{
				List<DataRow> selectedRows = getSelectedRows();
				if(selectedRows != null)
				{
					selectedDataObjects = new Object[selectedRows.size()];
					for (int i = 0; i < selectedRows.size(); i++) 
					{
						Object o = selectedRows.get(i).getBoundObject();
						selectedDataObjects[i] = o;
					}
				}
			}
			
			if(selectedDataObjects != null)
			{
				for (int i = 0; i < selectedDataObjects.length; i++) 
				{
					Object object = selectedDataObjects[i];
					int index = ((PagedDataSource<T>)dataSource).getRecordIndex((T)object);
					dataSource.selectRecord(index, false);
					dataSource.removeRecord(index);
				}
			}
			
			refresh();
		}
	}
	
	public Object[] getRemovedDataObjects()
	{
		if(isDataLoaded())
		{
			return toDataObjectArray(dataSource.getRemovedRecords());
		}
		
		return new Object[0];
	}
	
	public Object[] getCreatedDataObjects()
	{
		if(isDataLoaded())
		{
			return toDataObjectArray(dataSource.getNewRecords());
		}
		
		return new Object[0];
	}
	
	public Object[] getEditedDataObjects()
	{
		if(isDataLoaded())
		{
			return toDataObjectArray(dataSource.getUpdatedRecords());
		}
		
		return new Object[0];
	}
	
	private Object[] toDataObjectArray(DataSourceRecord<?>[] records)
	{
		Object[] dataObjects = new Object[0];
		
		if(records != null)
		{
			dataObjects = new Object[records.length];
		}
		
		for(int i = 0; i < records.length; i++)
		{
			DataSourceRecord<?> record = records[i];
			Object dataObject = record.getRecordObject();
			dataObjects[i] = dataObject;
		}
		
		return dataObjects;
	}

	@Override
    protected void onBeforeRender()
    {
	    assert (getDataSource() == null || getDataSource().getColumnDefinitions() != null):"DataSource dos not declare any ColumnDefinition";
    }
}