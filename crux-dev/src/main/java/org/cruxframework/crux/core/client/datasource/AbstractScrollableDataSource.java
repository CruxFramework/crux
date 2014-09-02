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

import java.util.Arrays;
import java.util.Comparator;

import org.cruxframework.crux.core.client.ClientMessages;
import org.cruxframework.crux.core.client.Legacy;



import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
abstract class AbstractScrollableDataSource<E> implements MeasurableDataSource<E>
{
	protected ColumnDefinitions<E> definitions = new ColumnDefinitions<E>();
	protected DataSourceRecord<E>[] data;
	protected int currentRecord = -1;
	protected boolean loaded = false;
	protected ClientMessages messages = GWT.create(ClientMessages.class);

	public ColumnDefinitions<E> getColumnDefinitions()
	{
		return definitions;
	}
	
	public void setColumnDefinitions(ColumnDefinitions<E> columnDefinitions)
	{
		this.definitions = columnDefinitions;
	}
	
	public Object getValue(String columnName)
	{
		if (currentRecord > -1)
		{
			DataSourceRecord<E> dataSourceRow = data[currentRecord];
			return getValue(columnName, dataSourceRow);
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
	
	@Deprecated
	public DataSourceRecord<E> getRecord(int index)
	{
		ensureLoaded();
		if (currentRecord > -1)
		{
			return data != null ? data[index] : null;
		}
		else
		{
			return null;
		}
	}
	
	public DataSourceRecord<E> getRecord()
	{
		ensureLoaded();
		if (currentRecord > -1)
		{
			return data != null ? data[currentRecord] : null;
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
	
	@Override
	public void sort(final String columnName, boolean ascending)
	{
		sort(columnName, ascending, false);
	}
	
	@Override
	public void sort(final String columnName, boolean ascending, boolean caseSensitive)
	{
		ensureLoaded();
		if (data != null)
		{
			sortArray(data,columnName, ascending, caseSensitive);
		}
	}
	
	protected void sortArray(DataSourceRecord<E>[] array, final String columnName, final boolean ascending, final boolean caseSensitive)
	{
		final String columnName1;
		String columnName2 = null;
		if(columnName.indexOf('#') > -1)
		{
			String[] columnsName = columnName.split("#");
			columnName1 = columnsName[0];
			columnName2 = columnsName[1];
		}else
		{
			columnName1 = columnName;
		}
		
		if (!definitions.getColumn(columnName1).isSortable())
		{
			throw new DataSourceExcpetion(messages.dataSourceErrorColumnNotComparable(columnName));
		}
		
		//optimization: infer column type only once
		if(array == null || array[0] == null)
		{
			return;
		}
		final boolean isStringColumn = getValue(columnName1, array[0]) instanceof String;
		final String finalColumnName2 = columnName2;
		
		final boolean isStringColumn2 = getValue(columnName2,array[0]) instanceof String;
		Arrays.sort(array, new Comparator<DataSourceRecord<E>>(){
			public int compare(DataSourceRecord<E> o1, DataSourceRecord<E> o2)
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
				
				Object value1 = getValue(columnName1, o1);
				Object value2 = getValue(columnName1, o2);

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

				int ret1 = compareNonNullValuesByType(value1,value2,ascending,caseSensitive, isStringColumn);
				
				if(ret1 != 0)
				{
					return ret1;
				}else if(finalColumnName2 != null)
				{
					
					Object value1col2 = getValue(finalColumnName2, o1);
					Object value2col2 = getValue(finalColumnName2, o2);

					if (ascending)
					{
						if (value1col2==null) return (value2col2==null?0:-1);
						if (value2col2==null) return 1;
					}
					else
					{
						if (value1col2==null) return (value2col2==null?0:1);
						if (value2col2==null) return -1;
					}
					
					return compareNonNullValuesByType(value1col2,value2col2,ascending,caseSensitive, value1col2 instanceof String);
				}else
				{
					return ret1;
				}
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			private int compareNonNullValuesByType(Object value1, Object value2, boolean ascending, boolean caseSensitive, boolean isStringColumns)
			{
				if(isStringColumns)
				{
					if (ascending)
					{
						return StringUtils.localeCompare((String)value1, (String)value2, caseSensitive);
					} else
					{
						return StringUtils.localeCompare((String)value2, (String)value1, caseSensitive);
					}
				}
				
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
			throw new DataSourceExcpetion(messages.dataSourceNotLoaded());
		}
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getValue(java.lang.String, org.cruxframework.crux.core.client.datasource.DataSourceRecord)
	 */
	@SuppressWarnings("unchecked")
    public Object getValue(String columnName, DataSourceRecord<?> dataSourceRecord)
	{
		ColumnDefinition<?, E> column = definitions.getColumn(columnName);
		if (column != null)
		{
			return dataSourceRecord != null ? column.getValue((E) dataSourceRecord.getRecordObject()) : null;
		}
		return null;
	}
	
	/**
	 * @return
	 * @deprecated Use getBoundObject instead
	 */
	@Deprecated
	@Legacy
	public E getBindedObject()
	{
		return getBindedObject(getRecord());
	}
	
	/**
	 * @param record
	 * @return
	 * @deprecated Use getBoundObject instead
	 */
	@Deprecated
	@Legacy
	public E getBindedObject(DataSourceRecord<E> record)
	{
		return getBoundObject(record);
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getBoundObject()
	 */
	public E getBoundObject()
	{
	    return getBoundObject(getRecord());
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getBoundObject(org.cruxframework.crux.core.client.datasource.DataSourceRecord)
	 */
	public E getBoundObject(DataSourceRecord<E> record)
	{
	    return null;
	}
}
