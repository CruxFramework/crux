package org.cruxframework.crux.showcase.client.datasource;

import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import org.cruxframework.crux.core.client.datasource.RemoteStreamingDataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import org.cruxframework.crux.showcase.client.dto.Contact;
import org.cruxframework.crux.showcase.client.remote.StreamingGridServiceAsync;


/**
 * Grid Streaming DataSouce
 */
@DataSource("streamingGridDataSource")
@DataSourceRecordIdentifier("address.street, name")
public class StreamingGridDataSource extends RemoteStreamingDataSource<Contact> {
	
	@Create
	protected StreamingGridServiceAsync service;

	/**
	 * @see org.cruxframework.crux.core.client.datasource.RemoteDataSource#fetch(int, int)
	 */
	public void fetch(int startRecord, int endRecord) {
		service.fetchContacts(startRecord, endRecord, new DataSourceAsyncCallbackAdapter<Contact>(this));			
	}				
}
