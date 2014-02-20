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

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SQLDatabase extends JavaScriptObject
{
	protected SQLDatabase(){}

	public final native void transaction(SQLTransactionCallback transactionCallback, SQLTransactionErrorCallback transactionErrorCallback, SQLCallback successCallback)/*-{
		this.transaction(function(tx){
			transactionCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLTransactionCallback::onTransaction(Lorg/cruxframework/crux/core/client/db/websql/SQLTransaction;)(tx);
		}, function(error){
			if (transactionErrorCallback)
			{
				transactionErrorCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLTransactionErrorCallback::onError(Lorg/cruxframework/crux/core/client/db/websql/SQLError;)(error);
			}
		}, function(){
			if (successCallback)
			{
				successCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLCallback::onSuccess()();
			}
		});
	}-*/;
	
	public final native void readTransaction(SQLTransactionCallback transactionCallback, SQLTransactionErrorCallback transactionErrorCallback, SQLCallback successCallback)/*-{
		this.readTransaction(function(tx){
			transactionCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLTransactionCallback::onTransaction(Lorg/cruxframework/crux/core/client/db/websql/SQLTransaction;)(tx);
		}, function(error){
			if (transactionErrorCallback)
			{
				transactionErrorCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLTransactionErrorCallback::onError(Lorg/cruxframework/crux/core/client/db/websql/SQLError;)(error);
			}
		}, function(){
			if (successCallback)
			{
				successCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLCallback::onSuccess()();
			}
		});
	}-*/;

	public final native void changeVersion(int oldVersion, int newVersion, SQLTransactionCallback transactionCallback, SQLTransactionErrorCallback transactionErrorCallback, SQLCallback successCallback)/*-{
		this.changeVersion(oldVersion, newVersion, function(tx){
			transactionCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLTransactionCallback::onTransaction(Lorg/cruxframework/crux/core/client/db/websql/SQLTransaction;)(tx);
		}, function(error){
			if (transactionErrorCallback)
			{
				transactionErrorCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLTransactionErrorCallback::onError(Lorg/cruxframework/crux/core/client/db/websql/SQLError;)(error);
			}
		}, function(){
			if (successCallback)
			{
				successCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabase.SQLCallback::onSuccess()();
			}
		});
	}-*/;

	public final native int getVersion()/*-{
		return this.version;
	}-*/;
	
	public static interface SQLTransactionCallback
	{
		void onTransaction(SQLTransaction tx);
	}
	
	public static interface SQLTransactionErrorCallback
	{
		void onError(SQLError error);
	}
	
	public static interface SQLCallback
	{
		void onSuccess();
	}
}
