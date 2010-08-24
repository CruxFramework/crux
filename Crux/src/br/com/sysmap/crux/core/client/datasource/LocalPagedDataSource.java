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
package br.com.sysmap.crux.core.client.datasource;

import br.com.sysmap.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState;



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class LocalPagedDataSource<T> extends AbstractPagedDataSource<T> 
				implements LocalDataSource<T> 
{
	protected LocalDataSourceCallback loadCallback = null;
	protected DataSourceOperations<T> operations = new DataSourceOperations<T>(this);

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#clearChanges()
	 */
	public void clearChanges()
	{
		this.operations.reset();
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getNewRecords()
	 */
	public DataSourceRecord<T>[] getNewRecords()
	{
		return operations.getNewRecords();
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getRemovedRecords()
	 */
	public DataSourceRecord<T>[] getRemovedRecords()
	{
		return operations.getRemovedRecords();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getSelectedRecords()
	 */
	public DataSourceRecord<T>[] getSelectedRecords()
	{
		return operations.getSelectedRecords();
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getUpdatedRecords()
	 */
	public DataSourceRecord<T>[] getUpdatedRecords()
	{
		return operations.getUpdatedRecords();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#insertRecord(int)
	 */
	public DataSourceRecord<T> insertRecord(int index)
	{
		return operations.insertRecord(index);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#removeRecord(int)
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
	 * @see br.com.sysmap.crux.core.client.datasource.LocalDataSource#setCallback(br.com.sysmap.crux.core.client.datasource.LocalDataSourceCallback)
	 */
	public void setCallback(LocalDataSourceCallback callback)
	{
		this.loadCallback = callback;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.LocalDataSource#update(R[])
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
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#updateState(br.com.sysmap.crux.core.client.datasource.DataSourceRecord, br.com.sysmap.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState)
	 */
	public void updateState(DataSourceRecord<T> record, DataSourceRecordState previousState)
	{
		operations.updateState(record, previousState);
	}
}