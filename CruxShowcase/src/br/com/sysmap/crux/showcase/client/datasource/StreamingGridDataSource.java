package br.com.sysmap.crux.showcase.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.RemoteStreamingDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.remote.StreamingGridServiceAsync;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DataSource("streamingGridDataSource")
@DataSourceRecordIdentifier("name")
public class StreamingGridDataSource extends RemoteStreamingDataSource<Contact> {
	
	@Create
	protected StreamingGridServiceAsync service;

	public void fetch(int startRecord, int endRecord)
	{
		service.fetchContacts(startRecord, endRecord, new DataSourceAsyncCallbackAdapter<Contact>(this));			
	}				
}
