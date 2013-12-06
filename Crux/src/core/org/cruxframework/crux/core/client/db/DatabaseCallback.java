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
 * Database operations callback. All db operations are asynchronous.
 * Use this callback to receive feedback for requested operations.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class DatabaseCallback extends Callback
{
	/**
	 * Called to inform that operation performed ok.
	 */
	public abstract void onSuccess();
}
