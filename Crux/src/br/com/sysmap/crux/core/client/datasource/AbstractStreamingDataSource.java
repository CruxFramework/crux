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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractStreamingDataSource<R extends DataSourceRecord, E> 
                           implements StreamingDataSource<R, E>
{	
	protected List<R> data = new ArrayList<R>();
	protected int currentRecord = -1;
	protected int pageSize = 10;
	protected int currentPage = 0;
	protected Metadata metadata;
	protected RemoteDataSourceCallback fetchCallback = null;
	
	public void setCallback(RemoteDataSourceCallback callback)
	{
		this.fetchCallback = callback;		
	}

	public void update(R[] records)
	{
		int startRecord = getPageStartRecord();
		int endRecord = (currentPage * pageSize) - 1;
		int updateRecordsCount = updateRecords(startRecord, endRecord, records);
		if (updateRecordsCount > 0 && this.fetchCallback != null)
		{
			fetchCallback.execute(startRecord, startRecord+updateRecordsCount-1);
		}
		else
		{
			fetchCallback.execute(-1, -1);
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
		int ret = 0;
		if (records != null)
		{
			for (int i = startRecord; i <= endRecord; i++)
			{
				this.data.add(records[i]);
			}
			ret = records.length;
		}
		if (ret < (endRecord - startRecord))
		{
			this.data.add(null);
		}
		return ret;
	}
	
	
	public void updateData(E[] data)
	{
	}

	public Metadata getMetadata()
	{
		return metadata;
	}

	public R getRecord()
	{
		ensureCurrentPageLoaded();		
		if (currentRecord > -1)
		{
			return data.get(currentRecord);
		}
		else
		{
			return null;
		}
	}

	public Object getValue(String columnName)
	{
		ensureCurrentPageLoaded();		
		if (currentRecord > -1)
		{
			R dataSourceRow = data.get(currentRecord);
			int position = metadata.getColumnPosition(columnName);
			if (position > -1)
			{
				return dataSourceRow.get(position);
			}
		}
		return null;
	}

	public boolean hasNextRecord()
	{
		ensureCurrentPageLoaded();
		return isRecordOnPage(currentRecord+1);
	}

	public void nextRecord()
	{
		if (hasNextRecord())
		{
			currentRecord++;
		}
	}
	
	public void firstRecord()
	{
		currentRecord = getPageStartRecord();
		ensureCurrentPageLoaded();
	}
	
	public boolean hasPreviousRecord()
	{
		ensureCurrentPageLoaded();
		return isRecordOnPage(currentRecord-1);
	}

	public void previousRecord()
	{
		if (hasPreviousRecord())
		{
			currentRecord--;
		}
	}

	public void reset()
	{
		this.data.clear();
		currentRecord = -1;
		currentPage = 0;
	}
	
	protected void ensureCurrentPageLoaded()
	{
		boolean loaded = isCurrentPageLoaded();
		if (!loaded)
		{
			throw new DataSoureExcpetion();//TODO message
		}
	}

	protected boolean isCurrentPageLoaded()
	{
		int pageStartRecord = getPageStartRecord();
		return (data.size() > pageStartRecord);
	}
	
	protected boolean isRecordOnPage(int record)
	{
		ensureCurrentPageLoaded();
		if (data == null)
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
		if (pageEndRecord >= this.data.size())
		{
			pageEndRecord = this.data.size()-1;
		}
		return pageEndRecord;
	}

	protected int getPageStartRecord()
	{
		return (currentPage - 1) * pageSize;
	}

	public int getCurrentPage()
	{
		return currentPage;
	}

	public int getCurrentPageSize()
	{
		int pageEndRecord = getPageEndRecord();
		return pageEndRecord - getPageStartRecord() + 1;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public boolean hasNextPage()
	{
		int pageEndRecord = getPageEndRecord();
		if (pageEndRecord < this.data.size())
		{
			if (pageEndRecord == this.data.size()-1)
			{
				return this.data.get(pageEndRecord) != null;
			}
			return true;
		}
		return false;
	}

	public boolean hasPreviousPage()
	{
		return (currentPage > 1);
	}

	public boolean nextPage()
	{
		if (hasNextPage())
		{
			currentPage++;
			updateCurrentRecord();
			fetchCurrentPage();
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
			fetchCurrentPage();
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
		updateCurrentRecord();
		fetchCurrentPage();
	}

	@SuppressWarnings("unchecked")
	public void sort(String columnName, boolean ascending)
	{
		if (currentRecord > -1)
		{
			List<R> pageData = new ArrayList<R>(pageSize);
			int startPageRecord = getPageStartRecord();
			int endPageRecord = getPageEndRecord();
			int pageSize = endPageRecord - startPageRecord + 1;
			for (int i = 0; i<=pageSize; i++)
			{
				pageData.add(data.get(i+startPageRecord));
			}
			sortList(pageData,columnName, ascending);
			updateRecords(startPageRecord, endPageRecord, (R[]) pageData.toArray());
		}
	}
	
	protected void sortList(List<R> list, final String columnName, final boolean ascending)
	{
		final int position = metadata.getColumnPosition(columnName);
		Collections.sort(list, new Comparator<R>(){
			public int compare(R o1, R o2)
			{
				if (ascending)
				{
					if (o1==null) return (o2==null?0:-1);
					if (o2==null) return 1;
				}
				else
				{
					if (o1==null) return (o2==null?0:1);
					if (o2==null) return -1;
				}
				
				Object value1 = o1.get(position);
				Object value2 = o2.get(position);

				if (ascending)
				{
					if (value1==null) return (value2==null?0:-1);
					if (value2==null) return 1;
				}
				else
				{
					if (value1==null) return (value2==null?0:1);
					if (value2==null) return -1;
				}

				return compareNonNullValuesByType(value1,value2,ascending);
			}

			@SuppressWarnings("unchecked")
			private int compareNonNullValuesByType(Object value1, Object value2,boolean ascending)
			{
				if (ascending)
				{
					return ((Comparable)value1).compareTo(value2);
				}
				else
				{
					return ((Comparable)value2).compareTo(value1);
				}
			}
		});
		firstRecord();
	}
	
	
	protected void updateCurrentRecord()
	{
		currentRecord = getPageStartRecord(); 
	}
	
	/**
	 * 
	 */
	protected void fetchCurrentPage()
	{
		if (!isCurrentPageLoaded())
		{
			fetch(getPageStartRecord(), getPageEndRecord());
		}
		else
		{
			fetchCallback.execute(getPageStartRecord(), getPageEndRecord());
		}
	}
	
}
