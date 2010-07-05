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
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.rebind.CruxScreenBridge;
import br.com.sysmap.crux.core.rebind.module.Module;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.utils.FileUtils;
import br.com.sysmap.crux.scannotation.ClasspathUrlFinder;
import br.com.sysmap.crux.tools.compile.utils.ModuleUtils;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParameterOption;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;

import com.google.gwt.dev.Compiler;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractCruxCompiler 
{
	private static final Log logger = LogFactory.getLog(AbstractCruxCompiler.class);
	
	private List<String> gwtCompilerArgs = new ArrayList<String>();
	private List<CruxPreProcessor> preProcessors = new ArrayList<CruxPreProcessor>();
	private List<CruxPostProcessor> postProcessors = new ArrayList<CruxPostProcessor>();
	private List<String> alreadyCompiledModules = new ArrayList<String>();
	
	protected File outputDir;
	protected File webDir;
	
	protected File pagesOutputDir;
	protected boolean indentPages;
	protected boolean keepPagesGeneratedFiles;

	protected String outputCharset;
	protected String pageFileExtension;
	
	/**
	 * @param parameters
	 */
	public AbstractCruxCompiler()
	{
		clearCruxBridgeProperties();
	}

	public void execute() throws CompilerException
	{
		try 
		{
			initializeCompiler();
			List<URL> urls = getURLs();
			for (URL url : urls)
			{
				Module module = ModuleUtils.findModuleFromPageUrl(url);
				if (module != null)
				{
					boolean isModuleNotCompiled = !isModuleCompiled(module);
					if (isModuleNotCompiled)
					{
						deleteModuleOutputDir(module);
					}
					CruxScreenBridge.getInstance().registerLastPageRequested(url.toString());
					URL preprocessedFile = preProcessCruxPage(url, module);
					if (isModuleNotCompiled)
					{
						maybeBackupPreProcessorsOutput(module);
						try
						{
							if (compileFile(preprocessedFile, module))
							{
								maybeRestoreBackup(module);
							}
						}
						catch (InterfaceConfigException e) 
						{
							logger.error(e.getMessage());
						}
					}
					else
					{
						logger.info("Module '"+ module.getFullName()+"' was already compiled. Skipping compilation.");
					}
					postProcessCruxPage(preprocessedFile, module);
				}
				else
				{
					logger.info("File [" + url.toString() + "] has no module declaration. Skipping compilation.");
				}
			} 
		} 
		catch (Throwable e) 
		{
			logger.error(e.getMessage(), e);
			new CompilerException(e.getMessage(), e);
		}
	}

	
	/**
	 * @param module
	 * @throws IOException 
	 */
	protected void maybeRestoreBackup(Module module) throws IOException
    {
    	File backupFile = new File(FileUtils.getTempDirFile(), "_moduleBackup");
	    if (backupFile.exists())
	    {
		    File output = new File(outputDir, module.getName());
	    	FileUtils.copyDirectory(backupFile, output);
	    	backupFile.delete();
	    }
    }

	/**
	 * @throws IOException 
	 * 
	 */
	protected void maybeBackupPreProcessorsOutput(Module module) throws IOException
    {
	    File output = new File(outputDir, module.getName());
	    if (output.exists())
	    {
	    	File backupFile = new File(FileUtils.getTempDirFile(), "_moduleBackup");
	    	FileUtils.copyDirectory(output, backupFile);
	    }
    }

	/**
	 * @param module
	 */
	protected void deleteModuleOutputDir(Module module)
    {
	    File output = new File(outputDir, module.getName());
	    if (output.exists())
	    {
	    	FileUtils.recursiveDelete(output);
	    }
    	File backupFile = new File(FileUtils.getTempDirFile(), "_moduleBackup");
	    if (backupFile.exists())
	    {
	    	FileUtils.recursiveDelete(backupFile);
	    }
    }
	
	/**
	 * @param preProcessor
	 */
	protected void addPreProcessor(CruxPreProcessor preProcessor)
	{
		this.preProcessors.add(preProcessor);
	}

	/**
	 * @param postProcessor
	 */
	protected void addPostProcessor(CruxPostProcessor postProcessor)
	{
		this.postProcessors.add(postProcessor);
	}
	
	/**
	 * 
	 */
	protected void initializeCompiler()
	{
		URL[] urls = ClasspathUrlFinder.findClassPaths();
		ModuleUtils.initializeScannerURLs(urls);
		ClassScanner.initialize(urls);
		
		for (CruxPreProcessor preprocess : this.preProcessors)
		{
			preprocess.initialize(urls);
		}
		for (CruxPostProcessor postprocess : this.postProcessors)
		{
			postprocess.initialize(urls);
		}
		initializeProcessors();
	}

	/**
	 * @param parameters
	 */
	protected void processParameters(Collection<ConsoleParameter> parameters)
    {
	    for (ConsoleParameter parameter : parameters)
        {
	        if (parameter.getName().equals("-gen") || parameter.getName().equals("-style") || parameter.getName().equals("-extra"))
	        {
	        	gwtCompilerArgs.add(parameter.getName());
	        	gwtCompilerArgs.add(parameter.getValue());
	        }
	        else if (parameter.getName().equals("-compileReport"))
	        {
	        	gwtCompilerArgs.add(parameter.getName());
	        }
	        else if (parameter.getName().equals("-draftCompile"))
	        {
	        	gwtCompilerArgs.add(parameter.getName());
	        }
	        else if (parameter.getName().equals("-validateOnly"))
	        {
	        	gwtCompilerArgs.add(parameter.getName());
	        }
	        else if (parameter.getName().equals("outputDir"))
	        {
	        	setOutputDir(parameter);
	        }
	        else if (parameter.getName().equals("webDir"))
	        {
	        	setWebDir(parameter);
	        }
	        else if (parameter.getName().equals("scanAllowedPackages"))
	        {
	    	    CruxScreenBridge.getInstance().registerScanAllowedPackages(parameter.getValue());
	        }
	        else if (parameter.getName().equals("scanIgnoredPackages"))
	        {
	    	    CruxScreenBridge.getInstance().registerScanIgnoredPackages(parameter.getValue());
	        }
	        else if (parameter.getName().equals("pagesOutputDir"))
	        {
	        	this.pagesOutputDir = new File(parameter.getValue());
	        }
	        else if (parameter.getName().equals("-indentPages"))
	        {
	        	this.indentPages = true;
	        }
	        else if (parameter.getName().equals("-keepPagesGeneratedFiles"))
	        {
	        	this.keepPagesGeneratedFiles = true;
	        }
	        else if (parameter.getName().equals("outputCharset"))
	        {
	        	this.outputCharset = parameter.getValue();
	        }
	        else if (parameter.getName().equals("pageFileExtension"))
	        {
	        	this.pageFileExtension = parameter.getValue();
	        }
        }
		if (this.outputDir != null)
		{
			gwtCompilerArgs.add("-war");
			try
            {
	            gwtCompilerArgs.add(this.outputDir.getCanonicalPath());
            }
            catch (IOException e)
            {
	            logger.error("Invalid output dir.", e);
            }
		}
		if (this.outputDir == null && this.webDir == null)
		{
			logger.error("You must inform at least one of outputDir and webDir parameters.");
			System.exit(1);
		}
    }

	/**
	 * @param parameter
	 */
	private void setOutputDir(ConsoleParameter parameter)
    {
	    this.outputDir = new File(parameter.getValue());
	    if (this.webDir == null)
	    {
	    	setWebDir(parameter);
	    }
    }

	/**
	 * @param parameter
	 */
	private void setWebDir(ConsoleParameter parameter)
    {
	    this.webDir = new File(parameter.getValue());
	    try
	    {
	        ClassPathResolverInitializer.getClassPathResolver().setWebInfClassesPath(new File(webDir, "WEB-INF/classes/").toURI().toURL());
	        ClassPathResolverInitializer.getClassPathResolver().setWebInfLibPath(new File(webDir, "WEB-INF/lib/").toURI().toURL());
	    }
	    catch (MalformedURLException e)
	    {
	    	logger.error("Invalid web folder");
	    }
	    if (this.outputDir == null)
	    {
	    	setOutputDir(parameter);
	    }
    }
	
	/**
	 * 
	 */
	protected void clearCruxBridgeProperties()
    {
	    CruxScreenBridge.getInstance().registerScanAllowedPackages("");
		CruxScreenBridge.getInstance().registerScanIgnoredPackages("");
		CruxScreenBridge.getInstance().registerLastPageRequested("");
    }

	/**
	 * Gets the list of URLs that will be compiled
	 * @return
	 */
	protected abstract List<URL> getURLs() throws Exception;
	
	/**
	 * A chain composed by CruxPreProcessor object is used.
	 * @param url
	 * @param module
	 * @return
	 * @throws Exception 
	 */
	protected URL preProcessCruxPage(URL url, Module module) throws Exception
	{
		for (CruxPreProcessor preprocess : this.preProcessors)
		{
			url = preprocess.preProcess(url, module);
		}
		logger.info("File ["+url.toString()+"] pre-processed.");
		
		return url;
	}
	
	/**
	 * A chain composed by CruxPostProcessor object is used.
	 * @param url
	 * @param module
	 * @return
	 * @throws Exception 
	 */
	protected void postProcessCruxPage(URL url, Module module) throws Exception
	{
		for (CruxPostProcessor postProcessor : this.postProcessors)
		{
			url = postProcessor.postProcess(url, module);
		}
		logger.info("File ["+url.toString()+"] post-processed.");
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
				
				logger.info("Compiling:"+url.toString());

				// Because of an AWT BUG, GWT needs to execute a System.exit command to 
				// finish its compilation. This class calls the Compiler from an ant command,
				// so, this bug is not a problem here. We need to compile all the modules on the same 
				// JVM. Call prerocessCruxPages on a separated JVM would cost a lot to our performance. 
				setSecurityManagerToAvoidSystemExit();
				
				try
				{
					Compiler.main(getGwtArgs(moduleName));
				}
				catch (DoNotExitException e) 
				{
					//Do nothing...continue compile looping
				}
				finally
				{
					restoreSecurityManager();
				}
				compiled = true;
			}
		}
		
		return compiled;
	}

	/**
	 * @param moduleName
	 * @return
	 */
	protected String[] getGwtArgs(String moduleName)
    {
	    String[] gwtArgs = new String[gwtCompilerArgs.size()+1];
	    for (int i=0; i<gwtCompilerArgs.size(); i++)
	    {
	        gwtArgs[i] = gwtCompilerArgs.get(i);
	    }
	    gwtArgs[gwtCompilerArgs.size()] = moduleName;
	    return gwtArgs;
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
	
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor(getProgramName());

		parameter = new ConsoleParameter("outputDir", "The folder where the compiled files will be created.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("webDir", "The application web root folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("pagesOutputDir", "The folder where the generated page files will be created.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("output", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("scanAllowedPackages", 
				"A list of packages (separated by commas) that will be scanned to find Controllers, Modules and Templates", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("allowed", "Allowed packages"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter =new ConsoleParameter("scanIgnoredPackages", 
				"A list of packages (separated by commas) that will not be scanned to find Controllers, Modules and Templates", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("ignored", "Ignored packages"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("outputCharset", "Charset used on output files", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("charset", "Output charset"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("pageFileExtension", "Extension of the pages generated", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("fileExtension", "File Extension"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-indentPages", "If true, the output pages will be indented.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-keepPagesGeneratedFiles", 
				"If false, the output pages will be removed after compilation.", false, true));

		parameter = new ConsoleParameter("-gen", "Specify the folder where the GWT generators will output generated classes.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("genFolder", "Folder Name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("-style", "Specify the output style for GWT generated code.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("style", "GWT output Style"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("-extra", "The directory into which extra files, not intended for deployment, will be written.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("extraFolder", "Folder Name"));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-validateOnly", " Validate all source code, but do not compile.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-compileReport", "Create a compile report that tells the Story of Your Compile.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-draftCompile", "Disable compiler optimizations and run faster.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
	}

	protected String getProgramName()
    {
	    return "CruxCompiler";
    }
	
	private SecurityManager originalSecurityManager = null;

	/**
	 * 
	 */
	private void setSecurityManagerToAvoidSystemExit()
    {
	    AccessController.doPrivileged(new PrivilegedAction<Boolean>()
	    {
	    	public Boolean run()
	        {
	    		originalSecurityManager = System.getSecurityManager();
	    		System.setSecurityManager(new SecurityManager(){
	    			
	    			@Override
	    			public void checkPermission(Permission perm){}
	    			
	    			@Override
	    			public void checkPermission(Permission perm, Object context){}
	    			
	    			@Override
	    			public void checkExit(int status)
	    			{
	    				if (status == 0)
	    				{
	    					throw new DoNotExitException();
	    				}
	    				super.checkExit(status);
	    			}
	    		});
	    		return true;
	        }
	    });
    }

	/**
	 * 
	 */
	private void restoreSecurityManager()
    {
		AccessController.doPrivileged(new PrivilegedAction<Boolean>()
		{
			public Boolean run()
			{
				System.setSecurityManager(originalSecurityManager);
				return true;
			}
		});
    }

	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class DoNotExitException extends SecurityException
	{
        private static final long serialVersionUID = -5285052847615664828L;
	}
	
	
	protected abstract void initializeProcessors();
	
}
