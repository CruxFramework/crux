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
 * Database write operation callback. 
 * Use this callback to retrieve the key from object writen into the stores.
 * @param <K> The type of the key returned to this callback.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class DatabaseWriteCallback<T> extends Callback
{
	/**
	 * Called after a successfully write operation into the database (an add or put operation). 
     * @param <T> The type of the key assigned to the writen object.
	 * @param result the object to be written
	 */
	public abstract void onSuccess(T result);
}
