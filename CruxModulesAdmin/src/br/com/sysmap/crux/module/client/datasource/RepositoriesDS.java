package br.com.sysmap.crux.module.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.LocalPagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import br.com.sysmap.crux.module.client.dto.Repository;
import br.com.sysmap.crux.module.client.remote.LoginServiceAsync;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DataSource("repositoriesDS")
@DataSourceRecordIdentifier("url")
public class RepositoriesDS extends LocalPagedDataSource<Repository>
{
	@Create
	protected LoginServiceAsync loginService;
	
	public void load()
	{
		loginService.getRegisteredRepositories(new DataSourceAsyncCallbackAdapter<Repository>(this));
	}
}