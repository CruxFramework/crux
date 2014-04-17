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
 * Define a callback for database operations.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Callback
{
	protected AbstractDatabase db;
	
	/**
	 * Called by Crux to bind the callback object to the database.
	 * @param db database that performed the operation handled by this callback. 
	 */
	protected void setDb(AbstractDatabase db)
	{
		this.db = db;
	}

	/**
	 * Called when an unexpected error occur. 
	 * @param message error message
	 */
	public void onError(String message)
	{
		if (db.errorHandler != null)
		{
			db.errorHandler.onError(message);
		}
	}
}
