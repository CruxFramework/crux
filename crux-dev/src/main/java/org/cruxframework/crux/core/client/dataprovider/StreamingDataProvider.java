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
import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord.DataProviderRecordState;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class StreamingDataProvider<T> extends AbstractDataProvider<T> implements StreamingProvider<T>
{
	protected StreamingDataProviderOperations<T> operations = new StreamingDataProviderOperations<T>(this);
	protected int pageSize = 10;
	protected int currentPage = 0;
	protected int previousPage = -1;
	protected Array<PageLoadedHandler> pageFetchHandlers;
	protected Array<PageRequestedHandler> pageRequestedHandlers;
	protected StreamingDataLoader<T> dataLoader;

	public StreamingDataProvider()
    {
    }
	
    public StreamingDataProvider(DataProvider.EditionDataHandler<T> handler)
	{
    	super(handler);
	}

	public StreamingDataProvider(DataProvider.EditionDataHandler<T> handler, StreamingDataLoader<T> dataLoader)
    {
		super(handler);
		this.dataLoader = dataLoader;
    }
	
	@Override
    public void setDataLoader(StreamingDataLoader<T> dataLoader)
    {
		this.dataLoader = dataLoader;
    }

	@Override
    public StreamingDataLoader<T> getDataLoader()
    {
	    return dataLoader;
    }

	@Override
	public boolean isDirty()
	{
	    return operations != null && operations.isDirty();
	}
	
	@Override
	public DataProviderRecord<T> add(int beforeIndex, T object)
	{
		return operations.insertRecord(beforeIndex, object);
	}
	
	@Override
	public DataProviderRecord<T> add(T object)
	{
		return operations.insertRecord(object);
	}

	@Override
	public DataProviderRecord<T> remove(int index)
	{
		return operations.removeRecord(index);
	}

	@Override
	public DataProviderRecord<T> set(int index, T object)
	{
		return operations.updateRecord(index, object);
	}

	@Override
	public void updateState(DataProviderRecord<T> record, DataProviderRecordState previousState)
	{
		operations.updateState(record, previousState);
	}

	@Override
	public DataProviderRecord<T>[] getNewRecords()
	{
		return operations.getNewRecords();
	}

	@Override
	public DataProviderRecord<T>[] getRemovedRecords()
	{
		return operations.getRemovedRecords();
	}

	@Override
	public DataProviderRecord<T>[] getUpdatedRecords()
	{
		return operations.getUpdatedRecords();
	}

	@Override
	public DataProviderRecord<T>[] getSelectedRecords()
	{
		return operations.getSelectedRecords();
	}	
	
	@Override
	public void rollback()
	{
		this.operations.rollback();
	}

	@Override
	public void commit()
	{
		this.operations.commit();
	}
	
	@Override
	public void load()
	{
		if(!isLoaded())
		{
			nextPage();
		}
	}
	
	@Override
	public DataProviderRecord<T> select(T object, boolean selected)
	{
		return operations.selectRecord(indexOf(object), selected);
	}
	
	@Override
	public DataProviderRecord<T> setReadOnly(int index, boolean readOnly)
	{
		return operations.setReadOnly(index, readOnly);
	}

	@Override
	public DataProviderRecord<T> setReadOnly(T object, boolean readOnly)
	{
		return operations.setReadOnly(indexOf(object), readOnly);
	}

	@Override
	public HandlerRegistration addPageLoadedHandler(final PageLoadedHandler handler)
	{
		if (pageFetchHandlers == null)
		{
			pageFetchHandlers = CollectionFactory.createArray();
		}
		
		pageFetchHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = pageFetchHandlers.indexOf(handler);
				if (index >= 0)
				{
					pageFetchHandlers.remove(index);
				}
			}
		};
	}

	@Override
	public HandlerRegistration addPageRequestedHandler(final PageRequestedHandler handler)
	{
		if (pageRequestedHandlers == null)
		{
			pageRequestedHandlers = CollectionFactory.createArray();
		}
		
		pageRequestedHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = pageRequestedHandlers.indexOf(handler);
				if (index >= 0)
				{
					pageRequestedHandlers.remove(index);
				}
			}
		};
	}
	
	@Override
	public int indexOf(T boundObject)
	{
		return operations.getRecordIndex(boundObject);
	}
	
	@Override
	public DataProviderRecord<T> select(int index, boolean selected)
	{
		return operations.selectRecord(index, selected);
	}

	@Override
	public void stopLoading()
	{
		previousPage = currentPage;
		currentPage--;
		updateCurrentRecord();
		super.stopLoading();
	}

	@Override
	public DataProviderRecord<T> getRecord()
	{
		if (isCurrentPageLoaded() && currentRecord > -1)
		{
			return data.get(currentRecord);
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean hasNext()
	{
		return isRecordOnPage(currentRecord+1);
	}
	
	@Override
	public void first()
	{
		if (currentPage != 1)
		{
			ensureNotDirty();
		}
		previousPage = currentPage;
		currentPage = 1;
		currentRecord = getPageStartRecord();
		ensureCurrentPageLoaded();
	}

	@Override
	public void firstOnPage()
	{
		int pageStartRecord = getPageStartRecord();
		if (pageStartRecord != currentRecord)
		{
			if (currentPage != 1)
			{
				ensureNotDirty();
			}
			currentRecord = pageStartRecord;
			ensureCurrentPageLoaded();
		}
	}
	
	@Override
	public boolean hasPrevious()
	{
		return isRecordOnPage(currentRecord-1);
	}

	@Override
	public void reset()
	{
		super.reset();
		previousPage = -1;
		currentPage = 0;
		operations.reset();
		
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
	public int getCurrentPageStartRecord()
	{
	    return getPageStartRecord();
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
		ensureNotDirty();
		if (hasNextPage())
		{
			previousPage = currentPage;
			currentPage++;
			if (currentPage == 1)
			{
				setLoaded();
			}
			updateCurrentRecord();
			firePageRequestedEvent(currentPage);
			fetchCurrentPage();
			return true;
		}
		
		return false;
	}

	@Override
	public boolean previousPage()
	{
		ensureNotDirty();
		if (hasPreviousPage())
		{
			previousPage = currentPage;
			currentPage--;
			updateCurrentRecord();
			firePageRequestedEvent(currentPage);
			fetchCurrentPage();
			return true;
		}
		
		return false;
	}

	protected boolean setCurrentPage(int pageNumber)
	{
		if (pageNumber < currentPage)
		{
			previousPage = currentPage;
			currentPage = pageNumber;
			updateCurrentRecord();
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
			updateCurrentRecord();
			fetchCurrentPage();
			firePageRequestedEvent(currentPage);			
		}
	}
	
    @Override
	public void setData(T[] data)
	{
		if (data == null)
		{
			Array<DataProviderRecord<T>> array = CollectionFactory.createArray(); 
			update(array);
		} 
		else 
		{
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(data.length); 
			for (int i=0; i<data.length; i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data[i]);
				ret.set(i, record);
			}
			update(ret);
		}
	}	
	
    @Override
	public void setData(List<T> data)
	{
		if (data == null)
		{
			Array<DataProviderRecord<T>> array = CollectionFactory.createArray(); 
			update(array);
		} 
		else 
		{
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(data.size());
			for (int i=0; i<data.size(); i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data.get(i));
				ret.set(i, record);
			}
			update(ret);
		}
	}

    @Override
	public void setData(Array<T> data)
	{
		if (data == null)
		{
			Array<DataProviderRecord<T>> array = CollectionFactory.createArray(); 
			update(array);
		} 
		else 
		{
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(data.size());
			for (int i=0; i<data.size(); i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data.get(i));
				ret.set(i, record);
			}
			update(ret);
		}
	}

	@Override
	public void sort(Comparator<T> comparator)
	{
		if (isCurrentPageLoaded() && currentRecord > -1)
		{
			Array<DataProviderRecord<T>> pageData = CollectionFactory.createArray(pageSize);
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<pageSize; i++)
			{
				pageData.add(data.get(i+startPageRecord));
			}
			sortArray(pageData, comparator);
			updatePageRecords(startPageRecord, endPageRecord, pageData);
			fireSortedEvent(false);
		}
	}
	
	@Override
    public void setData(T[] data, int startRecord)
    {
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
	public Array<T> filter(DataFilter<T> filter)
	{
		Array<T> result = CollectionFactory.createArray();

		if (data != null)
		{
			int size = data.size();
			for (int i = 0; i < size; i++)
			{
				DataProviderRecord<T> dataProviderRecord = data.get(i);
				if (dataProviderRecord != null)
				{
					T object = dataProviderRecord.getRecordObject();
					if (filter.accept(object))
					{
						result.add(object);
					}
				}
			}
		}

		return result;
	}
	
	protected void ensureCurrentPageLoaded()
	{
		boolean loaded = isCurrentPageLoaded();
		if (!loaded)
		{
			throw new DataProviderException("Error processing requested operation. DataProvider is not loaded yet.");
		}
	}

	protected boolean isCurrentPageLoaded()
	{
		int pageStartRecord = getPageStartRecord();
		return (data.size() > pageStartRecord);
	}
	
	protected boolean isRecordOnPage(int record)
	{
		if (data == null || !isCurrentPageLoaded())
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
		
		return pageEndRecord + operations.getNewRecordsCount() - operations.getRemovedRecordsCount();
	}

	protected int getPageStartRecord()
	{
		return getPageStartRecord(currentPage);
	}
	
	protected int getPageStartRecord(int page)
	{
		return (page - 1) * pageSize;
	}
	
	protected void sortArray(Array<DataProviderRecord<T>> array, final Comparator<T> comparator)
	{
		array.sort(new Comparator<DataProviderRecord<T>>(){
			public int compare(DataProviderRecord<T> o1, DataProviderRecord<T> o2)
			{
				return comparator.compare(o1.getRecordObject(), o2.getRecordObject());
			}
		});
		firstOnPage();
	}
	
	protected void updateCurrentRecord()
	{
		currentRecord = getPageStartRecord(); 
	}
	
	protected void fetchCurrentPage()
	{
		if (!isCurrentPageLoaded())
		{
			if (dataLoader != null)
			{
				dataLoader.onFetchData(new FetchDataEvent<T>(this, getPageStartRecord(), getPageEndRecord()));
			}
		}
		else
		{
			firePageLoadedEvent(getPageStartRecord(), getPageEndRecord());
		}
	}
	
	protected void firePageLoadedEvent(int start, int end)
    {
		if (pageFetchHandlers != null)
		{
			PageLoadedEvent event = new PageLoadedEvent(this, start, end, previousPage, currentPage);
			for (int i = 0; i< pageFetchHandlers.size(); i++)
			{
				pageFetchHandlers.get(i).onPageLoaded(event);
			}
		}
    }
	
	protected void firePageRequestedEvent(int pageNumber)
    {
		if (pageRequestedHandlers != null)
		{
			PageRequestedEvent event = new PageRequestedEvent(this, pageNumber);
			for (int i = 0; i< pageRequestedHandlers.size(); i++)
			{
				pageRequestedHandlers.get(i).onPageRequested(event);
			}
		}
    }
	
	protected void update(Array<DataProviderRecord<T>> records)
	{
		int recordCount = records!= null?records.size():0;
		data = CollectionFactory.createArray(recordCount);
		int startRecord = 0;
		int endRecord = recordCount -1;
		int updateRecordsCount = updateRecords(startRecord, endRecord, records);
		if (updateRecordsCount > 0)
		{
			previousPage = -1;
			currentPage = 0;
			currentRecord = -1;
			nextPage();
		}
	}
	
	protected void update(Array<DataProviderRecord<T>> records, int startRecord, int endRecord)
	{
		if (!loaded)
		{
			if (startRecord == 0 && endRecord > 0)
			{
				setLoaded();
			}
			else
			{
				return;
			}
		}
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

	protected int updateRecords(int startRecord, int endRecord, Array<DataProviderRecord<T>> records)
	{
		int ret = 0;
		if (records != null)
		{
			int count = Math.min(endRecord - startRecord + 1, records.size());
			for (ret = 0; ret < count ; ret++)
			{
				this.data.add(records.get(ret));
			}
		}
		if (ret < (endRecord - startRecord))
		{
			this.data.add(null);
		}
		return ret;
	}

	protected int updatePageRecords(int startRecord, int endRecord, Array<DataProviderRecord<T>> records)
	{
		int ret = 0;
		if (records != null)
		{
			int count = Math.min(endRecord - startRecord + 1, records.size());
			for (ret = 0; ret < count ; ret++)
			{
				this.data.set(ret+startRecord, records.get(ret));
			}
		}
		return ret;
	}

	protected Array<DataProviderRecord<T>> getTransactionRecords()
	{
		Array<DataProviderRecord<T>> currentPageRecordsArray = CollectionFactory.createArray();
		int start = getPageStartRecord();
		int end = getPageEndRecord();
		for (int i = start; i <= end; i++)
		{
			DataProviderRecord<T> record = data.get(i);
			currentPageRecordsArray.add(record.clone());
		}

		return currentPageRecordsArray;
	}	
	
	protected void replaceTransactionData(Array<DataProviderRecord<T>> transactionRecords)
	{
		int start = getPageStartRecord();
		int end = getPageEndRecord();
		data.remove(start, end-start+1);
		
		for (int i=0; i<transactionRecords.size(); i++)
		{
			data.insert(start+i, transactionRecords.get(i));
		}
	}

	protected int lockRecordForEdition(int recordIndex)
    {
		int pageForRecord = getPageForRecord(recordIndex);
		setCurrentPage(pageForRecord);
		return getPageStartRecord(pageForRecord);
    }
	
	protected int getPageForRecord(int recordNumber)
	{
		int pageSize = getPageSize();
		int index = recordNumber + 1;
		int result = (index / pageSize) + (index%pageSize==0?0:1);
		return result;
	}
	
}