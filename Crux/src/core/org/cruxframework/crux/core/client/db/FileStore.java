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
import org.cruxframework.crux.core.client.file.Blob;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface FileStore 
{
	public static final String OBJECT_STORE_NAME = "_CRUX_FILE_STORE_";

	void add(Blob file, String fileName);
	void put(Blob file, String fileName);
	void add(final Blob file, final String fileName, final DatabaseWriteCallback<String> callback);
	void put(final Blob file, final String fileName, final DatabaseWriteCallback<String> callback);
	void get(String key, DatabaseRetrieveCallback<Blob> callback);
	void delete(String key, DatabaseDeleteCallback callback);
	void delete(KeyRange<String> keyRange);
	void delete(KeyRange<String> keyRange, DatabaseDeleteCallback callback);
	void clear();
	void openCursor(FileStoreCursorCallback callback);
	void openCursor(KeyRange<String> keyRange, FileStoreCursorCallback callback);
	void openCursor(KeyRange<String> keyRange, CursorDirection direction, FileStoreCursorCallback callback);
	void count(DatabaseCountCallback callback);
	void count(KeyRange<String> range, DatabaseCountCallback callback);
	KeyRangeFactory<String> getKeyRangeFactory();
}
