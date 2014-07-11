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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.cruxframework.crux.core.client.ClientMessages;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author Thiago da Rosa de Bustamante
 *
 * @deprecated Use DataProvider instead.
 */
@Deprecated
@Legacy
public abstract class RemoteStreamingDataSource<T> implements StreamingDataSource<T>
{
	protected StreamingDataSourceOperations<T> editableOperations = new StreamingDataSourceOperations<T>(this);
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#insertRecord(int)
	 */
	public DataSourceRecord<T> insertRecord(int index)
	{
		return editableOperations.insertRecord(index);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#removeRecord(int)
	 */
	public DataSourceRecord<T> removeRecord(int index)
	{
		return editableOperations.removeRecord(index);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#updateState(org.cruxframework.crux.core.client.datasource.DataSourceRecord, org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState)
	 */
	public void updateState(DataSourceRecord<T> record, DataSourceRecordState previousState)
	{
		editableOperations.updateState(record, previousState);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getNewRecords()
	 */
	public DataSourceRecord<T>[] getNewRecords()
	{
		return editableOperations.getNewRecords();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getRemovedRecords()
	 */
	public DataSourceRecord<T>[] getRemovedRecords()
	{
		return editableOperations.getRemovedRecords();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getUpdatedRecords()
	 */
	public DataSourceRecord<T>[] getUpdatedRecords()
	{
		return editableOperations.getUpdatedRecords();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getSelectedRecords()
	 */
	public DataSourceRecord<T>[] getSelectedRecords()
	{
		return editableOperations.getSelectedRecords();
	}	
	
	private void checkChanges()
	{
		if (editableOperations.isDirty())
		{
			throw new DataSourceExcpetion(messages.remoteDataSourcePageDirty());
		}
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#clearChanges()
	 */
	public void clearChanges()
	{
		this.editableOperations.reset();
	}
	
	protected List<DataSourceRecord<T>> data = new ArrayList<DataSourceRecord<T>>();
	protected int currentRecord = -1;
	protected int pageSize = 10;
	protected int currentPage = 0;
	protected ColumnDefinitions<T> definitions = new ColumnDefinitions<T>();
	protected RemoteDataSourceCallback fetchCallback = null;
	protected ClientMessages messages = GWT.create(ClientMessages.class);
	
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#setCallback(org.cruxframework.crux.core.client.datasource.RemoteDataSourceCallback)
	 */
	public void setCallback(RemoteDataSourceCallback callback)
	{
		this.fetchCallback = callback;		
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#update(R[])
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
				int index = ret+startRecord;
				if(records.length > ret && this.data.size() > index)
				{
					this.data.set(index, records[ret]);
				}
			}
		}
		return ret;
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#updateData(E[])
	 */
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
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#cancelFetching()
	 */
	public void cancelFetching()
	{
		currentPage--;
		updateCurrentRecord();
		this.fetchCallback.cancelFetching();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getColumnDefinitions()
	 */
	public ColumnDefinitions<T> getColumnDefinitions()
	{
		return definitions;
	}

	/**
	 * @param columnDefinitions
	 */
	public void setColumnDefinitions(ColumnDefinitions<T> columnDefinitions)
	{
		this.definitions = columnDefinitions;
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getRecord()
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
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getValue(java.lang.String)
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
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#hasNextRecord()
	 */
	public boolean hasNextRecord()
	{
		ensureCurrentPageLoaded();
		return isRecordOnPage(currentRecord+1);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#nextRecord()
	 */
	public void nextRecord()
	{
		if (hasNextRecord())
		{
			currentRecord++;
		}
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#firstRecord()
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
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#hasPreviousRecord()
	 */
	public boolean hasPreviousRecord()
	{
		ensureCurrentPageLoaded();
		return isRecordOnPage(currentRecord-1);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#previousRecord()
	 */
	public void previousRecord()
	{
		if (hasPreviousRecord())
		{
			currentRecord--;
		}
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#reset()
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
			throw new DataSourceExcpetion(messages.dataSourceNotLoaded());
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
	 * @see org.cruxframework.crux.core.client.datasource.PagedDataSource#getCurrentPage()
	 */
	public int getCurrentPage()
	{
		return currentPage;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.PagedDataSource#getCurrentPageSize()
	 */
	public int getCurrentPageSize()
	{
		int pageEndRecord = getPageEndRecord() == this.data.size() ? getPageEndRecord() -1 : getPageEndRecord();
		return pageEndRecord - getPageStartRecord()+1;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.PagedDataSource#getPageSize()
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.PagedDataSource#hasNextPage()
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
	 * @see org.cruxframework.crux.core.client.datasource.PagedDataSource#hasPreviousPage()
	 */
	public boolean hasPreviousPage()
	{
		return (currentPage > 1);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.PagedDataSource#nextPage()
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
	 * @see org.cruxframework.crux.core.client.datasource.PagedDataSource#previousPage()
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
	 * @see org.cruxframework.crux.core.client.datasource.PagedDataSource#setPageSize(int)
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
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#sort(java.lang.String, boolean)
	 */
	@Override
	public void sort(String columnName, boolean ascending)
	{
		sort(columnName, ascending, false);
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#sort(java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
    public void sort(String columnName, boolean ascending, boolean caseSensitive)
	{
		if (currentRecord > -1)
		{
			DataSourceRecord<T>[] pageData = new DataSourceRecord[pageSize];
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<pageSize; i++)
			{
				if(this.data.size() > i && pageData.length > i)
				{
					pageData[i] = data.get(i+startPageRecord);
				}
			}
			sortArray(pageData,columnName, ascending, caseSensitive);
			updatePageRecords(startPageRecord, endPageRecord, pageData);
		}
	}
	
	protected void sortArray(DataSourceRecord<T>[] array, final String columnName, final boolean ascending, final boolean caseSensitive)
	{
		if (!definitions.getColumn(columnName).isSortable())
		{
			throw new DataSourceExcpetion(messages.dataSourceErrorColumnNotComparable(columnName));
		}
		
		//optimization: infer column type only once
		final boolean isStringColumn = getValue(columnName, array[0]) instanceof String;
		
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

				return compareNonNullValuesByType(value1,value2,ascending,caseSensitive, isStringColumn);
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			private int compareNonNullValuesByType(Object value1, Object value2, boolean ascending, boolean caseSensitive, boolean isStringColumns)
			{
				if(isStringColumns)
				{
					if (ascending)
					{
						return StringUtils.localeCompare((String)value1, (String)value2, caseSensitive);
					} else
					{
						return StringUtils.localeCompare((String)value2, (String)value1, caseSensitive);
					}
				}
				
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

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getValue(java.lang.String, org.cruxframework.crux.core.client.datasource.DataSourceRecord)
	 */
	@SuppressWarnings("unchecked")
	public Object getValue(String columnName, DataSourceRecord<?> dataSourceRecord)
	{
		ColumnDefinition<?, T> column = definitions.getColumn(columnName);
		if (column != null)
		{
			return column.getValue((T) dataSourceRecord.getRecordObject());
		}
		return null;
	}

	/**
	 * @return
	 * @deprecated Use getBoundObject instead
	 */
	@Deprecated
	@Legacy
	public T getBindedObject()
	{
		return getBindedObject(getRecord());
	}
	
	/**
	 * @param record
	 * @return
	 * @deprecated Use getBoundObject instead
	 */
	@Deprecated
	@Legacy
	public T getBindedObject(DataSourceRecord<T> record)
	{
		return getBoundObject(record);
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
		return null;
	}	
	
	public T cloneDTO(DataSourceRecord<?> record)
	{
		return null;
	}
}
