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

/**
 * Database cursor operations callback. 
 * Use this callback to read objects from a {@link Cursor}.
 * @param <K> The type of the key used to identify objects into the cursor.
 * @param <V> The type of the objects referenced by this cursor 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class DatabaseCursorCallback<K, V> extends Callback
{
	/**
	 * Called when an open cursor operation is performed successfully.
	 * @param result the cursor
	 */
	public abstract void onSuccess(Cursor<K, V> result);
}
