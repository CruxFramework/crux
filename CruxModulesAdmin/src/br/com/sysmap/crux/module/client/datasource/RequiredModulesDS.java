package br.com.sysmap.crux.module.client.datasource;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.datasource.LocalPagedDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import br.com.sysmap.crux.module.client.controller.ModuleInfoControllerCrossDoc;
import br.com.sysmap.crux.module.client.dto.ModuleRef;

@DataSource("requiredModulesDS")
@DataSourceRecordIdentifier("name")
public class RequiredModulesDS extends LocalPagedDataSource<ModuleRef>
{
	@Create
	protected ModuleInfoControllerCrossDoc crossDoc;
	
	public void load()
	{
		ModuleRef[] data = crossDoc.getModuleInfo().getRequiredModules();
		updateData(data);
	}
}

