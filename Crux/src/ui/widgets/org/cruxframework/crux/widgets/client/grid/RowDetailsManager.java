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
