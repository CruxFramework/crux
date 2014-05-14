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
