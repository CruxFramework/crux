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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 */
abstract class AbstractPagedDataProvider<E> extends AbstractScrollableDataProvider<E> 
                                              implements MeasurablePagedProvider<E>
{
	protected Array<PageLoadedHandler> pageLoadedHandlers;
	protected Array<PageChangeHandler> pageChangeHandlers;
	
	protected int pageSize = 10;
	protected int currentPage = 0;
	
	@Override
	public DataProviderRecord<E> add(E object)
	{
		int index = getPageEndRecord()+1;
		return operations.insertRecord(index, object);
	}
	
	@Override
	public int getCurrentPage()
	{
		return currentPage;
	}

	@Override
	public int getPageCount()
	{
		int numRecords = size();
		int pageSize = getPageSize();
		
		return (numRecords / pageSize) + (numRecords%pageSize==0?0:1);
	}

	@Override
	public int getPageSize()
	{
		return pageSize;
	}

	@Override
	public int getCurrentPageSize()
	{
		int pageEndRecord = getPageEndRecord();
		if (pageEndRecord < 0)
		{
			return 0;
		}
		return pageEndRecord - getPageStartRecord() + 1;
	}
	
	@Override
	public int getCurrentPageStartRecord()
	{
	    return getPageStartRecord();
	}
	
	@Override
	public boolean hasNextPage()
	{
		ensureLoaded();
		return (currentPage < getPageCount());
	}

	@Override
	public boolean hasPreviousPage()
	{
		ensureLoaded();
		return (currentPage > 1 );
	}

	@Override
	public boolean nextPage()
	{
		if (hasNextPage())
		{
			currentPage++;
			updateCurrentRecord();
			return true;
		}	
		return false;
	}

	@Override
	public boolean previousPage()
	{
		if (hasPreviousPage())
		{
			currentPage--;
			updateCurrentRecord();
			return true;
		}
		return false;
	}

	@Override
	public boolean setCurrentPage(int pageNumber)
	{
		return setCurrentPage(pageNumber, true);
	}
	
	@Override
	public void setPageSize(int pageSize)
	{
		if (pageSize < 1)
		{
			pageSize = 1;
		}
		this.pageSize = pageSize;
		if (this.loaded)
		{
			updateCurrentRecord();
		}
	}
	
	@Override
	public boolean hasNext()
	{
		return isRecordOnPage(currentRecord+1);
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
		currentPage = 0;
	}	

	@Override
	protected void setFirstPosition(boolean fireEvents)
	{
		setCurrentPage(1, fireEvents);
	}
	
	@Override
	public void firstOnPage()
	{
		ensureLoaded();
		currentRecord = getPageStartRecord();
	}

	@Override
	public void last()
	{
		ensureLoaded();
		currentRecord = getPageEndRecord();
	}

	@Override
	public HandlerRegistration addPageLoadedHandler(final PageLoadedHandler handler)
	{
		if (pageLoadedHandlers == null)
		{
			pageLoadedHandlers = CollectionFactory.createArray();
		}
		
		pageLoadedHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = pageLoadedHandlers.indexOf(handler);
				if (index >= 0)
				{
					pageLoadedHandlers.remove(index);
				}
			}
		};
	}
	
	@Override
	public HandlerRegistration addPageChangeHandler(final PageChangeHandler handler)
	{
		if (pageChangeHandlers == null)
		{
			pageChangeHandlers = CollectionFactory.createArray();
		}
		
		pageChangeHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = pageChangeHandlers.indexOf(handler);
				if (index >= 0)
				{
					pageChangeHandlers.remove(index);
				}
			}
		};
	}
	
	protected boolean setCurrentPage(int pageNumber, boolean fireEvents)
	{
		ensureLoaded();
		if (pageNumber > 0 && pageNumber <= getPageCount())
		{
			currentPage = pageNumber;
			updateCurrentRecord();
			return true;
		}
		return false;
	}

	protected boolean isRecordOnPage(int record)
	{
		ensureLoaded();
		if (data == null)
		{
			return false;
		}
		int startPageRecord = getPageStartRecord();
		int endPageRescord = getPageEndRecord();
		return (record >= startPageRecord && record <= endPageRescord);
	}

	protected int getPageEndRecord()
	{
		return getPageEndRecord(currentPage);
	}

	protected int getPageEndRecord(int page)
	{
		int pageEndRecord = (page * pageSize) - 1;
		if (page == currentPage)
		{
			pageEndRecord += operations.getNewRecordsCount() - operations.getRemovedRecordsCount();
		}
		if (pageEndRecord >= this.data.size())
		{
			pageEndRecord = this.data.size()-1;
		}
		return pageEndRecord;
	}

	protected int getPageStartRecord()
	{
		return getPageStartRecord(currentPage);
	}
	
	protected int getPageStartRecord(int page)
	{
		return (page - 1) * pageSize;
	}
	
	protected void firePageLoadedEvent(int start, int end)
    {
		if (pageLoadedHandlers != null)
		{
			PageLoadedEvent event = new PageLoadedEvent(this, start, end);
			for (int i = 0; i< pageLoadedHandlers.size(); i++)
			{
				pageLoadedHandlers.get(i).onPageLoaded(event);
			}
		}
    }
	
	protected void firePageChangeEvent(int pageNumber)
    {
		if (pageChangeHandlers != null)
		{
			PageChangeEvent event = new PageChangeEvent(this, pageNumber);
			for (int i = 0; i< pageChangeHandlers.size(); i++)
			{
				pageChangeHandlers.get(i).onPageChanged(event);
			}
		}
    }
	
	protected void updateCurrentRecord()
	{
		currentRecord = getPageStartRecord(); 
//		firePageChangeEvent(currentPage);
	}
	
	@Override
	protected void changePositionAfterSorting()
	{
	    firstOnPage();
	}
	
	protected int getPageForRecord(int recordNumber, boolean mayExpand)
	{
		int pageSize = getPageSize();
		int index = recordNumber + 1;
		int result = (index / pageSize) + (index%pageSize==0?0:1);
		if (!mayExpand && result > getPageCount())
		{
			throw new DataProviderExcpetion("Invalid record. Out of bounds");
		}
		
		return result;
	}
	
	@Override
	protected int lockRecordForEdition(int recordIndex)
    {
		int pageForRecord = getPageForRecord(recordIndex, true);
		setCurrentPage(pageForRecord, false);
		return getPageStartRecord(pageForRecord);
    }
	
	@Override
	protected Array<DataProviderRecord<E>> getTransactionRecords()
	{
		Array<DataProviderRecord<E>> currentPageRecordsArray = CollectionFactory.createArray();
		int start = getPageStartRecord();
		int end = getPageEndRecord();
		for (int i = start; i <= end; i++)
		{
			DataProviderRecord<E> record = data.get(i);
			currentPageRecordsArray.add(record.clone());
		}

		return currentPageRecordsArray;
	}	
	
	@Override
	protected void replaceTransactionData(Array<DataProviderRecord<E>> transactionRecords)
	{
		int start = getPageStartRecord();
		int end = getPageEndRecord();
		data.remove(start, end-start+1);
		
		for (int i=0; i<transactionRecords.size(); i++)
		{
			data.insert(start+i, transactionRecords.get(i));
		}
	}
}
