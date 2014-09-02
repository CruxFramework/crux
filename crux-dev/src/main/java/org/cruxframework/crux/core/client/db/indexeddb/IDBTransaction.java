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

import org.cruxframework.crux.core.client.db.indexeddb.events.IDBAbortEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCompleteEvent;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBTransaction extends IDBClass
{
    protected IDBTransaction() {}
    
    public static enum IDBTransactionMode {readonly, readwrite, versionchange}
    
    public final native IDBDatabase getDb() /*-{
	    return this.db;
	}-*/;
	
    public final native IDBObjectStore getObjectStore(String name) /*-{
	    return this.objectStore(name);
	}-*/; 

	public final IDBTransactionMode getMode()
	{
		return IDBTransactionMode.valueOf(getModeNative());
	}
	
	private native String getModeNative() /*-{
	    return this.mode;
	}-*/;

	public final native void abort() /*-{
	    this.abort();
	}-*/;
	
	public final native void onAbort(IDBAbortEvent.Handler handler) /*-{
		this.onabort = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBAbortEvent.Handler::onAbort(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBAbortEvent;)(evt);
		};              
	}-*/;

	public final native void onComplete(IDBCompleteEvent.Handler handler) /*-{
		this.oncomplete = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBCompleteEvent.Handler::onComplete(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBCompleteEvent;)(evt);
		};              
	}-*/;
}
