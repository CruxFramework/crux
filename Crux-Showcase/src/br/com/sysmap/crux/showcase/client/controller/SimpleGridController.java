package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.remote.SimpleGridServiceAsync;
import br.com.sysmap.crux.widgets.client.grid.impl.Grid;

@Controller("simpleGridController")
public class SimpleGridController {
	
	@Expose
	public void onLoad() {
		Grid grid = Screen.get("simpleGrid", Grid.class);
		grid.loadData();
	}
	
	@DataSource("simpleGridDataSource")
	@DataSourceBinding(identifier="name")
	public static class SimpleGridDataSource extends LocalBindableEditablePagedDataSource<Contact> {
		
		@Create
		protected SimpleGridServiceAsync service;
		
		public void load()
		{
			service.getContactList(new DataSourceAsyncCallbackAdapter<Contact>(this));
		}		
	}
}