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
 * Handle all database errors, when no error function is specified on 
 * database callback operation.
 * @author Thiago da Rosa de Bustamante
 */
public interface DatabaseErrorHandler
{
	/**
	 * Called when an uncaught error occurred on a database operation.
	 * @param message error message
	 */
	void onError(String message);
	/**
	 * Called when an uncaught error occurred on a database operation.
	 * @param message error message
	 * @param t the error 
	 */
	void onError(String message, Throwable t);
}
