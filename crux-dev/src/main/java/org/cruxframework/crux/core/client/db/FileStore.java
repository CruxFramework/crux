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
import org.cruxframework.crux.core.client.db.Transaction.Mode;
import org.cruxframework.crux.core.client.file.Blob;

/**
 * A Store for files on Crux Database. Use this to work with files on database. 
 * <p>
 * See the following example:
 * <pre>
 * {@link Transaction} transaction = database.getTransaction(new String[]{FileStore.OBJECT_STORE_NAME}, {@link Mode}.readWrite);
 * FileStore store = transaction.getFileStore();
 * store.openCursor(new {@link FileStoreCursorCallback}(){
 *   @Override
 *   public void onSuccess(FileCursor result){
 *     if (result != null && result.getValue() != null) {
 *       String fileName = result.getKey();
 *       Blob file = result.getValue();
 *       continueCusror();
 *     }
 *   }
 * });
 * </pre>
 * </p>
 * @author Thiago da Rosa de Bustamante
 * @see ObjectStore
 */
public interface FileStore 
{
	/**
	 * The name of the object store used to store the files on Crux Database. Use this constant when opening 
	 * a transaction to access the file store object.
	 */
	public static final String OBJECT_STORE_NAME = "_CRUX_FILE_STORE_";

	/**
	 * Insert a new file into the database.
	 * @param file file to store
	 * @param fileName file name
	 */
	void add(Blob file, String fileName);
	/**
	 * If database already contains a file stored with the given fileName, update the file stored. 
	 * Otherwise, Insert a new file into the database.
	 * @param file file to store
	 * @param fileName file name
	 */
	void put(Blob file, String fileName);
	/**
	 * Insert a new file into the database.
	 * @param file file to store
	 * @param fileName file name
	 * @param callback called when the operation complete
	 */
	void add(final Blob file, final String fileName, final DatabaseWriteCallback<String> callback);
	/**
	 * If database already contains a file stored with the given fileName, update the file stored. 
	 * @param file file to store
	 * @param fileName file name
	 * @param callback called when the operation complete
	 */
	void put(final Blob file, final String fileName, final DatabaseWriteCallback<String> callback);
	/**
	 * Retrieve a file from the database.
	 * @param key the fileName used to insert the object
	 * @param callback called when the operation complete
	 */
	void get(String key, DatabaseRetrieveCallback<Blob> callback);
	/**
	 * Remove the file from the database
	 * @param key the fileName used to insert the object
	 * @param callback called when the operation complete
	 */
	void delete(String key, DatabaseDeleteCallback callback);
	/**
	 * Remove a range of files from the database
	 * @param keyRange an object specifying the fileNames range to exclude
	 */
	void delete(KeyRange<String> keyRange);
	/**
	 * Remove a range of files from the database
	 * @param keyRange an object specifying the fileNames range to exclude
	 * @param callback called when the operation complete
	 */
	void delete(KeyRange<String> keyRange, DatabaseDeleteCallback callback);
	/**
	 * Removes all files from the database FileStore
	 */
	void clear();
	/**
	 * Open a {@link FileCursor} object, to allow iteration over the files on this store.
	 * @param callback called when the operation complete
	 */
	void openCursor(FileStoreCursorCallback callback);
	/**
	 * Open a {@link FileCursor} object, to allow iteration over the files on this store.
	 * @param keyRange an object specifying the fileNames range to appear on iteration
	 * @param callback called when the operation complete
	 */
	void openCursor(KeyRange<String> keyRange, FileStoreCursorCallback callback);
	/**
	 * Open a {@link FileCursor} object, to allow iteration over the files on this store.
	 * @param keyRange an object specifying the fileNames range to appear on iteration
	 * @param direction specifies the cursor iteration direction
	 * @param callback called when the operation complete
	 */
	void openCursor(KeyRange<String> keyRange, CursorDirection direction, FileStoreCursorCallback callback);
	/**
	 * Check the number of files on the fileStore
	 * @param callback called when the operation complete
	 */
	void count(DatabaseCountCallback callback);
	/**
	 * Check the number of files on the fileStore
	 * @param keyRange an object specifying the fileNames range to count
	 * @param callback called when the operation complete
	 */
	void count(KeyRange<String> keyRange, DatabaseCountCallback callback);
	/**
	 * Creates a factory for {@link KeyRange} objects used by this store.
	 * @return the factory
	 */
	KeyRangeFactory<String> getKeyRangeFactory();
}
