package br.com.sysmap.crux.module.client.remote;

import br.com.sysmap.crux.module.client.dto.Controller;
import br.com.sysmap.crux.module.client.dto.CruxSerializable;
import br.com.sysmap.crux.module.client.dto.Datasource;
import br.com.sysmap.crux.module.client.dto.Formatter;
import br.com.sysmap.crux.module.client.dto.ModuleInfo;
import br.com.sysmap.crux.module.client.dto.ModuleInformation;
import br.com.sysmap.crux.module.client.dto.Page;
import br.com.sysmap.crux.module.client.dto.PageParameter;

import com.google.gwt.user.client.rpc.RemoteService;

public interface ModuleInfoService extends RemoteService
{
	String[] getModuleNames() throws ModuleInfoException;
	ModuleInformation getModuleInfo(String module) throws ModuleInfoException;
	Page[] getPages(String module) throws ModuleInfoException;
	Controller[] getControllers(String module) throws ModuleInfoException;
	Datasource[] getDatasources(String module) throws ModuleInfoException;
	Formatter[] getFormatters(String module) throws ModuleInfoException;
	CruxSerializable[] getSerializables(String module) throws ModuleInfoException;
	ModuleInfo[] getDependentModules(String module) throws ModuleInfoException;
	ModuleInfo[] getDependentModules(String module, String version) throws ModuleInfoException;
	PageParameter[] getPageParameters(String module, String page) throws ModuleInfoException;

}
