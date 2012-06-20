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
import java.io.InputStream;

import org.cruxframework.crux.core.utils.FileUtils;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.cruxframework.crux.tools.schema.ModuleSchemaGenerator;
import org.cruxframework.crux.tools.schema.SchemaGenerator;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleLayoutProjectGenerator extends AbstractLayoutProjectGenerator
{
	private CruxProjectGeneratorOptions generatorOptions;

	@Override
    public void createProjectRootFiles() throws IOException
    {
		super.createProjectRootFiles();
		createFile(options.getProjectDir(), options.getProjectName() + ".launch", "modules/launch.xml");
    }

	@Override
    public void createSources() throws IOException
    {
		super.createSources();
		createModuleSources();
    }

	@Override
    public void createdBuildFiles() throws IOException
    {
		File buildLibDir = getBuildLibDir();
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "build"), buildLibDir);
		createFile(buildLibDir.getParentFile(), "build.xml", "modules/build.xml");
    }

	@Override
    public void createWebRootFiles() throws IOException
    {
		super.createWebRootFiles();
		
		String pageName = getPageName();
		
		createFile(getWebInfLibDir().getParentFile(), "web.xml", "modules/web.xml");
		createIndexPage(pageName);
    }

	protected void createIndexPage(String pageName) throws IOException
    {
	    createFile(getModulePublicDir(), pageName, "modules/index.crux.xml");
    }

	@Override
    public void createXSDs()
    {
		try
        {
	        StringBuilder classpath = new StringBuilder(".");
	        
	        String projectDir = options.getProjectDir().getCanonicalPath();
	        
	        for (String jar : listJars(getWebInfLibDir()))
	        {
	        	classpath.append(File.pathSeparator+projectDir+"/war/WEB-INF/lib/" + jar);
	        }
	        
	        for (String jar : listJars(getBuildLibDir()))
	        {
	        	classpath.append(File.pathSeparator+projectDir+"/build/lib/" + jar);
	        }

	        ProcessBuilder builder = new ProcessBuilder("java", "-cp", classpath.toString(),
	        		"-DCruxToolsConfig.schemaGeneratorClass="+ModuleSchemaGenerator.class.getName(),
	        		SchemaGenerator.class.getCanonicalName(), projectDir,  new File(options.getProjectDir(),"xsd").getCanonicalPath());

	        builder.redirectErrorStream(true);
	        Process process = builder.start();
	        InputStream processStream = process.getInputStream();
        	System.out.println(StreamUtils.readAsUTF8(processStream));
	        process.waitFor();
        }
        catch (Exception e)
        {
        	throw new RuntimeException("Error creating XSD files",e);
        }
    }

    /**
     * @see org.cruxframework.crux.tools.projectgen.LayoutProjectGenerator#getProjectLayout()
     */
    public String getProjectLayout()
    {
	    return "MODULE_APP";
    }

	protected void createModuleSources() throws IOException
    {
	    File sourceDir = createDir(options.getProjectDir(), "src");
		String packageDir = this.options.getModulePackage().replaceAll("\\.", "/");
		File moduleDir = createDir(sourceDir, packageDir);

		createFile(sourceDir, "Crux.properties", "modules/crux.properties.txt");
		createFile(sourceDir, "CruxModuleConfig.properties", "modules/cruxModuleConfig.properties.txt");
		createFile(moduleDir, this.options.getModuleSimpleName() + ".gwt.xml", "modules/module.xml");
		createFile(moduleDir, this.options.getModuleSimpleName() + ".module.xml", "modules/ModuleInfo.module.xml");
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
		createFile(options.getProjectDir(), ".classpath", "modules/classpath.xml");
    }
}
