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
import org.cruxframework.crux.core.client.dataprovider.FilterableProvider.FilterRegistration;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * An Helper class to implement common operations for different {@link DataProvider}s
 * @author Thiago da Rosa de Bustamante
 */
class DataProviderOperations<T>
{
	protected List<DataProviderRecord<T>> newRecords = new ArrayList<DataProviderRecord<T>>();
	protected List<DataProviderRecord<T>> removedRecords = new ArrayList<DataProviderRecord<T>>();
	protected List<DataProviderRecord<T>> changedRecords = new ArrayList<DataProviderRecord<T>>();
	protected List<DataProviderRecord<T>> selectedRecords = new ArrayList<DataProviderRecord<T>>();	

	protected AbstractScrollableDataProvider<T> dataProvider;
	protected Array<DataFilter<T>> filters;
	protected Array<DataProviderRecord<T>> initialData;
	protected Array<DataFilterHandler<T>> dataFilterHandlers;
	
	DataProviderOperations(AbstractScrollableDataProvider<T> dataProvider)
	{
		this.dataProvider = dataProvider;
	}

    DataProviderRecord<T> insertRecord(int index, T object)
	{
		this.dataProvider.ensureLoaded();
		checkRange(index);
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
		this.dataProvider.ensureLoaded();
		checkRange(index);
		DataProviderRecord<T> record = this.dataProvider.data.get(index);
		DataProviderRecordState previousState = record.getCurrentState();
		if (previousState.isReadOnly())
		{
			throw new DataProviderExcpetion("Can not update a read only information");//TODO i18n
		}
		record.setRemoved(true);
		this.dataProvider.data.remove(index);
	    if (hasFilters())
	    {
	    	index = initialData.indexOf(record);
	    	initialData.remove(index);
	    }
		updateState(record, previousState);
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
	
	/**
	 * @return
	 */
	int getNewRecordsCount()
	{
		return newRecords.size();
	}

	/**
	 * @return
	 */
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
			if(this.dataProvider.data.get(i) != null && this.dataProvider.data.get(i).recordObject.equals(boundObject))
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
	
	void rollback()
    {
	    // TODO Auto-generated method stub
	    
    }
	
	void commit()
    {
	    // TODO Auto-generated method stub
	    
    }
	
	boolean isDirty()
	{
		return (newRecords.size() > 0) || (removedRecords.size() > 0) || (changedRecords.size() > 0); 
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

	void removeFilters()
    {
		boolean forceDataFilterEvent = filters.size() > 0;
		filters.clear();
		applyAllFilters(forceDataFilterEvent);
    }
	
	void saveInitialData(Array<DataProviderRecord<T>> records)
	{
		this.initialData = records;
		dataProvider.setLoaded();
		applyAllFilters(false);
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
	
	private boolean hasFilters()
	{
		return filters != null && filters.size() > 0;
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

	private void checkRange(int index)
	{
		if (index < 0 || index >= this.dataProvider.data.size())
		{
			throw new IndexOutOfBoundsException();
		}
	}
}
