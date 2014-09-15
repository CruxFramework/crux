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

public abstract class RowDetailWidgetCreator 
{
	private Map<DataSourceRecord<?>, Map<String, Widget>> widgetsByRowAndOriginalId = new HashMap<DataSourceRecord<?>, Map<String, Widget>>();
	
	public abstract Widget createWidgetForRowDetail(DataRow row);
	
	protected void registerWidget(DataRow row, String widgetOriginalId, Widget widget)
	{
		DataSourceRecord<?> key = row.getDataSourceRecord();
		Map<String, Widget> widgetsById = widgetsByRowAndOriginalId.get(key);
		if(widgetsById == null)
		{
			widgetsById = new HashMap<String, Widget>();
			widgetsByRowAndOriginalId.put(key, widgetsById);
		}
		widgetsById.put(widgetOriginalId, widget);
	}

	@SuppressWarnings("unchecked")
	public <T extends Widget> T getWidget(DataRow row, String id)
	{
		T result = null;
		Map<String, Widget> widgets = widgetsByRowAndOriginalId.get(row.getDataSourceRecord());
		if(widgets != null)
		{
			result = (T) widgets.get(id);
		}
		return result;
	}
}
