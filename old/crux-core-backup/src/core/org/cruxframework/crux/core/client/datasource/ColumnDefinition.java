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

/**
 * Metadata information about a DataSource column
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ColumnDefinition<V, R>
{
	private String name;
	private boolean sortable;
	
	/**
	 * @param name
	 * @param sortable
	 */
	public ColumnDefinition(String name, boolean sortable)
	{
		this.name = name;
		this.sortable = sortable;
	}
	
	/**
	 * Extract the value associated with this column from the given 
	 * dataSource record object.
	 * @param recordObject
	 * @return
	 */
	public abstract V getValue(R recordObject);
	
	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return
	 */
	public boolean isSortable()
	{
		return sortable;
	}
}