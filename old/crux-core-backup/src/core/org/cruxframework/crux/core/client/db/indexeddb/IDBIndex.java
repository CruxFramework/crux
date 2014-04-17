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
package org.cruxframework.crux.core.client.db.indexeddb;

import java.util.Date;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursor.IDBCursorDirection;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectRetrieveRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectStoreRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCursorEvent;
import org.cruxframework.crux.core.client.utils.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBIndex extends JavaScriptObject
{
	public static class IDBIndexCursorRequest extends IDBRequest<IDBIndex>
	{
		protected IDBIndexCursorRequest(){}
		
		public final native void onSuccess(IDBCursorEvent.Handler handler) /*-{
			this.onsuccess = function(evt) {
		    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBCursorEvent.Handler::onSuccess(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBCursorEvent;)(evt);
			};              
		}-*/;
	}

	protected IDBIndex() {}
	
    public final native String getName() /*-{
	    return this.name;
	}-*/;
	
	public final native IDBObjectStore getObjectStore() /*-{
	    return this.objectStore;
	}-*/;
	
	public final native String getKeyPath() /*-{
	    return this.keyPath;
	}-*/;
	
    public final FastList<String> getCompositeKeyPath()
    {
    	return JsUtils.toFastList(getCompositeKeyPathNative());
    }
    
    private native JsArrayString getCompositeKeyPathNative() /*-{
	    return this.keyPath;
	}-*/;

	public final native boolean isUnique() /*-{
	    return this.unique;
	}-*/;
	
	public final native boolean isMultiEntry() /*-{
	    return this.unique;
	}-*/;
	
	public final native IDBIndexCursorRequest openCursor() /*-{
	    return this.openCursor();
	}-*/;
	
	public final native IDBIndexCursorRequest openCursor(IDBKeyRange range) /*-{
	    return this.openCursor(range);
	}-*/;
	
	public final IDBIndexCursorRequest openCursor(IDBKeyRange range, IDBCursorDirection direction)
	{
		return openCursorNative(range, direction.toString());
	}
	
	private native IDBIndexCursorRequest openCursorNative(IDBKeyRange range, String direction) /*-{
	    return this.openCursor(range,direction);
	}-*/;
	
	public final native IDBIndexCursorRequest openKeyCursor() /*-{
	    return this.openKeyCursor();
	}-*/;
	
	public final native IDBIndexCursorRequest openKeyCursor(IDBKeyRange range) /*-{
	    return this.openKeyCursor(range);
	}-*/;
	
	public final IDBIndexCursorRequest openKeyCursor(IDBKeyRange range, IDBCursorDirection direction)
	{
		return openKeyCursorNative(range, direction.toString());
	}
	
	private native IDBIndexCursorRequest openKeyCursorNative(IDBKeyRange range, String direction) /*-{
	    return this.openKeyCursor(range,direction);
	}-*/;

	public final native IDBObjectCountRequest count() /*-{
	    return this.count();
	}-*/;
	
	public final native IDBObjectCountRequest count(IDBKeyRange range) /*-{
	    return this.count(range);
	}-*/;

	public final native IDBObjectRetrieveRequest get(IDBKeyRange range) /*-{
	    return this.get(range);
	}-*/;

	public final native IDBObjectRetrieveRequest get(JsArrayMixed key) /*-{
	    return this.get(key);
	}-*/;
	
	public final native IDBObjectRetrieveRequest get(String key) /*-{
	    return this.get(key);
	}-*/;
	
	public final IDBObjectRetrieveRequest get(Date key)
	{
	    return get(key.getTime());
	}

	public final native IDBObjectRetrieveRequest get(double key) /*-{
	    return this.get(key);
	}-*/;
	
	public final native IDBObjectRetrieveRequest get(int key) /*-{
	    return this.get(key);
	}-*/;

	public final native IDBObjectStoreRequest getKey(JsArrayMixed key) /*-{
	    return this.getKey(key);
	}-*/;
	
	public final native IDBObjectStoreRequest getKey(IDBKeyRange range) /*-{
	    return this.getKey(range);
	}-*/;

	public final native IDBObjectStoreRequest getKey(String key) /*-{
	    return this.getKey(key);
	}-*/;
	
	public final native IDBObjectStoreRequest getKey(int key) /*-{
	    return this.getKey(key);
	}-*/;

	public final IDBObjectStoreRequest getKey(Date key)
	{
	    return getKey(key.getTime());
	}

	public final  native IDBObjectStoreRequest getKey(double key) /*-{
	    return this.getKey(key);
	}-*/;

}
