package br.com.sysmap.crux.module.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Parameter;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.module.client.dto.Page;
import br.com.sysmap.crux.module.client.remote.ModuleInfoServiceAsync;

@DataSource("pagesDS")
	@DataSourceBinding(identifier="name")
public class PagesDS extends LocalBindableEditablePagedDataSource<Page>
{
	@Parameter(required=true)
	protected String module;

	@Create
	protected ModuleInfoServiceAsync moduleService;
	
	public void load()
	{
		moduleService.getPages(module, new DataSourceAsyncCallbackAdapter<Page>(this));
	}
}