package org.cruxframework.crux.core.client.dataprovider;

import org.cruxframework.crux.core.client.event.BaseEvent;

public class DataChangedEvent extends BaseEvent<DataProvider<?>>
{
	private DataProviderRecord<?> currentRecord;
	
	protected DataChangedEvent(DataProvider<?> source,  DataProviderRecord<?> currentRecord)
    {
	    super(source);
	    this.currentRecord = currentRecord;
    }

	public DataProviderRecord<?> getCurrentRecord()
	{
		return currentRecord;
	}
}
