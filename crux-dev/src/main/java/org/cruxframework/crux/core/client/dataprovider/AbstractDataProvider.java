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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractDataProvider<T> implements DataProvider<T>
{
	protected Array<DataLoadedHandler> dataLoadedHandlers;
	protected Array<DataLoadStoppedHandler> dataStopLoadHandlers;
	protected Array<DataProviderRecord<T>> data = CollectionFactory.createArray();
	protected int currentRecord = -1;
	protected boolean loaded = false;

	@Override
	public void next()
	{
		if (hasNext())
		{
			currentRecord++;
		}
	}
	
	@Override
	public void previous()
	{
		if (hasPrevious())
		{
			currentRecord--;
		}
	}

	@Override
	public HandlerRegistration addDataLoadedHandler(final DataLoadedHandler handler)
	{
		if (dataLoadedHandlers == null)
		{
			dataLoadedHandlers = CollectionFactory.createArray();
		}
		
		dataLoadedHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = dataLoadedHandlers.indexOf(handler);
				if (index >= 0)
				{
					dataLoadedHandlers.remove(index);
				}
			}
		};
	}
	
	@Override
	public HandlerRegistration addLoadStoppedHandler(final DataLoadStoppedHandler handler)
	{
		if (dataStopLoadHandlers == null)
		{
			dataStopLoadHandlers = CollectionFactory.createArray();
		}
		
		dataStopLoadHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = dataStopLoadHandlers.indexOf(handler);
				if (index >= 0)
				{
					dataStopLoadHandlers.remove(index);
				}
			}
		};
	}
	
	@Override
	public T get()
	{
	    DataProviderRecord<T> record = getRecord();
		return (record != null?record.getRecordObject():null);
	}
	
	@Override
	public DataProviderRecord<T> set(int index, T object)
	{
	    DataProviderRecord<T> record = data.get(index);
		if (record != null)
		{
			if (record.isReadOnly())
			{
				throw new DataProviderExcpetion("Can not update a read only information");//TODO i18n
			}
			record.set(object);
		}
		return record;
	}
	
	@Override
	public void reset()
	{
		if(data != null)
		{
			data = CollectionFactory.createArray();
		}
		currentRecord = -1;
		loaded = false;
	}

	@Override
	public void stopLoading()
	{
	    fireStopLoadEvent();
	}
	
	protected void setLoaded()
	{
		loaded = true;
		fireLoadedEvent();
	}
	
	@Override
    public boolean isLoaded()
    {
	    return loaded;
    }
	
	protected void fireLoadedEvent()
    {
		if (dataLoadedHandlers != null)
		{
			DataLoadedEvent event = new DataLoadedEvent(this);
			for (int i = 0; i< dataLoadedHandlers.size(); i++)
			{
				dataLoadedHandlers.get(i).onLoaded(event);
			}
		}
    }
	
	protected void fireStopLoadEvent()
    {
		if (dataStopLoadHandlers != null)
		{
			DataLoadStoppedEvent event = new DataLoadStoppedEvent(this);
			for (int i = 0; i< dataStopLoadHandlers.size(); i++)
			{
				dataStopLoadHandlers.get(i).onLoadStopped(event);
			}
		}
    }

	protected abstract void updateState(DataProviderRecord<T> record, DataProviderRecord.DataProviderRecordState previousState);

}
