package br.com.sysmap.crux.module.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Parameter;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalPagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import br.com.sysmap.crux.module.client.dto.PageParameter;
import br.com.sysmap.crux.module.client.remote.ModuleInfoServiceAsync;

@DataSource("parametersDS")
	@DataSourceRecordIdentifier("name")
public class ParametersDS extends LocalPagedDataSource<PageParameter>
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