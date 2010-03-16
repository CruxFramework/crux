package br.com.sysmap.crux.module.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Parameter;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.module.client.dto.Datasource;
import br.com.sysmap.crux.module.client.remote.ModuleInfoServiceAsync;

@DataSource("datasourcesDS")
	@DataSourceBinding(identifier="name")
public class DatasourcesDS extends LocalBindableEditablePagedDataSource<Datasource>
{
	@Parameter(required=true)
	protected String module;

	@Create
	protected ModuleInfoServiceAsync moduleService;
	
	public void load()
	{
		moduleService.getDatasources(module, new DataSourceAsyncCallbackAdapter<Datasource>(this));
	}
}