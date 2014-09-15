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
package org.cruxframework.crux.widgets.client.grid;

import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.datasource.DataSourceRecord;

import com.google.gwt.user.client.ui.Widget;

/**
 * Manages the logic for create, index and destroy the widgets
 * 	used as row's details.
 * 
 * @author Gesse Dafe
 */
public class RowDetailsManager 
{
	private Map<DataSourceRecord<?>, Boolean> loadedDetails;
	private final RowDetailWidgetCreator rowDetailWidgetCreator;
	
	/**
	 * @param rowDetailsDefinition
	 */
	public RowDetailsManager(RowDetailWidgetCreator rowDetailWidgetCreator) 
	{
		this.rowDetailWidgetCreator = rowDetailWidgetCreator;
		this.loadedDetails = new HashMap<DataSourceRecord<?>, Boolean>();
	}
	
	/**
	 * Clears the index of loaded details and destroys the related widgets.
	 */
	public void reset()
	{
		this.loadedDetails.clear();
	}
		
	/**
	 * Creates the widget to be used as a row detail
	 * @return
	 */
	public Widget createWidget(DataRow row)
	{
		return rowDetailWidgetCreator.createWidgetForRowDetail(row);
	}
	
	/**
	 * @return <code>true</code> if the details related to this record have already being loaded.
	 */
	public boolean isDetailLoaded(DataSourceRecord<?> record)
	{
		return this.loadedDetails.get(record) != null;
	}

	/**
	 * Marks the details of the given record as loaded.
	 * 
	 * @param record
	 */
	void setDetailLoaded(DataSourceRecord<?> record) 
	{
		this.loadedDetails.put(record, Boolean.TRUE);		
	}
}
