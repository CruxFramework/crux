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
package br.com.sysmap.crux.core.client.datasource.remote;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.PagedDataSource;
import br.com.sysmap.crux.core.client.datasource.local.LocalPagedDataSource;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class PagedRemoteDataSource extends LocalPagedDataSource 
                           implements PagedDataSource<DataSourceRecord>, 
                                      RemoteDataSource<DataSourceRecord>
{	
	public PagedRemoteDataSource()
	{
		createDataObject();
	}

	@Override
	public void reset()
	{
		if(data != null)
		{
			data = new DataSourceRecord[getRowCount()];
		}
		currentRecord = -1;
		currentPage = 1;
	}
	
	@Override
	public void sort(String columnName)
	{
		if (currentRecord > -1 && ensurePageLoaded(currentRecord))
		{
			DataSourceRecord[] pageData = new DataSourceRecord[pageSize];
			int startPageRecord = getStartPageRecord();
			int endPageRecord = getEndPageRecord();
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
		int record = getStartPageRecord(); 
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
				loaded = updateFetchedData(fetchData(getRemoteStartPageRecord(), getRemoteEndPageRecord()), 
													getStartPageRecord(), getEndPageRecord());
			}
			catch (RuntimeException e)
			{
				//TODO: colocar mensagem
				Crux.getErrorHandler().handleError("", e);
			}
		}
		return loaded;
	}
	
	/**
	 * Get the end page record in remote (server) list of records.
	 * @return
	 */
	protected int getRemoteEndPageRecord()
	{
		return getEndPageRecord();
	}

	/**
	 * Get the start page record in remote (server) list of records.
	 * @return
	 */
	protected int getRemoteStartPageRecord()
	{
		return getStartPageRecord();
	}

	protected boolean updateFetchedData(DataSourceRecord[] pageData, int startRecord, int endRecord)
	{
		if (pageData == null || pageData.length < (endRecord-startRecord+1) || endRecord >= getRowCount())
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
		int startPageRecord = getStartPageRecord();
		return (data[startPageRecord]!= null);
	}

	protected void createDataObject()
	{
		this.data = new DataSourceRecord[getRowCount()];
	}
}
