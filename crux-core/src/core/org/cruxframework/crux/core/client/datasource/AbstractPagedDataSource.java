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



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
abstract class AbstractPagedDataSource<E> extends AbstractScrollableDataSource<E> 
                                              implements MeasurablePagedDataSource<E>
{
	protected int pageSize = 10;
	protected int currentPage = 0;
	
	public int getCurrentPage()
	{
		return currentPage;
	}

	public int getPageCount()
	{
		int numRecords = getRecordCount();
		int pageSize = getPageSize();
		
		return (numRecords / pageSize) + (numRecords%pageSize==0?0:1);
	}

	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.MeasurablePagedDataSource#getCurrentPageSize()
	 */
	public int getCurrentPageSize()
	{
		int pageEndRecord = getPageEndRecord();
		if (pageEndRecord < 0)
		{
			return 0;
		}
		return pageEndRecord - getPageStartRecord() + 1;
	}
	
	public boolean hasNextPage()
	{
		ensureLoaded();
		return (currentPage < getPageCount());
	}

	public boolean hasPreviousPage()
	{
		ensureLoaded();
		return (currentPage > 1 );
	}

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

	public boolean setCurrentPage(int pageNumber)
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
	public boolean hasNextRecord()
	{
		return isRecordOnPage(currentRecord+1);
	}
	
	@Override
	public boolean hasPreviousRecord()
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
	public void firstRecord()
	{
		ensureLoaded();
		currentRecord = getPageStartRecord();
	}

	@Override
	public void lastRecord()
	{
		ensureLoaded();
		currentRecord = getPageEndRecord();
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
		int pageEndRecord = (currentPage * pageSize) - 1;
		if (pageEndRecord >= this.data.length)
		{
			pageEndRecord = this.data.length-1;
		}
		return pageEndRecord;
	}

	protected int getPageStartRecord()
	{
		return (currentPage - 1) * pageSize;
	}
	
	protected void updateCurrentRecord()
	{
		currentRecord = getPageStartRecord(); 
	}
}
