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

import org.cruxframework.crux.core.client.db.Transaction.Mode;
import org.cruxframework.crux.core.client.file.Blob;

/**
 * Represents a cursor of files stored into the database.
 * 
 * <p>
 * To open a file cursor, you must ask to a {@link FileStore} object. 
 * </p>
 * <p>
 * See the following example:
 * <pre>
 * {@link Transaction} transaction = database.getTransaction(new String[]{FileStore.OBJECT_STORE_NAME}, {@link Mode}.readWrite);
 * transaction.getFileStore().openCursor(new {@link FileStoreCursorCallback}(){
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
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface FileCursor extends Cursor<String, Blob>
{

}
