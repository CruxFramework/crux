/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.dataprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord.DataProviderRecordState;

/**
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AsyncStreamingDataProvider<T> implements StreamingDataProvider<T>
{
	protected StreamingDataProviderOperations<T> editableOperations = new StreamingDataProviderOperations<T>(this);
	protected List<DataProviderRecord<T>> data = new ArrayList<DataProviderRecord<T>>();
	protected int currentRecord = -1;
	protected int pageSize = 10;
	protected int currentPage = 0;
	protected AsyncDataProviderCallback asynchronousCallback = null;
	private PagedDataProviderCallback pagedCallback;

	@Override
	public DataProviderRecord<T> insertRecord(int index)
	{
		return editableOperations.insertRecord(index);
	}

	@Override
	public DataProviderRecord<T> removeRecord(int index)
	{
		return editableOperations.removeRecord(index);
	}

	@Override
	public void updateState(DataProviderRecord<T> record, DataProviderRecordState previousState)
	{
		editableOperations.updateState(record, previousState);
	}

	@Override
	public DataProviderRecord<T>[] getNewRecords()
	{
		return editableOperations.getNewRecords();
	}

	@Override
	public DataProviderRecord<T>[] getRemovedRecords()
	{
		return editableOperations.getRemovedRecords();
	}

	@Override
	public DataProviderRecord<T>[] getUpdatedRecords()
	{
		return editableOperations.getUpdatedRecords();
	}

	@Override
	public DataProviderRecord<T>[] getSelectedRecords()
	{
		return editableOperations.getSelectedRecords();
	}	
	
	@Override
	public void clearChanges()
	{
		this.editableOperations.reset();
	}

	@Override
	public void setCallback(AsyncDataProviderCallback callback)
	{
		this.asynchronousCallback = callback;		
	}

	@Override
	public void setCallback(PagedDataProviderCallback callback)
	{
		this.pagedCallback = callback;
	}

	@Override
	public int getIndex(T boundObject)
	{
		return editableOperations.getRecordIndex(boundObject);
	}
	
	@Override
	public void selectRecord(int index, boolean selected)
	{
		editableOperations.selectRecord(index, selected);
	}

	@Override
	public void cancelFetching()
	{
		currentPage--;
		updateCurrentRecord();
		this.asynchronousCallback.onCancelFetching();
	}

	@Override
	public DataProviderRecord<T> getRecord()
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

	@Override
	public boolean hasNextRecord()
	{
		ensureCurrentPageLoaded();
		return isRecordOnPage(currentRecord+1);
	}

	@Override
	public void nextRecord()
	{
		if (hasNextRecord())
		{
			currentRecord++;
		}
	}
	
	@Override
	public void firstRecord()
	{
		if (currentPage != 1)
		{
			checkChanges();
		}
		currentRecord = getPageStartRecord();
		ensureCurrentPageLoaded();
	}
	
	@Override
	public boolean hasPreviousRecord()
	{
		ensureCurrentPageLoaded();
		return isRecordOnPage(currentRecord-1);
	}

	@Override
	public void previousRecord()
	{
		if (hasPreviousRecord())
		{
			currentRecord--;
		}
	}

	@Override
	public void reset()
	{
		this.data.clear();
		currentRecord = -1;
		currentPage = 0;
		editableOperations.reset();
		
	}

	@Override
	public int getCurrentPage()
	{
		return currentPage;
	}

	@Override
	public int getCurrentPageSize()
	{
		int pageEndRecord = getPageEndRecord();
		return pageEndRecord - getPageStartRecord() + 1;
	}

	@Override
	public int getPageSize()
	{
		return pageSize;
	}

	@Override
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

	@Override
	public boolean hasPreviousPage()
	{
		return (currentPage > 1);
	}

	@Override
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

	@Override
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

	@Override
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
	
	@SuppressWarnings("unchecked")
    @Override
	public void updateData(T[] data)
	{
		if (data == null)
		{
			update(new DataProviderRecord[0]);
		} 
		else 
		{
			DataProviderRecord<T>[] ret = new DataProviderRecord[data.length];
			for (int i=0; i<data.length; i++)
			{
				ret[i] = new DataProviderRecord<T>(this);
				ret[i].setRecordObject(data[i]);
			}
			update(ret);
		}
	}	
	
	@SuppressWarnings("unchecked")
    @Override
	public void updateData(List<T> data)
	{
		if (data == null)
		{
			update(new DataProviderRecord[0]);
		} 
		else 
		{
			DataProviderRecord<T>[] ret = new DataProviderRecord[data.size()];
			for (int i=0; i<data.size(); i++)
			{
				ret[i] = new DataProviderRecord<T>(this);
				ret[i].setRecordObject(data.get(i));
			}
			update(ret);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void sort(Comparator<T> comparator)
	{
		if (currentRecord > -1)
		{
			DataProviderRecord<T>[] pageData = new DataProviderRecord[pageSize];
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<pageSize; i++)
			{
				pageData[i] = data.get(i+startPageRecord);
			}
			sortArray(pageData, comparator);
			updatePageRecords(startPageRecord, endPageRecord, pageData);
		}
	}

	@Override
	public T getBoundObject()
	{
	    DataProviderRecord<T> record = getRecord();
		return (record != null?record.getRecordObject():null);
	}
	
	protected void ensureCurrentPageLoaded()
	{
		boolean loaded = isCurrentPageLoaded();
		if (!loaded)
		{
			throw new DataProviderExcpetion("Error processing requested operation. DataProvider is not loaded yet.");
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

	protected void sortArray(DataProviderRecord<T>[] array, final Comparator<T> comparator)
	{
		Arrays.sort(array, new Comparator<DataProviderRecord<T>>(){
			public int compare(DataProviderRecord<T> o1, DataProviderRecord<T> o2)
			{
				return comparator.compare(o1.getRecordObject(), o2.getRecordObject());
			}
		});
		firstRecord();
	}
	
	protected void updateCurrentRecord()
	{
		currentRecord = getPageStartRecord(); 
	}
	
	protected void fetchCurrentPage()
	{
		if (!isCurrentPageLoaded())
		{
			fetch(getPageStartRecord(), getPageEndRecord());
		}
		else if (pagedCallback != null)
		{
			pagedCallback.onPageFetched(getPageStartRecord(), getPageEndRecord());
		}
	}
	
	
	protected void update(DataProviderRecord<T>[] records)
	{
		int startRecord = getPageStartRecord();
		int endRecord = (currentPage * pageSize) - 1;
		int updateRecordsCount = updateRecords(startRecord, endRecord, records);
		if (this.pagedCallback != null)
		{
			if (updateRecordsCount > 0)
			{
				pagedCallback.onPageFetched(startRecord, startRecord+updateRecordsCount-1);
			}
			else
			{
				pagedCallback.onPageFetched(-1, -1);
			}
		}
	}
	
	protected int updateRecords(int startRecord, int endRecord, DataProviderRecord<T>[] records)
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

	protected int updatePageRecords(int startRecord, int endRecord, DataProviderRecord<T>[] records)
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

	protected void checkChanges()
	{
		if (editableOperations.isDirty())
		{
			throw new DataProviderExcpetion("DataProvider has changes on page. You must save or discard them before perform this operation.");
		}
	}
	
	protected AsyncDataProviderEvent<T> createAsynchronousDataProviderEvent(int startRecord, int endRecord)
	{
		return new AsyncDataProviderEvent<T>(this, startRecord, endRecord);
	}
}