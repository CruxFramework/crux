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

import org.cruxframework.crux.core.client.bean.BeanCopier;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.datasource.DataSourceRecord;
import org.cruxframework.crux.core.client.datasource.PagedDataSource;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;

public class DataRow extends Row
{
	private DataSourceRecord<?> dataSourceRecord;
	
	private boolean editMode = false;
	
	private boolean isNew = false;
	
	protected DataRow(int index, Element elem, AbstractGrid<?> grid, boolean hasSelectionCell, boolean hasRowDetails, boolean showRowDetailsIcon)
	{
		super(index, elem, grid, hasSelectionCell, hasRowDetails, showRowDetailsIcon);
	}
	
	protected DataRow(int index, Element elem, AbstractGrid<?> grid, boolean hasSelectionCell)
	{
		super(index, elem, grid, hasSelectionCell, false, false);
	}

	public DataSourceRecord<?> getDataSourceRecord()
	{
		return dataSourceRecord;
	}
	
	public DataRow(DataRow row)
	{
		super(row.getIndex(),row.getElem(),row.getGrid(),row.isHasSelectionCell());
		//this.setDataSourceRecord(row.getDataSourceRecord().getRecordObject());
	}
	

	public boolean isNew()
	{
		return isNew;
	}

	public void setNew(boolean isNew)
	{
		this.isNew = isNew;
	}

	/**
	 * @param dataSourceRowId the dataSourceRowId to set
	 */
	void setDataSourceRecord(DataSourceRecord<?> dataSourceRowId)
	{
		this.dataSourceRecord = dataSourceRowId;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Object getValue(String column)
	{
		PagedDataSource dataSource = ((Grid) getGrid()).getDataSource();
		return dataSource.getValue(column, dataSourceRecord);
	}
	
	/**
	 * @return
	 * @deprecated Use getBound() instead
	 */
	@Deprecated
	public Object getBindedObject()
	{
		return getBoundObject();
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Object getBoundObject()
	{
		Grid grid = (Grid) getGrid();
		DataSource dataSource = grid.getDataSource();
		return dataSource.getBoundObject(getDataSourceRecord());
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
	
	public boolean isEditMode() 
	{
		return editMode;
	}
	
	public void setEditMode(boolean editMode)
	{
		this.editMode = editMode;
	}
}
