package br.com.sysmap.crux.advanced.client.grid.datagrid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.advanced.client.grid.model.AbstractGrid;
import br.com.sysmap.crux.advanced.client.grid.model.Cell;
import br.com.sysmap.crux.advanced.client.grid.model.RowSelectionModel;
import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.PagedDataSource;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Grid for data rendering
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class PagedDataGrid extends AbstractGrid<DataColumnDefinition> {	

	private RowSelectionModel rowSelectionModel;
	private int pageSize;
	private PagedDataSource<EditableDataSourceRecord> dataSource;	
	private Map<Integer, EditableDataSourceRecord> rowsByIndex = new HashMap<Integer, EditableDataSourceRecord>();
	
	/**
	 * Constructor
	 * @param columnDefinitions
	 * @param pageSize
	 * @param rowSelectionModel
	 */
	public PagedDataGrid(DataColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelectionModel)
	{
		super(columnDefinitions);
		this.rowSelectionModel = rowSelectionModel;
		this.pageSize = pageSize;
		super.render();
	}
	
	/**
	 * Sets the data source, forcing re-rendering
	 * @param dataSource
	 */
	public void setDataSource(PagedDataSource<EditableDataSourceRecord> dataSource)
	{
		this.dataSource = dataSource;
		refresh();
	}

	private void refresh()
	{
		rowsByIndex = new HashMap<Integer, EditableDataSourceRecord>();
		super.clear();
		super.render();
	}

	/**
	 * Marks all data source rows as selected
	 */
	public void selectAllRows()
	{
		selectAllRows(true);
	}
	
	/**
	 * Marks all data source rows as unselected
	 */
	public void unselectAllRows()
	{
		selectAllRows(false);
	}	
	
	public void nextPage()
	{
		this.dataSource.nextPage();
		refresh();
	}
	
	public void previousPage()
	{
		this.dataSource.previousPage();
		refresh();
	}
	
	@SuppressWarnings("unchecked")
	private void selectAllRows(boolean select) 
	{
		if(dataSource != null && !RowSelectionModel.UNSELECTABLE.equals(rowSelectionModel))
		{
			dataSource.firstRecord();
			
			for(int i = 0; dataSource.hasNextRecord(); i++)
			{
				dataSource.nextRecord();
				dataSource.getRecord().setSelected(select);
				
				if(hasSelectionColumn())
				{
					((HasValue<Boolean>)getWidget(i, 0)).setValue(select);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void selectRow(int index, boolean select)
	{	
		EditableDataSourceRecord record = rowsByIndex.get(index);
		record.setSelected(select);
	
		if(hasSelectionColumn())
		{
			HasValue<Boolean> selector = (HasValue<Boolean>) getWidget(index, 0);
			selector.setValue(select);
		}
		
		if(select)
		{
			setRowStyle(index, "row-selected");
		}
		else
		{
			setRowStyle(index, "row");
		}
	}

	private ClickHandler createSelectAllClickHandler()
	{
		return new ClickHandler()
		{
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event)
			{
				HasValue<Boolean> source = (HasValue<Boolean>) event.getSource();
				boolean select = source.getValue();
				if(select)
				{
					selectAllRows();
				}
				else
				{
					unselectAllRows();
				}
								
			}					
		};
	}
	
	private boolean hasSelectionColumn()
	{
		return RowSelectionModel.MULTIPLE_WITH_CHECKBOX.equals(rowSelectionModel) || RowSelectionModel.SINGLE_WITH_RADIO.equals(rowSelectionModel);
	}
	
	protected int getFirstDataColumnIndex()
	{
		if(hasSelectionColumn())
		{
			return 1;
		}
		
		return 0;
	}

	private void preProcessRow(int rowIndex)
	{
		if(hasSelectionColumn())
		{
			HasClickHandlers selector = null;
				
			if(RowSelectionModel.MULTIPLE_WITH_CHECKBOX.equals(rowSelectionModel))
			{
				selector = new CheckBox();
				
			}
			else
			{
				selector = new RadioButton(getGridGeneratedId() + "_lineSelector");
			}
			
			selector.addClickHandler(createSelectRowClickHandler(rowIndex));
			Cell cell = createSimpleCell("", (Widget) selector);
			setWidget(cell, rowIndex, 0);
		}	
	}
	
	@Override
	protected void preProcessHeaderRow(int rowIndex)
	{
		Widget widget = null;
		
		if(hasSelectionColumn())
		{
			if(RowSelectionModel.MULTIPLE_WITH_CHECKBOX.equals(rowSelectionModel))
			{
				widget = new CheckBox();
				((CheckBox) widget).addClickHandler(createSelectAllClickHandler());
			}
			else if(RowSelectionModel.SINGLE_WITH_RADIO.equals(rowSelectionModel))
			{
				widget = new Label(" ");
			}
						
			Cell cell = createHeaderCell("", widget);
			cell.addStyleName("rowSelectionColumn");
			setWidget(cell, rowIndex, 0);
		}		
	}

	@Override
	protected void renderRows()
	{
		if(dataSource != null)
		{
			for(int i = 0; dataSource.hasNextRecord(); i++)
			{
				dataSource.nextRecord();
				int rowIndex = addRow(i);
				
				preProcessRow(rowIndex);				
								
				List<DataColumnDefinition> columns = getColumnDefinitions();
				for (DataColumnDefinition columnDefinition : columns)
				{
					if(columnDefinition.isVisible())
					{
						// TODO - usar masked label ou hasvalue
						String key = columnDefinition.getKey();
						String formatterName = columnDefinition.getFormatter();
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
						setWidget(label, rowIndex, key);
					}
				}
			}			
		}
	}

	@Override
	protected void clearRows()
	{
		
	}

	@Override
	protected int getRowsToBeRendered()
	{
		return pageSize;
	}
	
	private ClickHandler createSelectRowClickHandler(final int rowIndex)
	{
		return new ClickHandler()
		{
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event)
			{
				HasValue<Boolean> selector = (HasValue<Boolean>) event.getSource();
				rowsByIndex.get(rowIndex).setSelected(selector.getValue());
			}
		};
	}

	@Override
	protected int postProcessHeaders()
	{
		return 0;
	}

	@Override
	protected int preProcessHeaders()
	{
		return 0;
	}
}