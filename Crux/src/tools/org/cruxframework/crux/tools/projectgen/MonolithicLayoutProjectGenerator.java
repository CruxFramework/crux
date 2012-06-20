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
public class MonolithicLayoutProjectGenerator extends AbstractLayoutProjectGenerator
{
	private CruxProjectGeneratorOptions generatorOptions;

	@Override
    public void createProjectRootFiles() throws IOException
    {
		super.createProjectRootFiles();
		createFile(options.getProjectDir(), options.getProjectName() + ".launch", "launch.xml");
    }

	@Override
    public void createSources() throws IOException
    {
		super.createSources();

		File sourceDir = createDir(options.getProjectDir(), "src");
		String packageDir = this.options.getModulePackage().replaceAll("\\.", "/");
		File moduleDir = createDir(sourceDir, packageDir);
		
		createFile(moduleDir, this.options.getModuleSimpleName() + ".gwt.xml", "module.xml");
    }

	@Override
    public void createdBuildFiles() throws IOException
    {
		File buildLibDir = getBuildLibDir();
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "build"), buildLibDir);
		createFile(buildLibDir.getParentFile(), "build.xml", "build.xml");
    }

	@Override
    public void createWebRootFiles() throws IOException
    {
		super.createWebRootFiles();
		
		String pageName = getPageName();
		
		createFile(getWebInfLibDir().getParentFile(), "web.xml", "web.xml");
		createFile(getWarDir(), pageName, "index.crux.xml");		
    }

    /**
     * @see org.cruxframework.crux.tools.projectgen.LayoutProjectGenerator#getProjectLayout()
     */
    public String getProjectLayout()
    {
	    return "MONOLITHIC_APP";
    }

	@Override
    protected CruxProjectGeneratorOptions getCruxProjectGeneratorOptions(File workspaceDir, String projectName, String hostedModeStartupModule)
    {
	    try
        {
	    	if (generatorOptions == null)
	    	{
	    		generatorOptions = new CruxProjectGeneratorOptions(workspaceDir, projectName, hostedModeStartupModule);
	    	}
			return generatorOptions;
        }
        catch (Exception e)
        {
        	throw new LayoutProjectGeneratorException("Can not create the generator options object.", e);
        }
    }

	@Override
    protected void applyReplacementsToClassPathFileTemplate() throws IOException
    {
		createFile(options.getProjectDir(), ".classpath", "classpath.xml");
    }
}
