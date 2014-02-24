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
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.db.Transaction.Mode;
import org.cruxframework.crux.core.client.db.websql.SQLDatabase;
import org.cruxframework.crux.core.client.db.websql.SQLDatabaseFactory;
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementErrorCallback;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * HTML5 AbstractDatabase based on Web SQL (http://www.w3.org/TR/webdatabase/).
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class WSQLAbstractDatabase extends AbstractDatabase 
{
	private static int  DEFAULT_DB_SIZE = 5 * 1024 * 1024;
	private static boolean initialized = false;
	private static SQLDatabase systemDatabase;
	protected static Logger logger = Logger.getLogger(WSQLAbstractDatabase.class.getName());

	protected boolean opened = false;
	protected SQLDatabase database;
	protected Array<String> objectStoreNames;
	
	private static final void init(final SQLCallback callback)
	{
		if (!initialized)
		{
			systemDatabase = SQLDatabaseFactory.openDatabase("__sysdb__", "System Database", DEFAULT_DB_SIZE);
			systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
			{
				
				@Override
				public void onTransaction(SQLTransaction tx)
				{
					String sql = "CREATE TABLE IF NOT EXISTS dbVersions (name VARCHAR(255), version INT)";
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.FINE, "Running SQL ["+sql+"]");
					}
					JsArrayMixed args = JsArrayMixed.createArray().cast();
					tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
					{
						@Override
						public void onSuccess(SQLTransaction tx, SQLResultSet rs)
						{
							initialized = true;
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.INFO, "System table created.");
							}
							callback.onSuccess();
						}
					}, null);
				}
			}, new SQLDatabase.SQLTransactionErrorCallback()
			{
				
				@Override
				public void onError(SQLError error)
				{
					throw new DatabaseException("Could not create the systam table (__sysdb__). Error ["+error.getName()+"]. Message ["+error.getMessage()+"]");
				}
			}, null);
		}
	}
	
	@Override
	public void close()
	{
		if (isOpen())
		{
			opened = false;
		}
	}
	
    @Override
	public void delete(final DatabaseCallback callback)
	{
		if (StringUtils.isEmpty(getName()))
		{
			throw new DatabaseException(messages.databaseInvalidNameDBError(getName()));
		}
		if (!initialized)
		{
			init(new SQLCallback()
			{
				@Override
				public void onSuccess()
				{
					doDelete(callback);
				}
			});
		}
		else
		{
			doDelete(callback);
		}
	}		

    @Override
    public boolean isOpen()
    {
    	return opened;
    }

    @Override
    public boolean isSupported()
    {
        return SQLDatabaseFactory.isSupported();
    }
    
    @Override
    protected Transaction createTransaction(String[] storeNames, Mode mode)
    {
    	return new WSQLTransaction(this, storeNames, mode);
    }
    
    @Override
	protected void doOpen(final DatabaseCallback callback)
    {
		if (!initialized)
		{
			init(new SQLCallback()
			{
				@Override
				public void onSuccess()
				{
					openDB(callback);
				}
			});
		}
		else
		{
			openDB(callback);
		}
    }    	
    	
    private void openDB(final DatabaseCallback callback)
    {
		final SQLTransaction.SQLStatementErrorCallback errorCallback = new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, messages.databaseOpenError(getName(), error.getName()+"-"+error.getMessage()));
				}
				if (callback != null)
				{
					callback.onError(messages.databaseOpenError(getName(), error.getName()+"-"+error.getMessage()));
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(messages.databaseOpenError(getName(), error.getName()+"-"+error.getMessage()));
				}				
				return true;
			}
		};
		
		systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(SQLTransaction tx)
			{
				String sql = "SELECT * FROM dbVersions WHERE name = ?";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"]");
				}
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(name);
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						if (rs.getRows().length() > 0)
						{
							JsArrayMixed output = JsArrayMixed.createArray().cast();
							JsUtils.readPropertyValue(rs.getRows().itemObject(0), "version", output); 
							doOpenDB((int) output.getNumber(0), callback, errorCallback);
						}
						else
						{
							String sql = "INSERT INTO dbVersions VALUES (?,?)";
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.FINE, "Running SQL ["+sql+"]");
							}
							args.push(version);
							tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
							{
								@Override
								public void onSuccess(SQLTransaction tx, SQLResultSet rs)
								{
									doOpenDB(0, callback, errorCallback);
								}
							}, errorCallback);
						}
					}
				}, errorCallback);
			}
		}, null, null);
	}
    
	private void doOpenDB(final int oldVersion, final DatabaseCallback callback, final SQLTransaction.SQLStatementErrorCallback errorCallback)
    {
    	if (version <=0 || oldVersion > version)
    	{
    		callback.onError("Data Error - An attempt was made to open a database using a lower version than the existing version.");
    		return;
    	}
    	database = SQLDatabaseFactory.openDatabase(name, name, DEFAULT_DB_SIZE);
    	database.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			
			@Override
			public void onTransaction(SQLTransaction tx)
			{
				String sql = "CREATE TABLE IF NOT EXISTS __sys__ (name VARCHAR(255))";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"]");
				}
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						loadObjectStoreNames(new SQLCallback()
						{
							@Override
							public void onSuccess()
							{
								if (oldVersion < version)
								{
									updateDBVersionAndOpen(oldVersion, callback, errorCallback);
								}
								else
								{
									if (LogConfiguration.loggingIsEnabled())
									{
										logger.log(Level.INFO, messages.databaseOpened(getName()));
									}
									opened = true;
									callback.onSuccess();
								}
							}
						});
					}
				}, errorCallback);
			}
		}, null, null);
    }

    private void loadObjectStoreNames(final SQLCallback callback)
    {
    	database.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(final SQLTransaction tx)
			{
				String sql = "SELECT * FROM __sys__";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"]");
				}
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						objectStoreNames = CollectionFactory.createArray();
						
						int length = rs.getRows().length();
						for (int i = 0; i < length; i++)
						{
							String objectStoreName = JsUtils.readStringPropertyValue(rs.getRows().itemObject(i), "name");
							objectStoreNames.add(objectStoreName);
						}
						
						callback.onSuccess();
					}
				},new SQLTransaction.SQLStatementErrorCallback()
				{
					@Override
					public boolean onError(SQLTransaction tx, SQLError error)
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.SEVERE, messages.databaseOpenError(getName(), error.getName()+"-"+error.getMessage()));
						}
						throw new DatabaseException(messages.databaseOpenError(getName(), error.getName()+"-"+error.getMessage()));
					}
				});
			}
		}, new SQLDatabase.SQLTransactionErrorCallback()
		{
			@Override
			public void onError(SQLError error)
			{
				throw new DatabaseException("Error loading object store names. Error ["+error.getName()+"]. Message ["+error.getMessage()+"]");
			}
		}, null);
    }
    
	private void updateDBVersionAndOpen(final int oldVersion, final DatabaseCallback callback, final SQLTransaction.SQLStatementErrorCallback errorCallback)
    {
        systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
        {
        	@Override
        	public void onTransaction(SQLTransaction tx)
        	{
        		String sql = "UPDATE dbVersions SET version = ? WHERE name = ?";
        		if (LogConfiguration.loggingIsEnabled())
        		{
        			logger.log(Level.FINE, "Running SQL ["+sql+"]");
        		}
        		JsArrayMixed args = JsArrayMixed.createArray().cast();
        		args.push(version);
        		args.push(name);
        		tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
        		{
        			@Override
        			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
        			{
        				database.transaction(new SQLDatabase.SQLTransactionCallback()
						{
							
							@Override
							public void onTransaction(SQLTransaction tx)
							{
								try
								{
									updateDatabaseStructure(tx, callback);
								}
								catch (RuntimeException e) 
								{
									if (LogConfiguration.loggingIsEnabled())
									{
										logger.log(Level.SEVERE, messages.databaseUpgradeError(getName(), e.getMessage()), e);
									}
									throw e;
								}
							}
						}, new SQLDatabase.SQLTransactionErrorCallback()
						{
							
							@Override
							public void onError(SQLError error)
							{
								String message = messages.databaseUpgradeError(getName(), error.getMessage());
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.SEVERE, message);
								}
								throw new DatabaseException(message);
							}
						}, new SQLDatabase.SQLCallback()
						{
							@Override
							public void onSuccess()
							{
								opened = true;
								callback.onSuccess();
							}
						});
        			}
        		}, errorCallback);
        	}
        }, null, null);
    }	
    
	protected void doDelete(final DatabaseCallback callback)
    {
		final SQLTransaction.SQLStatementErrorCallback errorCallback = getDeleteErrorHandler(callback);
		doDelete(callback, errorCallback);
    }

	protected SQLStatementErrorCallback getDeleteErrorHandler(final DatabaseCallback callback)
    {
	    return new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, messages.databaseDeleteError(getName(), error.getName()+"-"+error.getMessage()));
				}
				if (callback != null)
				{
					callback.onError(messages.databaseDeleteError(getName(), error.getName()+"-"+error.getMessage()));
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(messages.databaseDeleteError(getName(), error.getName()+"-"+error.getMessage()));
				}				
				return true;
			}
		};
    }

    protected void doDelete(final DatabaseCallback callback, final SQLTransaction.SQLStatementErrorCallback errorCallback)
    {
	    systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(SQLTransaction tx)
			{
				String sql = "SELECT * FROM dbVersions WHERE name = ?";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"]");
				}
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(name);
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						if (rs.getRows().length() == 0)
						{
							if (callback != null)
							{
								callback.onSuccess();
							}
	        				return;
						}
						deleteDBTables(callback, errorCallback);
					}
				}, errorCallback);
			}
		}, null, null);
    }

	protected void deleteDBTables(SQLTransaction tx, final DatabaseCallback callback, final SQLStatementErrorCallback errorCallback, final boolean removeSystemTables)
	{
		String sql = "SELECT * FROM __sys__";
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL ["+sql+"]");
		}
		final JsArrayMixed args = JsArrayMixed.createArray().cast();
		tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				int length = rs.getRows().length();
				String sql;
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				for (int i=0; i< length; i++)
				{
					sql = "DROP TABLE "+JsUtils.readStringPropertyValue(rs.getRows().itemObject(i), "name");
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.FINE, "Running SQL ["+sql+"]");
					}
					tx.executeSQL(sql, args, null, errorCallback);
				}
				if (removeSystemTables)
				{
					sql = "DROP TABLE __sys__";
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.FINE, "Running SQL ["+sql+"]");
					}
					tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
					{
						@Override
						public void onSuccess(SQLTransaction tx, SQLResultSet rs)
						{
							deleteFromDbVersions(callback, errorCallback);
						}
					}, errorCallback);
				}
				else
				{
					callback.onSuccess();
				}
			}
		}, errorCallback);
	}
	
	protected void deleteDBTables(final DatabaseCallback callback, final SQLStatementErrorCallback errorCallback)
	{
    	final SQLDatabase database = SQLDatabaseFactory.openDatabase(name, name, DEFAULT_DB_SIZE);
    	database.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(final SQLTransaction tx)
			{
				deleteDBTables(tx, callback, errorCallback, true);
			}
		}, null, null);
	}
	
	protected void deleteFromDbVersions(final DatabaseCallback callback, final SQLStatementErrorCallback errorCallback)
	{
		systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(SQLTransaction tx)
			{
				String sql = "DELETE FROM dbVersions WHERE name = ? ";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"]");
				}
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(name);
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						callback.onSuccess();
					}
				}, errorCallback);
			}
		}, null, null);
	}    
	
	protected void createFileStore(SQLTransaction tx)
	{
		final SQLTransaction.SQLStatementErrorCallback errorCallback = new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				String message = messages.objectStoreOperationError(error.getName() + " - " + error.getMessage());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
				return true;
			}
		};

		String sql = "INSERT INTO __sys__(name) VALUES (?)";
		JsArrayMixed args = JsArrayMixed.createArray().cast();
		args.push(FileStore.OBJECT_STORE_NAME);
		tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				String sql = "CREATE TABLE \""+FileStore.OBJECT_STORE_NAME+"\" (value BLOB, fileName TEXT PRIMARY KEY)";
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				tx.executeSQL(sql, args, null, errorCallback);
			}
		}, errorCallback);

		
		
	}
	
	protected abstract void updateDatabaseStructure(SQLTransaction tx, DatabaseCallback callback);
	protected abstract <K, V> WSQLAbstractObjectStore<K, V> getObjectStore(String storeName, WSQLTransaction transaction);
	
	static interface SQLCallback
	{
		void onSuccess();
	}
}
