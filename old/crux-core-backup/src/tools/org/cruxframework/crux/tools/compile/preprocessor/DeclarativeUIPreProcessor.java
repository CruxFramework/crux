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

import org.cruxframework.crux.tools.compile.CruxPreProcessor;


/**
 * @author Thiago da Rosa de Bustamante
 */
public class DeclarativeUIPreProcessor extends AbstractDeclarativeUIPreProcessor implements CruxPreProcessor
{
	private String keepDirStructureUnder = null;
	

	/**
	 * @param urlFile
	 * @return
	 * @throws IOException
	 */
	protected File getDestDir(URL urlFile) throws IOException {
		File parentDir;
		if(!outputDir.exists())
		{
			outputDir.mkdirs();
		}
		
		if(keepDirStructureUnder != null)
		{
			keepDirStructureUnder = keepDirStructureUnder.replaceAll("[\\\\]", "/");
			keepDirStructureUnder = keepDirStructureUnder.replaceAll("[\\\\]", "/");
			
			String outputPath = outputDir.getCanonicalPath();
			outputPath = outputPath.replaceAll("[\\\\]", "/");
								
			String originalFilePath = urlFile.toString();
//			originalFilePath = originalFilePath.replaceAll("[\\\\]", "/");
			
			int indexOfDirStruct = originalFilePath.indexOf(keepDirStructureUnder);
			
			if(indexOfDirStruct >= 0)
			{
				String fileRelativePath = originalFilePath.substring(indexOfDirStruct + keepDirStructureUnder.length());
				fileRelativePath = fileRelativePath.substring(0, fileRelativePath.lastIndexOf("/"));
				fileRelativePath = fileRelativePath.startsWith("/") ? fileRelativePath.substring(1) : fileRelativePath;
				parentDir = new File(outputPath + "/" + fileRelativePath + "/");
			}
			else
			{
				parentDir = outputDir;
			}
		}
		else
		{
			parentDir = outputDir;
		}
		return parentDir;
	}
	
	public void setKeepDirStructureUnder(String keepDirStructureUnder)
	{
		this.keepDirStructureUnder = keepDirStructureUnder;
	}
}