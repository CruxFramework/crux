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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import br.com.sysmap.crux.core.client.ClientMessages;
import br.com.sysmap.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class RemoteStreamingDataSource<T> implements StreamingDataSource<T>
{
	protected StreamingDataSourceOperations<T> editableOperations = new StreamingDataSourceOperations<T>(this);
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#insertRecord(int)
	 */
	public DataSourceRecord<T> insertRecord(int index)
	{
		return editableOperations.insertRecord(index);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#removeRecord(int)
	 */
	public DataSourceRecord<T> removeRecord(int index)
	{
		return editableOperations.removeRecord(index);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#updateState(br.com.sysmap.crux.core.client.datasource.DataSourceRecord, br.com.sysmap.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState)
	 */
	public void updateState(DataSourceRecord<T> record, DataSourceRecordState previousState)
	{
		editableOperations.updateState(record, previousState);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getNewRecords()
	 */
	public DataSourceRecord<T>[] getNewRecords()
	{
		return editableOperations.getNewRecords();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getRemovedRecords()
	 */
	public DataSourceRecord<T>[] getRemovedRecords()
	{
		return editableOperations.getRemovedRecords();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getUpdatedRecords()
	 */
	public DataSourceRecord<T>[] getUpdatedRecords()
	{
		return editableOperations.getUpdatedRecords();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getSelectedRecords()
	 */
	public DataSourceRecord<T>[] getSelectedRecords()
	{
		return editableOperations.getSelectedRecords();
	}	
	
	private void checkChanges()
	{
		if (editableOperations.isDirty())
		{
			throw new DataSoureExcpetion(messages.remoteDataSourcePageDirty());
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#clearChanges()
	 */
	public void clearChanges()
	{
		this.editableOperations.reset();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected List<DataSourceRecord<T>> data = new ArrayList<DataSourceRecord<T>>();
	protected int currentRecord = -1;
	protected int pageSize = 10;
	protected int currentPage = 0;
	protected Metadata metadata;
	protected RemoteDataSourceCallback fetchCallback = null;
	protected ClientMessages messages = GWT.create(ClientMessages.class);
	
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#setCallback(br.com.sysmap.crux.core.client.datasource.RemoteDataSourceCallback)
	 */
	public void setCallback(RemoteDataSourceCallback callback)
	{
		this.fetchCallback = callback;		
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#update(R[])
	 */
	public void update(DataSourceRecord<T>[] records)
	{
		int startRecord = getPageStartRecord();
		int endRecord = (currentPage * pageSize) - 1;
		int updateRecordsCount = updateRecords(startRecord, endRecord, records);
		if (updateRecordsCount > 0)
		{
			if (this.fetchCallback != null)
			{
				fetchCallback.execute(startRecord, startRecord+updateRecordsCount-1);
			}
		}
		else
		{
			if (this.fetchCallback != null)
			{
				fetchCallback.execute(-1, -1);
			}
		}
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
		int ret = 0;
		if (records != null)
		{
			int count = Math.min(endRecord - startRecord + 1, records.length);
			for (ret = 0; ret < count ; ret++)
			{
				this.data.add(records[ret]);
			}
		}
		if (ret < (endRecord - startRecord))
		{
			this.data.add(null);
		}
		return ret;
	}

	/**
	 * 
	 * @param startRecord
	 * @param endRecord
	 * @param records
	 * @return
	 */
	protected int updatePageRecords(int startRecord, int endRecord, DataSourceRecord<T>[] records)
	{
		int ret = 0;
		if (records != null)
		{
			int count = Math.min(endRecord - startRecord + 1, records.length);
			for (ret = 0; ret < count ; ret++)
			{
				this.data.set(ret+startRecord, records[ret]);
			}
		}
		return ret;
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#updateData(E[])
	 */
	public void updateData(T[] data)
	{
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#cancelFetching()
	 */
	public void cancelFetching()
	{
		currentPage--;
		updateCurrentRecord();
		this.fetchCallback.cancelFetching();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getMetadata()
	 */
	public Metadata getMetadata()
	{
		return metadata;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getRecord()
	 */
	public DataSourceRecord<T> getRecord()
	{
		ensureCurrentPageLoaded();		
		if (currentRecord > -1)
		{
			return data.get(currentRecord);
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getValue(java.lang.String)
	 */
	public Object getValue(String columnName)
	{
		ensureCurrentPageLoaded();		
		if (currentRecord > -1)
		{
			DataSourceRecord<T> dataSourceRow = data.get(currentRecord);
			return getValue(columnName, dataSourceRow);
		}
		return null;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#hasNextRecord()
	 */
	public boolean hasNextRecord()
	{
		ensureCurrentPageLoaded();
		return isRecordOnPage(currentRecord+1);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#nextRecord()
	 */
	public void nextRecord()
	{
		if (hasNextRecord())
		{
			currentRecord++;
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#firstRecord()
	 */
	public void firstRecord()
	{
		if (currentPage != 1)
		{
			checkChanges();
		}
		currentRecord = getPageStartRecord();
		ensureCurrentPageLoaded();
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#hasPreviousRecord()
	 */
	public boolean hasPreviousRecord()
	{
		ensureCurrentPageLoaded();
		return isRecordOnPage(currentRecord-1);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#previousRecord()
	 */
	public void previousRecord()
	{
		if (hasPreviousRecord())
		{
			currentRecord--;
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#reset()
	 */
	public void reset()
	{
		this.data.clear();
		currentRecord = -1;
		currentPage = 0;
		editableOperations.reset();
		
	}
	
	protected void ensureCurrentPageLoaded()
	{
		boolean loaded = isCurrentPageLoaded();
		if (!loaded)
		{
			throw new DataSoureExcpetion(messages.dataSourceNotLoaded());
		}
	}

	protected boolean isCurrentPageLoaded()
	{
		int pageStartRecord = getPageStartRecord();
		return (data.size() > pageStartRecord);
	}
	
	protected boolean isRecordOnPage(int record)
	{
		ensureCurrentPageLoaded();
		if (data == null)
		{
			return false;
		}
		int startPageRecord = getPageStartRecord();
		int endPageRescord = getPageEndRecord();
		if (endPageRescord >= data.size())
		{
			endPageRescord = data.size()-1;
		}
		return (record >= startPageRecord) && (record <= endPageRescord) && (data.get(record) != null);
	}
	
	protected int getPageEndRecord()
	{
		int pageEndRecord = (currentPage * pageSize) - 1;
		int pageStartRecord = getPageStartRecord();
		
		if (pageEndRecord >= this.data.size())
		{
			if (this.data.size() > 0 && this.data.size() > pageStartRecord && this.data.get(this.data.size()-1) == null)
			{
				pageEndRecord = this.data.size()-2;
			}
		}
		
		return pageEndRecord + editableOperations.getNewRecordsCount() - editableOperations.getRemovedRecordsCount();
	}

	protected int getPageStartRecord()
	{
		return (currentPage - 1) * pageSize;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#getCurrentPage()
	 */
	public int getCurrentPage()
	{
		return currentPage;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#getCurrentPageSize()
	 */
	public int getCurrentPageSize()
	{
		int pageEndRecord = getPageEndRecord();
		return pageEndRecord - getPageStartRecord() + 1;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#getPageSize()
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#hasNextPage()
	 */
	public boolean hasNextPage()
	{
		int pageEndRecord = getPageEndRecord();
		
		if (pageEndRecord < 0)
		{
			return this.data.size() == 0;
		}
		
		if (pageEndRecord < this.data.size())
		{
			if (pageEndRecord == this.data.size() - 2)
			{
				return this.data.get(pageEndRecord) == null;
			}
			
			return true;
		}
		return false;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#hasPreviousPage()
	 */
	public boolean hasPreviousPage()
	{
		return (currentPage > 1);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#nextPage()
	 */
	public boolean nextPage()
	{
		checkChanges();
		if (hasNextPage())
		{
			currentPage++;
			updateCurrentRecord();
			fetchCurrentPage();
			return true;
		}
		
		return false;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#previousPage()
	 */
	public boolean previousPage()
	{
		checkChanges();
		if (hasPreviousPage())
		{
			currentPage--;
			updateCurrentRecord();
			fetchCurrentPage();
			return true;
		}
		
		return false;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#setPageSize(int)
	 */
	public void setPageSize(int pageSize)
	{
		if (pageSize < 1)
		{
			pageSize = 1;
		}
		
		boolean loaded = data.size() > 0;
		
		this.pageSize = pageSize;
		
		if(loaded)
		{
			fetchCurrentPage();
			updateCurrentRecord();
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#sort(java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
    public void sort(String columnName, boolean ascending)
	{
		if (currentRecord > -1)
		{
			DataSourceRecord<T>[] pageData = new DataSourceRecord[pageSize];
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<pageSize; i++)
			{
				pageData[i] = data.get(i+startPageRecord);
			}
			sortArray(pageData,columnName, ascending);
			updatePageRecords(startPageRecord, endPageRecord, pageData);
		}
	}
	
	protected void sortArray(DataSourceRecord<T>[] array, final String columnName, final boolean ascending)
	{
		if (!metadata.getColumn(columnName).isSortable())
		{
			throw new DataSoureExcpetion(messages.dataSourceErrorColumnNotComparable(columnName));
		}
		Arrays.sort(array, new Comparator<DataSourceRecord<T>>(){
			public int compare(DataSourceRecord<T> o1, DataSourceRecord<T> o2)
			{
				// Null elements must always be considered greater, because datasources uses the first null record to identify the page end.
				if (o1 == null)
				{
					return o2 == null ? 0 : 1;
				}
				
				if (o2 == null)
				{
					return -1;
				}
				
				Object value1 = getValue(columnName,o1);
				Object value2 = getValue(columnName,o2);

				if (ascending)
				{
					if (value1==null) return (value2 == null ? 0 : -1);
					if (value2==null) return 1;
				}
				else
				{
					if (value1==null) return (value2 == null ? 0 : 1);
					if (value2==null) return -1;
				}

				return compareNonNullValuesByType(value1,value2,ascending);
			}

			@SuppressWarnings("unchecked")
			private int compareNonNullValuesByType(Object value1, Object value2,boolean ascending)
			{
				if (ascending)
				{
					return ((Comparable)value1).compareTo(value2);
				}
				else
				{
					return ((Comparable)value2).compareTo(value1);
				}
			}
		});
		firstRecord();
	}
	
	protected void updateCurrentRecord()
	{
		currentRecord = getPageStartRecord(); 
	}
	
	/**
	 * 
	 */
	protected void fetchCurrentPage()
	{
		if (!isCurrentPageLoaded())
		{
			fetch(getPageStartRecord(), getPageEndRecord());
		}
		else
		{
			fetchCallback.execute(getPageStartRecord(), getPageEndRecord());
		}
	}
	
	public Object getValue(String columnName, DataSourceRecord<T> dataSourceRecord)
	{
		return null;
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getBindedObject()
	 */
	public T getBindedObject()
	{
		return getBindedObject(getRecord());
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getBindedObject(br.com.sysmap.crux.core.client.datasource.DataSourceRecord)
	 */
	public T getBindedObject(DataSourceRecord<T> record)
	{
		return null;
	}	
}
