package br.com.sysmap.crux.module.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Parameter;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.module.client.dto.PageParameter;
import br.com.sysmap.crux.module.client.remote.ModuleInfoServiceAsync;

@DataSource("parametersDS")
	@DataSourceBinding(identifier="name")
public class ParametersDS extends LocalBindableEditablePagedDataSource<PageParameter>
{
	@Create
	protected ModuleInfoServiceAsync moduleService;

	@Parameter(required=true)
	protected String module;
	
	@Parameter(required=true)
	protected String page;
	
	public void load()
	{
		moduleService.getPageParameters(module, page, new DataSourceAsyncCallbackAdapter<PageParameter>(this));
	}
}