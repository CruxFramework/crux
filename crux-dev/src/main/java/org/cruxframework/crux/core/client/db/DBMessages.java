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

import com.google.gwt.i18n.client.Messages;

/**
 * Resource messages used on Crux database system.
 * @author Thiago da Rosa de Bustamante
 */
public interface DBMessages extends Messages
{
	@DefaultMessage("Error retrieving object from database. Error [{0}]")
	String objectStoreGetError(String errorName);
	
	@DefaultMessage("Error removing object from database. Error [{0}]")
	String objectStoreDeleteError(String errorName);
	
	@DefaultMessage("Error writing object into database. Error [{0}]")
	String objectStoreWriteError(String errorName);
	
	@DefaultMessage("Error on database cursor. Error name [{0}]")
	String objectStoreCursorError(String errorName);

	@DefaultMessage("Database [{0}] opened.")
	String databaseOpened(String name);

	@DefaultMessage("Database [{0}] is blocked.")
	String databaseBlocked(String name);

	@DefaultMessage("Error opening Database [{0}]. Error [{1}]")
	String databaseOpenError(String databaseName, String errorName);
	
	@DefaultMessage("Browser is using an outdated Database [{0}]. Upgrading database structure.")
	String databaseUpgrading(String name);
	
	@DefaultMessage("Browser Database upgraded [{0}].")
	String databaseUpgraded(String name);

	@DefaultMessage("Error Upgrading Database [{0}]. Error message[{1}]")
	String databaseUpgradeError(String name, String message);

	@DefaultMessage("Error removing Database [{0}]. Error [{1}]")
	String databaseDeleteError(String databaseName, String errorName);

	@DefaultMessage("Can not found objectStore for type [{1}] on database [{0}]")
	String databaseObjectStoreNotFoundError(String databaseName, String storeName);

	@DefaultMessage("Database is not opened.")
	String databaseNotOpenedError();

	@DefaultMessage("Transaction on Database [{0}] is not active.")
	String databaseTransactionInactive(String name);

	@DefaultMessage("Transaction abborted on Database [{0}].")
	String databaseTransactionAborted(String name);

	@DefaultMessage("Transaction completed on Database [{0}].")
	String databaseTransactionCompleted(String name);
	
	@DefaultMessage("Transaction Error on Database [{0}]. Error [{1}].")
	String databaseTransactionError(String databaseName, String errorName);

	@DefaultMessage("Error counting object store items. Error [{0}].")
	String objectStoreCountError(String name);

	@DefaultMessage("Error clearing object store items. Error [{0}].")
	String objectStoreClearError(String message);
	
	@DefaultMessage("Error changing database property for Database[{0}]. This operation can not be performed on an open database.")
	String databaseSetPropertyOnOpenDBError(String name);

	@DefaultMessage("Invalid database name [{0}].")
	String databaseInvalidNameDBError(String name);

	@DefaultMessage("Database [{0}] is already open.")
	String databaseIAlreadyOpenDBError(String name);
	
	@DefaultMessage("Error opening Database [{0}]. Can not load database script")
	String databaseLoadingError(String databaseName);

	@DefaultMessage("IndexedDB not supported. Using Web SQL for persistence.")
	String databaseUsingWebSQL();

	@DefaultMessage("Crux database is not supported on this browser.")
	String databaseNotSupportedError();

	@DefaultMessage("Object Store [{0}] not found in this transaction. Make sure that you inform this objectStore name when transaction was created.")
	String databaseTransactionStoreNotFound(String storeName);

	@DefaultMessage("Transaction on database [{0}] tries to execute an operation not supported for its transaction Mode (eg. writes on readOnly transactions).")
	String databaseTransactionNotSupportedOperation(String name);

	@DefaultMessage("Error executing operation on database. Error [{0}]")
	String objectStoreOperationError(String error);

	@DefaultMessage("Can not derive key for objectStore [{0}].")
	String objectStoreDeriveKeyError(String storeName);
}
