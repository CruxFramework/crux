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

import org.cruxframework.crux.core.client.utils.StringUtils;


/**
 * A transaction on Crux Database, To create transactions, use one of {@link Database}'s getTransaction() method.
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class Transaction extends DBObject
{
	/**
	 * Transaction mode.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum Mode{readWrite, readOnly}
	
	protected TransactionCallback transactionCallback;
	private final String[] storeNames;
	private final Mode mode;

	protected Transaction(final AbstractDatabase db, String[] storeNames, Mode mode)
    {
		super(db);
		this.storeNames = storeNames;
		this.mode = mode;
    }
	
	/**
	 * Retrieve the object store names associated with this transaction
	 * @return
	 */
	public String[] getStoreNames()
    {
    	return storeNames;
    }

	/**
	 * Retrieve the trnasction mode
	 * @return
	 */
	public Mode getMode()
    {
    	return mode;
    }

	/**
	 * Inform a callback to monitor the current transaction state changes.
	 * @param callback
	 */
	public void setTransactionCallback(TransactionCallback callback)
	{
		if (transactionCallback != null)
		{
			transactionCallback.setDb(null);
		}
		transactionCallback = callback;
		if (transactionCallback != null)
		{
			transactionCallback.setDb(db);
		}
	}
	
	/**
	 * Retrieve an ObjectStore manipulated by the current transaction.
	 * @param <K>
	 * @param <V>
	 * @param storeName
	 * @return
	 */
	public abstract <K, V> ObjectStore<K, V> getObjectStore(String storeName);

	/**
	 * Abort current transaction and rollback operations.
	 */
	public abstract void abort();
	
	/**
	 * Retrieve a specialized object store for files handling.
	 * @return
	 */
	public abstract FileStore getFileStore();
	
	/**
	 * A callback to monitor the current transaction state changes.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public abstract static class TransactionCallback extends Callback
	{
		/**
		 * Called when the transaction completes with success.
		 */
		public abstract void onComplete();

		/**
		 * Called if the transaction is aborted before completion (rolled back).
		 */
		public void onAbort()
		{
			if (db.errorHandler != null)
			{
				db.errorHandler.onError(db.messages.databaseTransactionAborted(db.getName()));
			}
		}
	}
	
	/**
	 * 
	 * @param storeName
	 * @return
	 */
	protected boolean containsObjectStore(String storeName)
	{
		for (String objectStore : storeNames)
        {
	        if (StringUtils.unsafeEquals(storeName, objectStore))
	        {
	        	return true;
	        }
        }
		return false;
	}
}
