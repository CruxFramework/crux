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

import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.indexeddb.IDBIndex;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectCountRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCountEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * Indexed DB implementation for Index Interface. Use the interface {@link Index} instead. 
 * @author Thiago da Rosa de Bustamante
 * @param <K> object key type
 * @param <I> index key type. The type of the indexed column
 * @param <V> object type
 * @author Thiago da Rosa de Bustamante
 */
public abstract class IDXIndex<K, I, V> extends Index<K, I, V> 
{
	protected final IDBIndex idbIndex;
	protected final IDXAbstractDatabase db;

	protected IDXIndex(IDXAbstractDatabase db, IDBIndex idbIndex)
    {
		super(db);
		this.db = db;
		this.idbIndex = idbIndex;
    }

	/**
	 * Retrieve the index name
	 * @return
	 */
	@Override
	public String getName()
	{
		return idbIndex.getName();
	}

	@Override
	public boolean isUnique()
	{
		return idbIndex.isUnique();
	}
	
	@Override
	public boolean isMultiEntry()
	{
		return idbIndex.isMultiEntry();
	}
	
	/**
	 * Return the number of items referenced by the index.
	 * @param callback
	 */
	@Override
	public void count(DatabaseCountCallback callback)
	{
		IDBObjectCountRequest countRequest = idbIndex.count();
		handleCountCallback(callback, countRequest);
	}

	/**
	 * Return the number of items referenced by the index in the given range.
	 * @param range
	 * @param callback
	 */
	@Override
	public void count(KeyRange<I> range, DatabaseCountCallback callback)
	{
		IDBObjectCountRequest countRequest = idbIndex.count(IDXKeyRange.getNativeKeyRange(range));
		handleCountCallback(callback, countRequest);
	}

	/**
	 * Retrieve the object associated with the given key from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param key
	 * @param callback
	 */
	public abstract void get(I key, DatabaseRetrieveCallback<V> callback);
	
	/**
	 * Retrieve the object in the given keyRange from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param keyRange
	 * @param callback
	 */
	public abstract void get(KeyRange<I> keyRange, DatabaseRetrieveCallback<V> callback);
	
	/**
	 * Retrieve the object key associated with the given key from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param key
	 * @param callback
	 */
	public abstract void getKey(I key, DatabaseRetrieveCallback<K> callback);
	
	/**
	 * Retrieve the object key in the given keyRange from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param keyRange
	 * @param callback
	 */
	public abstract void getKey(KeyRange<I> keyRange, DatabaseRetrieveCallback<K> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param callback
	 */
	public abstract void openCursor(DatabaseCursorCallback<I, V> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param callback
	 */
	public abstract void openCursor(KeyRange<I> keyRange, DatabaseCursorCallback<I, V> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param direction
	 * @param callback
	 */
	public abstract void openCursor(KeyRange<I> keyRange, CursorDirection direction, DatabaseCursorCallback<I, V> callback);

	/**
	 * Open a cursor to iterate over the object store.
	 * @param callback
	 */
	public abstract void openKeyCursor(DatabaseCursorCallback<I, K> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param callback
	 */
	public abstract void openKeyCursor(KeyRange<I> keyRange, DatabaseCursorCallback<I, K> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param direction
	 * @param callback
	 */
	public abstract void openKeyCursor(KeyRange<I> keyRange, CursorDirection direction, DatabaseCursorCallback<I, K> callback);
	
	/**
	 * Retrieve a factory to create KeyRange objects used by this index.
	 * @return
	 */	
	public abstract KeyRangeFactory<I> getKeyRangeFactory();

	private void handleCountCallback(final DatabaseCountCallback callback, IDBObjectCountRequest countRequest)
    {
		if (callback != null || db.errorHandler != null)
		{
			if (callback != null)
			{
				callback.setDb(db);
			}
			countRequest.onError(new IDBErrorEvent.Handler()
			{
				@Override
				public void onError(IDBErrorEvent event)
				{
					reportError(callback, db.messages.objectStoreCountError(event.getName()), null);
				}
			});
			countRequest.onSuccess(new IDBCountEvent.Handler()
			{

				@Override
				public void onSuccess(IDBCountEvent event)
				{
					if (callback != null)
					{
						try
						{
							callback.onSuccess(event.getCount());
							callback.setDb(null);
						}
						catch(Exception e)
						{
							reportError(callback, db.messages.objectStoreCountError(e.getMessage()), e);
						}
					}
				}
			});
		}
    }
}
