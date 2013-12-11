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
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.Transaction.Mode;
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementErrorCallback;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class WSQLAbstractObjectStore<K, V> extends AbstractObjectStore<K, V> 
{
	protected final String name;
	protected final WSQLTransaction transaction;
	protected final WSQLAbstractDatabase db;
	protected Array<String> indexAndKeyColumnNames;
	protected Array<String> keyPath;
	protected Array<String> indexColumnNames;

	protected WSQLAbstractObjectStore(WSQLAbstractDatabase db, String name, WSQLTransaction transaction)
	{
		super(db);
		this.db = db;
		this.name = name;
		this.transaction = transaction;
		this.keyPath = getKeyPath();
		this.indexColumnNames = getIndexedColumnNames();
		this.indexAndKeyColumnNames = getIndexAndKeyColumnNames();
	}
	
	@Override
	public String getObjectStoreName()
	{
	    return name;
	}
	
	@Override
	public void add(final V object, final DatabaseWriteCallback<K> callback)
	{
    	transaction.addRequest(new WSQLTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
				insertObject(object, tx, callback, getKey(object));
    		}
    	}, new Mode[]{Mode.readWrite});
	}
	
	@Override
	public void put(final V object, final DatabaseWriteCallback<K> callback)
	{
    	transaction.addRequest(new WSQLTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
				updateObject(object, tx, callback, getKey(object));
    		}
    	}, new Mode[]{Mode.readWrite});
	}

	@Override
	public void get(final K key, final DatabaseRetrieveCallback<V> callback)
	{
    	transaction.addRequest(new WSQLTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				StringBuilder sql = new StringBuilder("SELECT * FROM \"").append(name).append("\"");
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				if (key != null)
				{
					sql.append(" WHERE ");
					addKeyToQuery(key, sql, args);
				}
				runSelectSQL(callback, tx, args, sql.toString(), key);
			}
		}, new Mode[]{Mode.readOnly, Mode.readWrite});
    }
	
	@Override
	public void delete(final KeyRange<K> range, final DatabaseDeleteCallback callback)
	{
		if (callback != null)
		{
			callback.setDb(db);
		}
    	transaction.addRequest(new WSQLTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
    			StringBuilder sql = new StringBuilder("DELETE FROM \"").append(name).append("\"");
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				if (range != null)
				{
					addKeyRangeToQuery(range, sql, args);
				}
				String sqlStatement = sql.toString(); 
				runDeleteSQL(callback, tx, args, sqlStatement);
    		}
    	}, new Mode[]{Mode.readWrite});
	}
	
	@Override
	public void delete(final K key, final DatabaseDeleteCallback callback)
	{
		if (callback != null)
		{
			callback.setDb(db);
		}
    	transaction.addRequest(new WSQLTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
    			StringBuilder sql = new StringBuilder("DELETE FROM \"").append(name).append("\"");
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				if (key != null)
				{
					sql.append(" WHERE ");
					addKeyToQuery(key, sql, args);
				}
				String sqlStatement = sql.toString(); 
				runDeleteSQL(callback, tx, args, sqlStatement);
    		}
    	}, new Mode[]{Mode.readWrite});
	}
	
	@Override
	public void clear(final DatabaseCallback callback)
	{
		if (callback != null)
		{
			callback.setDb(db);
		}
    	transaction.addRequest(new WSQLTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				String sql = "DELETE FROM __sys__ WHERE name = ?";
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(name);
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"]");
				}
				tx.executeSQL(sql, args, null, getErrorHandler(callback));
			}
		}, new Mode[]{Mode.readWrite});
    	transaction.addRequest(new WSQLTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				String sql = "DELETE FROM \"" + name + "\"";
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				runDatabaseSQL(callback, tx, args, sql);
			}
		}, new Mode[]{Mode.readWrite});
	}
	
	public void createTable(SQLTransaction tx, final DatabaseCallback callback)
	{
		if (callback != null)
		{
			callback.setDb(db);
		}
		String sql = "INSERT INTO __sys__(name) VALUES (?)";
		JsArrayMixed args = JsArrayMixed.createArray().cast();
		args.push(name);
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL ["+sql+"]");
		}
		tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				String sql = getCreateTableSQL();
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				runDatabaseSQL(callback, tx, args, sql);
			}
		}, getErrorHandler(callback));
	}
	
	@Override
	public void count(final DatabaseCountCallback callback)
	{
		count(null, callback);
	}

	@Override
	public void count(final KeyRange<K> range, final DatabaseCountCallback callback)
	{
		if (callback != null)
		{
			callback.setDb(db);
		}
    	transaction.addRequest(new WSQLTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM \"").append(name).append("\"");
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				if (range != null)
				{
					addKeyRangeToQuery(range, sql, args);
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
						if (callback != null)
						{
							try
							{
								int count;
								if (rs.getRows().length() > 0)
								{
									count = JsUtils.readIntPropertyValue(rs.getRows().itemObject(0), "total");
								}
								else
								{
									String message = db.messages.objectStoreCountError("No rows returned");
									if (LogConfiguration.loggingIsEnabled())
									{
										logger.log(Level.SEVERE, message);
									}
									callback.onError(message);
									callback.setDb(null);
									return;
								}
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.FINE, "There are ["+count+"] records on object store ["+name+"].");
								}
								callback.onSuccess(count);
								callback.setDb(null);
							}
							catch (Exception e) 
							{
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.SEVERE, db.messages.objectStoreOperationError(e.getMessage()), e);
								}
							}
						}
					}
				}, getErrorHandler(callback));
			}
		}, new Mode[]{Mode.readOnly, Mode.readWrite});
    }

	@Override
	public void openCursor(DatabaseCursorCallback<K, V> callback)
	{
		openCursor(null, CursorDirection.next, callback);
	}
	
	@Override
	public void openCursor(KeyRange<K> keyRange, DatabaseCursorCallback<K, V> callback)
	{
		openCursor(keyRange, CursorDirection.next, callback);
	}
	
	protected void insertObject(final V object, final SQLTransaction tx, final DatabaseWriteCallback<K> callback, final K key)
    {
		encodeObject(object, new EncodeCallback()
		{
			@Override
			public void onEncode(JSONObject encoded)
			{
		    	insertObject(tx, callback, key, encoded);
			}
		});
    }

	protected void insertObject(final SQLTransaction tx, final DatabaseWriteCallback<K> callback, final K key, JSONObject encoded)
	{
		StringBuilder sqlStart = new StringBuilder("INSERT INTO ").append("\""+ name +"\" (");
		StringBuilder sqlEnd = new StringBuilder(" VALUES(");
		JavaScriptObject encodedObject = encoded.getJavaScriptObject();
		
    	JsArrayMixed sqlValues = JsArrayMixed.createArray().cast();
    	
    	if (!isAutoIncrement() || key != null)
    	{
    		getIndexesValuesForObject(encodedObject, indexAndKeyColumnNames, sqlValues);
    		
    		for (int i=0; i< indexAndKeyColumnNames.size(); i++)
    		{
    			String k = indexAndKeyColumnNames.get(i);
    			sqlStart.append("\""+k +"\",");
    			sqlEnd.append("?,");
    		}
    	}
    	else
    	{
    		getIndexesValuesForObject(encodedObject, indexColumnNames, sqlValues);
    		
    		for (int i=0; i< indexColumnNames.size(); i++)
    		{
    			String k = indexColumnNames.get(i);
    			sqlStart.append("\""+k +"\",");
    			sqlEnd.append("?,");
    		}
    	}
		
		sqlStart.append("value)");
		sqlEnd.append("?)");
		sqlValues.push(encoded.toString());
		String sqlStatement = sqlStart.toString()+" "+sqlEnd.toString() ;
		runInsertQL(callback, tx, sqlValues, sqlStatement, key);
	}

	protected void updateObject(final V object, final SQLTransaction tx, final DatabaseWriteCallback<K> callback, final K key)
    {
		encodeObject(object, new EncodeCallback()
		{
			@Override
			public void onEncode(JSONObject encoded)
			{
		    	StringBuilder sql = new StringBuilder("UPDATE ").append("\""+ name +"\" SET ");
		    	
		    	JsArrayMixed sqlValues = JsArrayMixed.createArray().cast();
		    	getIndexesValuesForObject(encoded.getJavaScriptObject(), indexColumnNames, sqlValues);
		    	for (int i=0; i< indexColumnNames.size(); i++)
		    	{
		    		String key = indexColumnNames.get(i);
		    		sql.append("\""+key +"\" = ?, ");
		    	}
		    	
				sql.append("value = ?");
				sqlValues.push(encoded.toString());
				sql.append(" WHERE ");
				addKeyToQuery(key, sql, sqlValues);
				
		        String sqlStatement = sql.toString();
				runUpdateSQL(callback, tx, sqlValues, sqlStatement, key, encoded);
			}
		});
    }
	
	protected Array<String> getIndexAndKeyColumnNames()
	{
		Array<String> indexAndKeys = CollectionFactory.createArray();
		for (int i=0; i< keyPath.size(); i++)
		{
			String k = keyPath.get(i);
			indexAndKeys.add(k);
		}
		for (int i=0; i< indexColumnNames.size(); i++)
		{
			String k = indexColumnNames.get(i);
			if (indexAndKeys.indexOf(k) == -1)
			{
				indexAndKeys.add(k);
			}
		}
		return indexAndKeys;
	}

	protected void getIndexesValuesForObject(JavaScriptObject object, Array<String> columnNames, JsArrayMixed output)
	{
		for (int i=0; i<columnNames.size(); i++)
		{
			JsUtils.readPropertyValue(object, columnNames.get(i), output, true);
		}
	}

	protected void runUpdateSQL(final DatabaseWriteCallback<K> callback, final SQLTransaction tx, JsArrayMixed args, String sqlStatement, 
								final K key, final JSONObject encodedObject)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL ["+sqlStatement+"]");
		}
        tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				if (rs.getRowsAffected() == 0)
				{
					insertObject(tx, callback, key, encodedObject);
				}
				else
				{
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.FINE, "Operation executed on database. Table ["+name+"]");
					}
					if (callback != null)
					{
						try
						{
							callback.onSuccess(key);
							callback.setDb(null);
						}
						catch (Exception e) 
						{
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.SEVERE, db.messages.objectStoreOperationError(e.getMessage()), e);
							}
						}
					}
				}
			}
		}, getErrorHandler(callback));
    }
	
	protected void runInsertQL(final DatabaseWriteCallback<K> callback, final SQLTransaction tx, JsArrayMixed args, String sqlStatement, final K key)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL ["+sqlStatement+"]");
		}
        tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
		{
			@SuppressWarnings("unchecked")
            @Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Operation executed on database. Table ["+name+"]");
				}
				if (callback != null)
				{
					try
					{
						if (isAutoIncrement())
						{   // Only integer keys can be auto incremented.
							callback.onSuccess((K) new Integer(rs.getInsertId()));
						}
						else
						{
							callback.onSuccess(key);
						}
						callback.setDb(null);
					}
					catch (Exception e) 
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.SEVERE, db.messages.objectStoreOperationError(e.getMessage()), e);
						}
					}
				}
			}
		}, getErrorHandler(callback));
    }
	
	protected void runDeleteSQL(final DatabaseDeleteCallback callback, final SQLTransaction tx, JsArrayMixed args, String sqlStatement)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL ["+sqlStatement+"]");
		}
        tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Operation executed on database. Table ["+name+"]");
				}
				if (callback != null)
				{
					try
					{
						callback.onSuccess();
						callback.setDb(null);
					}
					catch (Exception e) 
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.SEVERE, db.messages.objectStoreOperationError(e.getMessage()), e);
						}
					}
				}
			}
		}, getErrorHandler(callback));
    }

	protected void runSelectSQL(final DatabaseRetrieveCallback<V> callback, final SQLTransaction tx, JsArrayMixed args, String sqlStatement, final K key)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL ["+sqlStatement+"]");
		}
        tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Operation executed on database. Table ["+name+"]");
				}
				if (callback != null)
				{
					try
					{
						if (rs.getRows().length() > 0)
						{
							String encodedObject = JsUtils.readStringPropertyValue(rs.getRows().itemObject(0), "value");
							if (!StringUtils.isEmpty(encodedObject))
							{
								V object = decodeObject(encodedObject);
								if (isAutoIncrement() && key != null)
								{
									setObjectKey(object, key);
								}
								callback.onSuccess(object);
							}
							else
							{
								callback.onError(db.messages.objectStoreOperationError("Read error"));
								return;
							}
						}
						else
						{
							callback.onSuccess(null);
						}
						callback.setDb(null);
					}
					catch (Exception e) 
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.SEVERE, db.messages.objectStoreOperationError(e.getMessage()), e);
						}
					}
				}
			}
		}, getErrorHandler(callback));
    }
	
	protected void runDatabaseSQL(final DatabaseCallback callback, final SQLTransaction tx, JsArrayMixed args, String sqlStatement)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL ["+sqlStatement+"]");
		}
        tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Operation executed on database. Table ["+name+"]");
				}
				if (callback != null)
				{
					try
					{
						callback.onSuccess();
						callback.setDb(null);
					}
					catch (Exception e) 
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.SEVERE, db.messages.objectStoreOperationError(e.getMessage()), e);
						}
					}
				}
			}
		}, getErrorHandler(callback));
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
	
	protected void reportError(String message)
	{
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.SEVERE, message);
		}
		if (db.errorHandler != null)
		{
			db.errorHandler.onError(message);
		}
	}
	
	protected abstract Array<String> getIndexedColumnNames();
	protected abstract Array<String> getKeyPath();
	protected abstract void addKeyRangeToQuery(final KeyRange<K> range, StringBuilder sql, JsArrayMixed args);
	protected abstract void addKeyToQuery(final K key, StringBuilder sql, JsArrayMixed args);
	protected abstract K getKey(V object);
	protected abstract void setObjectKey(V object, K key);
	protected abstract V decodeObject(String encodedObject);
	protected abstract void encodeObject(V object, EncodeCallback callback);
	protected abstract String getCreateTableSQL();
		
	public static interface EncodeCallback
	{
		void onEncode(JSONObject encoded);
	}
}
