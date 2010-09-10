package br.com.sysmap.crux.showcase.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.remote.SimpleGridServiceAsync;

@DataSource("simpleGridDataSource")
@DataSourceBinding(identifier="name")
public class SimpleGridDataSource extends LocalBindableEditablePagedDataSource<Contact> {
	
	@Create
	protected SimpleGridServiceAsync service;
	
	public void load()
	{
		service.getContactList(new DataSourceAsyncCallbackAdapter<Contact>(this));
	}		
}

