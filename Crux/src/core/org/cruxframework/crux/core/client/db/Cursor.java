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
 * @author Thiago da Rosa de Bustamante
 * @param <K> The type of the key used to identify objects into the cursor.
 * @param <V> The type of the objects referenced by this cursor 
 *
 */
public interface Cursor<K, V>
{
	/**
	 * Direction for the cursor
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum CursorDirection { next, nextunique, prev, prevunique }
	void advance(int count);
	void continueCursor();
	void delete();
	boolean hasValue();
	CursorDirection getDirection();
	JsArrayMixed getNativeArrayKey();
	void update(V value);
	K getKey();
	V getValue();
	void continueCursor(K key);
}
