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

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord.DataProviderRecordState;

/**
 * @author Thiago da Rosa de Bustamante
 */
class StreamingDataProviderOperations<T>
{
	protected List<DataProviderRecord<T>> newRecords = new ArrayList<DataProviderRecord<T>>();
	protected List<DataProviderRecord<T>> removedRecords = new ArrayList<DataProviderRecord<T>>();
	protected List<DataProviderRecord<T>> changedRecords = new ArrayList<DataProviderRecord<T>>();
	protected List<DataProviderRecord<T>> selectedRecords = new ArrayList<DataProviderRecord<T>>();	

	protected StreamingDataProvider<T> dataProvider;
	
	protected Array<DataProviderRecord<T>> originalData = null;
	
	StreamingDataProviderOperations(StreamingDataProvider<T> dataProvider)
	{
		this.dataProvider = dataProvider;
	}
    
    DataProviderRecord<T> insertRecord(int index, T object)
	{
    	beginTransaction();
		this.dataProvider.ensureCurrentPageLoaded();
		checkRange(index);
		DataProviderRecord<T> record = new DataProviderRecord<T>(this.dataProvider);
		record.setCreated(true);
		record.set(object);
		this.dataProvider.data.insert(index, record);
		newRecords.add(record);
		this.dataProvider.fireDataChangedEvent(new DataChangedEvent(this.dataProvider, record));
		return record;
	}
	
    DataProviderRecord<T> insertRecord(T object)
	{

    	int index = this.dataProvider.data.size()-1;
    	if (index < 0)
    	{
    		index = 0;
    	}
    	return insertRecord(index, object);
	}
    
	DataProviderRecord<T> removeRecord(int index)
	{
		beginTransaction();
		this.dataProvider.ensureCurrentPageLoaded();
		checkRange(index);
		DataProviderRecord<T> record = this.dataProvider.data.get(index);
		DataProviderRecordState previousState = record.getCurrentState();
		if (previousState.isReadOnly())
		{
			throw new DataProviderExcpetion("Can not update a read only information");//TODO i18n
		}
		record.setRemoved(true);
		this.dataProvider.data.remove(index);
		updateState(record, previousState);
		this.dataProvider.fireDataChangedEvent(new DataChangedEvent(this.dataProvider, record));
		return record;
	}

	void updateState(DataProviderRecord<T> record, DataProviderRecordState previousState)
	{
		this.dataProvider.ensureCurrentPageLoaded();
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

	void rollback()
    {
		if(originalData != null)
	    {
	    	this.dataProvider.data = this.originalData;
	    }
    }
	
	void commit()
    {
		for(DataProviderRecord<T> newRecord : newRecords)
		{
			newRecord.setCreated(false);
			newRecord.setDirty(false);
		}
		
		for(DataProviderRecord<T> changedRecord : changedRecords)
		{
			changedRecord.setCreated(false);
			changedRecord.setDirty(false);
		}
	
		newRecords.clear();
		removedRecords.clear();
		changedRecords.clear(); 
		endTransaction();	    
    }
	
	void beginTransaction()
	{
		originalData = this.dataProvider.data.clone();
	}
	
	void endTransaction()
	{
		originalData = null;
	}
	
	Array<DataProviderRecord<T>> getCurrentPageRecords(boolean cleanRecordsChanges)
	{
		Array<DataProviderRecord<T>> currentPageRecordsArray = CollectionFactory.createArray();
		int start = this.dataProvider.getPageStartRecord();
		int end = this.dataProvider.getPageEndRecord(); 
		for (int i = start; i <= end; i++)
    	{
    		DataProviderRecord<T> record = dataProvider.data.get(i);
    		currentPageRecordsArray.add(record);
    		
    		if(cleanRecordsChanges)
    		{
    			record.setCreated(false);
				record.setDirty(false);
    		}
    	}	
		
		return currentPageRecordsArray;
	}
	
	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getNewRecords()
	{
		return newRecords.toArray(new DataProviderRecord[0]);
	}

	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getRemovedRecords()
	{
		return removedRecords.toArray(new DataProviderRecord[0]);
	}
	
	int getNewRecordsCount()
	{
		return newRecords.size();
	}

	int getRemovedRecordsCount()
	{
		return removedRecords.size();
	}

	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getUpdatedRecords()
	{
		return changedRecords.toArray(new DataProviderRecord[0]);
	}
	
	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getSelectedRecords()
	{
		return selectedRecords.toArray(new DataProviderRecord[0]);
	}
	
	int getRecordIndex(T boundObject)
	{
		for(int i = 0; i < this.dataProvider.data.size(); i++)
		{
			if(this.dataProvider.data.get(i).recordObject.equals(boundObject))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	DataProviderRecord<T> selectRecord(int index, boolean selected)
	{
		checkRange(index);
		DataProviderRecord<T> record = this.dataProvider.data.get(index);
		record.setSelected(selected);
		return record;
	}
	
	DataProviderRecord<T> setReadOnly(int index, boolean readOnly)
    {
		checkRange(index);
		DataProviderRecord<T> record = this.dataProvider.data.get(index);
		record.setReadOnly(readOnly);
		return record;
    }
	
	void reset()
	{
		newRecords.clear();
		removedRecords.clear();
		changedRecords.clear();
		selectedRecords.clear();
	}
	
	boolean isDirty()
	{
		return (newRecords.size() > 0) || (removedRecords.size() > 0) || (changedRecords.size() > 0); 
	}
	
	private void checkRange(int index)
	{
		if (index < 0 || index >= this.dataProvider.data.size())
		{
			throw new IndexOutOfBoundsException();
		}
	}
}
