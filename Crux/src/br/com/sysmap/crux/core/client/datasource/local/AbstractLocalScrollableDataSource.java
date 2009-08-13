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

import java.util.Arrays;
import java.util.Comparator;

import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.ScrollableDataSource;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
abstract class AbstractLocalScrollableDataSource<T extends DataSourceRecord> extends AbstractLocalDataSource<T> implements ScrollableDataSource<T>
{		
	public boolean hasPreviousRecord()
	{
		if (ensureLoaded())
		{

			return (data != null && currentRecord > 0 && data.length > 0);
		}
		else
		{
			return false;
		}
	}

	public void previousRecord()
	{
		if (hasPreviousRecord())
		{
			currentRecord--;
		}
	}
	
	public void sort(final String columnName)
	{
		if (ensureLoaded() && data != null)
		{
			sortArray(data,columnName);
		}
	}

	protected void sortArray(DataSourceRecord[] array, final String columnName)
	{
		final int position = metadata.getColumnPosition(columnName);
		Arrays.sort(array, new Comparator<DataSourceRecord>(){
			public int compare(DataSourceRecord o1, DataSourceRecord o2)
			{
				if (o1==null) return (o2==null?0:-1);
				if (o2==null) return 1;

				Object value1 = o1.get(position);
				Object value2 = o2.get(position);

				if (value1==null) return (value2==null?0:-1);
				if (value2==null) return 1;

				return compareNonNullValuesByType(value1,value2);
			}

			@SuppressWarnings("unchecked")
			private int compareNonNullValuesByType(Object value1, Object value2)
			{
				return ((Comparable)value1).compareTo(value2);
			}
		});
		firstRecord();
	}
	
	public int getRowCount()
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
		if (ensureLoaded())
		{
			currentRecord = getRowCount()-1;
		}
	}
}
