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

import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.indexeddb.IDBKeyRange;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectCountRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectCursorRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectDeleteRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectRetrieveRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectStoreRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCountEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCursorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectDeleteEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectRetrieveEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectStoreEvent;
import org.cruxframework.crux.core.client.file.Blob;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * Indexed DB implementation for FileStore Interface. Use the interface {@link FileStore} instead. 
 * @author Thiago da Rosa de Bustamante
 */
public class IDXFileStore extends DBObject implements FileStore
{
	protected final IDBObjectStore idbObjectStore;
	protected final IDXAbstractDatabase db;

	protected IDXFileStore(IDXAbstractDatabase db, IDBObjectStore idbObjectStore)
	{
		super(db);
		this.db = db; 
		this.idbObjectStore = idbObjectStore;
	}

	@Override
    public void add(Blob file, String fileName)
    {
		add(file, fileName, null);
    }

	@Override
    public void put(Blob file, String fileName)
    {
		put(file, fileName, null);
    }

	@Override
    public void delete(KeyRange<String> keyRange)
    {
		delete(keyRange, null);
    }
	
	public void add(final Blob file, final String fileName, final DatabaseWriteCallback<String> callback)
	{
		IDBObjectStoreRequest storeRequest = idbObjectStore.add(file, fileName);
		handleWriteCallback(callback, storeRequest);
	}

	public void put(final Blob file, final String fileName, final DatabaseWriteCallback<String> callback)
	{
		IDBObjectStoreRequest storeRequest = idbObjectStore.put(file, fileName);
		handleWriteCallback(callback, storeRequest);
	}

	public void get(String key, DatabaseRetrieveCallback<Blob> callback)
	{
		IDBObjectRetrieveRequest retrieveRequest = idbObjectStore.get(key);
		handleRetrieveCallback(callback, retrieveRequest);
	}

	public void delete(String key, DatabaseDeleteCallback callback)
	{
		IDBObjectDeleteRequest deleteRequest = idbObjectStore.delete(key);
		handleDeleteCallback(callback, deleteRequest);
	}

	public void delete(KeyRange<String> keyRange, DatabaseDeleteCallback callback)
	{
		IDBObjectDeleteRequest deleteRequest = idbObjectStore.delete(IDXKeyRange.getNativeKeyRange(keyRange));
		handleDeleteCallback(callback, deleteRequest);
	}

	public void clear()
	{
		idbObjectStore.clear();
	}

	public void openCursor(FileStoreCursorCallback callback)
	{
		IDBObjectCursorRequest cursorRequest = idbObjectStore.openCursor();
		handleCursorCallback(callback, cursorRequest);
	}

	public void openCursor(KeyRange<String> keyRange, FileStoreCursorCallback callback)
	{
		IDBObjectCursorRequest cursorRequest = idbObjectStore.openCursor(IDXKeyRange.getNativeKeyRange(keyRange));
		handleCursorCallback(callback, cursorRequest);
	}

	public void openCursor(KeyRange<String> keyRange, CursorDirection direction, FileStoreCursorCallback callback)
	{
		IDBObjectCursorRequest cursorRequest = idbObjectStore.openCursor(IDXKeyRange.getNativeKeyRange(keyRange), IDXCursor.getNativeCursorDirection(direction));
		handleCursorCallback(callback, cursorRequest);
	}

	public void count(DatabaseCountCallback callback)
	{
		IDBObjectCountRequest countRequest = idbObjectStore.count();
		handleCountCallback(callback, countRequest);
	}

	public void count(KeyRange<String> range, DatabaseCountCallback callback)
	{
		IDBObjectCountRequest countRequest = idbObjectStore.count(IDXKeyRange.getNativeKeyRange(range));
		handleCountCallback(callback, countRequest);
	}

	public KeyRangeFactory<String> getKeyRangeFactory()
	{
		return new IDXFileKeyRangeFactory();
	}

	private void handleWriteCallback(final DatabaseWriteCallback<String> callback, IDBObjectStoreRequest writeRequest)
	{
		if (callback != null || db.errorHandler != null)
		{
			if (callback != null)
			{
				callback.setDb(db);
			}
			writeRequest.onError(new IDBErrorEvent.Handler()
			{
				@Override
				public void onError(IDBErrorEvent event)
				{
					reportError(callback, db.messages.objectStoreWriteError(event.getName()), null);
				}
			});
			if (callback != null)
			{
				writeRequest.onSuccess(new IDBObjectStoreEvent.Handler()
				{
					@Override
					public void onSuccess(IDBObjectStoreEvent event)
					{
						try
						{
							callback.onSuccess(event.getStringKey());
							callback.setDb(null);
						}
						catch (Exception e) 
						{
							reportError(callback, db.messages.objectStoreWriteError(e.getMessage()), e);
						}
					}
				});
			}
		}
	}

	private void handleCountCallback(final DatabaseCountCallback callback, IDBObjectCountRequest countRequest)
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
			if (callback != null)
			{
				countRequest.onSuccess(new IDBCountEvent.Handler()
				{
					@Override
					public void onSuccess(IDBCountEvent event)
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
				});
			}
		}
    }

	private void handleDeleteCallback(final DatabaseDeleteCallback callback, IDBObjectDeleteRequest deleteRequest)
    {
		if (callback != null || db.errorHandler != null)
		{
			if (callback != null)
			{
				callback.setDb(db);
			}
			deleteRequest.onError(new IDBErrorEvent.Handler()
			{
				@Override
				public void onError(IDBErrorEvent event)
				{
					reportError(callback, db.messages.objectStoreDeleteError(event.getName()), null);
				}
			});
			if (callback != null)
			{
				deleteRequest.onSuccess(new IDBObjectDeleteEvent.Handler()
				{
					@Override
					public void onSuccess(IDBObjectDeleteEvent event)
					{
						try
						{
							callback.onSuccess();
							callback.setDb(null);
						}
						catch (Exception e) 
						{
							reportError(callback, db.messages.objectStoreDeleteError(e.getMessage()), e);
						}
					}
				});
			}
		}
    }

	private void handleCursorCallback(final FileStoreCursorCallback callback, IDBObjectCursorRequest cursorRequest)
    {
		if (callback != null || db.errorHandler != null)
		{
			if (callback != null)
			{
				callback.setDb(db);
			}
			cursorRequest.onError(new IDBErrorEvent.Handler()
			{
				@Override
				public void onError(IDBErrorEvent event)
				{
					reportError(callback, db.messages.objectStoreCursorError(event.getName()), null);
				}
			});
			if (callback != null)
			{
				cursorRequest.onSuccess(new IDBCursorEvent.Handler()
				{
					@Override
					public void onSuccess(IDBCursorEvent event)
					{
						try
						{
							callback.onSuccess(new IDXFileCursor(event.getCursor()));
							callback.setDb(null);
						}
						catch (Exception e) 
						{
							reportError(callback, db.messages.objectStoreCursorError(e.getMessage()), e);
						}
					}
				});
			}
		}
    }

	private void handleRetrieveCallback(final DatabaseRetrieveCallback<Blob> callback, IDBObjectRetrieveRequest retrieveRequest)
	{
		if (callback != null || db.errorHandler != null)
		{
			if (callback != null)
			{
				callback.setDb(db);
			}
			retrieveRequest.onError(new IDBErrorEvent.Handler()
			{
				@Override
				public void onError(IDBErrorEvent event)
				{
					reportError(callback, db.messages.objectStoreGetError(event.getName()), null);
				}
			});
			if (callback != null)
			{
				retrieveRequest.onSuccess(new IDBObjectRetrieveEvent.Handler()
				{
					@Override
					public void onSuccess(IDBObjectRetrieveEvent event)
					{
						try
						{
							Blob file = event.getObject().cast();
							callback.onSuccess(file);
							callback.setDb(null);
						}
						catch (Exception e) 
						{
							reportError(callback, db.messages.objectStoreGetError(e.getMessage()), e);
						}
					}
				});
			}
		}
	}

	static class IDXFileKeyRangeFactory implements KeyRangeFactory<String>
	{
		@Override
        public KeyRange<String> only(String key)
        {
	        return new IDXKeyRange<String>(IDBKeyRange.only(key));
        }

		@Override
        public KeyRange<String> lowerBound(String key, boolean open)
        {
	        return new IDXKeyRange<String>(IDBKeyRange.lowerBound(key, open));
        }

		@Override
        public KeyRange<String> lowerBound(String key)
        {
	        return new IDXKeyRange<String>(IDBKeyRange.lowerBound(key));
        }

		@Override
        public KeyRange<String> upperBound(String key, boolean open)
        {
	        return new IDXKeyRange<String>(IDBKeyRange.upperBound(key, open));
        }

		@Override
        public KeyRange<String> upperBound(String key)
        {
	        return new IDXKeyRange<String>(IDBKeyRange.upperBound(key));
        }

		@Override
        public KeyRange<String> bound(String startKey, String endKey, boolean startOpen, boolean endOpen)
        {
	        return new IDXKeyRange<String>(IDBKeyRange.bound(startKey, endKey, startOpen, endOpen));
        }

		@Override
        public KeyRange<String> bound(String startKey, String endKey)
        {
	        return new IDXKeyRange<String>(IDBKeyRange.bound(startKey, endKey));
        }
	}
}
