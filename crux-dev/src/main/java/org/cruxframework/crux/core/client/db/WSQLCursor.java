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

import java.util.logging.Level;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.db.Transaction.Mode;
import org.cruxframework.crux.core.client.db.WSQLAbstractObjectStore.EncodeCallback;
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementErrorCallback;
import org.cruxframework.crux.core.client.utils.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 * @param <K> The type of the key used to identify objects into the cursor.
 * @param <V> The type of the objects referenced by this cursor 
 *
 */
public abstract class WSQLCursor<K, P, V> extends DBObject implements Cursor<K, V> 
{
	private static final int NOT_INITIALIZED = -1;
	private static final int CURSOR_BEGIN = 0;

	protected final org.cruxframework.crux.core.client.db.Cursor.CursorDirection direction;
	protected final WSQLTransaction transaction;
	protected WSQLKeyRange<K> keyRange;
	protected String objectStoreName; 
	protected int offset;
	protected int length;
	protected SQLResultSet resultSet;
	protected DatabaseCursorCallback<K, V> callback;
	protected Array<String> keyPath;
	protected Array<String> indexColumnNames;
	protected K cursorKey;
	protected final boolean autoIncrement;
	
	protected WSQLCursor(WSQLAbstractDatabase db, WSQLKeyRange<K> range, String objectStoreName, boolean autoIncrement, CursorDirection direction, WSQLTransaction transaction)
	{
		super(db);
		this.keyRange = range;
		this.objectStoreName = objectStoreName;
		this.autoIncrement = autoIncrement;
		this.direction = direction;
		this.transaction = transaction;
		this.offset = NOT_INITIALIZED;
		this.length = NOT_INITIALIZED;
		this.keyPath = getKeyPath();
		this.indexColumnNames = getIndexedColumnNames();
	}

	public void start(final DatabaseCursorCallback<K, V> c)
	{
		transaction.addRequest(new WSQLTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				StringBuilder sql = new StringBuilder("SELECT * FROM \"").append(objectStoreName).append("\"");
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				
				if ((keyRange != null) || (cursorKey != null))
				{
					sql.append(" WHERE ");
				}

				if (keyRange != null)
				{
					addKeyRangeToQuery(keyRange, sql, args);
				}
				if (cursorKey != null)
				{
					if (keyRange != null)
					{
						sql.append(" AND ");
					}
					addKeyToQuery(cursorKey, sql, args);
				}
				if (getDirection().equals(CursorDirection.nextunique) || getDirection().equals(CursorDirection.prevunique))
				{
					sql.append(" GROUP BY ");
					appendGroupColumns(sql);
				}
				sql.append(" ORDER BY ");
				appendGroupColumns(sql);
				if (getDirection().equals(CursorDirection.prev) || getDirection().equals(CursorDirection.prevunique))
				{
					sql.append(" DESC");
				}
				
				String sqlStatement = sql.toString(); 
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sqlStatement+"]");
				}
				tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
				{

					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						resultSet = rs;
						offset = CURSOR_BEGIN;
						length = rs.getRows().length();
						callback = c;
						fireSuccess();
					}

				}, getErrorHandler(c));
			}

		}, new Mode[]{Mode.readOnly, Mode.readWrite});
	}

	@Override
	public void advance(int count)
	{
		if (offset == NOT_INITIALIZED)
		{
			throw new DatabaseException("Cursor is not initialized. Object store ["+objectStoreName+"]");
		}
		if (count <= 0)
		{
			throw new DatabaseException("Count can not be 0 or negative. Object store ["+objectStoreName+"]");
		}
		offset += count;
		if (LogConfiguration.loggingIsEnabled())
		{
			if (offset >= length)
			{
				logger.log(Level.FINE, "Reached the end of cursor");
			}
		}
		fireSuccess();
	};
	
	@Override
	public void continueCursor()
	{
		continueCursor(null);
	}

	public void continueCursor(K key)
	{
		this.cursorKey = key;
		
		if (offset == NOT_INITIALIZED || key != null)
		{
			if (offset != NOT_INITIALIZED)
			{
				offset = NOT_INITIALIZED;
				length = NOT_INITIALIZED;
				resultSet = null;
			}
			start(callback);
		}
		else
		{
			offset++;
			if (LogConfiguration.loggingIsEnabled())
			{
				if (offset == length)
				{
					logger.log(Level.FINE, "Reached the end of cursor");
				}
			}
			fireSuccess();
		}
	}

	@Override
	public void delete()
	{
		if (offset == NOT_INITIALIZED)
		{
			throw new DatabaseException("Cursor is not initialized. Object store ["+objectStoreName+"]");
		}
		if (offset >= length)
		{
			throw new DatabaseException("Can not update cursors. It is out of range. Object store ["+objectStoreName+"]");
		}
		transaction.addRequest(new WSQLTransaction.RequestOperation()
		{
			@Override
			public void doOperation(SQLTransaction tx)
			{
				StringBuilder sql = new StringBuilder("DELETE FROM  \"").append(objectStoreName).append("\"");
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				sql.append(" WHERE ");
				addPrimaryKeyToQuery(getPrimaryKey(), sql, args);
				
				String sqlStatement = sql.toString();
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sqlStatement+"]");
				}
				tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						if (rs.getRowsAffected() == 1)
						{
							fireSuccess();
						}
						else
						{
							callback.onError("No rowns with key found");
							transaction.abort();
						}
					}

				}, getErrorHandler(callback));
			}
		}, new Mode[]{Mode.readWrite});
	}

	public void update(V value)
	{
		if (offset == NOT_INITIALIZED)
		{
			throw new DatabaseException("Cursor is not initialized. Object store ["+objectStoreName+"]");
		}
		if (offset >= length)
		{
			throw new DatabaseException("Can not update cursors. It is out of range. Object store ["+objectStoreName+"]");
		}
		encodeObject(value, new EncodeCallback()
		{
			@Override
            public void onEncode(final JSONObject encoded)
            {
				transaction.addRequest(new WSQLTransaction.RequestOperation()
				{
					@Override
					public void doOperation(SQLTransaction tx)
					{
						StringBuilder sql = new StringBuilder("UPDATE \"").append(objectStoreName).append("\" SET ");
						final JsArrayMixed args = JsArrayMixed.createArray().cast();
						args.push(encoded.toString());
				    	JsArrayMixed sqlValues = JsArrayMixed.createArray().cast();
				    	getIndexesValuesForObject(encoded.getJavaScriptObject(), indexColumnNames, sqlValues);
				    	for (int i=0; i< indexColumnNames.size(); i++)
				    	{
				    		String key = indexColumnNames.get(i);
				    		sql.append(key +" = ?, ");
				    	}
						
						sql.append("value = ? WHERE ");
						addPrimaryKeyToQuery(getPrimaryKey(), sql, args);
						
						String sqlStatement = sql.toString();
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Running SQL ["+sqlStatement+"]");
						}
						tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								if (rs.getRowsAffected() == 1)
								{
									fireSuccess();
								}
								else
								{
									callback.onError("No rowns with key found");
									transaction.abort();
								}
							}
						}, getErrorHandler(callback));
					}
				}, new Mode[]{Mode.readWrite});
            }
		});
	}
		
	@Override
	public boolean hasValue()
	{
		return getValue() != null;
	}

	protected int size()
	{
		return length;
	}
	
	protected void getIndexesValuesForObject(JavaScriptObject object, Array<String> columnNames, JsArrayMixed output)
	{
		for (int i=0; i<columnNames.size(); i++)
		{
			JsUtils.readPropertyValue(object, columnNames.get(i), output, true);
		}
	}

	protected void appendGroupColumns(StringBuilder sql)
	{
		for (int i=0; i< keyPath.size(); i++)
		{
			if (i > 0)
			{
				sql.append(", ");
			}
			sql.append("\""+keyPath.get(i)+"\"");
		}
	}
	
	protected SQLStatementErrorCallback getErrorHandler(final Callback callback)
    {
	    return new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				String message = db.messages.objectStoreOperationError(error.getName() + " - " + error.getMessage());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
					callback.setDb(null);
				}
				else if (db.errorHandler != null)
				{
					db.errorHandler.onError(message);
				}
				return true;
			}
		};
    }	
	
	@Override
	public CursorDirection getDirection()
	{
		return direction;
	}

	public V getValue()
	{
		if (offset == NOT_INITIALIZED)
		{
			throw new DatabaseException("Cursor is not initialized. Object store ["+objectStoreName+"]");
		}
		if (offset < length)
		{
			V object = decodeObject(JsUtils.readStringPropertyValue(resultSet.getRows().itemObject(offset), "value"));
			if (autoIncrement)
			{
				setObjectKey(object, getPrimaryKey());
			}
			return object;
		}
		else
		{
			return null;
		}
	}
	
	public JsArrayMixed getNativeArrayKey()
	{
		if (offset == NOT_INITIALIZED)
		{
			throw new DatabaseException("Cursor is not initialized. Object store ["+objectStoreName+"]");
		}
		if (offset < length && keyPath != null && keyPath.size() > 0)
		{
			JsArrayMixed out = JsArrayMixed.createArray().cast();
			JavaScriptObject object = resultSet.getRows().itemObject(offset);
			for (int i = 0; i< keyPath.size(); i++)
			{
				JsUtils.readPropertyValue(object, keyPath.get(i), out, true);
			}
			return out;
		}
		return null;
	}
	
	protected void fireSuccess()
	{
		delayCallbackSuccessCall(this);
	}
	
	private native void delayCallbackSuccessCall(WSQLCursor<K, P, V> cursor)/*-{
        setTimeout(function(){
        	cursor.@org.cruxframework.crux.core.client.db.WSQLCursor::callCallbackSuccess()();
        }, 0);
	}-*/;
	
	private void callCallbackSuccess()
	{
		callback.onSuccess((offset <= length)?this:null);		
	}

	protected abstract void setObjectKey(V object, P key);
	protected abstract Array<String> getIndexedColumnNames();
	protected abstract P getPrimaryKey();
	protected abstract Array<String> getKeyPath();
	protected abstract void addKeyRangeToQuery(final KeyRange<K> range, StringBuilder sql, JsArrayMixed args);
	protected abstract void addKeyToQuery(final K key, StringBuilder sql, JsArrayMixed args);
	protected abstract void addPrimaryKeyToQuery(final P key, StringBuilder sql, JsArrayMixed args);
	protected abstract void encodeObject(V object, EncodeCallback callback);
	protected abstract V decodeObject(String encodedObject);
}
