/*
 * Copyright 2009 Google Inc.
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
package org.cruxframework.crux.core.client.collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * The root Map type that provides read-access to a dictionary that might still
 * be mutable by another actor.
 * 
 * See {@link MutableMap} for a description of the role
 * {@link Map#adapt(Object)} plays in the behavior of this class.
 * 
 * @param <V>
 *            the type of values stored in the Map
 */
public class Map<V> extends JavaScriptObject
{

	protected Map()
	{
	}

	/**
	 * Removes all entries from this map.
	 */
	public final void clear()
	{
		jsniClear();
	}

	/**
	 * Determines if a key is in the set of keys contained in the map. {@code
	 * key} can take any value that allow {@link Map#adapt(Object)} to
	 * successfully complete execution.
	 * 
	 * @param key
	 *            to use for testing membership
	 * @return {@code true} if the key is contained in the map
	 */
	public final boolean containsKey(String key)
	{
		return key != null && jsniContainsKey(key);
	}

	/**
	 * Get a value indexed by a key.
	 * 
	 * Notice that if the Map contains {@code null} values, a returned {@code
	 * null} value does not guarantee that there is no such mapping. Use {@code
	 * containsKey(K)} to determine key membership. {@code key} can take any
	 * value that allow {@link Map#adapt(Object)} to successfully complete
	 * execution.
	 * 
	 * @param key
	 *            index to use for retrieval
	 * @return value associated to the key or {@code null} otherwise
	 */
	public final V get(String key)
	{
		return key == null ? null : jsniGet(key);
	}

	/**
	 * Tests whether this Map contains any element.
	 * 
	 * @return {@code true} if the map contains no entries
	 */
	public final native boolean isEmpty() /*-{
		for (var k in this) {
			return false;
		}
		return true;
	}-*/;

	/**
	 * @return
	 */
	public final Array<String> keys()
	{
		if (!GWT.isScript())
		{
			Array<String> jsniKeys = jsniKeys();
			int index = jsniKeys.indexOf("__gwt_ObjectId");
			if (index >= 0)
			{
				jsniKeys.remove(index);
			}
			return jsniKeys;
		}
		else
		{
			return jsniKeys();
		}
	}
	
	private native Array<String> jsniKeys()/*-{
		var result = Array();
		for (var k in this) {
			result.push(k);
		}
		return result;
	}-*/;
	
	
	/**
	 * Put the value in the map at the given key. {@code key} must be a value
	 * accepted by the underlying adapter; that is, a call to {@code
	 * adapt(element)} produces a non-null result.
	 * 
	 * @param key
	 *            index to the value
	 * @param value
	 *            value to be stored
	 */
	public final void put(String key, V value)
	{
		assert key != null : "Unsupported value";
		jsniPut(key, value);
	}

	/**
	 * Deletes a key-value entry if the key is a member of the key set. {@code
	 * key} must be such that a call to {@code adapt(element)} successfully
	 * completes.
	 * 
	 * @param key
	 *            index to the key-value
	 */
	public final void remove(String key)
	{
		if (key != null)
		{
			jsniRemove(key);
		}
	}

	private native void jsniClear() /*-{
		for (k in this) {
			delete this[k];
		}
	}-*/;

	private native boolean jsniContainsKey(String index) /*-{
		return this[index] !== undefined;
	}-*/;

	private native V jsniGet(String index) /*-{
		return this[index];
	}-*/;

	private native void jsniPut(String index, V value) /*-{
		this[index] = value;
	}-*/;

	private native void jsniRemove(String index) /*-{
		delete this[index];
	}-*/;
}
