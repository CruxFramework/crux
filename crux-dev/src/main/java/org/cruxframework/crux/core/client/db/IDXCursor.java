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

import org.cruxframework.crux.core.client.db.indexeddb.IDBCursor.IDBCursorDirection;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursorWithValue;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * Indexed DB implementation for Cursor Interface. Use the interface {@link Cursor} instead. 
 * @author Thiago da Rosa de Bustamante
 * @param <K> The type of the key used to identify objects into the cursor.
 * @param <V> The type of the objects referenced by this cursor 
 *
 */
public abstract class IDXCursor<K, V> implements Cursor<K, V>
{
	protected final IDBCursorWithValue idbCursor;

	protected IDXCursor(IDBCursorWithValue idbCursor)
	{
		this.idbCursor = idbCursor;
	}

	@Override
	public void advance(int count)
	{
		idbCursor.advance(count);
	};
	
	@Override
	public void continueCursor()
	{
		idbCursor.continueCursor();
	}

	@Override
	public void delete()
	{
		idbCursor.delete();
	}

	public <T extends JavaScriptObject> T getNativeValue()
	{
		return idbCursor.getValue().cast();
	}
	
	@Override
	public boolean hasValue()
	{
		return idbCursor.getValue() != null;
	}
	
	@Override
	public CursorDirection getDirection()
	{
		switch (idbCursor.getDirection())
        {
        	case next:
        		return CursorDirection.next;
        	case nextunique:
    	        return CursorDirection.nextunique;
        	case prev:
    	        return CursorDirection.prev;
        	default:
    	        return CursorDirection.prevunique;
        }
	}
	
	public static IDBCursorDirection getNativeCursorDirection(CursorDirection direction)
	{
		switch (direction)
        {
        	case nextunique: return IDBCursorDirection.nextunique;
        	case prev: return IDBCursorDirection.prev;
        	case prevunique: return IDBCursorDirection.prevunique;
        	default: return IDBCursorDirection.next;
        }
	}
	
	public abstract JsArrayMixed getNativeArrayKey();
	public abstract void update(V value);
	public abstract K getKey();
	public abstract V getValue();
	public abstract void continueCursor(K key);
}
