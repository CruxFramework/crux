package br.com.sysmap.crux.advanced.client.grid.datagrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.sysmap.crux.advanced.client.grid.model.AbstractGrid;
import br.com.sysmap.crux.advanced.client.grid.model.Cell;
import br.com.sysmap.crux.advanced.client.grid.model.ColumnDefinitions;
import br.com.sysmap.crux.advanced.client.grid.model.RowSelectionModel;
import br.com.sysmap.crux.advanced.client.paging.Pageable;
import br.com.sysmap.crux.advanced.client.paging.Pager;
import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.EditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSourceCallback;
import br.com.sysmap.crux.core.client.datasource.MeasurableDataSource;
import br.com.sysmap.crux.core.client.datasource.MeasurablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.MeasurableRemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSourceCallback;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Grid for data rendering
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class PagedDataGrid extends AbstractGrid<DataColumnDefinition, DataRow> implements Pageable {	

	private int pageSize;
	private EditablePagedDataSource dataSource;
	private List<ColumnHeader> headers = new ArrayList<ColumnHeader>();
	private boolean autoLoadData;
	private boolean loaded;
	private String currentSortingColumn;
	private boolean ascendingSort;
	private Pager pager; 
	private RowSelectionModel rowSelectionModel;
	
	/**
	 * Constructor
	 * @param columnDefinitions
	 * @param pageSize
	 * @param rowSelectionModel
	 */
	public PagedDataGrid(DataColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelectionModel, int cellSpacing, boolean autoLoadData)
	{
		super(columnDefinitions, rowSelectionModel, cellSpacing);
		this.pageSize = pageSize;
		this.rowSelectionModel = rowSelectionModel;
		this.autoLoadData = autoLoadData;
		super.render();
	}
	
	/**
	 * Sets the data source, forcing re-rendering
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
			this.loaded = true;
			
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
		return new DataRow(index, element, this, hasSelectionColumn());
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
	protected void onSelectRow(boolean select, DataRow row)
	{
		if(select && (RowSelectionModel.SINGLE.equals(rowSelectionModel) || RowSelectionModel.SINGLE_WITH_RADIO.equals(rowSelectionModel)))
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
				dataRow.markAsSelected(false);
			}			
		}		
		
		row.getDataSourceRecord().setSelected(select);
		row.markAsSelected(select);
	}

	@Override
	protected void renderRow(DataRow row)
	{
		row.setDataSourceRecord(dataSource.getRecord());
		
		ColumnDefinitions<DataColumnDefinition> defs = getColumnDefinitions();
		Iterator<DataColumnDefinition> it = defs.getIterator();
		while (it.hasNext())
		{
			DataColumnDefinition column = it.next();
			
			if(column.isVisible())
			{
				String key = column.getKey();
				String formatterName = column.getFormatter();
				Object value = dataSource.getValue(key);
				String str = "";
				
				if(value != null)
				{
					if(formatterName != null && formatterName.length() > 0)
					{
						Formatter formatter = Screen.getFormatter(formatterName);
						str = formatter.format(value);
					}
					else
					{
						str = value.toString();
					}
				}
				
				Label label = new Label(str);
				row.setCell(createCell(label), key);
			}			
		}
		
		row.markAsSelected(row.getDataSourceRecord().isSelected());
		
		if(dataSource.hasNextRecord())
		{
			dataSource.nextRecord();
		}
	}
	
	@Override
	protected Cell createColumnHeaderCell(final DataColumnDefinition columnDefinition)
	{
		ColumnHeader header = new ColumnHeader(columnDefinition, this);		
		headers.add(header);
		Cell cell = createHeaderCell(header);
		cell.setWidth("100%");
		cell.setHeight("100%");
		return cell;
	}

	@Override
	protected void onBeforeRenderRows()
	{
		if(this.dataSource != null)
		{
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
		if(this.dataSource != null && this.pager != null)
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
	
	/*
	public List<Object> getSelectedRows()
	{
		if(this.dataSource instanceof Bindable)
		{
			Bindable<?> bindable = (Bindable<?>) this.dataSource;
			bindable.getBindedObject();			
		}
	}
	*/
	
	protected static class ColumnHeader extends Composite
	{
		private FocusPanel clickable;
		private Label columnLabelArrow;
		
		private PagedDataGrid grid;
		private DataColumnDefinition columnDefinition;
		
		public ColumnHeader(DataColumnDefinition columnDefinition, PagedDataGrid grid)
		{
			this.grid = grid;
			this.columnDefinition = columnDefinition;
			
			clickable = new FocusPanel();
			clickable.setWidth("100%");
			clickable.setHeight("100%");
			
			HorizontalPanel panel = new HorizontalPanel();
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			panel.setHeight("100%");
			
			Label columnLabel = new Label(columnDefinition.getLabel());
			columnLabel.setStyleName("label");
			
			columnLabelArrow = new Label(" ");
			columnLabelArrow.setStyleName("arrow");
			
			panel.add(columnLabel);
			panel.add(columnLabelArrow);
			
			clickable.add(panel);
			clickable.addClickHandler(createClickHandler());
			
			initWidget(clickable);
			
			setStyleName("columnSorter");
		}
		
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
	EditablePagedDataSource getDataSource()
	{
		return dataSource;
	}
}