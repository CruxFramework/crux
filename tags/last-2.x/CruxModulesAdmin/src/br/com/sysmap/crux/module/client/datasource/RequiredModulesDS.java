package br.com.sysmap.crux.module.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.module.client.controller.ModuleInfoController.ModuleInfoControllerInvoker;
import br.com.sysmap.crux.module.client.dto.ModuleRef;

@DataSource("requiredModulesDS")
@DataSourceBinding(identifier="name")
public class RequiredModulesDS extends LocalBindableEditablePagedDataSource<ModuleRef>
{
	@Create
	protected ModuleInfoControllerInvoker moduleInfoInvoker;
	
	public void load()
	{
		ModuleRef[] data = moduleInfoInvoker.getModuleInfoOnSelf().getRequiredModules();
		updateData(data);
	}
}

