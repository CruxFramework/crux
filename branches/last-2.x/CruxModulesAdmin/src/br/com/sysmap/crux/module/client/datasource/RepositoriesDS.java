package br.com.sysmap.crux.module.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.module.client.dto.Repository;
import br.com.sysmap.crux.module.client.remote.LoginServiceAsync;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DataSource("repositoriesDS")
@DataSourceBinding(identifier="url")
public class RepositoriesDS extends LocalBindableEditablePagedDataSource<Repository>
{
	@Create
	protected LoginServiceAsync loginService;
	
	public void load()
	{
		loginService.getRegisteredRepositories(new DataSourceAsyncCallbackAdapter<Repository>(this));
	}
}