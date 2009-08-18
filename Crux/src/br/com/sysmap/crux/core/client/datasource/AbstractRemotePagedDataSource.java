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

import br.com.sysmap.crux.core.client.Crux;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractRemotePagedDataSource<R extends DataSourceRecord, E> extends AbstractLocalPagedDataSource<R, E> 
                           implements PagedDataSource<R>, 
                                      RemoteDataSource<R,E>
{	
	public AbstractRemotePagedDataSource()
	{
		this.data = createDataObject(getRecordCount());
	}

	@Override
	public void reset()
	{
		if(data != null)
		{
			this.data = createDataObject(getRecordCount());
		}
		currentRecord = -1;
		currentPage = 1;
	}
	
	@Override
	public void sort(String columnName)
	{
		if (currentRecord > -1 && ensurePageLoaded(currentRecord))
		{
			R[] pageData = createDataObject(pageSize);
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<=pageSize; i++)
			{
				pageData[i] = data[i+startPageRecord];
			}
			sortArray(pageData,columnName);
			updateFetchedData(pageData, startPageRecord, endPageRecord);
		}
	}
	
	@Override
	public void firstRecord()
	{
		super.firstRecord();
		ensurePageLoaded(currentRecord);
	}
	
	@Override
	public void lastRecord()
	{
		super.lastRecord();
		ensurePageLoaded(currentRecord);
	}
	
	@Override
	protected boolean updateCurrentRecord()
	{
		int record = getPageStartRecord(); 
		if (ensurePageLoaded(record))
		{
			currentRecord = record;
			return true;
		}
		return false;
	}

	/*
	 * Remote Data source are always loaded... Its pages can eventually not be fetched yet..
	 */
	@Override
	protected boolean ensureLoaded()
	{
		return true;
	}
	
	public E[] loadData()
	{
		return null;
	}

	@Override
	protected boolean isRecordOnPage(int record)
	{
		if (ensurePageLoaded(record))
		{
			return super.isRecordOnPage(record);
		}
		else
		{
			return false;
		}
	}
	
	protected boolean ensurePageLoaded(int recordNumber)
	{
		boolean loaded = isPageLoaded(getPageForRecord(recordNumber));
		if (!loaded)
		{
			try
			{
				int startPageRecord = getPageStartRecord();
				int endPageRecord = getPageEndRecord();
				loaded = updateFetchedData(fetch(startPageRecord, endPageRecord), 
													startPageRecord, endPageRecord);
			}
			catch (RuntimeException e)
			{
				Crux.getErrorHandler().handleError(messages.remoteDataSourceErrorLoadingData(e.getMessage()), e);
			}
		}
		return loaded;
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#fetch(int, int)
	 */
	public R[] fetch(int startRecord, int endRecord)
	{
		return null;
	}

	protected boolean updateFetchedData(R[] pageData, int startRecord, int endRecord)
	{
		if (pageData == null || pageData.length < (endRecord-startRecord+1) || endRecord >= getRecordCount())
		{
			return false;
		}
		for (int i = startRecord; i <= endRecord; i++)
		{
			this.data[i] = pageData[i];
		}
		return true;
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

	protected abstract R[] createDataObject(int count);
}
