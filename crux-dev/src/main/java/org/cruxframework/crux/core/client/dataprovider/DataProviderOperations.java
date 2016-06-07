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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord.DataProviderRecordState;
import org.cruxframework.crux.core.client.dataprovider.FilterableProvider.FilterRegistration;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * An Helper class to implement common operations for different {@link DataProvider}s
 * @author Thiago da Rosa de Bustamante
 */
class DataProviderOperations<T>
{
	protected List<DataProviderRecord<T>> changedRecords = new ArrayList<DataProviderRecord<T>>();
	protected Array<DataFilterHandler<T>> dataFilterHandlers;
	protected AbstractScrollableDataProvider<T> dataProvider;
	protected Array<DataFilter<T>> filters;	
	protected Array<DataProviderRecord<T>> initialData;

	protected List<DataProviderRecord<T>> newRecords = new ArrayList<DataProviderRecord<T>>();
	protected Set<DataProviderRecord<T>> readOnlyRecords = new HashSet<DataProviderRecord<T>>();
	protected List<DataProviderRecord<T>> removedRecords = new ArrayList<DataProviderRecord<T>>();
	protected List<DataProviderRecord<T>> selectedRecords = new ArrayList<DataProviderRecord<T>>();
	
	protected Array<DataProviderRecord<T>> transactionOriginalData = null;
	
	DataProviderOperations(AbstractScrollableDataProvider<T> dataProvider)
	{
		this.dataProvider = dataProvider;
	}

    HandlerRegistration addDataFilterHandler(final DataFilterHandler<T> handler)
	{
		if (dataFilterHandlers == null)
		{
			dataFilterHandlers = CollectionFactory.createArray();
		}
		
		dataFilterHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = dataFilterHandlers.indexOf(handler);
				if (index >= 0)
				{
					dataFilterHandlers.remove(index);
				}
			}
		};
	}
	
    FilterRegistration<T> addFilter(final DataFilter<T> filter)
	{
		assert(this.dataProvider instanceof FilterableProvider) : "Invalid operation. Can not add filters on not FilterableDataProvider";
		if (filters == null)
		{
			filters = CollectionFactory.createArray();
		}
		filters.add(filter);
		applyFilter(filter);
	    return new FilterRegistration<T>()
		{
			private DataFilter<T> bindFilter = filter;
	    	
	    	@Override
			public void remove()
			{
				int index = filters.indexOf(bindFilter);
				if (index >= 0)
				{
					filters.remove(index);
					applyAllFilters(true);
				}
			}
			
			@Override
			public void replace(DataFilter<T> newFilter, boolean incrementalFiltering)
			{
				int index = filters.indexOf(bindFilter);
				if (index >= 0)
				{
					filters.remove(index);
					filters.insert(index, newFilter);
					bindFilter = newFilter;
					if (incrementalFiltering)
					{
						applyFilter(newFilter);
					}
					else
					{
						applyAllFilters(true);
					}
				}
			}
		};
	}
    
	void beginTransaction(int recordIndex)
	{
		if(transactionOriginalData == null)
		{
			int firstRecordToLock = dataProvider.lockRecordForEdition(recordIndex);
			this.transactionOriginalData = dataProvider.getTransactionRecords();
			dataProvider.fireTransactionStartEvent(firstRecordToLock);
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
		
		dataProvider.concludeEdition(true);
		endTransaction();
    }

	Array<T> filter(DataFilter<T> filter)
	{
		Array<T> result = CollectionFactory.createArray();
		
		Array<DataProviderRecord<T>> toSearch = (initialData != null? initialData : dataProvider.data);
		if (toSearch != null)
		{
			int size = toSearch.size();
			for (int i = 0; i < size; i++)
			{
				DataProviderRecord<T> dataProviderRecord = toSearch.get(i);
				if (dataProviderRecord != null)
				{
					T object = dataProviderRecord.getRecordObject();
					if (filter.accept(object))
					{
						result.add(object);
					}
				}
			}
		}
		
		return result;
	}

	Array<T> getData()
	{
	    if (dataProvider.data != null)
	    {
	    	int size = dataProvider.data.size();
			Array<T> allData = CollectionFactory.createArray(size);
	    	for (int i = 0; i < size; i++)
	    	{
	    		allData.add(dataProvider.data.get(i).getRecordObject());
	    	}
	    	
	    	return allData;
	    }
	    return null;
	}

	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getNewRecords()
	{
		return newRecords.toArray(new DataProviderRecord[newRecords.size()]);
	}
	
	/**
	 * @return
	 */
	int getNewRecordsCount()
	{
		return newRecords.size();
	}
	
	
	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getReadOnlyRecords()
	{
		return readOnlyRecords.toArray(new DataProviderRecord[readOnlyRecords.size()]);
	}

	int getRecordIndex(T boundObject)
	{
		for(int i = 0; i < this.dataProvider.data.size(); i++)
		{
			if(this.dataProvider.data.get(i) != null && this.dataProvider.data.get(i).recordObject.equals(boundObject))
			{
				return i;
			}
		}
		
		return -1;
	}

	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getRemovedRecords()
	{
		return removedRecords.toArray(new DataProviderRecord[removedRecords.size()]);
	}
	
	/**
	 * @return
	 */
	int getRemovedRecordsCount()
	{
		return removedRecords.size();
	}
	
	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getSelectedRecords()
	{
		return selectedRecords.toArray(new DataProviderRecord[selectedRecords.size()]);
	}
	
	@SuppressWarnings("unchecked")
    DataProviderRecord<T>[] getUpdatedRecords()
	{
		return changedRecords.toArray(new DataProviderRecord[changedRecords.size()]);
	}
	
	DataProviderRecord<T> insertRecord(int index, T object)
	{
		this.dataProvider.ensureLoaded();
		
		checkRange(index, true);
		beginTransaction(index);
		DataProviderRecord<T> record = new DataProviderRecord<T>(this.dataProvider);
		record.setCreated(true);
		record.set(object);
		this.dataProvider.data.insert(index, record);
		newRecords.add(record);
	    if (hasFilters())
	    {
	    	index = initialData.indexOf(this.dataProvider.data.get(index+1));
	    	initialData.insert(index, record);
	    }

	    this.dataProvider.fireDataChangedEvent(record, index);
		return record;
	}
	
	DataProviderRecord<T> insertRecord(T object)
	{
    	int index = this.dataProvider.data.size();
    	return insertRecord(index, object);
	}
	
	boolean isDirty()
	{
		return (newRecords.size() > 0) || (removedRecords.size() > 0) || (changedRecords.size() > 0); 
	}
	
	void removeFilters()
    {
		boolean forceDataFilterEvent = filters.size() > 0;
		filters.clear();
		applyAllFilters(forceDataFilterEvent);
    }
	
	DataProviderRecord<T> removeRecord(int index)
	{
		this.dataProvider.ensureLoaded();

		checkRange(index, false);
		beginTransaction(index);
		DataProviderRecord<T> record = this.dataProvider.data.get(index);
		DataProviderRecordState previousState = record.getCurrentState();
		if (previousState.isReadOnly())
		{
			throw new DataProviderException("Can not update a read only information");//TODO i18n
		}
		record.setRemoved(true);
		this.dataProvider.data.remove(index);
	    if (hasFilters())
	    {
	    	index = initialData.indexOf(record);
	    	initialData.remove(index);
	    }
		updateState(record, previousState);
		this.dataProvider.fireDataChangedEvent(record, index);
		return record;
	}
	
	void reset()
	{
		newRecords.clear();
		removedRecords.clear();
		changedRecords.clear();
		selectedRecords.clear();
		readOnlyRecords.clear();
		transactionOriginalData = null;
	}

	void rollback()
    {
	    if(transactionOriginalData != null)
	    {
			dataProvider.concludeEdition(false);
			dataProvider.replaceTransactionData(this.transactionOriginalData);
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					dataProvider.unselectAllRecords();
				}
			});
			endTransaction();
	    }
    }
	
	void saveInitialData(Array<DataProviderRecord<T>> records)
	{
		this.initialData = records;
		dataProvider.setLoaded();
		applyAllFilters(false);
	}
	
	void selectAllRecords(boolean selected)
	{
		Array<DataProviderRecord<T>> changedRecords = CollectionFactory.createArray();;

		for(int i = 0; i < this.dataProvider.data.size(); i++)
		{
			DataProviderRecord<T> record = this.dataProvider.data.get(i);
			if(record != null && record.isSelected() != selected)
			{
				changedRecords.add(record);
				record.state.setSelected(selected);
				if (selected)
				{
					selectedRecords.add(record);
				}
				else
				{
					selectedRecords.remove(record);
				}
			}
		}

		if (changedRecords.size() > 0)
		{
			dataProvider.fireDataSelectionEvent(changedRecords);
		}
    }
	
	DataProviderRecord<T> selectRecord(int index, boolean selected, boolean fireEvents)
	{
		checkRange(index, false);
		DataProviderRecord<T> record = this.dataProvider.data.get(index);
		record.setSelected(selected, fireEvents);
		return record;
	}
	
	DataProviderRecord<T> setReadOnly(int index, boolean readOnly)
	{
		checkRange(index, false);
		DataProviderRecord<T> record = this.dataProvider.data.get(index);
		record.setReadOnly(readOnly);
		readOnlyRecords.add(record);
		return record;
	}
	
	DataProviderRecord<T> updateRecord(int index, T object)
	{
		this.dataProvider.ensureLoaded();

		checkRange(index, false);
		beginTransaction(index);
		DataProviderRecord<T> record = this.dataProvider.data.get(index);
		if (record != null)
		{
			if (record.isReadOnly())
			{
				throw new DataProviderException("Can not update a read only information");//TODO i18n
			}
			record.set(object);
		}
		this.dataProvider.fireDataChangedEvent(record, index);
		return record;
	}

	void updateState(DataProviderRecord<T> record, DataProviderRecordState previousState)
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
				if (previousState.isSelected())
				{
					selectedRecords.remove(record);
				}
			}
		}
		else if (record.isDirty() && !previousState.isDirty())
		{
			changedRecords.add(record);
		}
		
		if (record.isSelected() && !previousState.isSelected() && !record.isRemoved())
		{
			selectedRecords.add(record);
		}
		else if (!record.isSelected() && previousState.isSelected())
		{
			selectedRecords.remove(record);
		}
	}
	
	private void applyAllFilters(boolean forceDataFilterEvent)
	{
		if (initialData != null)
		{
			if (hasFilters())
			{
				Array<DataProviderRecord<T>> array = CollectionFactory.createArray();
				int size = initialData.size();
				int filtersSize = filters.size();
				for (int i = 0; i < size; i++)
				{
					DataProviderRecord<T> record = initialData.get(i);
					T recordObject = record.getRecordObject();
					if (checkFilter(filtersSize, record, recordObject))
					{
						array.add(record);
					}
				}
				dataProvider.data = array;
			}
			else
			{
				dataProvider.data = initialData;
			}
			dataProvider.setFirstPosition(false);
			if (forceDataFilterEvent || hasFilters())
			{
				fireDataFilterEvent();
			}
		}
	}
	
	private void applyFilter(final DataFilter<T> filter)
    {
		Array<DataProviderRecord<T>> array = CollectionFactory.createArray();
		Array<DataProviderRecord<T>> data = dataProvider.data;
		if (data != null && data.size() > 0)
		{
			int size = data.size();
			for (int i = 0; i < size; i++)
			{
				DataProviderRecord<T> record = data.get(i);
				if (filter.accept(record.getRecordObject()))
				{
					array.add(record);
				}
			}
			dataProvider.data = array;
			dataProvider.setFirstPosition(false);
			fireDataFilterEvent();
		}
    }

	private boolean checkFilter(int filtersSize, DataProviderRecord<T> record, T recordObject)
    {
	    for (int f = 0; f < filtersSize; f ++)
	    {
	    	if (!filters.get(f).accept(recordObject))
	    	{
	    		return false;
	    	}
	    }
	    return true;
    }
	
	private void checkRange(int index, boolean mayExpand)
	{
		if (index < 0 || index > this.dataProvider.data.size())
		{
			throw new IndexOutOfBoundsException();
		}
		if (!mayExpand && index == this.dataProvider.data.size())
		{
			throw new IndexOutOfBoundsException();
		}
	}
	
	private void endTransaction()
	{
		newRecords.clear();
		removedRecords.clear();
		changedRecords.clear(); 

		this.transactionOriginalData = null;
	}
	
	@SuppressWarnings("unchecked")
	private void fireDataFilterEvent()
    {
		if (dataFilterHandlers != null)
		{
			DataFilterEvent<T> event = new DataFilterEvent<T>((FilterableProvider<T>) dataProvider);
			for (int i = 0; i< dataFilterHandlers.size(); i++)
			{
				dataFilterHandlers.get(i).onFiltered(event);
			}
		}
    }
	
	private boolean hasFilters()
	{
		return filters != null && filters.size() > 0;
	}
}
