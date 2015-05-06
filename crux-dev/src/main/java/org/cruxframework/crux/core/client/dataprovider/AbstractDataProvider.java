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
	protected int currentRecord = -1;
	protected Array<DataProviderRecord<T>> data = CollectionFactory.createArray();
	protected Array<DataChangedHandler> dataChangedHandlers;
	protected DataProvider.DataHandler<T> dataHandler;
	protected Array<DataLoadedHandler> dataLoadedHandlers;
	protected Array<DataLoadStoppedHandler> dataStopLoadHandlers;
	protected boolean loaded = false;
	protected Array<TransactionEndHandler> transactionEndHandlers;
	protected Array<TransactionStartHandler> transactionStartHandlers;
	
	public AbstractDataProvider(DataHandler<T> handler)
    {
		this.dataHandler = handler;
    }
	
	@Override
	public HandlerRegistration addDataChangedHandler(final DataChangedHandler handler)
	{
		if (dataChangedHandlers == null)
		{
			dataChangedHandlers = CollectionFactory.createArray();
		}
		
		dataChangedHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = dataChangedHandlers.indexOf(handler);
				if (index >= 0)
				{
					dataChangedHandlers.remove(index);
				}
			}
		};
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
	public HandlerRegistration addTransactionEndHandler(final TransactionEndHandler handler)
	{
		if (transactionEndHandlers == null)
		{
			transactionEndHandlers = CollectionFactory.createArray();
		}
		
		transactionEndHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = transactionEndHandlers.indexOf(handler);
				if (index >= 0)
				{
					transactionEndHandlers.remove(index);
				}
			}
		};
	}

	@Override
	public HandlerRegistration addTransactionStartHandler(final TransactionStartHandler handler)
	{
		if (transactionStartHandlers == null)
		{
			transactionStartHandlers = CollectionFactory.createArray();
		}
		
		transactionStartHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = transactionStartHandlers.indexOf(handler);
				if (index >= 0)
				{
					transactionStartHandlers.remove(index);
				}
			}
		};
	}
	
	@Override
	public T get()
	{
		DataProviderRecord<T> record = getRecord();
		if(record != null)
		{
			dataHandler.clone(record.getRecordObject());
		}
		return null;
	}
	
	@Override
	public T get(int index)
	{
	    DataProviderRecord<T> record = data.get(index);
		if(record != null)
		{
			dataHandler.clone(record.getRecordObject());
		}
		return null;
	}
	
    @Override
    public boolean isLoaded()
    {
	    return loaded;
    }
	
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
	public void read(DataReader<T> reader)
	{
		if (reader != null)
		{
			DataProviderRecord<T> record = getRecord();
			if(record != null)
			{
				reader.read(record.getRecordObject());
			}
			else
			{
				reader.read(null);
			}
		}
	}
	
	@Override
	public void read(int index, DataReader<T> reader)
	{
		if (reader != null)
		{
		    DataProviderRecord<T> record = data.get(index);
			if (record != null)
			{
				reader.read(record.getRecordObject());
			}
			else
			{
				reader.read(null);
			}
		}
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
	
	protected void concludeEdition(boolean commited)
    {
		fireTransactionEndEvent(commited);
	}
	
	protected void fireDataChangedEvent(DataChangedEvent event)
    {
		if (dataChangedHandlers != null)
		{
			for (int i = 0; i< dataChangedHandlers.size(); i++)
			{
				dataChangedHandlers.get(i).onDataChanged(event);
			}
		}
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

	protected void fireTransactionEndEvent(boolean commited)
    {
		if (transactionEndHandlers != null)
		{
			TransactionEndEvent event = new TransactionEndEvent(this, commited);
			for (int i = 0; i< transactionEndHandlers.size(); i++)
			{
				transactionEndHandlers.get(i).onTransactionEnd(event);
			}
		}
    }
	
	protected void fireTransactionStartEvent(int firstRecordToLock)
    {
		if (transactionStartHandlers != null)
		{
			TransactionStartEvent event = new TransactionStartEvent(this, firstRecordToLock);
			for (int i = 0; i< transactionStartHandlers.size(); i++)
			{
				transactionStartHandlers.get(i).onTransactionStart(event);
			}
		}
    }
	
	protected void setLoaded()
	{
		loaded = true;
		fireLoadedEvent();
	}

	protected abstract void updateState(DataProviderRecord<T> record, DataProviderRecord.DataProviderRecordState previousState);

}