package br.com.sysmap.crux.module.client.remote;

import br.com.sysmap.crux.module.client.dto.Controller;
import br.com.sysmap.crux.module.client.dto.CruxSerializable;
import br.com.sysmap.crux.module.client.dto.Datasource;
import br.com.sysmap.crux.module.client.dto.Formatter;
import br.com.sysmap.crux.module.client.dto.ModuleInfo;
import br.com.sysmap.crux.module.client.dto.ModuleInformation;
import br.com.sysmap.crux.module.client.dto.Page;
import br.com.sysmap.crux.module.client.dto.PageParameter;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModuleInfoServiceAsync
{
	void getModuleNames(AsyncCallback<String[]> callback);
	void getModuleInfo(String module, AsyncCallback<ModuleInformation> callback);
	void getPages(String module, AsyncCallback<Page[]> callback);
	void getControllers(String module, AsyncCallback<Controller[]> callback);
	void getDatasources(String module, AsyncCallback<Datasource[]> callback);
	void getFormatters(String module, AsyncCallback<Formatter[]> callback);
	void getDependentModules(String module, AsyncCallback<ModuleInfo[]> callback);
	void getDependentModules(String module, String version, AsyncCallback<ModuleInfo[]> callback);
	void getPageParameters(String module, String page, AsyncCallback<PageParameter[]> callback);
	void getSerializables(String module, AsyncCallback<CruxSerializable[]> callback);
}
