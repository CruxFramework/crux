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

/**
 * Represents an index into this database.
 * @param <K> object key type
 * @param <I> index key type. The type of the indexed column
 * @param <V> object type
 * @author Thiago da Rosa de Bustamante
 */
public abstract class WSQLIndex<K, I, V> extends Index<K, I, V> 
{
	protected final WSQLAbstractDatabase db;
	protected final WSQLTransaction transaction;

	protected WSQLIndex(WSQLAbstractDatabase db, WSQLTransaction transaction)
    {
		super(db);
		this.db = db;
		this.transaction = transaction;
    }

	/**
	 * Return the number of items referenced by the index.
	 * @param callback
	 */
	@Override
	public void count(final DatabaseCountCallback callback)
	{
		count(null, callback);
	}

	/**
	 * Return the number of items referenced by the index in the given range.
	 * @param range
	 * @param callback
	 */
	@Override
	public void count(KeyRange<I> range, final DatabaseCountCallback callback)
	{
		openCursor(range, new DatabaseCursorCallback<I, V>()
		{
			@SuppressWarnings("unchecked")
            @Override
            public void onSuccess(Cursor<I, V> result)
            {
				callback.onSuccess(((WSQLCursor<I, K, V>) result).size());
            }
		});
	}

	/**
	 * Retrieve the object associated with the given key from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param key
	 * @param callback
	 */
	public void get(I key, DatabaseRetrieveCallback<V> callback)
	{
		get(getKeyRangeFactory().only(key), callback);
	}
	
	/**
	 * Retrieve the object in the given keyRange from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param keyRange
	 * @param callback
	 */
	public void get(KeyRange<I> keyRange, final DatabaseRetrieveCallback<V> callback)
	{
		openCursor(keyRange, new DatabaseCursorCallback<I, V>()
		{
			@Override
            public void onSuccess(Cursor<I, V> result)
            {
				callback.onSuccess(result.getValue());
            }
		});
	}
	
	/**
	 * Retrieve the object key associated with the given key from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param key
	 * @param callback
	 */
	public void getKey(I key, DatabaseRetrieveCallback<K> callback)
	{
		getKey(getKeyRangeFactory().only(key), callback);
	}
	
	/**
	 * Retrieve the object key in the given keyRange from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param keyRange
	 * @param callback
	 */
	public void getKey(KeyRange<I> keyRange, final DatabaseRetrieveCallback<K> callback)
	{
		openCursor(keyRange, new DatabaseCursorCallback<I, V>()
		{
			@SuppressWarnings("unchecked")
            @Override
            public void onSuccess(Cursor<I, V> result)
            {
				callback.onSuccess(((WSQLCursor<I, K, V>) result).getPrimaryKey());
            }
		});
	}
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param callback
	 */
	public void openCursor(DatabaseCursorCallback<I, V> callback)
	{
		openCursor(null, CursorDirection.next, callback);
	}
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param callback
	 */
	public void openCursor(KeyRange<I> keyRange, DatabaseCursorCallback<I, V> callback)
	{
		openCursor(keyRange, CursorDirection.next, callback);
		
	}
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param callback
	 */
	public void openKeyCursor(DatabaseCursorCallback<I, K> callback)
	{
		openKeyCursor(null, CursorDirection.next, callback);
	}
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param callback
	 */
	public void openKeyCursor(KeyRange<I> keyRange, DatabaseCursorCallback<I, K> callback)
	{
		openKeyCursor(keyRange, CursorDirection.next, callback);
	}
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param direction
	 * @param callback
	 */
	public abstract void openCursor(KeyRange<I> keyRange, CursorDirection direction, DatabaseCursorCallback<I, V> callback);

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
}
