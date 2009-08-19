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

import java.util.Arrays;
import java.util.Comparator;

import br.com.sysmap.crux.core.client.ClientMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
abstract class AbstractLocalScrollableDataSource<R extends DataSourceRecord, E>
                                                   implements ScrollableDataSource<R>, LocalDataSource<R,E>
{
	protected Metadata metadata;
	protected R[] data;
	protected int currentRecord = -1;
	protected boolean loaded = false;
	protected ClientMessages messages = GWT.create(ClientMessages.class);
	protected LocalDataSourceCallback loadCallback = null;

	public Metadata getMetadata()
	{
		return metadata;
	}
	
	public Object getValue(String columnName)
	{
		if (currentRecord > -1)
		{
			DataSourceRecord dataSourceRow = data[currentRecord];
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
		ensureLoaded();
		return (data != null && currentRecord < data.length -1);
	}

	public void nextRecord()
	{
		if (hasNextRecord())
		{
			currentRecord++;
		}
	}

	public void reset()
	{
		if(data != null)
		{
			data = null;
		}
		currentRecord = -1;
		loaded = false;
	}
	
	public R getRecord()
	{
		ensureLoaded();
		if (currentRecord > -1)
		{
			return data[currentRecord];
		}
		else
		{
			return null;
		}
	}
	
	public boolean hasPreviousRecord()
	{
		ensureLoaded();
		return (data != null && currentRecord > 0 && data.length > 0);
	}

	public void previousRecord()
	{
		if (hasPreviousRecord())
		{
			currentRecord--;
		}
	}
	
	public void sort(final String columnName, boolean ascending)
	{
		ensureLoaded();
		if (data != null)
		{
			sortArray(data,columnName, ascending);
		}
	}

	protected void sortArray(DataSourceRecord[] array, final String columnName, final boolean ascending)
	{
		final int position = metadata.getColumnPosition(columnName);
		Arrays.sort(array, new Comparator<DataSourceRecord>(){
			public int compare(DataSourceRecord o1, DataSourceRecord o2)
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
	
	public int getRecordCount()
	{
		return (data!=null?data.length:0);
	}
	
	public void firstRecord()
	{
		currentRecord = -1;
		nextRecord();
	}
	
	public void lastRecord()
	{
		ensureLoaded();
		currentRecord = getRecordCount()-1;
	}
	
	protected void ensureLoaded()
	{
		if (!loaded)
		{
			throw new DataSoureExcpetion();//TODO message
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.LocalDataSource#update(R[])
	 */
	public void update(R[] records)
	{
		loaded = true;
		this.data = records;
		if (this.loadCallback != null)
		{
			this.loadCallback.execute();
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.LocalDataSource#setCallback(br.com.sysmap.crux.core.client.datasource.LocalDataSourceCallback)
	 */
	public void setCallback(LocalDataSourceCallback callback)
	{
		this.loadCallback = callback;
	}
	
	public void updateData(E[] data)
	{
	}
}
