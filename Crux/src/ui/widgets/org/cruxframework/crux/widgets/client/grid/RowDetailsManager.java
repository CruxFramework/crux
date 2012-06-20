package org.cruxframework.crux.widgets.client.grid;

import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.datasource.DataSourceRecord;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.Widget;

/**
 * Manages the logic for create, index and destroy the widgets
 * 	used as row's details.
 * 
 * @author Gesse Dafe
 */
public class RowDetailsManager 
{
	private static long nextRowDetailId = 0;
	private Map<DataSourceRecord<?>, Boolean> loadedDetails;
	private FastList<String> detailWidgetsIds;
	private final RowDetailWidgetCreator rowDetailWidgetCreator;
	
	/**
	 * @param rowDetailsDefinition
	 */
	public RowDetailsManager(RowDetailWidgetCreator rowDetailWidgetCreator) 
	{
		this.rowDetailWidgetCreator = rowDetailWidgetCreator;
		this.detailWidgetsIds = new FastList<String>();
		this.loadedDetails = new HashMap<DataSourceRecord<?>, Boolean>();
	}
	
	/**
	 * Clears the index of loaded details and destroys the related widgets.
	 */
	public void reset()
	{
		this.loadedDetails.clear();
		clearRendering();
	}
	
	/**
	 * Destroys the details widgets
	 */
	public void clearRendering()
	{
		for(int i = 0; i < this.detailWidgetsIds.size(); i++)
		{
			String id = this.detailWidgetsIds.get(i);
			if(Screen.get(id) != null)
			{
				Screen.remove(id);
			}
		}
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
