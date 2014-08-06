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

import java.util.logging.Logger;

import org.cruxframework.crux.core.client.db.Transaction.TransactionCallback;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * Base class for Crux databases. Use the interface Database to define your databases 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractDatabase implements Database
{
	protected static Logger logger = Logger.getLogger(AbstractDatabase.class.getName());
	protected DBMessages messages = GWT.create(DBMessages.class);
	protected DatabaseErrorHandler errorHandler;
	protected String name;
	protected int version;

	@Override
	public String getName()
	{
	    return name;
	}
	
	@Override
	public void setName(String newName) throws DatabaseException
	{
		if (isOpen())
		{
			throw new DatabaseException(messages.databaseSetPropertyOnOpenDBError(getName()));
		}
		this.name = newName;
	}
	
	@Override
	public int getVersion()
	{
	    return version;
	}
	
	@Override
	public void setVersion(int newVersion) throws DatabaseException
	{
		if (isOpen())
		{
			throw new DatabaseException(messages.databaseSetPropertyOnOpenDBError(getName()));
		}
		this.version = newVersion;
	}
	
    @Override
	public void open(final DatabaseCallback callback)
	{
		if (checkOpenPreConditions(callback))
		{
			doOpen(callback);
		}
	}

	@Override
	public Transaction getTransaction(String[] storeNames, Transaction.Mode mode)
	{
		return getTransaction(storeNames, mode, null);
	}

    @Override
	public Transaction getTransaction(String[] storeNames, Transaction.Mode mode, TransactionCallback callback)
	{
		Transaction transaction = createTransaction(storeNames, mode);
		transaction.setTransactionCallback(callback);
		return transaction;
	}


    @Override
	public <V> void add(V[] objects, String objectStoreName, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<?, V> objectStore = transaction.getObjectStore(objectStoreName);
    	for (V object : objects)
        {
    		objectStore.add(object);
        }
	}

    @Override
	public <V> void put(V[] objects, String objectStoreName, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<?, V> objectStore = transaction.getObjectStore(objectStoreName);
    	for (V object : objects)
        {
    		objectStore.put(object);
        }
	}

    @Override
    public <K, V> void get(K key, String objectStoreName, final DatabaseRetrieveCallback<V> callback)
    {
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readOnly);
    	ObjectStore<K, V> objectStore = transaction.getObjectStore(objectStoreName);
    	objectStore.get(key, callback);
    }

    @Override
    public <K> void delete(K key, String objectStoreName, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, ?> objectStore = transaction.getObjectStore(objectStoreName);
    	objectStore.delete(key);
	}
        
    @Override
    public <K> void delete(KeyRange<K> keys, String objectStoreName, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, ?> objectStore = transaction.getObjectStore(objectStoreName);
    	objectStore.delete(keys);
	}
    
    @Override
    public void setDefaultErrorHandler(DatabaseErrorHandler errorHandler)
    {
		this.errorHandler = errorHandler;
        
    }
    
	private boolean checkOpenPreConditions(final DatabaseCallback callback)
    {
	    if (StringUtils.isEmpty(getName()))
		{
			callback.onError(messages.databaseInvalidNameDBError(getName()));
		}
	    else if (isOpen())
		{
			callback.onError(messages.databaseIAlreadyOpenDBError(getName()));
		}
	    else if (!isSupported())
		{
			callback.onError(messages.databaseNotSupportedError());
		}
	    else
	    {
	    	return true;
	    }
	    return false;
    }

	private TransactionCallback getCallbackForWriteTransaction(final DatabaseCallback callback)
    {
		if (callback == null && errorHandler == null)
		{
			return null;
		}
	    return new TransactionCallback()
		{
			@Override
			public void onError(String message)
			{
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
			
			@Override
			public void onAbort()
			{
				if (callback != null)
				{
					callback.onError(messages.databaseTransactionAborted(getName()));
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(messages.databaseTransactionAborted(getName()));
				}
			}
			
			@Override
			public void onComplete()
			{
				if (callback != null)
				{
					callback.onSuccess();
				}
			}
		};
    }
	
	@Override
	public void useIndexedDB()
	{
	}

	@Override
	public void useWebSQL()
	{
	}
	
    protected abstract void doOpen(DatabaseCallback callback);
    protected abstract Transaction createTransaction(String[] storeNames, Transaction.Mode mode);
}
