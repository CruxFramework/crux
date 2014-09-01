/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.client.db;

import java.util.logging.Level;

import org.cruxframework.crux.core.client.db.Transaction.Mode;
import org.cruxframework.crux.core.client.db.indexeddb.IDBDatabase;
import org.cruxframework.crux.core.client.db.indexeddb.IDBDeleteDBRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBFactory;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBOpenDBRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBBlockedEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBDatabaseDeleteEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBOpenedEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBUpgradeNeededEvent;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.logging.client.LogConfiguration;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * Indexed DB implementation for Database Interface. Use the interface {@link Database} to define your databases. 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class IDXAbstractDatabase extends AbstractDatabase 
{
	protected IDBDatabase db = null;

	@Override
	public void close()
	{
		if (isOpen())
		{
			db.close();
			db = null;
		}
	}
	
    @Override
	public void delete(final DatabaseCallback callback)
	{
		if (StringUtils.isEmpty(getName()))
		{
			throw new DatabaseException(messages.databaseInvalidNameDBError(getName()));
		}
		IDBDeleteDBRequest deleteDatabase = IDBFactory.get().deleteDatabase(getName());
		deleteDatabase.onSuccess(new IDBDatabaseDeleteEvent.Handler()
		{
			@Override
			public void onDelete(IDBDatabaseDeleteEvent event)
			{
				db = null;
				if (callback != null)
				{
					callback.onSuccess();
				}
			}
		});
		deleteDatabase.onBlocked(new IDBBlockedEvent.Handler()
		{
			@Override
			public void onBlocked(IDBBlockedEvent event)
			{
				String message = messages.databaseBlocked(getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
		});
		deleteDatabase.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = messages.databaseDeleteError(getName(), event.getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
		});
	}

    @Override
    public boolean isOpen()
    {
    	return db != null;
    }

    @Override
    public boolean isSupported()
    {
        return IDBFactory.isSupported();
    }
    
    @Override
    protected Transaction createTransaction(String[] storeNames, Mode mode)
    {
    	return new IDXTransaction(this, storeNames, mode);
    }
    
    @Override
	protected void doOpen(final DatabaseCallback callback)
    {
	    final IDBOpenDBRequest openDBRequest = IDBFactory.get().open(getName(), getVersion());
		openDBRequest.onSuccess(new IDBOpenedEvent.Handler()
		{
			@Override
			public void onSuccess(IDBOpenedEvent event)
			{
				db = event.getResult();
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, messages.databaseOpened(getName()));
				}
				if (callback != null)
				{
					callback.onSuccess();
				}
			}
		});
		
		openDBRequest.onBlocked(new IDBBlockedEvent.Handler()
		{
			@Override
			public void onBlocked(IDBBlockedEvent event)
			{
				String message = messages.databaseBlocked(getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
		});
		
		openDBRequest.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = messages.databaseOpenError(getName(), event.getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
		});
		openDBRequest.onUpgradeNeeded(new IDBUpgradeNeededEvent.Handler()
		{
			@Override
			public void onUpgradeNeeded(IDBUpgradeNeededEvent event)
			{
				db = event.getResult();
				try
				{
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.INFO, messages.databaseUpgrading(getName()));
					}
					updateDatabaseStructure(openDBRequest);
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.INFO, messages.databaseUpgraded(getName()));
					}
				}
				catch (RuntimeException e) 
				{
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.SEVERE, messages.databaseUpgradeError(getName(), e.getMessage()), e);
					}
					throw e;
				}
			}
		});
    }
    
	protected abstract void updateDatabaseStructure(IDBOpenDBRequest openDBRequest);
	protected abstract <K, V> ObjectStore<K, V> getObjectStore(String storeName, IDBObjectStore idbObjectStore);
}
