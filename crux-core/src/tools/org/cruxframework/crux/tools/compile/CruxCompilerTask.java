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
package org.cruxframework.crux.tools.compile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.DirSet;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */

public class CruxCompilerTask extends AbstractCruxCompilerTask
{
	private List<DirSet> dirsets = new ArrayList<DirSet>();
	
	public void addDirset(DirSet dirset) 
	{
		dirsets.add(dirset);
	}
	
	/**
	 * Gets the list of directories that will be compiled
	 * @return
	 */
	protected List<File> getInputFiles() throws Exception
	{
		List<File> files = new LinkedList<File>();
		for (DirSet fs : dirsets)
		{
			files.add(fs.getDir(getProject()));
		}

		return files;
	}
	
	/**
	 * @param javatask
	 * @throws Exception
	 */
	@Override
	protected void addCompilerParameters(Java javatask) throws Exception
    {
		List<File> inputFiles = getInputFiles();
		StringBuilder pagesDirs = new StringBuilder();
		boolean needsComma = false;
		for (File file : inputFiles)
        {
			if (needsComma)
			{
				pagesDirs.append(",");
			}
			needsComma = true;
			pagesDirs.append(file.getCanonicalPath());
        }
		if (pagesDirs.length() > 0)
		{
			javatask.createArg().setValue("cruxPagesDir");
			javatask.createArg().setValue(pagesDirs.toString());
		}
		super.addCompilerParameters(javatask);
    }
}
