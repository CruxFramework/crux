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
package org.cruxframework.crux.core.client.datasource;

import java.util.List;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState;

import com.google.gwt.user.client.ui.HasValue;



/**
 * @author Thiago da Rosa de Bustamante
 *
 * @deprecated Use DataProvider instead.
 */
@Deprecated
@Legacy
public abstract class LocalScrollableDataSource<T> extends AbstractScrollableDataSource<T>
											implements LocalDataSource<T>
{
	protected LocalDataSourceCallback loadCallback = null;
	protected DataSourceOperations<T> operations = new DataSourceOperations<T>(this);

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#clearChanges()
	 */
	public void clearChanges()
	{
		this.operations.reset();
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getNewRecords()
	 */
	public DataSourceRecord<T>[] getNewRecords()
	{
		return operations.getNewRecords();
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getRemovedRecords()
	 */
	public DataSourceRecord<T>[] getRemovedRecords()
	{
		return operations.getRemovedRecords();
	}	
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getSelectedRecords()
	 */
	public DataSourceRecord<T>[] getSelectedRecords()
	{
		return operations.getSelectedRecords();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getUpdatedRecords()
	 */
	public DataSourceRecord<T>[] getUpdatedRecords()
	{
		return operations.getUpdatedRecords();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#insertRecord(int)
	 */
	public DataSourceRecord<T> insertRecord(int index)
	{
		return operations.insertRecord(index);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#removeRecord(int)
	 */
	public DataSourceRecord<T> removeRecord(int index)
	{
		return operations.removeRecord(index);
	}

	@Override
	public void reset()
	{
		super.reset();
		operations.reset();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.LocalDataSource#setCallback(org.cruxframework.crux.core.client.datasource.LocalDataSourceCallback)
	 */
	public void setCallback(LocalDataSourceCallback callback)
	{
		this.loadCallback = callback;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.LocalDataSource#update(R[])
	 */
	public void update(DataSourceRecord<T>[] records)
	{
		loaded = true;
		this.data = records;
		if (this.loadCallback != null)
		{
			this.loadCallback.execute();
		}
	}

	public void updateData(T[] data)
	{
	}	
	
	public void updateData(List<T> data)
	{
	}
	
	public void copyValueToWidget(HasValue<?> valueContainer, String key, DataSourceRecord<?> dataSourceRecord)
	{
	}
	
	public void setValue(Object value, String columnKey, DataSourceRecord<?> dataSourceRecord)
	{
	}
	
	public int getRecordIndex(T boundObject)
	{
		return operations.getRecordIndex(boundObject);
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#updateState(org.cruxframework.crux.core.client.datasource.DataSourceRecord, org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState)
	 */
	public void updateState(DataSourceRecord<T> record, DataSourceRecordState previousState)
	{
		operations.updateState(record, previousState);
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getBoundObject()
	 */
	public T getBoundObject()
	{
		return getBoundObject(getRecord());
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getBoundObject(org.cruxframework.crux.core.client.datasource.DataSourceRecord)
	 */
	public T getBoundObject(DataSourceRecord<T> record)
	{
		return super.getBoundObject(record);
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#cloneDTO(org.cruxframework.crux.core.client.datasource.DataSourceRecord)
	 */
	public T cloneDTO(DataSourceRecord<?> record)
	{
		return null;
	}
}