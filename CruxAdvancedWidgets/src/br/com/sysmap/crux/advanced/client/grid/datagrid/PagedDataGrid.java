package br.com.sysmap.crux.advanced.client.grid.datagrid;

import java.util.Iterator;

import br.com.sysmap.crux.advanced.client.grid.model.AbstractGrid;
import br.com.sysmap.crux.advanced.client.grid.model.Cell;
import br.com.sysmap.crux.advanced.client.grid.model.ColumnDefinition;
import br.com.sysmap.crux.advanced.client.grid.model.ColumnDefinitions;
import br.com.sysmap.crux.advanced.client.grid.model.RowSelectionModel;
import br.com.sysmap.crux.advanced.client.paging.Pageable;
import br.com.sysmap.crux.advanced.client.paging.Pager;
import br.com.sysmap.crux.core.client.datasource.EditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSourceCallback;
import br.com.sysmap.crux.core.client.datasource.PagedDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSourceCallback;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Grid for data rendering
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class PagedDataGrid extends AbstractGrid<DataColumnDefinition, DataRow> implements Pageable {	

	private int pageSize;
	private EditablePagedDataSource dataSource;
	private boolean autoLoadData;
	private boolean loaded;
	private String currentSortingColumn;
	private boolean ascendingSort;
	private Pager pager; 
	
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
					refresh();
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
			this.loaded = true;
			
			if(this.dataSource instanceof LocalDataSource)
			{
				LocalDataSource<?, ?> local = (LocalDataSource<?, ?>) this.dataSource;
				local.load();
			}
			else if(this.dataSource instanceof RemoteDataSource)
			{
				this.dataSource.nextPage();
			}
		}
	}

	private void refresh()
	{
		super.clear();
		super.render();
	}

	@Override
	protected DataRow createRow(int index, Element element)
	{	
		return new DataRow(index, element, this, hasSelectionColumn());
	}

	@Override
	protected int getRowsToBeRendered()
	{
		if(this.dataSource != null)
		{
			return this.dataSource.getCurrentPageSize();
		}
		
		return 0;
	}

	@Override
	protected void onClear()
	{
		this.dataSource.reset();
		this.loaded = false;
	}

	@Override
	protected void onSelectRow(boolean select, DataRow row)
	{
		row.getDataSourceRecord().setSelected(select);
	}

	@Override
	protected void renderRow(DataRow row)
	{
		dataSource.nextRecord();
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
				row.setCell(createCell(column.getWidth(), label), key);
			}			
		}
		
		row.markAsSelected(row.getDataSourceRecord().isSelected());
	}
	
	@Override
	protected Cell createHeaderCell(final DataColumnDefinition columnDefinition)
	{
		FocusPanel clickable = new FocusPanel();
		
		clickable.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				String column = columnDefinition.getKey();
				String previousSorting = currentSortingColumn;
				boolean resorting = column.equals(previousSorting);
				
				currentSortingColumn = column;
								
				if(!resorting)
				{
					ascendingSort = true;
				}
				else
				{
					ascendingSort = !ascendingSort;
				}
				
				dataSource.sort(column, ascendingSort);
				refresh();
			}			
		});		
		
		HorizontalPanel panel = new HorizontalPanel();
		
		Label columnLabel = new Label(columnDefinition.getLabel());
		columnLabel.setStyleName("label");
		
		Label columnLabelArrow = new Label(" ");
		columnLabelArrow.setStyleName("arrow");
		
		panel.add(columnLabel);
		panel.add(columnLabelArrow);
		
		clickable.add(panel);
		
		Cell cell = createHeaderCell(columnDefinition.getWidth(), clickable);
		
		return cell;
	}

	@Override
	protected void onBeforeRenderRows()
	{
		if(this.dataSource != null)
		{
			boolean hasMorePages = false;
			
			if(!(this.dataSource instanceof StreamingDataSource))
			{
				hasMorePages = this.dataSource.hasNextPage();
			}
			else
			{
				// TODO
			}			
			
			this.pager.update(this.dataSource.getCurrentPage(), !hasMorePages);
			
			dataSource.firstRecord();
		}
	}

	public int getPageCount()
	{
		if(!(this.dataSource instanceof StreamingDataSource))
		{
			this.dataSource.getPageCount();
		}
		else
		{
			return -1;
		}
		
		return 0;
	}

	public void nextPage()
	{
		this.dataSource.nextPage();
	}

	public void previousPage()
	{
		this.dataSource.previousPage();	
	}

	public void setPager(Pager pager)
	{
		this.pager = pager;		
	}
}