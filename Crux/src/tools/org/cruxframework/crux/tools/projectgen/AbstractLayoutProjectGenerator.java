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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.utils.FileUtils;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.cruxframework.crux.tools.projectgen.CruxProjectGenerator.Names;
import org.cruxframework.crux.tools.schema.SchemaGenerator;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractLayoutProjectGenerator implements LayoutProjectGenerator
{
	protected CruxProjectGeneratorOptions options;
	protected List<String[]> replacements;
	
	/**
	 * @see org.cruxframework.crux.tools.projectgen.LayoutProjectGenerator#init(java.io.File, java.lang.String, java.lang.String)
	 */
	public void init(File workspaceDir, String projectName, String hostedModeStartupModule)
	{
		this.options = getCruxProjectGeneratorOptions(workspaceDir, projectName, hostedModeStartupModule);
	}
	
	/**
	 * @throws IOException
	 */
	public void createClasspathFile() throws IOException
	{
		StringBuilder libs = new StringBuilder();
		
		for (String jar : listJars(getWebInfLibDir()))
		{
			libs.append("<classpathentry kind=\"lib\" path=\"war/WEB-INF/lib/" + jar + "\"/>\n\t");
		}
		
		for (String jar : listJars(getBuildLibDir()))
		{
			libs.append("<classpathentry kind=\"lib\" path=\"build/lib/" + jar + "\"/>\n\t");
		}
		
		getReplacements().add(new String[]{"classpathLibs", libs.toString()});
		
		applyReplacementsToClassPathFileTemplate();
	}

	/**
	 * @throws IOException
	 */
	public abstract void createdBuildFiles() throws IOException;	
	
	/**
	 * @throws IOException
	 */
	public void createProjectRootFiles() throws IOException
	{
		createFile(options.getProjectDir(), ".project", "project.xml");		
	}
	
	/**
	 * @throws IOException
	 */
	public void createSources() throws IOException
	{
		File sourceDir = createDir(options.getProjectDir(), "src");
		String packageDir = this.options.getModulePackage().replaceAll("\\.", "/");
		File moduleDir = createDir(sourceDir, packageDir);
		
		File clientPackage = createDir(moduleDir, "client");
		File clientRemotePackage = createDir(clientPackage, "remote");
		File clientControllerPackage = createDir(clientPackage, "controller");
		File serverPackage = createDir(moduleDir, "server");
		
		createFile(clientRemotePackage, "GreetingService.java", "GreetingService.java.txt");
		createFile(clientRemotePackage, "GreetingServiceAsync.java", "GreetingServiceAsync.java.txt");
		createFile(clientControllerPackage, "MyController.java", "MyController.java.txt");
		createFile(serverPackage, "GreetingServiceImpl.java", "GreetingServiceImpl.java.txt");
	}
	
	/**
	 * @throws IOException
	 */
	public void createWebRootFiles() throws IOException
	{
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "web-inf"), getWebInfLibDir());
	}
	
	/**
	 * 
	 */
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
	 * @throws IOException
	 */
	public void generate() throws IOException
    {
		createProjectRootFiles();
		createSources();
		createdBuildFiles();
		createWebRootFiles();
		createClasspathFile();
		createXSDs();
    }

	/**
	 * @return
	 */
	public CruxProjectGeneratorOptions getCruxProjectGeneratorOptions()
	{
		return this.options;
	}

	/**
	 * @return
	 */
	public List<String[]> getReplacements()
	{
		if(this.replacements == null)
		{
			this.replacements = new ArrayList<String[]>();
			this.replacements.add(new String[]{"projectLayout", getProjectLayout()});

			this.replacements.add(new String[]{"projectName", this.options.getProjectName()});
			this.replacements.add(new String[]{"hostedModeStartupURL", this.options.getHostedModeStartupURL()});
			this.replacements.add(new String[]{"hostedModeStartupModule", this.options.getHostedModeStartupModule()});
			this.replacements.add(new String[]{"hostedModeVMArgs", this.options.getHostedModeVMArgs()});
			this.replacements.add(new String[]{"appDescription", this.options.getAppDescription()});
			
			this.replacements.add(new String[]{"moduleSimpleNameUpperCase", this.options.getModuleSimpleName()});
			this.replacements.add(new String[]{"moduleSimpleName", this.options.getModuleSimpleName().toLowerCase()});
			this.replacements.add(new String[]{"modulePackage", this.options.getModulePackage()});
		}
		return this.replacements;		
	}
	
	/**
	 * @param config
	 * @return
	 */
	public void loadGeneratorOptions(Properties config)
	{
		String hostedModeStartupURL = config.getProperty(Names.hostedModeStartupURL);
		String hostedModeVMArgs = config.getProperty(Names.hostedModeVMArgs);
		String appDescription = config.getProperty(Names.appDescription);
		options.setHostedModeStartupURL(hostedModeStartupURL);
		options.setHostedModeVMArgs(hostedModeVMArgs);
		options.setAppDescription(appDescription);
	}

	/**
	 * @throws IOException
	 */
	protected abstract void applyReplacementsToClassPathFileTemplate() throws IOException;

	/**
	 * @param parentDir
	 * @param dirName
	 * @return
	 */
	protected File createDir(File parentDir, String dirName)
	{
		File dir = new File(parentDir, dirName + "/");
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * @param rootDir
	 * @param fileName
	 * @param templateName
	 * @param replacements
	 * @throws IOException
	 */
	protected void createFile(File rootDir, String fileName, String templateName) throws IOException
	{
		String templateContent = getTemplateFile(templateName);
		File file = new File(rootDir, fileName);
		file.createNewFile();
		templateContent = replaceParameters(templateContent, getReplacements());
		FileUtils.write(templateContent, file);
	}
	
	/**
	 * @return
	 */
	protected File getBuildLibDir()
	{
		return createDir(options.getProjectDir(), "build/lib");
	}

	protected abstract CruxProjectGeneratorOptions getCruxProjectGeneratorOptions(File workspaceDir, String projectName, String hostedModeStartupModule);
	
	/**
	 * @return
	 */
	protected File getModulePublicDir()
	{
		String packageDir = this.options.getModulePackage().replaceAll("\\.", "/");
		File moduleDir = new File(options.getProjectDir(), "src/"+packageDir);
		
		return createDir(moduleDir, "public");
	}	
	
	protected String getPageName()
    {
	    String pageName = this.options.getHostedModeStartupURL();
		if (pageName == null || pageName.length() == 0)
		{
			pageName = "index.crux.xml";
		}
		else if (pageName.endsWith(".html"))
		{
			pageName = pageName.substring(0, pageName.length()-5) + ".crux.xml";
		}
	    return pageName;
    }
	
	/**
	 * @param templateName
	 * @return
	 * @throws IOException 
	 */
	protected String getTemplateFile(String templateName) throws IOException
	{
		InputStream in = this.getClass().getResourceAsStream("/org/cruxframework/crux/tools/projectgen/templates/" + templateName);
		return FileUtils.read(in);
	}
	
	/**
	 * @return
	 */
	protected File getWarDir()
	{
		return createDir(options.getProjectDir(), "war");
	}

	/**
	 * @return
	 */
	protected File getWebInfLibDir()
	{
		return createDir(options.getProjectDir(), "war/WEB-INF/lib");
	}

	/**
	 * @param buildLibDir
	 * @return
	 */
	protected List<String> listJars(File dir)
	{
		List<String> jars = new ArrayList<String>();
		
		File[] files = dir.listFiles();
		for (File file : files)
		{
			String fileName = file.getName();
			if(fileName.endsWith(".jar"))
			{
				jars.add(fileName);
			}
		}
		
		return jars;
	}
	
	/**
	 * @param text
	 * @param replacements
	 * @return
	 */
	protected String replaceParameters(String text, List<String[]> replacements)
	{
		for (String[] replacement : replacements)
		{
			String from = "${" + replacement[0] + "}";
			String to = replacement[1];
			text = StringUtils.replace(text, from, to);
		}
		
		return text;
	}	
}
