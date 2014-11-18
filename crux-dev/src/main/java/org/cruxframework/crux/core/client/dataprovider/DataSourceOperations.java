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
package org.cruxframework.crux.core.client.dataprovider;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord.DataProviderRecordState;



/**
 * @author Thiago da Rosa de Bustamante
 */
class DataProviderOperations<E>
{
	protected List<DataProviderRecord<E>> newRecords = new ArrayList<DataProviderRecord<E>>();
	protected List<DataProviderRecord<E>> removedRecords = new ArrayList<DataProviderRecord<E>>();
	protected List<DataProviderRecord<E>> changedRecords = new ArrayList<DataProviderRecord<E>>();
	protected List<DataProviderRecord<E>> selectedRecords = new ArrayList<DataProviderRecord<E>>();	

	protected AbstractScrollableDataProvider<E> dataProvider;
	
	public DataProviderOperations(AbstractScrollableDataProvider<E> dataProvider)
	{
		this.dataProvider = dataProvider;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#insertRecord(int)
	 */
    public DataProviderRecord<E> insertRecord(int index)
	{
		this.dataProvider.ensureLoaded();
		checkRange(index);
		DataProviderRecord<E> record = new DataProviderRecord<E>((DataProvider<E>)this.dataProvider);
		record.setCreated(true);
		insertDataRecord(index, record);
		newRecords.add(record);
		return record;
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#removeRecord(int)
	 */
	public DataProviderRecord<E> removeRecord(int index)
	{
		this.dataProvider.ensureLoaded();
		checkRange(index);
		DataProviderRecord<E> record = this.dataProvider.data[index];
		DataProviderRecordState previousState = record.getCurrentState();
		record.setRemoved(true);
		removeDataRecord(index);
		updateState(record, previousState);
		return record;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#updateState(org.cruxframework.crux.core.client.datasource.DataProviderRecord, org.cruxframework.crux.core.client.datasource.DataProviderRecord.DataProviderRecordState)
	 */
	public void updateState(DataProviderRecord<E> record, DataProviderRecordState previousState)
	{
		this.dataProvider.ensureLoaded();
		if (record.isCreated())
		{
			if (record.isRemoved())
			{
				newRecords.remove(record);
			}
		}
		else if (record.isRemoved())
		{
			if (!previousState.isRemoved())
			{
				removedRecords.add(record);
				if (previousState.isDirty())
				{
					changedRecords.remove(record);
				}
			}
		}
		else if (record.isDirty() && !previousState.isDirty())
		{
			changedRecords.add(record);
		}
		
		if (record.isSelected() && !previousState.isSelected())
		{
			selectedRecords.add(record);
		}
		else if (!record.isSelected() && previousState.isSelected())
		{
			selectedRecords.remove(record);
		}
	}
	
	/**
	 * @param index
	 * @param record
	 */
	protected void insertDataRecord(int index, DataProviderRecord<E> record)
	{
		int size = this.dataProvider.data.length;
		this.dataProvider.data = copyOf(this.dataProvider.data, size+1);
		System.arraycopy(this.dataProvider.data, index, this.dataProvider.data, index + 1, size - index);
		this.dataProvider.data[index] = record;
	}
	
	/**
	 * @param index
	 */
	protected void removeDataRecord(int index)
	{
		int size = this.dataProvider.data.length;
		int numMoved = size - index - 1;
		if (numMoved > 0)
		    System.arraycopy(this.dataProvider.data, index+1, this.dataProvider.data, index, numMoved);
		this.dataProvider.data = copyOf(this.dataProvider.data, size-1);
	}

	/**
	 * 
	 * @param original
	 * @param newLength
	 * @return
	 */
	@SuppressWarnings("unchecked")
    protected DataProviderRecord<E>[] copyOf(DataProviderRecord<E>[] original, int newLength)
	{
		DataProviderRecord<E>[] copy = new DataProviderRecord[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}
	
	/**
	 * @param index
	 */
	protected void checkRange(int index)
	{
		if (index < 0 || index >= this.dataProvider.data.length)
		{
			throw new IndexOutOfBoundsException();
		}
	}
	
	@SuppressWarnings("unchecked")
    public DataProviderRecord<E>[] getNewRecords()
	{
		return newRecords.toArray(new DataProviderRecord[0]);
	}

	@SuppressWarnings("unchecked")
    public DataProviderRecord<E>[] getRemovedRecords()
	{
		return removedRecords.toArray(new DataProviderRecord[0]);
	}
	
	/**
	 * @return
	 */
	public int getNewRecordsCount()
	{
		return newRecords.size();
	}

	/**
	 * @return
	 */
	public int getRemovedRecordsCount()
	{
		return removedRecords.size();
	}

	@SuppressWarnings("unchecked")
    public DataProviderRecord<E>[] getUpdatedRecords()
	{
		return changedRecords.toArray(new DataProviderRecord[0]);
	}
	
	@SuppressWarnings("unchecked")
    public DataProviderRecord<E>[] getSelectedRecords()
	{
		return selectedRecords.toArray(new DataProviderRecord[0]);
	}
	
	public int getRecordIndex(E boundObject)
	{
		for(int i = 0; i < this.dataProvider.data.length; i++)
		{
			if(this.dataProvider.data[i] != null && this.dataProvider.data[i].recordObject.equals(boundObject))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public void selectRecord(int index, boolean selected)
	{
		checkRange(index);
		this.dataProvider.data[index].setSelected(selected);
	}
	
	public void reset()
	{
		newRecords.clear();
		removedRecords.clear();
		changedRecords.clear();
		selectedRecords.clear();
	}
	
	public boolean isDirty()
	{
		return (newRecords.size() > 0) || (removedRecords.size() > 0) || (changedRecords.size() > 0); 
	}
}
