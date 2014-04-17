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
 * Represents an ObjectStore on Crux Database. ObjectStores are collections of objects inserted into the Database.
 * @param <K> The type of the key used to identify objects into the store.
 * @param <V> The type of the objects stored into the store. You can inform JavaScriptObject 
 * to this type and insert any valid javascript object into the store. Different javascript objects can be inserted into the store.
 * @author Thiago da Rosa de Bustamante
 */
public interface ObjectStore<K, V>
{
	/**
	 * Retrive the name associated with the storage. It is the objectStore identifier.
	 * @return
	 */
	String getObjectStoreName();

	/**
	 * Insert the given object into the store.
	 * @param object
	 */
	void add(V object);

	/**
	 * Insert the given object into the store.
	 * @param object
	 * @param callback
	 */
	void add(V object, DatabaseWriteCallback<K> callback);
	
	/**
	 * Update the given object into the storage. If it does not exists, insert it. 
	 * @param object
	 */
	void put(V object);
	
	/**
	 * Update the given object into the storage. If it does not exists, insert it. 
	 * @param object
	 * @param callback
	 */
	void put(V object, DatabaseWriteCallback<K> callback);
	
	/**
	 * Retrieve the object associated with the given key from the store. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param key
	 * @param callback
	 */
	void get(K key, DatabaseRetrieveCallback<V> callback);
	
	/**
	 * Remove the object associated with the given key from the store.
	 * @param key
	 */
	void delete(K key);

	/**
	 * Remove the object associated with the given key from the store.
	 * @param key
	 * @param callback
	 */
	void delete(K key, DatabaseDeleteCallback callback);

	/**
	 * Remove all the objects in the given range from the store.
	 * @param keyRange
	 */
	void delete(KeyRange<K> keyRange);
	
	/**
	 * Remove all the objects in the given range from the store.
	 * @param keyRange
	 * @param callback
	 */
	void delete(KeyRange<K> keyRange, DatabaseDeleteCallback callback);
	
	/**
	 * Return the names of the indexes associated with this object store.
	 * @return
	 */
	String[] getIndexNames();

	/**
	 * Return true if this object store auto increments its keys.
	 * @return
	 */
	boolean isAutoIncrement();
	
	/**
	 * Clear this object store
	 * @param callback
	 */
	void clear(DatabaseCallback callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param callback
	 */
	void openCursor(DatabaseCursorCallback<K, V> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param callback
	 */
	void openCursor(KeyRange<K> keyRange, DatabaseCursorCallback<K, V> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange
	 * @param direction
	 * @param callback
	 */
	void openCursor(KeyRange<K> keyRange, CursorDirection direction, DatabaseCursorCallback<K, V> callback);
	
	/**
	 * Return the number of items stored into this object store.
	 * @param callback
	 */
	void count(DatabaseCountCallback callback);

	/**
	 * Return the number of items stored into this object store in the given range.
	 * @param range
	 * @param callback
	 */
	void count(KeyRange<K> range, DatabaseCountCallback callback);

	/**
	 * Retrieve the index for the given name
	 * @param name
	 * @return
	 */
	<I> Index<K, I, V> getIndex(String name);
	
	/**
	 * Retrieve a factory to create KeyRange objects used by this object store.
	 * @return
	 */
	KeyRangeFactory<K> getKeyRangeFactory();
}
