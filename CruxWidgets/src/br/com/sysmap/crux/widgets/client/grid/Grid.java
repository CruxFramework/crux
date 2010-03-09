package br.com.sysmap.crux.widgets.client.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.sysmap.crux.core.client.datasource.BindableDataSource;
import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.EditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.HasDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSourceCallback;
import br.com.sysmap.crux.core.client.datasource.MeasurableDataSource;
import br.com.sysmap.crux.core.client.datasource.MeasurablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.MeasurableRemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSourceCallback;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.RegisteredWidgetFactories;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.widgets.client.WidgetMessages;
import br.com.sysmap.crux.widgets.client.event.row.BeforeRowSelectEvent;
import br.com.sysmap.crux.widgets.client.event.row.BeforeRowSelectHandler;
import br.com.sysmap.crux.widgets.client.event.row.HasBeforeRowSelectHandlers;
import br.com.sysmap.crux.widgets.client.event.row.RowClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderEvent;
import br.com.sysmap.crux.widgets.client.paging.Pageable;
import br.com.sysmap.crux.widgets.client.paging.Pager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A paged sortable data grid
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class Grid extends AbstractGrid<DataRow> implements Pageable, HasDataSource<EditablePagedDataSource>, HasBeforeRowSelectHandlers {	

	private int pageSize;
	private EditablePagedDataSource dataSource;
	private List<ColumnHeader> headers = new ArrayList<ColumnHeader>();
	private boolean autoLoadData;
	private boolean loaded;
	private String currentSortingColumn;
	private boolean ascendingSort;
	private Pager pager; 
	private RowSelectionModel rowSelectionModel;
	private RegisteredWidgetFactories registeredWidgetFactories = null;
	private long generatedWidgetId = 0;
	private WidgetMessages messages = GWT.create(WidgetMessages.class);
	private final String emptyDataFilling;
	
	/**
	 * Full constructor
	 * @param columnDefinitions the columns to be rendered
	 * @param pageSize the number of rows per page
	 * @param rowSelection the behavior of the grid about line selection
	 * @param cellSpacing the space between the cells
	 * @param autoLoadData if <code>true</code>, when a data source is set, its first page records are fetched and rendered. 
	 * 	If <code>false</code>, the method <code>loadData()</code> must be invoked for rendering the first page.
	 */
	public Grid(ColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelectionModel, int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling)
	{
		super(columnDefinitions, rowSelectionModel, cellSpacing, stretchColumns, highlightRowOnMouseOver);
		getColumnDefinitions().setGrid(this);
		this.emptyDataFilling = emptyDataFilling != null ? emptyDataFilling : " ";
		this.registeredWidgetFactories = (RegisteredWidgetFactories) GWT.create(RegisteredWidgetFactories.class);
		this.pageSize = pageSize;
		this.rowSelectionModel = rowSelectionModel;
		this.autoLoadData = autoLoadData;
		super.render();
	}
	
	/**
	 * Sets the data source and re-renders the grid
	 * @param dataSource
	 */
	public void setDataSource(EditablePagedDataSource dataSource)
	{
		this.dataSource = dataSource;
		this.dataSource.setPageSize(this.pageSize);
		
		if(this.dataSource instanceof LocalDataSource)
		{
			LocalDataSource<?, ?> local = (LocalDataSource<?, ?>) this.dataSource;
			
			local.setCallback(new LocalDataSourceCallback()
			{
				public void execute()
				{
					loaded = true;
					render();
				}
			});
			
			if(autoLoadData)
			{
				loadData();
			}
		}
		else if(this.dataSource instanceof RemoteDataSource)
		{
			RemoteDataSource<?, ?> remote = (RemoteDataSource<?, ?>) this.dataSource;
			
			remote.setCallback(new RemoteDataSourceCallback()
			{
				public void execute(int startRecord, int endRecord)
				{
					loaded = true;
					render();
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
	}
	
	@SuppressWarnings("unchecked")
	public void loadData()
	{
		if(!this.loaded)
		{
			if(this.dataSource instanceof LocalDataSource)
			{
				LocalDataSource<?, ?> local = (LocalDataSource<?, ?>) this.dataSource;
				local.load();
			}
			else if(this.dataSource instanceof RemoteDataSource)
			{
				if(this.dataSource instanceof MeasurableDataSource)
				{
					((MeasurableRemoteDataSource) this.dataSource).load();
				}
				else
				{
					this.dataSource.nextPage();
				}
			}
		}
	}

	@Override
	protected DataRow createRow(int index, Element element)
	{	
		return new DataRow(index, element, this, hasSelectionColumn(), this.messages);
	}

	@Override
	protected int getRowsToBeRendered()
	{
		if(this.dataSource != null && loaded)
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
			if(select && (RowSelectionModel.single.equals(rowSelectionModel) || RowSelectionModel.singleWithRadioButton.equals(rowSelectionModel)))
			{
				EditableDataSourceRecord[] records = dataSource.getSelectedRecords();
				if(records != null)
				{
					for (int i = 0; i < records.length; i++)
					{
						EditableDataSourceRecord editableDataSourceRecord = records[i];
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
		row.setDataSourceRecord(dataSource.getRecord());
		
		ColumnDefinitions defs = getColumnDefinitions();
		Iterator<ColumnDefinition> it = defs.getIterator();
		while (it.hasNext())
		{
			ColumnDefinition column = it.next();
			
			if(column.isVisible())
			{
				Widget widget = null;
				String key = column.getKey();
				
				if(column instanceof DataColumnDefinition)
				{
					widget = createDataLabel(column, key);					
				}
				else if(column instanceof WidgetColumnDefinition)
				{
					widget = createWidget((WidgetColumnDefinition) column, row);
				}
				
				row.setCell(createCell(widget), key);
			}
		}
		
		row.setSelected(row.getDataSourceRecord().isSelected());
		row.setEnabled(!row.getDataSourceRecord().isReadOnly());
		
		if(dataSource.hasNextRecord())
		{
			dataSource.nextRecord();
		}
	}

	/**
	 * TODO - Gessé - cloned element does not have a parent
	 * @param column
	 * @param row
	 * @return
	 * @throws InterfaceConfigException 
	 */
	private Widget createWidget(WidgetColumnDefinition column, DataRow row)
	{
		try
		{
			Element template = column.getWidgetTemplate();
			Element clone = (Element) template.cloneNode(true);
			clone.setId(template.getId() + "_" + generateWidgetIdSufix());
			WidgetFactory<?> factory = registeredWidgetFactories.getWidgetFactory(template.getAttribute("_type"));
			return factory.createWidget(clone, clone.getId(), false);
		}
		catch (InterfaceConfigException e)
		{
			GWT.log(e.getMessage(), e);
			throw new RuntimeException(messages.errorCreatingWidgetForColumn(column.getKey()));
		}
	}

	/**
	 * 
	 * @return
	 */
	private long generateWidgetIdSufix()
	{
		if(generatedWidgetId == 0)
		{
			generatedWidgetId = new Date().getTime();
		}
		
		return ++generatedWidgetId;
	}

	private Widget createDataLabel(ColumnDefinition column, String key)
	{
		DataColumnDefinition dataColumn = (DataColumnDefinition) column;
		String formatterName = dataColumn.getFormatter();
		Object value = dataSource.getValue(key);
		String str = emptyDataFilling;
		boolean useEmptyDataStyle = true;
		
		if(value != null)
		{
			if(formatterName != null && formatterName.length() > 0)
			{
				Formatter formatter = Screen.getFormatter(formatterName);
				str = formatter.format(value);
				useEmptyDataStyle = false;
			}
			else
			{
				String strValue = value.toString(); 
				if(str.length() > 0)
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
		if(this.dataSource != null)
		{
			if(this.dataSource.getCurrentPage() > 0 && this.dataSource.getCurrentPageSize() == 0)
			{
				this.previousPage();
				return;
			}
			
			updatePager();
	
			for (ColumnHeader header : headers)
			{
				header.applySortingLayout();
			}
			
			dataSource.firstRecord();
		}
	}
	
	private void updatePager()
	{
		if(this.dataSource != null && this.pager != null && this.loaded)
		{
			if(this.pager != null)
			{
				this.pager.update(this.dataSource.getCurrentPage(),  !this.dataSource.hasNextPage());
			}
		}
	}

	public int getPageCount()
	{
		if(this.dataSource instanceof MeasurablePagedDataSource)
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
		if(this.dataSource != null && loaded)
		{
			this.dataSource.nextPage();
			
			if(!(this.dataSource instanceof RemoteDataSource))
			{
				render();
			}
		}
	}

	public void previousPage()
	{
		if(this.dataSource != null && loaded)
		{
			this.dataSource.previousPage();
			
			if(!(this.dataSource instanceof RemoteDataSource))
			{
				render();
			}
		}
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.Pageable#setPager(br.com.sysmap.crux.widgets.client.paging.Pager)
	 */
	public void setPager(Pager pager)
	{
		this.pager = pager;
		updatePager();
	}
	
	@Override
	protected void onClearRendering()
	{
		this.headers = new ArrayList<ColumnHeader>();		
	}
	
	/**
	 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
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
			return (columnDefinition instanceof DataColumnDefinition) && 
			        this.grid.getDataSource() != null && 
			        this.grid.getDataSource().getMetadata().getColumn(columnDefinition.getKey()).isSortable();
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
					if(grid.dataSource != null && grid.loaded)
					{
						String column = columnDefinition.getKey();
						String previousSorting = grid.currentSortingColumn;
						boolean resorting = column.equals(previousSorting);
						
						grid.currentSortingColumn = column;
				
						if(!resorting)
						{
							grid.ascendingSort = true;
						}
						else
						{
							grid.ascendingSort = !grid.ascendingSort;
						}
						
						grid.dataSource.sort(column, grid.ascendingSort);
						
						grid.render();
					}				
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

	/**
	 * @return the dataSource
	 */
	public EditablePagedDataSource getDataSource()
	{
		return dataSource;
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
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object[] getSelectedDataRows()
	{
		if(this.dataSource != null)
		{
			EditableDataSourceRecord[] selectedRecords = this.dataSource.getSelectedRecords();
			
			if(selectedRecords != null)
			{
				if(this.dataSource instanceof BindableDataSource)
				{
					BindableDataSource<EditableDataSourceRecord, ?> bindable = (BindableDataSource<EditableDataSourceRecord, ?>) this.dataSource;
					
					Object[] selectedObjs = new Object[selectedRecords.length]; 
					
					for (int i = 0; i < selectedRecords.length; i++)
					{
						Object o = bindable.getBindedObject(selectedRecords[i]);
						selectedObjs[i] = o;					
					}
					
					return selectedObjs;
				}
				else
				{
					return  selectedRecords;
				}
			}			
		}		
		
		return new Object[0];
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.row.HasBeforeRowSelectHandlers#addBeforeRowSelectHandler(br.com.sysmap.crux.widgets.client.event.row.BeforeRowSelectHandler)
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
}