package org.cruxframework.crux.showcase.client.datasource;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.datasource.LocalPagedDataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.showcase.client.dto.Contact;
import org.cruxframework.crux.showcase.client.remote.SimpleGridServiceAsync;


@DataSource("simpleGridDataSource")
@DataSourceRecordIdentifier("name")
public class SimpleGridDataSource extends LocalPagedDataSource<Contact> {
	
	@Create
	protected SimpleGridServiceAsync service;
	
	public void load()
	{
		service.getContactList(new AsyncCallbackAdapter<ArrayList<Contact>>(this)
		{
			@Override
			public void onComplete(ArrayList<Contact> result)
			{
				updateData(result);
			}
		});
	}		
}

