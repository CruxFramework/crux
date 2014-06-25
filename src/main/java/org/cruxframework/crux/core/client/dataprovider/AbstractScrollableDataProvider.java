/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.dataprovider;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.cruxframework.crux.core.client.collection.Array;

/**
 * @author Thiago da Rosa de Bustamante
 */
abstract class AbstractScrollableDataProvider<T> implements MeasurableDataProvider<T>
{
	protected DataProviderRecord<T>[] data;
	protected int currentRecord = -1;
	protected boolean loaded = false;
		
	@Override
	public boolean hasNextRecord()
	{
		ensureLoaded();
		return (data != null && currentRecord < data.length -1);
	}

	@Override
	public void nextRecord()
	{
		if (hasNextRecord())
		{
			currentRecord++;
		}
	}

	@Override
	public void reset()
	{
		if(data != null)
		{
			data = null;
		}
		currentRecord = -1;
		loaded = false;
	}
	
	@Override
	public DataProviderRecord<T> getRecord()
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
	
	@Override
	public boolean hasPreviousRecord()
	{
		ensureLoaded();
		return (data != null && currentRecord > 0 && data.length > 0);
	}

	@Override
	public void previousRecord()
	{
		if (hasPreviousRecord())
		{
			currentRecord--;
		}
	}
	
	@Override
	public void sort(Comparator<T> comparator)
	{
		ensureLoaded();
		if (data != null)
		{
			sortArray(data, comparator);
		}
	}
	
	@Override
	public int getRecordCount()
	{
		return (data!=null?data.length:0);
	}
	
	@Override
	public void firstRecord()
	{
		currentRecord = -1;
		nextRecord();
	}
	
	@Override
	public void lastRecord()
	{
		ensureLoaded();
		currentRecord = getRecordCount()-1;
	}
	
	@Override
	public T getBoundObject()
	{
	    DataProviderRecord<T> record = getRecord();
		return (record != null?record.getRecordObject():null);
	}

	@SuppressWarnings("unchecked")
    @Override
	public void updateData(T[] data)
	{
		if (data == null)
		{
			update(new DataProviderRecord[0]);
		} 
		else 
		{
			DataProviderRecord<T>[] ret = new DataProviderRecord[data.length];
			for (int i=0; i<data.length; i++)
			{
				ret[i] = new DataProviderRecord<T>(this);
				ret[i].setRecordObject(data[i]);
			}
			update(ret);
		}
	}	
	
	@SuppressWarnings("unchecked")
    @Override
	public void updateData(List<T> data)
	{
		if (data == null)
		{
			update(new DataProviderRecord[0]);
		} 
		else 
		{
			DataProviderRecord<T>[] ret = new DataProviderRecord[data.size()];
			for (int i=0; i<data.size(); i++)
			{
				ret[i] = new DataProviderRecord<T>(this);
				ret[i].setRecordObject(data.get(i));
			}
			update(ret);
		}
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public void updateData(Array<T> data)
	{
		if (data == null)
		{
			update(new DataProviderRecord[0]);
		} 
		else 
		{
			DataProviderRecord<T>[] ret = new DataProviderRecord[data.size()];
			for (int i=0; i<data.size(); i++)
			{
				ret[i] = new DataProviderRecord<T>(this);
				ret[i].setRecordObject(data.get(i));
			}
			update(ret);
		}
	}

	protected void ensureLoaded()
	{
		if (!loaded)
		{
			throw new DataProviderExcpetion("Error processing requested operation. DataProvider is not loaded yet.");
		}
	}

	protected void sortArray(DataProviderRecord<T>[] array, final Comparator<T> comparator)
	{
		Arrays.sort(array, new Comparator<DataProviderRecord<T>>(){
			public int compare(DataProviderRecord<T> o1, DataProviderRecord<T> o2)
			{
				return comparator.compare(o1.getRecordObject(), o2.getRecordObject());
			}
		});
		firstRecord();
	}
	
	protected abstract void update(DataProviderRecord<T>[] records);
}
