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
package br.com.sysmap.crux.tools.schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.sysmap.crux.core.utils.StreamUtils;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleSchemaGenerator extends DefaultSchemaGenerator
{

	/**
	 * @param projectBaseDir
	 * @param destDir
	 * @param webDir
	 * @throws IOException
	 */
	public ModuleSchemaGenerator(File projectBaseDir, File destDir, File webDir) throws IOException
    {
	    super(projectBaseDir, destDir, webDir);
    }

	/**
	 * @see br.com.sysmap.crux.tools.schema.DefaultSchemaGenerator#generateSchemas()
	 */
	@Override
	public void generateSchemas() throws IOException
	{
		copyModuleSchema();

	    super.generateSchemas();
	}
	
	/**
	 * @throws IOException
	 */
	private void copyModuleSchema() throws IOException
	{
		File xhtmlFile = new File(destDir, "module.xsd");
		if (xhtmlFile.exists())
		{
			xhtmlFile.delete();
		}
		xhtmlFile.createNewFile();
		FileOutputStream out = new FileOutputStream(xhtmlFile);

		String targetNS = "http://www.sysmap.com.br/module";
		registerNamespaceForCatalog(targetNS, xhtmlFile);
		
		StreamUtils.write(getClass().getResourceAsStream("/META-INF/module.xsd"), out, true);
	}	
}
