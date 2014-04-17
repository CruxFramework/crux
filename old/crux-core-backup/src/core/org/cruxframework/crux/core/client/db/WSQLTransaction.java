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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.db.websql.SQLDatabase;
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;

import com.google.gwt.logging.client.LogConfiguration;

/**
 * A transaction on Crux Database, To create transactions, use one of {@link Database}'s getTransaction() method.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WSQLTransaction extends Transaction implements SQLDatabase.SQLTransactionCallback, SQLDatabase.SQLTransactionErrorCallback, SQLDatabase.SQLCallback
{
    private SQLTransaction transaction;
	protected final WSQLAbstractDatabase db;
	private RequestProcessor requestProcessor;
	private boolean aborted;
	private boolean active;
	
	protected WSQLTransaction(final WSQLAbstractDatabase db, String[] storeNames, Mode mode)
    {
		super(db, storeNames, mode);
		this.db = db;
		this.active = true;
		this.aborted = false;
		this.transaction = null;
		this.requestProcessor = new RequestProcessor(this);
	    if (db == null || !db.isOpen())
		{
			throw new DatabaseException(db.messages.databaseNotOpenedError());
		}
		
		if (mode != null && Mode.readOnly.equals(getMode()))
		{
			db.database.readTransaction(this, this, this);
		}
		else
		{
			db.database.transaction(this, this, this);
		}
    }

	public void onTransaction(SQLTransaction tx)
	{
		transaction = tx;
		requestProcessor.executeRequests();
	}
	
	public void onError(SQLError error)
	{
		onError(error.getName() + " - " + error.getMessage());
	}	
	
	public void onError(String errorMessage)
	{
		String message = db.messages.databaseTransactionError(db.getName(), errorMessage);
		active = false;
		requestProcessor.stop();
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.SEVERE, message);
		}
		if (transactionCallback != null)
		{
			transactionCallback.onError(message);
		}
	}
	
	public void onSuccess()
	{
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, db.messages.databaseTransactionCompleted(db.getName()));
		}
		active = false;
		requestProcessor.stop();
		if (transactionCallback != null)
		{
			try
			{
				transactionCallback.onComplete();
			}
			catch (Exception e) 
			{
				String message = db.messages.databaseTransactionError(db.getName(), e.getMessage());
				reportError(transactionCallback, message, e);
			}
		}
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
		if (!containsObjectStore(storeName))
		{
			onError(db.messages.databaseTransactionStoreNotFound(storeName));
			return null;
		}
		WSQLAbstractObjectStore<K, V> objectStore = db.getObjectStore(storeName, this);
		return objectStore;
	}

	/**
	 * Abort current transaction and rollback operations.
	 */
	@Override
	public void abort()
	{
		abort(true);
	}
	
	/**
	 * Abort current transaction and rollback operations.
	 * @param fireEvent
	 */
	public void abort(boolean fireEvent)
	{
        if (!aborted && active)
        {
        	if (transaction != null)
        	{
        		transaction.executeSQL("invalid sql statement", null, null, new SQLTransaction.SQLStatementErrorCallback()
        		{
        			@Override
        			public boolean onError(SQLTransaction tx, SQLError error)
        			{
        				return true;// tell web sql to rollback transaction
        			}
        		});
        		
        		transaction = null;
        	}
        	aborted = true;
        	active = false;
        	requestProcessor.stop();
        	if (fireEvent)
        	{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, db.messages.databaseTransactionAborted(db.getName()));
				}
				if (transactionCallback != null)
				{
					try
					{
						transactionCallback.onAbort();
					}
					catch (Exception e) 
					{
						String message = db.messages.databaseTransactionError(db.getName(), e.getMessage());
						reportError(transactionCallback, message, e);
					}
				}
        	}
        }
    }
	
	/**
	 * Retrieve a specialized object store for files handling.
	 * @return
	 */
	@Override
	public FileStore getFileStore()
	{
		if (!containsObjectStore(FileStore.OBJECT_STORE_NAME))
		{
			onError(db.messages.databaseTransactionStoreNotFound(FileStore.OBJECT_STORE_NAME));
			return null;
		}
//		return new WSQLFileStore(db, this);
		return null;//TODO terminar o fileStore
	}

	public void addRequest(RequestOperation operation, Mode[] supportedMode)
	{
		if (!active || aborted)
		{
			onError(db.messages.databaseTransactionInactive(db.getName()));
			return;
		}
		boolean supported = checkSupportedModes(supportedMode);
		if (!supported)
		{
			onError(db.messages.databaseTransactionNotSupportedOperation(db.getName()));
			return;
		}
		
		requestProcessor.addRequest(operation).executeRequests();
	}
	
	public static abstract class RequestOperation
	{
		public abstract void doOperation(SQLTransaction tx);
	}	
	
	private boolean checkSupportedModes(Mode[] supportedMode)
    {
	    boolean supported = false;
		for (Mode mode : supportedMode)
        {
			if (mode.equals(getMode()))
			{
				supported = true;
				break;
			}
        }
	    return supported;
    }

	static class RequestProcessor
	{
		private int currentOperationIdx = 0;
		private boolean processing = false;
		private Array<RequestOperation> requests;
		private WSQLTransaction wsqlTransaction;
		
		RequestProcessor(WSQLTransaction transaction)
		{
			requests = CollectionFactory.createArray();
			wsqlTransaction = transaction;
		}
		
		public void stop()
        {
			processing = false;
			requests.clear();
			wsqlTransaction = null;
        }

		public RequestProcessor addRequest(RequestOperation operation)
		{
			requests.add(operation);
			return this;
		}
		
		public void executeRequests()
		{
			if (!processing)
			{
				if (wsqlTransaction.transaction != null)
				{
					process();					
				} // else just wait for transaction creation, that will start request processor again.
			}
		}

		private void process()
		{
			if (processing)
			{
				return;
			}
			processing = true;
			while (currentOperationIdx < requests.size())
			{
				RequestOperation operation = requests.get(currentOperationIdx++);
				operation.doOperation(wsqlTransaction.transaction);
			}
			processing = false;
		}
	}
	
	static interface Callback
	{
		void onSuccess();
	}
}
