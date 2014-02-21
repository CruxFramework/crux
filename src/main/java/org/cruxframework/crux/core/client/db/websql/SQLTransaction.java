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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SQLTransaction extends JavaScriptObject
{
	protected SQLTransaction(){}
	
	public final native void executeSQL(String sqlStatement, JsArrayMixed args, SQLStatementCallback stmtCallback, SQLStatementErrorCallback errorCallback)/*-{
		this.executeSql(sqlStatement, args, function(tx,rs){
			if (stmtCallback)
			{
				stmtCallback.@org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementCallback::onSuccess(Lorg/cruxframework/crux/core/client/db/websql/SQLTransaction;Lorg/cruxframework/crux/core/client/db/websql/SQLResultSet;)(tx,rs);
			}
		}, function(tx, error){
			if (errorCallback)
			{
				return errorCallback.@org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementErrorCallback::onError(Lorg/cruxframework/crux/core/client/db/websql/SQLTransaction;Lorg/cruxframework/crux/core/client/db/websql/SQLError;)(tx,error);
			}
		});
	}-*/;
	
	public static interface SQLStatementCallback
	{
		void onSuccess(SQLTransaction tx, SQLResultSet rs);
	}
	
	public static interface SQLStatementErrorCallback
	{
		/**
		 * Return true if the transaction must be rolled back
		 * @param tx
		 * @param error
		 * @return
		 */
		boolean onError(SQLTransaction tx, SQLError error);
	}
}
