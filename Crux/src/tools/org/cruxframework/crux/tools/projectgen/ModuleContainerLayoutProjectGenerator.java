/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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
package org.cruxframework.crux.tools.projectgen;

import java.io.File;
import java.io.IOException;

import org.cruxframework.crux.core.utils.FileUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleContainerLayoutProjectGenerator extends ModuleLayoutProjectGenerator
{
	@Override
    public void createdBuildFiles() throws IOException
    {
		File buildLibDir = getBuildLibDir();
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "build"), buildLibDir);
		createFile(buildLibDir.getParentFile(), "build.xml", "modulescontainer/build.xml");
    }

    /**
     * @see org.cruxframework.crux.tools.projectgen.LayoutProjectGenerator#getProjectLayout()
     */
    public String getProjectLayout()
    {
	    return "MODULE_CONTAINER_APP";
    }
    
    @Override
    public void createSources() throws IOException
    {
        createModuleSources();
    }
    
    @Override
	protected void createIndexPage(String pageName) throws IOException
    {
	    createFile(getModulePublicDir(), pageName, "modulescontainer/index.crux.xml");
    }
}
