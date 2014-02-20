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

import com.google.gwt.core.client.JsArrayMixed;

/**
 * <p>Represents a Cursor into Crux Database. Cursors can be used to iterate over the result of a database query.</p>
 * <p>
 * To open a cursor, you must ask to an {@link ObjectStore} or an {@link Index} object. Both classes define an openCursor method. 
 * </p>
 * <p>
 * See the following example:
 * <pre>
 * {@link Transaction} transaction = database.getTransaction(new String[]{"MyObjectStore"}, Mode.readOnly);
 * {@link ObjectStore}{@code<Integer, MyObject>} store = transaction.getObjectStore("MyObjectStore");
 * store.openCursor(new {@link DatabaseCursorCallback}{@code <Integer, MyObject>}(){
 *    public void onSuccess({@code<Integer, MyObject>} result) {
 *      if (result != null && result.getValue() != null) {
 *         Window.alert("Object found. ID ["+result.getKey()+"]");
 *         continueCusror();
 *      }
 *    }
 * });
 * </pre>
 * </p>
 * 
 * @author Thiago da Rosa de Bustamante
 * @param <K> The type of the key used to identify objects into the cursor.
 * @param <V> The type of the objects referenced by this cursor 
 *
 */
public interface Cursor<K, V>
{
	/**
	 * Direction for the cursor. Define how to iterate over the results.
	 * @author Thiago da Rosa de Bustamante
	 */
	public static enum CursorDirection 
	{
		/**
		 * Cursor will iterate using natural order.
		 */
		next, 
		/**
		 * Cursor will iterate using natural order and skipping duplicated entries.
		 */
		nextunique, 
		/**
		 * Cursor will iterate using reverse order.
		 */
		prev, 
		/**
		 * Cursor will iterate using reverse order and skipping duplicated entries.
		 */
		prevunique 
	}
	/**
	 * Advance the cursor current position by a number of positions. Cursor callback onSuccess method will be called 
	 * again, receiving the new value where cursor points after the advance.
	 * @param count number of items to advance
	 */
	void advance(int count);
	/**
	 * Make cursor to go to the next element. Cursor callback onSuccess method will be called 
	 * again, receiving the new value where cursor points after the advance.
	 */
	void continueCursor();
	/**
	 * Make cursor to go to the element referenced by the given key. Cursor callback onSuccess method will be called 
	 * again, receiving the new value where cursor points after the advance.
	 * @param key key of the element to be pointed by the cursor.
	 */
	void continueCursor(K key);
	/**
	 * Delete the current value, pointed by the cursor, from the database.
	 */
	void delete();
	/**
	 * Return true if cursor points to a valid value.
	 * @return true if cursor points to a valid value
	 */
	boolean hasValue();
	/**
	 * Direction of iteration used by this cursor.
	 * @return cursor direction
	 */
	CursorDirection getDirection();
	/**
	 * Return the cursor key, as a native javascript object.
	 * @return cursor key.
	 */
	JsArrayMixed getNativeArrayKey();
	/**
	 * Update current record pointed by the cursor with the given value.
	 * @param value new value to update
	 */
	void update(V value);
	/**
	 * Return the key of record pointed by the cursor.
	 * @return key of the element pointed by the cursor
	 */
	K getKey();
	/**
	 * Return the value of record pointed by the cursor.
	 * @return value of the element pointed by the cursor
	 */
	V getValue();
}
