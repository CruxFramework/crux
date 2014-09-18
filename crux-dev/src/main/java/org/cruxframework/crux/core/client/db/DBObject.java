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

import com.google.gwt.logging.client.LogConfiguration;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * <p>Base class for object objects that perform operations on Crux Database.</p> 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class DBObject
{
	protected static Logger logger = Logger.getLogger(DBObject.class.getName());
	protected final AbstractDatabase db;

	/**
	 * Constructor
	 * @param db database reference
	 */
	protected DBObject(AbstractDatabase db)
	{
		this.db = db;
	}
	
	/**
	 * Report an error on a database operation
	 * @param callback called to handle the error
	 * @param error message describing the error occurred
	 * @param e the error
	 */
	protected void reportError(final Callback callback, String error, Exception e)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			if (e != null)
			{
				logger.log(Level.SEVERE, error, e);
			}
			else
			{
				logger.log(Level.SEVERE, error);
			}
		}
        if (callback != null)
		{
			callback.onError(error);
			callback.setDb(null);
		}
		else if (db.errorHandler != null)
		{
			db.errorHandler.onError(error);
		}
    }
}
