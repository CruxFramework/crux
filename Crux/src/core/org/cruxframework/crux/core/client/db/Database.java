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

import org.cruxframework.crux.core.client.db.Transaction.TransactionCallback;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef;

import com.google.gwt.dom.client.PartialSupport;

/**
 * A Crux client database. Uses IndexedDB like interface to store objects on application's client side.
 * To declare a new database, create a new interface extending Database and use {@link DatabaseDef} 
 * annotation on it to specify database structure.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@PartialSupport
public interface Database
{
	/**
	 * Return true if the current database is open.
	 * @return
	 */
	boolean isOpen();
	
	/**
	 * Retrieve the database name. This information is extracted from {@code @}DatabaseDef annotation
	 * @return
	 */
	String getName(); 

	/**
	 * Change the database name. This operation can not be executed on an open database.
	 * @return
	 */
	void setName(String newName) throws DatabaseException; 

	/**
	 * Change the database version. This operation can not be executed on an open database.
	 * @return
	 */
	void setVersion(int newVersion) throws DatabaseException; 

	/**
	 * Retrieve the database version. This information is extracted from {@code @}DatabaseDef annotation
	 * @return
	 */
	int getVersion(); 

	/**
	 * Open the database. If it does not exists, create a new database.
	 * @param callback - called when operation is completed
	 */
	void open(final DatabaseCallback callback);

	/**
	 * Close the current database.
	 */
	void close();

	/**
	 * Remove the current database from client browser.
	 * @param callback - called when operation is completed
	 */
	void delete(final DatabaseCallback callback);
	
	/**
	 * Create a new transaction targeting the given objectStores.
	 * @param storeNames
	 * @param mode
	 * @return
	 */
	Transaction getTransaction(String[] storeNames, Transaction.Mode mode);

	/**
	 * Create a new transaction targeting the given objectStores.
	 * @param storeNames
	 * @param mode
	 * @param callback
	 * @return
	 */
	Transaction getTransaction(String[] storeNames, Transaction.Mode mode, TransactionCallback callback);

	/**
	 * Insert all objects into its associated objectStore. If no objectStore is associated with object store, a DatabaseException is threw
	 * @param <V>
	 * @param object
	 * @param callback
	 */
	<V> void add(V[] objects, String objectStore, DatabaseCallback callback);

	/**
	 * Update all received objects into its associated objectStore. If one object does not exists, create a new one.
	 * If no objectStore is associated with object store, a DatabaseException is threw  
	 * @param <K>
	 * @param <V>
	 * @param object
	 * @param objectStore
	 * @param callback
	 */
	<V> void put(V[] objects, String objectStore, DatabaseCallback callback);

    /**
     * Retrieve the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object store, a DatabaseException is threw  
	 * @param <K>
	 * @param <V>
     * @param key
     * @param objectStore
     * @param callback
     */
	<K, V> void get(K key, String objectStore, DatabaseRetrieveCallback<V> callback);
	
    /**
     * Remove the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object store, a DatabaseException is threw  
	 * @param <K>
     * @param compositeKey
     * @param objectStore
     * @param callback
     */
	<K> void delete(K key, String objectStore, DatabaseCallback callback);

    /**
     * Remove all objects in the given range from its associated objectStore. 
	 * If no objectStore is associated with object store, a DatabaseException is threw  
	 * @param <K>
     * @param compositeKey
     * @param objectStore
     * @param callback
     */
	<K> void delete(KeyRange<K> keyRange, String objectStore, DatabaseCallback callback);

	/**
	 * An error handler called to handle uncaught errors. 
	 * @param errorHandler
	 */
	void setDefaultErrorHandler(DatabaseErrorHandler errorHandler);
	
	/**
	 * Return true if Crux Database is supported by current browser.
	 * @return
	 */
	boolean isSupported();
	
	/**
	 * Forces Crux to use WEB SQL implementation for its database.
	 * This method should be called only for tests purposes. Crux already 
	 * can detect and choose the better native implementation. 
	 */
	void useWebSQL();

	/**
	 * Forces Crux to use Indexed DB implementation for its database.
	 * This method should be called only for tests purposes. Crux already 
	 * can detect and choose the better native implementation. 
	 */
	void useIndexedDB();
}
