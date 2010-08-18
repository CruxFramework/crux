package br.com.sysmap.crux.module.client.controller;

import br.com.sysmap.crux.core.client.controller.crossdoc.CrossDocument;
import br.com.sysmap.crux.module.client.dto.ModuleInfo;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ModuleInfoControllerCrossDoc extends CrossDocument
{
	void confirmLogin();
	ModuleInfo getModuleInfo();
}
