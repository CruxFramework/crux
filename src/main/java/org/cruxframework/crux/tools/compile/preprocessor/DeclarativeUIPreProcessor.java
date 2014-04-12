/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.tools.compile.preprocessor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.tools.compile.CruxPreProcessor;


/**
 * @author Thiago da Rosa de Bustamante
 */
public class DeclarativeUIPreProcessor extends AbstractDeclarativeUIPreProcessor implements CruxPreProcessor
{
	private Module module;
	
	@Override
	public URL preProcess(URL url, Module module) throws IOException, InterfaceConfigException 
	{
		this.module = module;
		return super.preProcess(url, module);
	}
	
	
	@Override
	protected File getDestDir(URL urlFile) throws IOException
	{
		File parentDir = outputDir;
		if(!outputDir.exists())
		{
			outputDir.mkdirs();
		}
		
		String outputPath = outputDir.getCanonicalPath();
		outputPath = outputPath.replaceAll("[\\\\]", "/");

		String originalModulePath = urlFile.toString();		
		URL moduleRoot = module.getLocation();
		String moduleRootString = moduleRoot.toString();
		if (moduleRootString.endsWith("/"))
		{
			moduleRootString = moduleRootString.substring(0, moduleRootString.length()-1);
		}
		
		for (String publicPath : module.getPublicPaths()) 
		{
			int indexOfDirStruct = originalModulePath.indexOf(moduleRootString+"/"+publicPath);
			
			if(indexOfDirStruct >= 0)
			{
				String fileRelativePath = originalModulePath.substring(indexOfDirStruct + (moduleRootString+"/"+publicPath).length());
				fileRelativePath = fileRelativePath.substring(0, fileRelativePath.lastIndexOf("/"));
				fileRelativePath = fileRelativePath.startsWith("/") ? fileRelativePath.substring(1) : fileRelativePath;
				parentDir = new File(outputPath + "/" + module.getName() + "/" + fileRelativePath + "/");
			}
			
		}
		return parentDir;
	}
}