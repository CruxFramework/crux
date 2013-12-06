/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.client.datasource;

import org.cruxframework.crux.core.client.Legacy;

/**
 * @author Thiago da Rosa de Bustamante
 * @deprecated Use RemoteStreamingDataSource instead
 */
@Deprecated
@Legacy
public abstract class RemoteBindableStreamingDataSource<T> extends RemoteStreamingDataSource<T>
{
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getBoundObject()
	 */
	public T getBoundObject()
	{
		return super.getBoundObject(getRecord());
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getBoundObject(org.cruxframework.crux.core.client.datasource.DataSourceRecord)
	 */
	public T getBoundObject(DataSourceRecord<T> record)
	{
		return super.getBoundObject(record);
	}
}
