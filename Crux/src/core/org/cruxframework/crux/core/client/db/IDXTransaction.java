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

import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBTransaction;
import org.cruxframework.crux.core.client.db.indexeddb.IDBTransaction.IDBTransactionMode;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBAbortEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCompleteEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;

import com.google.gwt.logging.client.LogConfiguration;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * Indexed DB implementation for Transaction Interface. Use the interface {@link Transaction} instead. 
 * @author Thiago da Rosa de Bustamante
 */
public class IDXTransaction extends Transaction
{
	private IDBTransaction transaction;
	protected final IDXAbstractDatabase db;

	protected IDXTransaction(final IDXAbstractDatabase db, String[] storeNames, Mode mode)
    {
		super(db, storeNames, mode);
		this.db = db;
		IDBTransactionMode idbMode;
		switch (mode)
        {
        	case readWrite:
        		idbMode = IDBTransactionMode.readwrite;
        	break;
        	default:
        		idbMode = IDBTransactionMode.readonly;
        }
	    if (db == null || !db.isOpen())
		{
			throw new DatabaseException(db.messages.databaseNotOpenedError());
		}
				
		transaction = db.db.getTransaction(storeNames, idbMode);
		transaction.onAbort(new IDBAbortEvent.Handler()
		{
			@Override
			public void onAbort(IDBAbortEvent event)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, IDXTransaction.this.db.messages.databaseTransactionAborted(IDXTransaction.this.db.getName()));
				}
				if (transactionCallback != null)
				{
					try
					{
						transactionCallback.onAbort();
					}
					catch (Exception e) 
					{
						String message = IDXTransaction.this.db.messages.databaseTransactionError(IDXTransaction.this.db.getName(), e.getMessage());
						reportError(transactionCallback, message, e);
					}
				}
			}
		});
		transaction.onComplete(new IDBCompleteEvent.Handler()
		{
			@Override
			public void onComplete(IDBCompleteEvent event)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, IDXTransaction.this.db.messages.databaseTransactionCompleted(IDXTransaction.this.db.getName()));
				}
				if (transactionCallback != null)
				{
					try
					{
						transactionCallback.onComplete();
					}
					catch (Exception e) 
					{
						String message = IDXTransaction.this.db.messages.databaseTransactionError(IDXTransaction.this.db.getName(), e.getMessage());
						reportError(transactionCallback, message, e);
					}
						
				}
			}
		});
		transaction.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = IDXTransaction.this.db.messages.databaseTransactionError(IDXTransaction.this.db.getName(), event.getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (transactionCallback != null)
				{
					transactionCallback.onError(message);
				}
			}
		});
    }
	
	/**
	 * Retrieve an ObjectStore manipulated by the current transaction.
	 * @param <K>
	 * @param <V>
	 * @param storeName
	 * @return
	 */
	@Override
	public <K, V> ObjectStore<K, V> getObjectStore(String storeName)
	{
		IDBObjectStore idbObjectStore = transaction.getObjectStore(storeName);
		return db.getObjectStore(storeName, idbObjectStore);
	}

	/**
	 * Abort current transaction and rollback operations.
	 */
	@Override
	public void abort()
	{
		transaction.abort();
	}
	
	/**
	 * Retrieve a specialized object store for files handling.
	 * @return
	 */
	@Override
	public FileStore getFileStore()
	{
		IDBObjectStore idbObjectStore = transaction.getObjectStore(FileStore.OBJECT_STORE_NAME);
		return new IDXFileStore(db, idbObjectStore);
	}
}
