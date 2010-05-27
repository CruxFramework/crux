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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.rebind.CruxScreenBridge;
import br.com.sysmap.crux.core.rebind.module.Module;
import br.com.sysmap.crux.core.rebind.module.ModulesScanner;
import br.com.sysmap.crux.tools.compile.utils.ModuleUtils;
import br.com.sysmap.crux.tools.compile.utils.TaskClassPathUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * @deprecated - Use CruxCompilerTask instead.
 */
@Deprecated
public class CruxCompile extends Task
{
	private List<FileSet> filesets = new ArrayList<FileSet>();
	private List<Path> classpath = new ArrayList<Path>();
	private List<Argument> args = new ArrayList<Argument>();
	private List<Argument> jvmArgs = new ArrayList<Argument>();
	private List<CruxPreProcessor> preProcessors = new ArrayList<CruxPreProcessor>();
	private List<CruxPostProcessor> postProcessors = new ArrayList<CruxPostProcessor>();
	private List<String> alreadyCompiledModules = new ArrayList<String>();
	private File outputDir;
	private File srcDir;
	
	public void addFileset(FileSet fileset) 
	{
		filesets.add(fileset);
	}

	public void addClasspath(Path classpath)
	{
		this.classpath.add(classpath);
	}
	
	public void addArg(Argument arg)
	{
		this.args.add(arg);
	}

	public void addJvmarg(Argument jvmarg)
	{
		this.jvmArgs.add(jvmarg);
	}
	
	public void add(CruxPreProcessor preProcessor)
	{
		this.preProcessors.add(preProcessor);
	}

	public void add(CruxPostProcessor postProcessor)
	{
		this.postProcessors.add(postProcessor);
	}
	
	public void setOutputDir(File outputDir)
	{
		this.outputDir = outputDir;
	}
	
	public void setSrcDir(File srcDir)
	{
		this.srcDir = srcDir;
	}

	public CruxCompile()
	{
		CruxScreenBridge.getInstance().registerScanAllowedPackages("");
		CruxScreenBridge.getInstance().registerScanIgnoredPackages("");
		CruxScreenBridge.getInstance().registerLastPageRequested("");
	}
	
	public void execute() throws BuildException
	{
		try 
		{
			initializeCompilerClassPath();
			List<URL> urls = getURLs();
			for (URL url : urls)
			{
				Module module = ModuleUtils.findModuleFromPageUrl(url);
				if (module != null)
				{
					boolean isModuleNotCompiled = !isModuleCompiled(module);
					if (isModuleNotCompiled)
					{
						//delete outputDir/moduleName...
					}
					CruxScreenBridge.getInstance().registerLastPageRequested(url.toString());
					URL preprocessedFile = preProcessCruxPage(url, module);
					if (isModuleNotCompiled)
					{
						//backup all files generated on outputdir/moduleName by preprocessors
						try
						{
							if (compileFile(preprocessedFile, module))
							{
								//restore any backup to outputdir/moduleName
							}
						}
						catch (InterfaceConfigException e) 
						{
							log(e.getMessage());
						}
					}
					else
					{
						log("Module '"+ module.getFullName()+"' was already compiled. Skipping compilation.");
					}
					postProcessCruxPage(preprocessedFile, module);
				}
				else
				{
					log("File [" + url.toString() + "] has no module declaration. Skipping compilation.");
				}
			} 
		} 
		catch (Throwable e) 
		{
			log(e.getMessage(), e, Project.MSG_ERR);
			new BuildException(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 */
	private void initializeCompilerClassPath()
	{
		URL[] urls = TaskClassPathUtils.getUrlsFromPaths(classpath);
		ModuleUtils.initializeScannerURLs(urls);
		
		List<String> classesDir = new ArrayList<String>();
		for (URL url : urls)
		{
			if (!url.toString().endsWith(".jar"))
			{
				classesDir.add(url.toString());
			}
		}
		if (classesDir.size() > 0)
		{
			ModulesScanner.getInstance().setClassesDir(classesDir.toArray(new String[classesDir.size()]));
		}
		
		for (CruxPreProcessor preprocess : this.preProcessors)
		{
			preprocess.initialize(urls);
		}
		for (CruxPostProcessor postprocess : this.postProcessors)
		{
			postprocess.initialize(urls);
		}
	}

	/**
	 * Gets the list of URLs that will be compiled
	 * @return
	 */
	protected List<URL> getURLs() throws Exception
	{
		List<URL> files = new LinkedList<URL>();
		for (FileSet fs : filesets)
		{
			DirectoryScanner ds = fs.getDirectoryScanner( getProject() );

			String[] dsFiles = ds.getIncludedFiles();
			for (int j = 0; j < dsFiles.length; j++) 
			{
				File f = new File(dsFiles[j]);
				if ( !f.isFile() ) 
				{
					f = new File( ds.getBasedir(), dsFiles[j] );
				}

				files.add(f.toURI().toURL());
			}
		}

		return files;
	}
	
	/**
	 * Resolve Crux pre-requisites for compilation. It includes register in crux bridge 
	 * the current page been compiled and eventual translations from CruxHTMLTags language.
	 * 
	 * A chain composed by CruxPreProcessor object is used.
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws InterfaceConfigException 
	 */
	protected URL preProcessCruxPage(URL url, Module module) throws Exception
	{
		for (CruxPreProcessor preprocess : this.preProcessors)
		{
			url = preprocess.preProcess(url, module);
		}
		String fileName = url.toString();
		log("File ["+fileName+"] preprocessed.");
		
		return url;
	}
	
	/**
	 * 
	 * @param file
	 */
	protected void postProcessCruxPage(URL url, Module module) throws Exception
	{
		for (CruxPostProcessor postProcessor : this.postProcessors)
		{
			url = postProcessor.postProcess(url, module);
		}
	}
	
	/**
	 * Compile files using GWT compiler
	 * @param url
	 * @throws Exception
	 */
	protected boolean compileFile(URL url, Module module) throws Exception
	{
		boolean compiled = false;
		
		if(module != null)
		{
			String moduleName = module.getFullName();
			if(!alreadyCompiledModules.contains(moduleName))
			{
				setCompiledModule(moduleName);
				
				log("Compiling:"+url.toString());
				Java javatask = (Java) getProject().createTask("java");
				javatask.setClassname("com.google.gwt.dev.Compiler");
				javatask.setFork(true);
		
				for (Argument arg : jvmArgs)
				{
					if(arg != null)
					{
						javatask.createJvmarg().setValue(arg.getParts()[0]);
					}
				}
				
				if (this.outputDir != null)
				{
					javatask.createArg().setValue("-war");
					javatask.createArg().setValue("\""+this.outputDir.getCanonicalPath()+"\"");
				}
				
				for (Argument arg : args)
				{
					if(arg != null)
					{
						javatask.createArg().setValue(arg.getParts()[0]);
					}
				}
				
				javatask.createArg().setValue(moduleName);
		
				for (Path path : this.classpath)
				{
					javatask.setClasspath(path);
				}
				
				javatask.setClasspath(new Path(getProject(), srcDir.getCanonicalPath()));
		
				int resultCode = javatask.executeJava();
				if (resultCode != 0)
				{
					log("The file '"+url.toString()+"' contains errors and can not be compiled.", 1);
				}
				else
				{
					compiled = true;
				}
			}
		}
		
		return compiled;
	}

	/**
	 * @param moduleName
	 * @return
	 */
	protected boolean isModuleCompiled(Module module)
	{
		return module!= null && alreadyCompiledModules.contains(module.getFullName());
	}
	
	/**
	 * 
	 * @param moduleName
	 */
	protected void setCompiledModule(String moduleName)
	{
		alreadyCompiledModules.add(moduleName);
	}

}
