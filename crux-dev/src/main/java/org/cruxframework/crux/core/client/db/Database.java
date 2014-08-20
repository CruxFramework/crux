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

import java.util.List;

import org.cruxframework.crux.core.client.db.Transaction.TransactionCallback;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.ObjectStoreDef;

import com.google.gwt.dom.client.PartialSupport;

/**
 * <p>
 * A Crux client database. Uses IndexedDB (http://www.w3.org/TR/IndexedDB/) like interface to store objects on application's client side.
 * To declare a new database, create a new interface extending Database and use {@link DatabaseDef} 
 * annotation on it to specify database structure.
 * </p>
 * <p>
 * See the following example:
 * <pre>
 * {@code @}{@link DatabaseDef}(name="CruxCompanyDatabase", version=1, defaultErrorHandler=CompanyDatabase.ErrorHandler.class, 
 *              objectStores={{@code @}{@link ObjectStoreDef}(targetClass=Person.class)})
 * public interface CompanyDatabase extends Database{
 *   public static class ErrorHandler implements {@link DatabaseErrorHandler} {
 *     {@code @}Override
 *     public void onError(String message) {
 *        Crux.getErrorHandler().handleError(message);
 *     }
 *     {@code @}Override
 *     public void onError(String message, Throwable t) {
 *        Crux.getErrorHandler().handleError(message, t);
 *     }
 *   }
 * }
 * </pre>
 * </p>
 * <p>
 * To use the database, just call GWT.create on the given interface, or inject it on 
 * your class. 
 * <pre>
 * public class MyController {
 *    {@code @Inject}
 *    private CompanyDatabase database;
 *    
 *    {@code @Expose}
 *    public void myMethod() {
 *       database.open(new {@link DatabaseCallback}(){
 *         public void onSuccess(){
 *            Window.alert("database ready for use");
 *         }
 *       });
 *    }
 * }
 * </pre>
 * </p>
 * 
 * @author Thiago da Rosa de Bustamante
 */
@PartialSupport
public interface Database
{
	/**
	 * Return true if the current database is open.
	 * @return true if open
	 */
	boolean isOpen();
	
	/**
	 * Retrieve the database name. This information is extracted from {@code @}{@link DatabaseDef} annotation
	 * @return database name
	 */
	String getName(); 

	/**
	 * Change the database name. This operation can not be executed on an open database.
	 * @param newName new database name
	 */
	void setName(String newName) throws DatabaseException; 

	/**
	 * Change the database version. This operation can not be executed on an open database.
	 * @param newVersion new database version
	 */
	void setVersion(int newVersion) throws DatabaseException; 

	/**
	 * Retrieve the database version. This information is extracted from {@code @}{@link DatabaseDef} annotation
	 * @return database version
	 */
	int getVersion(); 

	/**
	 * Open the database. If it does not exists, create a new database.
	 * @param callback called when operation is completed
	 */
	void open(final DatabaseCallback callback);

	/**
	 * Close the current database.
	 */
	void close();

	/**
	 * Remove the current database from client browser.
	 * @param callback called when operation is completed
	 */
	void delete(final DatabaseCallback callback);
	
	/**
	 * Create a new transaction targeting the given objectStores.
	 * @param storeNames stores referenced by the transaction. You can not use any object store inside your transaction if it is not listed here.
	 * @param mode transaction mode. See {@link Mode} for available modes
	 * @return the transaction
	 */
	Transaction getTransaction(String[] storeNames, Transaction.Mode mode);

	/**
	 * Create a new transaction targeting the given objectStores. If an unknown object store is informed, a DatabaseException is threw
	 * @param storeNames stores referenced by the transaction. You can not use any object store inside your transaction if it is not listed here.
	 * @param mode transaction mode. See {@link Mode} for available modes
	 * @param callback called when operation is completed
	 * @return the transaction
	 * @throws DatabaseException if an unknown object store is informed
	 */
	Transaction getTransaction(String[] storeNames, Transaction.Mode mode, TransactionCallback callback);

	/**
	 * Insert all objects into its associated objectStore. If no objectStore is associated with object informed, a {@link DatabaseException} is threw
	 * @param <V> object type
	 * @param objectStore object store name, where objects will be inserted
	 * @param objects objects to be inserted
	 * @param callback called when operation is completed
	 * @throws DatabaseException if no objectStore is associated with object informed
	 */
	<V> void add(V[] objects, String objectStore, DatabaseCallback callback);


	/**
	 * Insert all objects into its associated objectStore. If no objectStore is associated with object informed, a {@link DatabaseException} is threw
	 * @param <V> object type
	 * @param objectStore object store name, where objects will be inserted
	 * @param objects objects to be inserted
	 * @param callback called when operation is completed
	 * @throws DatabaseException if no objectStore is associated with object informed
	 */
	<V> void add(List<V> objects, String objectStore, DatabaseCallback callback);

	/**
	 * Update all received objects into its associated objectStore. If one object does not exists, create a new one.
	 * If no objectStore is associated with object store, a {@link DatabaseException} is threw  
	 * @param <V> object type
	 * @param objectStore object store name, where objects will be saved
	 * @param objects objects to be saved
	 * @param callback called when operation is completed
	 * @throws DatabaseException if no objectStore is associated with object informed
	 */
	<V> void put(V[] objects, String objectStore, DatabaseCallback callback);

	/**
	 * Update all received objects into its associated objectStore. If one object does not exists, create a new one.
	 * If no objectStore is associated with object store, a {@link DatabaseException} is threw  
	 * @param <V> object type
	 * @param objectStore object store name, where objects will be saved
	 * @param objects objects to be saved
	 * @param callback called when operation is completed
	 * @throws DatabaseException if no objectStore is associated with object informed
	 */
	<V> void put(List<V> objects, String objectStore, DatabaseCallback callback);

	/**
     * Retrieve the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object store, a {@link DatabaseException} is threw  
	 * @param <K> key type
	 * @param <V> object type
     * @param key object key
	 * @param objectStore object store name, where objects will be loaded from
	 * @param callback called when operation is completed
	 * @throws DatabaseException if no objectStore is associated with object informed
     */
	<K, V> void get(K key, String objectStore, DatabaseRetrieveCallback<V> callback);
	
    /**
     * Remove the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object store, a {@link DatabaseException} is threw  
	 * @param <K> key type
     * @param key object key
	 * @param objectStore object store name, where objects will be loaded from
	 * @param callback called when operation is completed
	 * @throws DatabaseException if no objectStore is associated with object informed
     */
	<K> void delete(K key, String objectStore, DatabaseCallback callback);

    /**
     * Remove all objects in the given range from its associated objectStore. 
	 * If no objectStore is associated with object store, a {@link DatabaseException} is threw  
	 * @param <K> key type
     * @param keyRange object key range
	 * @param objectStore object store name, where objects will be loaded from
	 * @param callback called when operation is completed
	 * @throws DatabaseException if no objectStore is associated with object informed
     */
	<K> void delete(KeyRange<K> keyRange, String objectStore, DatabaseCallback callback);

	/**
	 * Sets an error handler to be called to handle uncaught errors. 
	 * @param errorHandler the error handler
	 */
	void setDefaultErrorHandler(DatabaseErrorHandler errorHandler);
	
	/**
	 * Return true if Crux Database is supported by current browser.
	 * @return true if supported
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
