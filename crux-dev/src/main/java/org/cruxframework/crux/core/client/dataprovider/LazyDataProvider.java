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
import java.util.List;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;


/**
 * @author Thiago da Rosa de Bustamante
 */
public class LazyDataProvider<T> extends AbstractPagedDataProvider<T> implements MeasurableLazyProvider<T>
{
	protected int size;
	protected LazyDataLoader<T> dataLoader;

	public LazyDataProvider()
    {
    }
	
    public LazyDataProvider(DataProvider.EditionDataHandler<T> handler)
	{
    	super(handler);
		this.data = CollectionFactory.createArray();
	}

	public LazyDataProvider(DataProvider.EditionDataHandler<T> handler, LazyDataLoader<T> dataLoader)
    {
		this(handler);
		this.dataLoader = dataLoader;
    }
	
	@Override
    public void setDataLoader(LazyDataLoader<T> dataLoader)
    {
		this.dataLoader = dataLoader;
    }

	@Override
    public LazyDataLoader<T> getDataLoader()
    {
	    return dataLoader;
    }
	
    @Override
    public void stopLoading()
    {
    	previousPage--;
    	currentPage--;
		updateCurrentRecord();
		super.stopLoading();
	}

	@Override
	public void load()
	{
		if (!isLoaded() && dataLoader != null)
		{
			dataLoader.onMeasureData(new MeasureDataEvent<T>(this));
		}
	}
		
	@Override
	public void first()
	{
		if (currentPage != 1)
		{
			checkChanges();
		}
		super.first();
	}
	
	@Override
	public void firstOnPage()
	{
		super.firstOnPage();
		ensurePageLoaded(currentRecord);
	}

	@Override
	public int size()
	{
		ensureLoaded();
		return size;
	}	

	@Override
	public void last()
	{
		if (currentPage != getPageCount())
		{
			checkChanges();
		}
		super.last();
		ensurePageLoaded(currentRecord);
	}

	@Override
	public boolean nextPage()
	{
		checkChanges();
		if (super.nextPage())
		{
			fetchCurrentPage(true);
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
			fetchCurrentPage(true);
			return true;
		}
		return false;
	}
	
    @Override
	public void reset()
	{
		if(data != null)
		{
			this.data = CollectionFactory.createArray();
		}
		currentRecord = -1;
		currentPage = 0;
		previousPage = -1;
		loaded = false;
		size = -1;
		operations.reset();
		fireResetEvent(); 
	}
		
	@Override
	public void setSize(int recordCount)
	{
		this.size = recordCount;
		if (this.size >= 0)
		{
			this.data = CollectionFactory.createArray(size);
			setLoaded();
			this.setCurrentPage(1);
		}
	}
	
	@Override
	public void setPageSize(int pageSize)
	{
		super.setPageSize(pageSize);
		if (this.loaded && dataLoader != null)
		{
			dataLoader.onMeasureData(new MeasureDataEvent<T>(this));
		}
	}

	@Override
	public void sort(Comparator<T> comparator)
	{
		ensurePageLoaded(currentRecord);
		if (currentRecord > -1)
		{
			Array<DataProviderRecord<T>> pageData = CollectionFactory.createArray(pageSize);
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<pageSize; i++)
			{
				pageData.set(i, data.get(i+startPageRecord));
			}
			sortArray(pageData, comparator, true);
			updateRecords(startPageRecord, endPageRecord, pageData);
			fireSortedEvent(false);
		}
	}	

	@Override
    public void setData(T[] data, int startRecord)
    {
		ensureLoaded();
		if (data != null)
		{
			int dataSize = data.length;
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(dataSize);
			for (int i = 0; i < dataSize; i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data[i]);
				ret.set(i, record);
			}
			update(ret, startRecord, startRecord+dataSize-1);
		}
    }

	@Override
    public void setData(List<T> data, int startRecord)
    {
		ensureLoaded();
		if (data != null)
		{
			int dataSize = data.size();
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(dataSize);
			for (int i = 0; i < dataSize; i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data.get(i));
				ret.set(i, record);
			}
			update(ret, startRecord, startRecord+dataSize-1);
		}
    }

	@Override
    public void setData(Array<T> data, int startRecord)
    {
		ensureLoaded();
		if (data != null)
		{
			int dataSize = data.size();
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(dataSize);
			for (int i = 0; i < dataSize; i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data.get(i));
				ret.set(i, record);
			}
			update(ret, startRecord, startRecord+dataSize-1);
		}
    }
	
	@Override
	protected boolean setCurrentPage(int pageNumber, boolean fireEvents)
	{
		if (currentPage == pageNumber)
		{
			return true;
		}
		checkChanges();
		if (super.setCurrentPage(pageNumber, fireEvents))
		{
			fetchCurrentPage(fireEvents);
			return true;
		}
		return false;
	}

	@Override
	protected void update(Array<DataProviderRecord<T>> records)
	{
		size = records!= null?records.size():0;
		data = CollectionFactory.createArray(size);
		int startRecord = 0;
		int endRecord = size -1;
		int updateRecordCount = updateRecords(startRecord, endRecord, records);
		if (updateRecordCount > 0)
		{
			setLoaded();
			this.setCurrentPage(1);
		}
	}
	
	protected void update(Array<DataProviderRecord<T>> records, int startRecord, int endRecord)
	{
		int updateRecordsCount = updateRecords(startRecord, endRecord, records);
		if (updateRecordsCount > 0)
		{
			firePageLoadedEvent(startRecord, startRecord+updateRecordsCount-1);
		}
		else
		{
			firePageLoadedEvent(-1, -1);
		}
	}

	protected void ensurePageLoaded(int recordNumber)
	{
		boolean loaded = isPageLoaded(getPageForRecord(recordNumber, false));
		if (!loaded)
		{
			throw new DataProviderException("Error processing requested operation. DataProvider is not loaded yet.");
		}
	}
	
	/**
	 * 
	 */
	protected void fetchCurrentPage(boolean fireEvents)
	{
		int pageEndRecord = (currentPage * pageSize) - 1;
		if (!isPageLoaded(currentPage))
		{
			if (dataLoader != null)
			{
				dataLoader.onFetchData(new FetchDataEvent<T>(this, getPageUnloadedStartRecord(), pageEndRecord));
			}
		}
		else if (fireEvents)
		{
			firePageLoadedEvent(getPageStartRecord(), getPageEndRecord());
		}
	}
	
	protected int getPageUnloadedStartRecord()
	{
		int pageStartRecord = getPageStartRecord();
		int pageEndRecord = getPageEndRecord();
		
		for (int i = pageStartRecord; i < pageEndRecord; i++)
		{
			if (data.get(i) == null)
			{
				return i;
			}
		}
		
		return pageEndRecord;
	}
	
	protected boolean isPageLoaded(int pageNumber)
	{
		int startPageRecord = getPageStartRecord(pageNumber);
		int pageEndRecord = getPageEndRecord(pageNumber);
		return (data.size() > 0 && data.get(startPageRecord) != null && data.get(pageEndRecord) != null);
	}
	
	/**
	 * 
	 * @param startRecord
	 * @param endRecord
	 * @param records
	 * @return
	 */
	protected int updateRecords(int startRecord, int endRecord, Array<DataProviderRecord<T>> records)
	{
		if (records != null && endRecord < size)
		{
			for (int i = startRecord, j = 0; i <= endRecord && j < records.size(); i++, j++)
			{
				this.data.set(i, records.get(j));
			}
			
			return records.size();
		}
		return 0;
	}

	protected void checkChanges()
	{
		if (operations.isDirty())
		{//TODO i18n
			throw new DataProviderException("DataProvider has changes on page. You must save or discard them before perform this operation.");
		}
	}
	
	@Override
	protected void concludeEdition(boolean commited)
	{
		if (commited)
		{
			size = size + operations.getNewRecordsCount() - operations.getRemovedRecordsCount();
		}
	    super.concludeEdition(commited);
	}
}
