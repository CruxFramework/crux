/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.tools.build;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.cruxframework.crux.core.utils.FileUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class LegacyHandlerTask extends Task
{
	private ArrayList<SourceFolder> sourceFolders = new ArrayList<SourceFolder>();
	private File destinationFolder;
	
	public File getDestinationFolder()
    {
    	return destinationFolder;
    }
	
	public void setDestinationFolder(File destinationFolder)
    {
		this.destinationFolder = destinationFolder;
    }
	
	public void addSourceFolder(SourceFolder folder) 
	{
		sourceFolders.add(folder);
    }	
	
	@Override
	public void execute() throws BuildException
	{
		try
		{
			LegacyHandler legacyHandler = new LegacyHandler();
			FileUtils.recursiveDelete(destinationFolder);
			legacyHandler.setDestinationFolder(destinationFolder);

			for (SourceFolder sourceFolder: sourceFolders)
			{
				legacyHandler.addSourceFolder(sourceFolder.getFile());
			}
			legacyHandler.scanSourceFiles();
		}
        catch (Exception e)
        {
			e.printStackTrace();
        	log(e.getMessage(), Project.MSG_ERR);
			new BuildException(e.getMessage(), e);
        }
	}
}
