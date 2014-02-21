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

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.db.indexeddb.IDBTransaction.IDBTransactionMode;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBAbortEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBVersionChangeEvent;
import org.cruxframework.crux.core.client.utils.JsUtils;

import com.google.gwt.core.client.JsArrayString;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBDatabase extends IDBClass
{
	protected IDBDatabase() {}

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native int getVersion() /*-{
		return this.version;
	}-*/;

	public final String[] getObjectStoreNames()
	{
		return JsUtils.toArray(getObjectStoreNamesNative());
	}

	public final FastList<String> listObjectStoreNames()
	{
		return JsUtils.toFastList(getObjectStoreNamesNative());
	}

	private native JsArrayString getObjectStoreNamesNative() /*-{
		return this.objectStoreNames;
	}-*/;

	public final native void close() /*-{
		this.close();
	}-*/;
	
	public final IDBTransaction getTransaction(String... storeNames)
	{
		return getTransaction(storeNames, IDBTransactionMode.readonly);
	}

	public final IDBTransaction getTransaction(String[] storeNames, IDBTransactionMode mode) 
	{
		return getTransactionNative(JsUtils.toJsArray(storeNames), mode.toString());
	}

	private native IDBTransaction getTransactionNative(JsArrayString storeNames, String mode) /*-{
		return this.transaction(storeNames, mode);
	}-*/;
	
	public final native IDBObjectStore createObjectStore(String name) /*-{
		return this.createObjectStore(name);
	}-*/;

	public final native IDBObjectStore createObjectStore(String name, IDBObjectStoreParameters params) /*-{
		return this.createObjectStore(name, params);
	}-*/;

	public final native void deleteObjectStore(String name) /*-{
    	this.deleteObjectStore(name);
    }-*/;
	
	public final native void onAbort(IDBAbortEvent.Handler handler) /*-{
		this.onabort = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBAbortEvent.Handler::onAbort(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBAbortEvent;)(evt);
		};              
	}-*/;
	
	public final native void onVersionChange(IDBVersionChangeEvent.Handler handler) /*-{
		this.onversionchange = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBVersionChangeEvent.Handler::onVersionChange(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBVersionChangeEvent;)(evt);
		};              
	}-*/;
}