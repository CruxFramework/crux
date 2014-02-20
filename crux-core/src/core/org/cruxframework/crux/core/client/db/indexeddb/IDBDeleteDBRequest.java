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

import org.cruxframework.crux.core.client.db.indexeddb.events.IDBBlockedEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBDatabaseDeleteEvent;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBDeleteDBRequest extends IDBRequest<IDBDatabase>
{
	protected IDBDeleteDBRequest(){}
	
	public final native void onBlocked(IDBBlockedEvent.Handler handler) /*-{
		this.onblocked = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBBlockedEvent.Handler::onBlocked(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBBlockedEvent;)(evt);
		};              
	}-*/;

	public final native void onSuccess(IDBDatabaseDeleteEvent.Handler handler) /*-{
		this.onsuccess = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBDatabaseDeleteEvent.Handler::onDelete(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBDatabaseDeleteEvent;)(evt);
		};              
	}-*/;
}
