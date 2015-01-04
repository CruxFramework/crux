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

import java.util.Comparator;
import java.util.List;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord.DataProviderRecordState;

/**
 * @author Thiago da Rosa de Bustamante
 */
abstract class AbstractScrollableDataProvider<T> extends AbstractDataProvider<T> implements MeasurableProvider<T>
{
	protected DataProviderOperations<T> operations = new DataProviderOperations<T>(this);
		
	@Override
	public void rollback()
	{
		this.operations.rollback();
	}
	
	@Override
	public void commit()
	{
		this.operations.commit();
	}
	
	@Override
	public DataProviderRecord<T> select(int index, boolean selected)
	{
		return operations.selectRecord(index, selected);
	}

	@Override
	public DataProviderRecord<T> select(T object, boolean selected)
	{
		return operations.selectRecord(indexOf(object), selected);
	}
	
	@Override
	public DataProviderRecord<T> setReadOnly(int index, boolean readOnly)
	{
		return operations.setReadOnly(index, readOnly);
	}

	@Override
	public DataProviderRecord<T> setReadOnly(T object, boolean readOnly)
	{
		return operations.setReadOnly(indexOf(object), readOnly);
	}

	@Override
	public int indexOf(T boundObject)
	{
		return operations.getRecordIndex(boundObject);
	}

	@Override
	public DataProviderRecord<T>[] getNewRecords()
	{
		return operations.getNewRecords();
	}
	
	@Override
	public DataProviderRecord<T>[] getRemovedRecords()
	{
		return operations.getRemovedRecords();
	}	
	
	@Override
	public DataProviderRecord<T>[] getSelectedRecords()
	{
		return operations.getSelectedRecords();
	}	
	
	@Override
	public DataProviderRecord<T>[] getUpdatedRecords()
	{
		return operations.getUpdatedRecords();
	}

	@Override
	public DataProviderRecord<T> add(int beforeIndex, T object)
	{
		return operations.insertRecord(beforeIndex, object);
	}
	
	@Override
	public DataProviderRecord<T> add(T object)
	{
		return operations.insertRecord(object);
	}

	@Override
	public DataProviderRecord<T> remove(int index)
	{
		return operations.removeRecord(index);
	}

	@Override
	public void reset()
	{
		super.reset();
		operations.reset();
	}

	@Override
	protected void updateState(DataProviderRecord<T> record, DataProviderRecordState previousState)
	{
		operations.updateState(record, previousState);
	}
	
	@Override
	public boolean hasNext()
	{
		ensureLoaded();
		return (data != null && currentRecord < data.size() -1);
	}

	@Override
	public DataProviderRecord<T> getRecord()
	{
		ensureLoaded();
		if (currentRecord > -1)
		{
			return data.get(currentRecord);
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public boolean hasPrevious()
	{
		ensureLoaded();
		return (data != null && currentRecord > 0 && data.size() > 0);
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
	public int size()
	{
		return (data!=null?data.size():0);
	}
	
	@Override
	public void first()
	{
		setFirstPosition(true);
	}

	@Override
	public void last()
	{
		ensureLoaded();
		currentRecord = size()-1;
	}
	
    @Override
	public void setData(T[] data)
	{
		if (data == null)
		{
			Array<DataProviderRecord<T>> array = CollectionFactory.createArray(); 
			update(array);
		} 
		else 
		{
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(data.length); 
			for (int i=0; i<data.length; i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data[i]);
				ret.set(i, record);
			}
			update(ret);
		}
	}	
	
    @Override
	public void setData(List<T> data)
	{
		if (data == null)
		{
			Array<DataProviderRecord<T>> array = CollectionFactory.createArray(); 
			update(array);
		} 
		else 
		{
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(data.size());
			for (int i=0; i<data.size(); i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data.get(i));
				ret.set(i, record);
			}
			update(ret);
		}
	}
	
    @Override
	public void setData(Array<T> data)
	{
		if (data == null)
		{
			Array<DataProviderRecord<T>> array = CollectionFactory.createArray(); 
			update(array);
		} 
		else 
		{
			Array<DataProviderRecord<T>> ret = CollectionFactory.createArray(data.size());
			for (int i=0; i<data.size(); i++)
			{
				DataProviderRecord<T> record = new DataProviderRecord<T>(this);
				record.setRecordObject(data.get(i));
				ret.set(i, record);
			}
			update(ret);
		}
	}

	@Override
	public Array<T> filter(DataFilter<T> filter)
	{
	    return operations.filter(filter);
	}
    
	protected void setFirstPosition(boolean fireEvents)
	{
		currentRecord = -1;
		next();
	}
	
	protected void ensureLoaded()
	{
		if (!loaded)
		{
			throw new DataProviderExcpetion("Error processing requested operation. DataProvider is not loaded yet.");
		}
	}

	protected void sortArray(Array<DataProviderRecord<T>> array, final Comparator<T> comparator)
	{
		array.sort(new Comparator<DataProviderRecord<T>>(){
			public int compare(DataProviderRecord<T> o1, DataProviderRecord<T> o2)
			{
				return comparator.compare(o1.getRecordObject(), o2.getRecordObject());
			}
		});
		changePositionAfterSorting();
	}

	protected void changePositionAfterSorting()
    {
	    first();
    }
	
	protected abstract void update(Array<DataProviderRecord<T>> records);
}
