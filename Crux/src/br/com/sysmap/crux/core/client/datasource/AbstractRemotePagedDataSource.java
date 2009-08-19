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


/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractRemotePagedDataSource<R extends DataSourceRecord, E> extends AbstractLocalPagedDataSource<R, E> 
                           implements PagedDataSource<R>, 
                                      RemoteDataSource<R,E>
{	
	protected RemoteDataSourceCallback fetchCallback = null;
	
	public AbstractRemotePagedDataSource()
	{
		this.data = createDataObject(getRecordCount());
		this.loaded = true;
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
		ensurePageLoaded(currentRecord);
		if (currentRecord > -1)
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
			updateRecords(startPageRecord, endPageRecord, pageData);
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
	public boolean nextPage()
	{
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
		if (super.previousPage())
		{
			fetchCurrentPage();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setCurrentPage(int pageNumber)
	{
		if (super.setCurrentPage(pageNumber))
		{
			fetchCurrentPage();
			return true;
		}
		return false;
	}
	
	@Override
	public void setPageSize(int pageSize)
	{
		super.setPageSize(pageSize);
		fetchCurrentPage();
	}
	
	public final void load()
	{
	}

	protected void ensurePageLoaded(int recordNumber)
	{
		boolean loaded = isPageLoaded(getPageForRecord(recordNumber));
		if (!loaded)
		{
			throw new DataSoureExcpetion();//TODO message
		}
/*			try
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
		return loaded;*/
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#fetch(int, int)
	 */
	public void fetch(int startRecord, int endRecord)
	{
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#update(int, int, R[])
	 */
	public void update(int startRecord, int endRecord, R[] records)
	{
		if (updateRecords(startRecord, endRecord, records) && this.fetchCallback != null)
		{
			fetchCallback.execute(startRecord, endRecord);
		}
	}
	
	/**
	 * 
	 * @param startRecord
	 * @param endRecord
	 * @param records
	 * @return
	 */
	protected boolean updateRecords(int startRecord, int endRecord, R[] records)
	{
		if (records != null && records.length >= (endRecord-startRecord+1) && endRecord < getRecordCount())
		{
			for (int i = startRecord; i <= endRecord; i++)
			{
				this.data[i] = records[i];
			}
			return true;
		}
		return false;
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
	 */
	protected void fetchCurrentPage()
	{
		if (!isPageLoaded(currentPage))
		{
			fetch(getPageStartRecord(), getPageEndRecord());
		}
		else
		{
			fetchCallback.execute(getPageStartRecord(), getPageEndRecord());
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#setCallback(br.com.sysmap.crux.core.client.datasource.RemoteDataSourceCallback)
	 */
	public void setCallback(RemoteDataSourceCallback callback)
	{
		this.fetchCallback = callback;
	}
	
	protected abstract R[] createDataObject(int count);
}
