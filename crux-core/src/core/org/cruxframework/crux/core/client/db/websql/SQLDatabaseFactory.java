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
package org.cruxframework.crux.core.client.db.websql;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.db.DBMessages;
import org.cruxframework.crux.core.client.db.DatabaseException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class SQLDatabaseFactory
{
	protected static Logger logger = Logger.getLogger(SQLDatabaseFactory.class.getName());
	public static SQLDatabase openDatabase(String name, String displayName, int estimatedSize)
	{
		return openDatabase(name, displayName, estimatedSize, null);
	}

	public static SQLDatabase openDatabase(String name, String displayName, int estimatedSize, DatabaseCallback creationCallback)
	{
		try
		{
			return openDatabaseNative(name, displayName, estimatedSize, null);
		}
		catch (Exception e) 
		{
			DBMessages messages = GWT.create(DBMessages.class);
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.SEVERE, messages.databaseOpenError(name, e.getMessage()), e);
			}
			throw new DatabaseException(messages.databaseOpenError(name, e.getMessage()), e);
		}
	}
	
	private static native SQLDatabase openDatabaseNative(String name, String displayName, int estimatedSize, DatabaseCallback creationCallback)/*-{
		return $wnd.openDatabase(name, '', displayName, estimatedSize, function(db){
			if (creationCallback)
			{
				creationCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabaseFactory.DatabaseCallback::onCreated(Lorg/cruxframework/crux/core/client/db/websql/SQLDatabase;)(db);
			}
		});
	}-*/;
	
	public static native boolean isSupported()/*-{
		var sqlsupport = !!$wnd.openDatabase;
		return sqlsupport;
	}-*/;
	
	public static interface DatabaseCallback
	{
		void onCreated(SQLDatabase db);
	}
}
