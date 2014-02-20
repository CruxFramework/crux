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

import org.cruxframework.crux.core.client.db.indexeddb.events.IDBEvent;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBRequest<T extends JavaScriptObject> extends IDBClass 
{
    protected IDBRequest() {}

    public static enum IDBRequestReadyState{pending, done}
    
	public final native T getSource() /*-{
		return this.source;
	}-*/;

    public final native IDBTransaction getTransaction() /*-{
		return this.transaction;
	}-*/;

	public final IDBRequestReadyState readyState() 
	{
		return IDBRequestReadyState.valueOf(readyStateNative());
	}

	private native String readyStateNative() /*-{
		return this.readyState;
	}-*/;
	
	public final native void onSuccess(IDBEvent.Handler handler) /*-{
		this.onsuccess = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBEvent.Handler::onSuccess(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBEvent;)(evt);
		};              
	}-*/;
	
}