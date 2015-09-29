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

	public AbstractScrollableDataProvider()
    {
    }
	
	public AbstractScrollableDataProvider(DataProvider.EditionDataHandler<T> handler)
    {
	    super(handler);
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
	public void commit()
	{
		this.operations.commit();
	}

	@Override
	public Array<T> filter(DataFilter<T> filter)
	{
	    return operations.filter(filter);
	}
	
	@Override
	public void first()
	{
		setFirstPosition(true);
	}

	@Override
	public DataProviderRecord<T>[] getNewRecords()
	{
		return operations.getNewRecords();
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
	public boolean hasNext()
	{
		ensureLoaded();
		return (data != null && currentRecord < data.size() -1);
	}	
	
	@Override
	public boolean hasPrevious()
	{
		ensureLoaded();
		return (data != null && currentRecord > 0 && data.size() > 0);
	}

	@Override
	public int indexOf(T boundObject)
	{
		return operations.getRecordIndex(boundObject);
	}
	
	@Override
	public boolean isDirty()
	{
	    return operations != null && operations.isDirty();
	}

	@Override
	public void last()
	{
		ensureLoaded();
		currentRecord = size()-1;
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
	public void rollback()
	{
		this.operations.rollback();
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
	public DataProviderRecord<T> set(int index, T object)
	{
		return operations.updateRecord(index, object);
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
	public int size()
	{
		return (data!=null?data.size():0);
	}
	
    @Override
	public void sort(Comparator<T> comparator)
	{
		ensureLoaded();
		ensureNotDirty();
		if (data != null)
		{
			sortArray(data, comparator, true);
			fireSortedEvent(false);			
		}
	}

	protected void changePositionAfterSorting()
    {
	    first();
    }
    
	protected void ensureLoaded()
	{
		if (!loaded)
		{
			throw new DataProviderException("Error processing requested operation. DataProvider is not loaded yet.");
		}
	}
	
	protected Array<DataProviderRecord<T>> getTransactionRecords()
	{
		int size = data.size();
		Array<DataProviderRecord<T>> transactionRecords = CollectionFactory.createArray(size);
		
		for(int i=0; i< size; i++)
		{
			transactionRecords.add(data.get(i).clone());
		}
		
		return transactionRecords;
	}

	protected int lockRecordForEdition(int recordIndex)
    {
		return 0;
    }

	protected void replaceTransactionData(Array<DataProviderRecord<T>> transactionRecords)
	{
		this.data = transactionRecords;
	}
	
	protected void setFirstPosition(boolean fireEvents)
	{
		currentRecord = -1;
		next();
	}
	
	protected void sortArray(Array<DataProviderRecord<T>> array, final Comparator<T> comparator, boolean changePosition)
	{
		array.sort(new Comparator<DataProviderRecord<T>>(){
			public int compare(DataProviderRecord<T> o1, DataProviderRecord<T> o2)
			{
				return comparator.compare(o1.getRecordObject(), o2.getRecordObject());
			}
		});
		if (changePosition)
		{
			changePositionAfterSorting();
		}
	}
	
	protected abstract void update(Array<DataProviderRecord<T>> records);

	@Override
	protected void updateState(DataProviderRecord<T> record, DataProviderRecordState previousState)
	{
		operations.updateState(record, previousState);
	}
}
