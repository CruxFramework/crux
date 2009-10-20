package br.com.sysmap.crux.advanced.client.grid.impl;

import br.com.sysmap.crux.advanced.client.AdvancedWidgetMessages;
import br.com.sysmap.crux.advanced.client.grid.model.AbstractGrid;
import br.com.sysmap.crux.advanced.client.grid.model.Cell;
import br.com.sysmap.crux.advanced.client.grid.model.Row;
import br.com.sysmap.crux.core.client.datasource.BindableDataSource;
import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.EditablePagedDataSource;

import com.google.gwt.dom.client.Element;

public class DataRow extends Row
{
	private EditableDataSourceRecord dataSourceRecord;
	
	protected DataRow(int index, Element elem, AbstractGrid<?> grid, boolean hasSelectionCell, AdvancedWidgetMessages messages)
	{
		super(index, elem, grid, hasSelectionCell, messages);
	}

	/**
	 * @return the dataSourceRowId
	 */
	public EditableDataSourceRecord getDataSourceRecord()
	{
		return dataSourceRecord;
	}

	/**
	 * @param dataSourceRowId the dataSourceRowId to set
	 */
	void setDataSourceRecord(EditableDataSourceRecord dataSourceRowId)
	{
		this.dataSourceRecord = dataSourceRowId;
	}
	
	public Object getValue(String column)
	{
		int index = ((Grid) getGrid()).getDataSource().getMetadata().getColumnPosition(column);
		return dataSourceRecord.get(index);
	}
	
	@SuppressWarnings("unchecked")
	public Object getBindedObject()
	{
		Grid grid = (Grid) getGrid();
		
		EditablePagedDataSource dataSource = grid.getDataSource();
		
		if(dataSource instanceof BindableDataSource)
		{
			BindableDataSource bindable = (BindableDataSource) dataSource;
			return bindable.getBindedObject(getDataSourceRecord());
		}
		else
		{
			throw new GridException(getMessages().getBindedObjectNotSupported());
		}
	}
	
	@Override
	protected void setCell(Cell cell, String column)
	{
		super.setCell(cell, column);
	}	
	
	@Override
	public boolean isSelected()
	{
		return this.dataSourceRecord.isSelected();
	}
	
	@Override
	public void setSelected(boolean selected)
	{
		getDataSourceRecord().setSelected(selected);
		super.setSelected(selected);
	}
	
	@Override
	public boolean isEnabled()
	{
		return !this.dataSourceRecord.isReadOnly();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		this.dataSourceRecord.setReadOnly(!enabled);
		super.setEnabled(enabled);
	}
}