package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.RemoteBindableEditableStreamingDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.remote.StreamingGridServiceAsync;
import br.com.sysmap.crux.widgets.client.grid.impl.Grid;

@Controller("streamingGridController")
public class StreamingGridController {
	
	@Expose
	public void onLoad() {
		Grid grid = Screen.get("streamingGrid", Grid.class);
		grid.loadData();
	}
	
	@DataSource("streamingGridDataSource")
	@DataSourceBinding(identifier="name")
	public static class StreamingGridDataSource extends RemoteBindableEditableStreamingDataSource<Contact> {
		
		@Create
		protected StreamingGridServiceAsync service;

		public void fetch(int startRecord, int endRecord)
		{
			service.fetchContacts(startRecord, endRecord, new DataSourceAsyncCallbackAdapter<Contact>(this));			
		}				
	}
}