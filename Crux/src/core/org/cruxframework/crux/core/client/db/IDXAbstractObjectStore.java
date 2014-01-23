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

import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectCountRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCountEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBEvent;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * Indexed DB implementation for ObjectStore Interface. Use the interface {@link ObjectStore} instead. 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class IDXAbstractObjectStore<K, V> extends AbstractObjectStore<K, V> 
{
	protected final IDBObjectStore idbObjectStore;
	protected final IDXAbstractDatabase db;

	protected IDXAbstractObjectStore(IDXAbstractDatabase db, IDBObjectStore idbObjectStore)
	{
		super(db);
		this.db = db;
		this.idbObjectStore = idbObjectStore;
	}
	
	@Override
	public String[] getIndexNames()
	{
		return idbObjectStore.getIndexNames();
	}
	
	@Override
	public boolean isAutoIncrement()
	{
	    return idbObjectStore.isAutoIncrement();
	}
	
	@Override
	public void clear(DatabaseCallback callback)
	{
		IDBRequest<IDBObjectStore> request = idbObjectStore.clear();
		handleCallback(callback, request);
	}
	
	@Override
	public void count(final DatabaseCountCallback callback)
	{
		IDBObjectCountRequest countRequest = idbObjectStore.count();
		handleCallback(callback, countRequest);
	}

	@Override
	public void count(KeyRange<K> range, final DatabaseCountCallback callback)
	{
		IDBObjectCountRequest countRequest = idbObjectStore.count(IDXKeyRange.getNativeKeyRange(range));
		handleCallback(callback, countRequest);
	}
	
	private void handleCallback(final DatabaseCountCallback callback, IDBObjectCountRequest countRequest)
    {
		if (callback != null || db.errorHandler != null)
		{
			if (callback != null)
			{
				callback.setDb(db);
			}
			countRequest.onError(new IDBErrorEvent.Handler()
			{
				@Override
				public void onError(IDBErrorEvent event)
				{
					reportError(callback, db.messages.objectStoreCountError(event.getName()), null);
				}
			});
			countRequest.onSuccess(new IDBCountEvent.Handler()
			{

				@Override
				public void onSuccess(IDBCountEvent event)
				{
					if (callback != null)
					{
						try
						{
							callback.onSuccess(event.getCount());
							callback.setDb(null);
						}
						catch (Exception e) 
						{
							reportError(callback, db.messages.objectStoreCountError(e.getMessage()), e);
						}
					}
				}
			});
		}
    }

	private void handleCallback(final DatabaseCallback callback, IDBRequest<IDBObjectStore> request)
    {
		if (callback != null || db.errorHandler != null)
		{
			if (callback != null)
			{
				callback.setDb(db);
			}
			request.onError(new IDBErrorEvent.Handler()
			{
				@Override
				public void onError(IDBErrorEvent event)
				{
					reportError(callback, db.messages.objectStoreClearError(event.getName()), null);
				}
			});
			request.onSuccess(new IDBEvent.Handler()
			{

				@Override
				public void onSuccess(IDBEvent event)
				{
					if (callback != null)
					{
						try
						{
							callback.onSuccess();
							callback.setDb(null);
						}
						catch (Exception e) 
						{
							reportError(callback, db.messages.objectStoreClearError(e.getMessage()), e);
						}
					}
				}
			});
		}
    }
}
