/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.deviceadaptivegrid;


import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinitions;


/**
 * @author wesley.diniz
 *
 */
public class DeviceAdaptiveGridColumnDefinitions
{
	private ColumnDefinitions largeColumnDefinitions = new ColumnDefinitions();
	private ColumnDefinitions smallColumnDefinitions = new ColumnDefinitions();


	/**
	 * Register a new column definition
	 * @param size
	 * @param key
	 * @param definition
	 */
	public void add(Size deviceSize, String key, ColumnDefinition definition)
	{
		if (Size.large.equals(deviceSize))
		{
			largeColumnDefinitions.add(key, definition);
		}
		else
		{
			smallColumnDefinitions.add(key, definition);
		}
	}

	/**
	 * @return Large columns definitions
	 **/
	public ColumnDefinitions getLargeColumnDefinitions()
	{
		return largeColumnDefinitions;
	}

	/**
	 * @return Small columns definitions
	 **/
	public ColumnDefinitions getSmallColumnDefinitions()
	{
		return smallColumnDefinitions;
	}

}