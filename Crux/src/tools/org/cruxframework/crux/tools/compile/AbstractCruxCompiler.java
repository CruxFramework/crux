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
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.core.utils.FileUtils;
import org.cruxframework.crux.scannotation.ClasspathUrlFinder;
import org.cruxframework.crux.tools.compile.utils.ClassPathUtils;
import org.cruxframework.crux.tools.compile.utils.ModuleUtils;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;



import com.google.gwt.dev.Compiler;

/**
 * A Tool for crux projects compilation
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractCruxCompiler 
{
	private static final Log logger = LogFactory.getLog(AbstractCruxCompiler.class);
	
	protected File compilerWorkDir;
	protected boolean indentPages;
	protected boolean initialized = false;
	protected boolean keepPagesGeneratedFiles;
	
	protected String outputCharset;
	protected File outputDir;
	
	protected String pageFileExtension;
	protected File pagesOutputDir;
	protected File webDir;

	private List<String> alreadyCompiledModules = new ArrayList<String>();
	private List<String> gwtCompilerArgs = new ArrayList<String>();
	private SecurityManager originalSecurityManager = null;
	private List<CruxPostProcessor> postProcessors = new ArrayList<CruxPostProcessor>();

	private boolean preCompileJavaSource = true;
	private List<CruxPreProcessor> preProcessors = new ArrayList<CruxPreProcessor>();

	protected File sourceDir;
	protected File resourcesDir;
	private File classpathDir;
	
	/**
	 * @param parameters
	 */
	public AbstractCruxCompiler()
	{
		CruxBridge.getInstance().setSingleVM(true);
		clearCruxBridgeProperties();
	}

	/**
	 * Runs the crux compilation loop. 
	 * 
	 * <p>First, if a sourceFolder is provided, it is compiled.</p>
	 * <p>Then, a scanner searches for modules, based on all crux pages found (returned by <code>getUrls()</code> method.).</p>
	 * <p>Each of those modules is compiled and all pages found is pre processed and post processed.</p>
	 * @throws CompilerException
	 */
	public void execute() throws CompilerException
	{
		try 
		{
			compileJavaSource();
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
					doCompileModule(url, module);
				}
				else
				{
					logger.info("File [" + url.toString() + "] has no module declaration. Skipping compilation.");
				}
			}
			releaseCompilerResources();
		} 
		catch (Throwable e) 
		{
			logger.error(e.getMessage(), e);
			throw new CompilerException(e.getMessage(), e);
		}
	}

	public String getOutputCharset()
    {
    	return outputCharset;
    }

	public File getOutputDir()
    {
    	return outputDir;
    }

	public String getPageFileExtension()
    {
    	return pageFileExtension;
    }

	public File getPagesOutputDir()
    {
    	return pagesOutputDir;
    }

	public File getWebDir()
    {
    	return webDir;
    }

	/**
	 * 
	 */
	public void initializeCompiler()
	{
		if (!initialized)
		{
			URL[] urls = ClasspathUrlFinder.findClassPaths();
			ModuleUtils.initializeScannerURLs(urls);
			ClassScanner.initialize(urls);

			initializeProcessors();
			for (int i=0; i< this.preProcessors.size(); i++)
			{
				this.preProcessors.get(i).initialize(urls);
			}
			for (int i=0; i< this.postProcessors.size(); i++)
			{
				this.postProcessors.get(i).initialize(urls);
			}
			this.initialized = true;
		}
	}

	public boolean isIndentPages()
    {
    	return indentPages;
    }

	public boolean isKeepPagesGeneratedFiles()
    {
    	return keepPagesGeneratedFiles;
    }

	public boolean isPreCompileJavaSource()
    {
    	return preCompileJavaSource;
    }

	public void setIndentPages(boolean indentPages)
    {
    	this.indentPages = indentPages;
    }

	public void setKeepPagesGeneratedFiles(boolean keepPagesGeneratedFiles)
    {
    	this.keepPagesGeneratedFiles = keepPagesGeneratedFiles;
    }

	public void setOutputCharset(String outputCharset)
    {
    	ViewProcessor.setOutputCharset(outputCharset);
		this.outputCharset = outputCharset;
    }

	/**
	 * @param parameter
	 */
	public void setOutputDir(File file)
    {
	    this.outputDir = file;
	    this.gwtCompilerArgs.add("-war");
	    try
	    {
	    	this.gwtCompilerArgs.add(this.outputDir.getCanonicalPath());
	    }
	    catch (IOException e)
	    {
	    	logger.error("Invalid output dir.", e);
	    }
	    if (this.webDir == null)
	    {
	    	setWebDir(file);
	    }
    }

	public void setPageFileExtension(String pageFileExtension)
    {
    	this.pageFileExtension = pageFileExtension;
    }

	public void setPagesOutputDir(File pagesOutputDir)
    {
    	this.pagesOutputDir = pagesOutputDir;
    }

	public void setPreCompileJavaSource(boolean preCompileJavaSource)
    {
    	this.preCompileJavaSource = preCompileJavaSource;
    }

	
	/**
	 * @param parameter
	 */
	public void setScanAllowedPackages(String packages)
    {
		ConfigurationFactory.getConfigurations().setScanAllowedPackages(packages);
    }

	/**
	 * @param parameter
	 */
	public void setScanIgnoredPackages(String packages)
    {
		ConfigurationFactory.getConfigurations().setScanIgnoredPackages(packages);
    }

	/**
	 * @param parameter
	 */
	public void setWebDir(File file)
    {
	    this.webDir = file;
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
	    	setOutputDir(file);
	    }
    }
	
	/**
	 * @param postProcessor
	 */
	protected void addPostProcessor(CruxPostProcessor postProcessor)
	{
		this.postProcessors.add(postProcessor);
	}

	/**
	 * @param preProcessor
	 */
	protected void addPreProcessor(CruxPreProcessor preProcessor)
	{
		this.preProcessors.add(preProcessor);
	}
	
	/**
	 * 
	 */
	protected void clearCruxBridgeProperties()
    {
		CruxBridge.getInstance().registerLastPageRequested("");
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
			if(!isModuleCompiled(module))
			{
				setModuleAsCompiled(module);
				doCompileFile(url, module.getFullName());
				compiled = true;
			}
		}
		
		return compiled;
	}

	/**
	 * Pre compile java source folder, if provided
	 */
	protected void compileJavaSource() throws CompilerException
    {
		if (preCompileJavaSource && sourceDir != null)
		{
		    try
		    {
		    	initializeCompilerDir();
		        
		        JCompiler compiler = new JCompiler();
		        compiler.setOutputDirectory(compilerWorkDir);
		        compiler.setSourcepath(sourceDir);
		        if(classpathDir != null)
		        {
		        	compiler.setClasspath(getClasspath());
		        }
		    	logger.info("Compiling java source");
		        if (!compiler.compile(sourceDir))
		    	{
		    		throw new CompilerException("Error compiling java code. See console for details");
		    	}
	        }
	        catch (Exception e)
	        {
		        throw new CompilerException("Error initializing Java Compiler", e);
	        }
		}
    }

	//Wildcard is not supported by all JVM's, so let's use ';' approach.
	private String getClasspath() {
		File[] listOfFiles = classpathDir.listFiles();
		StringBuffer classpath = new StringBuffer();
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  classpath.append(listOfFiles[i].getAbsolutePath()+";");
		  }
		}
		return classpath.toString();
    }
	
	/**
	 * @return
	 */
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor(getProgramName());

		parameter = new ConsoleParameter("outputDir", "The folder where the compiled files will be created.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("sourceDir", "The project source folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("webDir", "The application web root folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("classpathDir", "The classpath folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("classpathDir", "Classpath dir"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("resourcesDir", "The resources folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("resourcesDir", "Resources dir"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("pagesOutputDir", "The folder where the generated page files will be created.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("output", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("scanAllowedPackages", 
				"A list of packages (separated by commas) that will be scanned to find Controllers, Modules and CrossDevices", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("allowed", "Allowed packages"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter =new ConsoleParameter("scanIgnoredPackages", 
				"A list of packages (separated by commas) that will not be scanned to find Controllers, Modules and CrossDevices", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("ignored", "Ignored packages"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("outputCharset", "Charset used on output files", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("charset", "Output charset"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("pageFileExtension", "Extension of the pages generated", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("fileExtension", "File Extension"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-indentPages", "If true, the output pages will be indented.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-keepPagesGeneratedFiles", 
				"If false, the output pages will be removed after compilation.", false, true));

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-doNotPreCompileJavaSource", "Makes compiler ignore java pre compilation.", false, true));

		parameter = new ConsoleParameter("-gen", "Specify the folder where the GWT generators will output generated classes.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("genFolder", "Folder Name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("-style", "Specify the output style for GWT generated code.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("style", "GWT output Style"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("-extra", "The directory into which extra files, not intended for deployment, will be written.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("extraFolder", "Folder Name"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-localWorkers", "Number of threads used to compile the permutations in parallel.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("numberOfWorkers", "Number of Workers"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-logLevel", "Level of Logging", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("level", "Level"));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-validateOnly", " Validate all source code, but do not compile.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-compileReport", "Create a compile report that tells the Story of Your Compile.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-draftCompile", "Disable compiler optimizations and run faster.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-strict", "Only succeed if no input files have errors.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
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
	 * @param url
	 * @param moduleName
	 */
	protected void doCompileFile(URL url, String moduleName)
    {
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
    }

	/**
	 * @param url
	 * @param module
	 * @throws Exception
	 */
	protected void doCompileModule(URL url, Module module) throws Exception
    {
		boolean isModuleNotCompiled = !isModuleCompiled(module);
	    CruxBridge.getInstance().registerLastPageRequested(url.toString());
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

	/**
	 * 
	 * @param args
	 */
	public void addGwtCompilerArgs(String[] args)
	{
		if (args != null)
		{
			for (String arg : args)
            {
	            gwtCompilerArgs.add(arg);
            }
		}
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
	
	protected String getProgramName()
    {
	    return "CruxCompiler";
    }
	
	/**
	 * Gets the list of URLs that will be compiled
	 * @return
	 */
	protected abstract List<URL> getURLs() throws Exception;
	
	/**
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	protected void initializeCompilerDir() throws IOException, MalformedURLException
    {
	    compilerWorkDir = new File (FileUtils.getTempDirFile(), "crux_compiler"+System.currentTimeMillis());
	    compilerWorkDir.mkdirs();
	    ClassPathUtils.addURL(compilerWorkDir.toURI().toURL());
	    ClassPathResolverInitializer.getClassPathResolver().setWebInfClassesPath(compilerWorkDir.toURI().toURL());
    }

	protected abstract void initializeProcessors();

	/**
	 * @param moduleName
	 * @return
	 */
	protected boolean isModuleCompiled(Module module)
	{
		return module!= null && alreadyCompiledModules.contains(module.getFullName());
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
	 * @param parameters
	 */
	protected void processParameters(Collection<ConsoleParameter> parameters)
	{
		for (ConsoleParameter parameter : parameters)
        {
	        if (parameter.getName().equals("-gen") || parameter.getName().equals("-style") || parameter.getName().equals("-extra") || parameter.getName().equals("-localWorkers")
	        		|| parameter.getName().equals("-logLevel"))
	        {
	        	gwtCompilerArgs.add(parameter.getName());
	        	gwtCompilerArgs.add(parameter.getValue());
	        }
	        else if (parameter.getName().equals("-compileReport"))
	        {
	        	gwtCompilerArgs.add(parameter.getName());
	        }
	        else if (parameter.getName().equals("-strict"))
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
	        	setOutputDir(new File(parameter.getValue()));
	        }
	        else if (parameter.getName().equals("webDir"))
	        {
	        	setWebDir(new File(parameter.getValue()));
	        }
	        else if (parameter.getName().equals("scanAllowedPackages"))
	        {
	    	    setScanAllowedPackages(parameter.getValue());
	        }
	        else if (parameter.getName().equals("scanIgnoredPackages"))
	        {
	    	    setScanIgnoredPackages(parameter.getValue());
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
	        	setOutputCharset(parameter.getValue());
	        }
	        else if (parameter.getName().equals("-doNotPreCompileJavaSource"))
	        {
	        	this.preCompileJavaSource = false;
	        }
	        else if (parameter.getName().equals("pageFileExtension"))
	        {
	        	this.pageFileExtension = parameter.getValue();
	        }
        }
		if (this.outputDir == null && this.webDir == null)
		{
			logger.error("You must inform at least one of outputDir and webDir parameters.");
			System.exit(1);
		}
    }		
		
	/**
	 * @param parameters
	 */
	protected void processSourceParameter(ConsoleParameter parameter)
    {
        if (parameter != null)
        {
    	    this.sourceDir = new File(parameter.getValue());
	    	try
            {
                ClassPathUtils.addURL(sourceDir.toURI().toURL());
            }
            catch (Exception e)
            {
    			logger.error("Invalid sourceDir informed.", e);
    			System.exit(1);
            }
        }
    }

	/**
	 * @param parameters
	 */
	public void processResourcesParameter(ConsoleParameter parameter) {
		if (parameter != null)
        {
    	    this.resourcesDir = new File(parameter.getValue());
	    	try
            {
                ClassPathUtils.addURL(resourcesDir.toURI().toURL());
            }
            catch (Exception e)
            {
    			logger.error("Invalid resourcesDir informed.", e);
    			System.exit(1);
            }
        }
	}
	
	/**
	 * @param parameters
	 */
	protected void processClasspathParameter(ConsoleParameter parameter)
    {
        if (parameter != null)
        {
	    	try
            {
	    		this.classpathDir = new File(parameter.getValue());
	    		//TODO: change for system jar separator ';'. Where is it???
	    		String[] classpaths = getClasspath().split(";");
	    		for(String classpath : classpaths)
	    		{
	    			ClassPathUtils.addURL(new File(classpath).toURI().toURL());
	    		}
            }
            catch (Exception e)
            {
    			logger.error("Invalid classpathDir informed.", e);
    			System.exit(1);
            }
        }
    }

	/**
	 * Release any resource reserved during compilation
	 */
	protected void releaseCompilerResources()
    {
		if (compilerWorkDir != null && compilerWorkDir.exists())
		{
			FileUtils.recursiveDelete(compilerWorkDir);
		}
    }

	/**
	 * @param module
	 */
	protected void setModuleAsCompiled(Module module)
	{
		if (module!= null)
		{
			alreadyCompiledModules.add(module.getFullName());
		}
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
	    			public void checkExit(int status)
	    			{
	    				if (status == 0)
	    				{
	    					throw new DoNotExitException();
	    				}
	    				super.checkExit(status);
	    			}
	    			
	    			@Override
	    			public void checkPermission(Permission perm){}
	    			
	    			@Override
	    			public void checkPermission(Permission perm, Object context){}
	    		});
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
}
