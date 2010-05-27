/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.tools.compile;

import java.io.File;

import br.com.sysmap.crux.module.CruxModuleBridge;
import br.com.sysmap.crux.tools.schema.SchemaGeneratorTask;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SchemaGeneratorModulesTask extends SchemaGeneratorTask
{
	private String moduleName;
	
	public String getModuleName()
	{
		return moduleName;
	}

	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	@Override
	protected void generateSchemas(File baseDir, String outputDir) throws Exception
	{
		CruxModuleBridge.getInstance().registerCurrentModule(moduleName);
		super.generateSchemas(baseDir, outputDir);
	}
}
