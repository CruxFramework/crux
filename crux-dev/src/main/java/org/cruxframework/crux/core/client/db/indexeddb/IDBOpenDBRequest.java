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
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBOpenedEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBUpgradeNeededEvent;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBOpenDBRequest extends IDBRequest<IDBDatabase>
{
	protected IDBOpenDBRequest(){}
	
	public final native void onBlocked(IDBBlockedEvent.Handler handler) /*-{
		this.onblocked = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBBlockedEvent.Handler::onBlocked(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBBlockedEvent;)(evt);
		};              
	}-*/;

	public final native void onUpgradeNeeded(IDBUpgradeNeededEvent.Handler handler) /*-{
		this.onupgradeneeded = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBUpgradeNeededEvent.Handler::onUpgradeNeeded(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBUpgradeNeededEvent;)(evt);
		};              
	}-*/;

	public final native void onSuccess(IDBOpenedEvent.Handler handler) /*-{
		this.onsuccess = function(evt) {
	    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBOpenedEvent.Handler::onSuccess(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBOpenedEvent;)(evt);
		};              
	}-*/;
}
