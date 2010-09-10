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
 * @author Thiago da Rosa de Bustamante
 *
 */
abstract class AbstractRemotePagedDataSource<R extends DataSourceRecord, E> extends AbstractPagedDataSource<R, E> 
                           implements MeasurableRemoteDataSource<R,E>
{	
	protected RemoteDataSourceCallback fetchCallback = null;
	private RemoteDataSourceConfiguration config;
	
	public AbstractRemotePagedDataSource()
	{
		this.data = createDataObject(0);
	}

	@Override
	public void reset()
	{
		if(data != null)
		{
			this.data = createDataObject(0);
		}
		currentRecord = -1;
		currentPage = 0;
		loaded = false;
		this.config = null;
	}
	
	@Override
	public void sort(String columnName, boolean ascending)
	{
		ensurePageLoaded(currentRecord);
		if (currentRecord > -1)
		{
			R[] pageData = createDataObject(pageSize);
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<pageSize; i++)
			{
				pageData[i] = data[i+startPageRecord];
			}
			sortArray(pageData,columnName, ascending);
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
		if (this.loaded)
		{
			load();
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#cancelFetching()
	 */
	public void cancelFetching()
	{
		currentPage--;
		updateCurrentRecord();
		this.fetchCallback.cancelFetching();
	}	
	
	protected void ensurePageLoaded(int recordNumber)
	{
		boolean loaded = isPageLoaded(getPageForRecord(recordNumber));
		if (!loaded)
		{
			throw new DataSoureExcpetion(messages.dataSourceNotLoaded());
		}
	}
	
	@Override
	public int getRecordCount()
	{
		if (!loaded)
		{
			throw new DataSoureExcpetion(messages.dataSourceNotLoaded());
		}
		return this.config.getRecordCount();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#update(R[])
	 */
	public void update(R[] records)
	{
		int startRecord = getPageStartRecord();
		int endRecord = getPageEndRecord();
		int updateRecordsCount = updateRecords(startRecord, endRecord, records);
		if (this.fetchCallback != null)
		{
			if (updateRecordsCount > 0)
			{
				fetchCallback.execute(startRecord, startRecord+updateRecordsCount-1);
			}
			else
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
	protected int updateRecords(int startRecord, int endRecord, R[] records)
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
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#setLoadData(br.com.sysmap.crux.core.client.datasource.RemoteDataSourceConfiguration)
	 */
	public void setLoadData(RemoteDataSourceConfiguration config)
	{
		this.config = config;
		this.loaded = (this.config != null);
		this.data = createDataObject(getRecordCount());
		this.setCurrentPage(1);
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.RemoteDataSource#setCallback(br.com.sysmap.crux.core.client.datasource.RemoteDataSourceCallback)
	 */
	public void setCallback(RemoteDataSourceCallback callback)
	{
		this.fetchCallback = callback;
	}
	
	public void updateData(E[] data)
	{
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
		int pageEndRecord = (currentPage * pageSize) - 1;
		if (!isPageLoaded(currentPage))
		{
			fetch(getPageStartRecord(), pageEndRecord);
		}
		else
		{
			fetchCallback.execute(getPageStartRecord(), pageEndRecord);
		}
	}
	
	protected abstract R[] createDataObject(int count);
}
