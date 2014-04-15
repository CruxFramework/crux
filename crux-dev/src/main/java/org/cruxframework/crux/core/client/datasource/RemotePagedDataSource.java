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

import org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState;

import com.google.gwt.user.client.ui.HasValue;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class RemotePagedDataSource<T> extends AbstractPagedDataSource<T> 
implements MeasurableRemoteDataSource<T>
{
	protected DataSourceOperations<T> editableOperations = new DataSourceOperations<T>(this);

	protected RemoteDataSourceCallback fetchCallback = null;
	
	private RemoteDataSourceConfiguration config;

	@SuppressWarnings("unchecked")
    public RemotePagedDataSource()
	{
		this.data = new DataSourceRecord[0];
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#cancelFetching()
	 */
	public void cancelFetching()
	{
		currentPage--;
		updateCurrentRecord();
		this.fetchCallback.cancelFetching();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#clearChanges()
	 */
	public void clearChanges()
	{
		this.editableOperations.reset();
	}

	@Override
	public void firstRecord()
	{
		if (currentPage != 1)
		{
			checkChanges();
		}
		super.firstRecord();
		ensurePageLoaded(currentRecord);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getNewRecords()
	 */
	public DataSourceRecord<T>[] getNewRecords()
	{
		return editableOperations.getNewRecords();
	}

	@Override
	public int getRecordCount()
	{
		if (!loaded)
		{
			throw new DataSourceExcpetion(messages.dataSourceNotLoaded());
		}
		return this.config.getRecordCount();
	}	
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getRemovedRecords()
	 */
	public DataSourceRecord<T>[] getRemovedRecords()
	{
		return editableOperations.getRemovedRecords();
	}	
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getSelectedRecords()
	 */
	public DataSourceRecord<T>[] getSelectedRecords()
	{
		return editableOperations.getSelectedRecords();
	}	
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getUpdatedRecords()
	 */
	public DataSourceRecord<T>[] getUpdatedRecords()
	{
		return editableOperations.getUpdatedRecords();
	}
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#insertRecord(int)
	 */
	public DataSourceRecord<T> insertRecord(int index)
	{
		return editableOperations.insertRecord(index);
	}
	
	@Override
	public void lastRecord()
	{
		if (currentPage != getPageCount())
		{
			checkChanges();
		}
		super.lastRecord();
		ensurePageLoaded(currentRecord);
	}

	@Override
	public boolean nextPage()
	{
		checkChanges();
		if (super.nextPage())
		{
			fetchCurrentPage();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean previousPage()
	{
		checkChanges();
		if (super.previousPage())
		{
			fetchCurrentPage();
			return true;
		}
		return false;
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#removeRecord(int)
	 */
	public DataSourceRecord<T> removeRecord(int index)
	{
		return editableOperations.removeRecord(index);
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public void reset()
	{
		if(data != null)
		{
			this.data = new DataSourceRecord[0];
		}
		currentRecord = -1;
		currentPage = 0;
		loaded = false;
		this.config = null;
		editableOperations.reset();
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#setCallback(org.cruxframework.crux.core.client.datasource.RemoteDataSourceCallback)
	 */
	public void setCallback(RemoteDataSourceCallback callback)
	{
		this.fetchCallback = callback;
	}
	
	@Override
	public boolean setCurrentPage(int pageNumber)
	{
		if (this.currentPage != pageNumber)
		{
			checkChanges();
		}
		if (super.setCurrentPage(pageNumber))
		{
			fetchCurrentPage();
			return true;
		}
		return false;
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#setLoadData(org.cruxframework.crux.core.client.datasource.RemoteDataSourceConfiguration)
	 */
	@SuppressWarnings("unchecked")
    public void setLoadData(RemoteDataSourceConfiguration config)
	{
		this.config = config;
		this.loaded = (this.config != null);
		this.data = new DataSourceRecord[getRecordCount()];
		this.setCurrentPage(1);
	}
	
	@Override
	public void setPageSize(int pageSize)
	{
		super.setPageSize(pageSize);
		if (this.loaded)
		{
			load();
		}
	}

	@Override
	public void sort(String columnName, boolean ascending)
	{
		sort(columnName, ascending, false);
	}
	
	@SuppressWarnings("unchecked")
	public void sort(String columnName, boolean ascending, boolean caseSensitive)
	{
		ensurePageLoaded(currentRecord);
		if (currentRecord > -1)
		{
			DataSourceRecord<T>[] pageData = new DataSourceRecord[pageSize];
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<pageSize; i++)
			{
				pageData[i] = data[i+startPageRecord];
			}
			sortArray(pageData, columnName, ascending, caseSensitive);
			updateRecords(startPageRecord, endPageRecord, pageData);
		}
	}	
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#update(R[])
	 */
	public void update(DataSourceRecord<T>[] records)
	{
		int startRecord = getPageStartRecord();
		int endRecord = getPageEndRecord();
		int updateRecordsCount = updateRecords(startRecord, endRecord, records);
		if (this.fetchCallback != null)
		{
			if (updateRecordsCount > 0)
			{
				fetchCallback.execute(startRecord, startRecord+updateRecordsCount-1);
			}
			else
			{
				fetchCallback.execute(-1, -1);
			}
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
		return editableOperations.getRecordIndex(boundObject);
	}
	
	public void selectRecord(int index, boolean selected)
	{
		editableOperations.selectRecord(index, selected);
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#updateState(org.cruxframework.crux.core.client.datasource.DataSourceRecord, org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState)
	 */
	public void updateState(DataSourceRecord<T> record, DataSourceRecordState previousState)
	{
		editableOperations.updateState(record, previousState);
	}
	
	protected void ensurePageLoaded(int recordNumber)
	{
		boolean loaded = isPageLoaded(getPageForRecord(recordNumber));
		if (!loaded)
		{
			throw new DataSourceExcpetion(messages.dataSourceNotLoaded());
		}
	}
	
	/**
	 * 
	 */
	protected void fetchCurrentPage()
	{
		int pageEndRecord = (currentPage * pageSize) - 1;
		if (!isPageLoaded(currentPage))
		{
			fetch(getPageStartRecord(), pageEndRecord);
		}
		else
		{
			fetchCallback.execute(getPageStartRecord(), pageEndRecord);
		}
	}
	
	@Override
	protected int getPageEndRecord()
	{
		int endPageRecord = super.getPageEndRecord();
		return endPageRecord + editableOperations.getNewRecordsCount() - editableOperations.getRemovedRecordsCount();
	}
	
	protected int getPageForRecord(int recordNumber)
	{
		int pageSize = getPageSize();
		return (recordNumber / pageSize) + (recordNumber%pageSize==0?0:1);
	}
	
	protected boolean isPageLoaded(int pageNumber)
	{
		int startPageRecord = getPageStartRecord();
		return (data.length > 0 && data[startPageRecord]!= null);
	}
	
	/**
	 * 
	 * @param startRecord
	 * @param endRecord
	 * @param records
	 * @return
	 */
	protected int updateRecords(int startRecord, int endRecord, DataSourceRecord<T>[] records)
	{
		if (records != null && endRecord < getRecordCount())
		{
			for (int i = startRecord, j = 0; i <= endRecord && j < records.length; i++, j++)
			{
				this.data[i] = records[j];
			}
			
			return records.length;
		}
		return 0;
	}

	private void checkChanges()
	{
		if (editableOperations.isDirty())
		{
			throw new DataSourceExcpetion(messages.remoteDataSourcePageDirty());
		}
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
}
