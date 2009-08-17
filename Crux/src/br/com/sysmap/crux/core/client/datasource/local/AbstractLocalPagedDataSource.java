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
package br.com.sysmap.crux.core.client.datasource.local;

import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.PagedDataSource;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
abstract class AbstractLocalPagedDataSource<R extends DataSourceRecord, E> extends AbstractLocalScrollableDataSource<R,E> 
                                              implements PagedDataSource<R>
{
	protected int pageSize = 10;
	protected int currentPage = 1;
	
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

	public boolean hasNextPage()
	{
		if (ensureLoaded())
		{
			return (currentPage < getPageCount());
		}
		else
		{
			return false;
		}
	}

	public boolean hasPreviousPage()
	{
		if (ensureLoaded())
		{
			return (currentPage > 1 );
		}
		else
		{
			return false;
		}
	}

	public void nextPage()
	{
		if (hasNextPage())
		{
			currentPage++;
			if (!updateCurrentRecord())
			{
				currentPage--;
			}
		}	
	}

	public void previousPage()
	{
		if (hasPreviousPage())
		{
			currentPage--;
			if (!updateCurrentRecord())
			{
				currentPage++;
			}
		}	
	}

	public void setCurrentPage(int pageNumber)
	{
		if (ensureLoaded())
		{
			if (pageNumber > 0 && pageNumber <= getPageCount())
			{
				int previousPage = currentPage;
				currentPage = pageNumber;
				if (!updateCurrentRecord())
				{
					currentPage = previousPage;
				}
			}
		}
	}

	public void setPageSize(int pageSize)
	{
		if (pageSize < 1)
		{
			pageSize = 1;
		}
		this.pageSize = pageSize;
		if (ensureLoaded())
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
		currentPage = 1;
	}	

	@Override
	public void firstRecord()
	{
		if (ensureLoaded())
		{
			currentRecord = getStartPageRecord();
		}
	}

	@Override
	public void lastRecord()
	{
		if (ensureLoaded())
		{
			currentRecord = getEndPageRecord();
		}
	}

	protected boolean isRecordOnPage(int record)
	{
		if (ensureLoaded())
		{
			if (data == null)
			{
				return false;
			}
			int startPageRecord = getStartPageRecord();
			int endPageRescord = getEndPageRecord();
			if (endPageRescord >= data.length)
			{
				endPageRescord = data.length-1;
			}
			return (record >= startPageRecord && record <= endPageRescord);
		}
		else
		{
			return false;
		}
	}

	protected int getEndPageRecord()
	{
		return (currentPage * pageSize) - 1;
	}

	protected int getStartPageRecord()
	{
		return (currentPage - 1) * pageSize;
	}
	
	protected boolean updateCurrentRecord()
	{
		currentRecord = ((currentPage-1)* pageSize) + 1; 
		return true;
	}
}
