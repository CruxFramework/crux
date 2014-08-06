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

import java.util.Comparator;

import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord.DataProviderRecordState;


/**
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AsyncPagedDataProvider<T> extends AbstractPagedDataProvider<T> implements MeasurableAsyncDataProvider<T>
{
	protected DataProviderOperations<T> operations = new DataProviderOperations<T>(this);
	protected AsyncDataProviderCallback asynchronousCallback = null;
	protected PagedDataProviderCallback pagedCallback;
	protected int recordCount;
	
	@SuppressWarnings("unchecked")
    public AsyncPagedDataProvider()
	{
		this.data = new DataProviderRecord[0];
	}

	@Override
	public void cancelFetching()
	{
		//TODO deveria voltar para pagina anterior... que precisa ser guardada... nao necessariamente vai pedir em ordem
		currentPage--;
		updateCurrentRecord();
		this.asynchronousCallback.onCancelFetching();
	}

	@Override
	public void clearChanges()
	{
		this.operations.reset();
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

	@Override
	public DataProviderRecord<T>[] getNewRecords()
	{
		return operations.getNewRecords();
	}

	@Override
	public int getRecordCount()
	{
		ensureLoaded();
		return recordCount;
	}	
	
	@Override
	public DataProviderRecord<T>[] getRemovedRecords()
	{
		return operations.getRemovedRecords();
	}	
	
	@Override
	public DataProviderRecord<T>[] getSelectedRecords()
	{
		return operations.getSelectedRecords();
	}	
	
	@Override
	public DataProviderRecord<T>[] getUpdatedRecords()
	{
		return operations.getUpdatedRecords();
	}

	@Override
	public DataProviderRecord<T> insertRecord(int index)
	{
		return operations.insertRecord(index);
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
	
	@Override
	public DataProviderRecord<T> removeRecord(int index)
	{
		return operations.removeRecord(index);
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public void reset()
	{
		if(data != null)
		{
			this.data = new DataProviderRecord[0];
		}
		currentRecord = -1;
		currentPage = 0;
		loaded = false;
		recordCount = -1;
		operations.reset();
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

	@Override
	@SuppressWarnings("unchecked")
	public void setRecordCount(int recordCount)
	{
		this.recordCount = recordCount;
		this.loaded = (this.recordCount >= 0);
		if (loaded)
		{
			this.data = new DataProviderRecord[getRecordCount()];
			this.setCurrentPage(1);
		}
	}
	
	@Override
	public void setPageSize(int pageSize)
	{
		super.setPageSize(pageSize);
		if (this.loaded)
		{
			initialize();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void sort(Comparator<T> comparator)
	{
		ensurePageLoaded(currentRecord);
		if (currentRecord > -1)
		{
			DataProviderRecord<T>[] pageData = new DataProviderRecord[pageSize];
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<pageSize; i++)
			{
				pageData[i] = data[i+startPageRecord];
			}
			sortArray(pageData, comparator);
			updateRecords(startPageRecord, endPageRecord, pageData);
		}
	}	

	@Override
	public int getIndex(T boundObject)
	{
		return operations.getRecordIndex(boundObject);
	}
	
	@Override
	public void selectRecord(int index, boolean selected)
	{
		operations.selectRecord(index, selected);
	}
	
	@Override
	public void updateState(DataProviderRecord<T> record, DataProviderRecordState previousState)
	{
		operations.updateState(record, previousState);
	}
		
	@Override
	protected void update(DataProviderRecord<T>[] records)
	{
		int startRecord = getPageStartRecord();
		int endRecord = getPageEndRecord();
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
	
	protected void ensurePageLoaded(int recordNumber)
	{
		boolean loaded = isPageLoaded(getPageForRecord(recordNumber));
		if (!loaded)
		{
			throw new DataProviderExcpetion("Error processing requested operation. DataProvider is not loaded yet.");
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
		else if (pagedCallback != null)
		{
			pagedCallback.onPageFetched(getPageStartRecord(), pageEndRecord);
		}
	}
	
	@Override
	protected int getPageEndRecord()
	{
		int endPageRecord = super.getPageEndRecord();
		return endPageRecord + operations.getNewRecordsCount() - operations.getRemovedRecordsCount();
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
	protected int updateRecords(int startRecord, int endRecord, DataProviderRecord<T>[] records)
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

	protected void checkChanges()
	{
		if (operations.isDirty())
		{
			throw new DataProviderExcpetion("DataProvider has changes on page. You must save or discard them before perform this operation.");
		}
	}
	
	protected AsyncDataProviderEvent<T> createAsynchronousDataProviderEvent(int startRecord, int endRecord)
	{
		return new AsyncDataProviderEvent<T>(this, startRecord, endRecord);
	}
	
	protected MeasurableAsyncDataProviderEvent<T> createMeasurableDataProviderEvent()
	{
		return new MeasurableAsyncDataProviderEvent<T>(this);
	}
}
