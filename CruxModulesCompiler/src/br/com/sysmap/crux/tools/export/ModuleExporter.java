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
package br.com.sysmap.crux.tools.export;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.utils.FileUtils;
import br.com.sysmap.crux.module.classpath.ModuleClassPathResolver;
import br.com.sysmap.crux.module.validation.CruxModuleValidator;
import br.com.sysmap.crux.scannotation.ClasspathUrlFinder;
import br.com.sysmap.crux.tools.compile.AbstractCruxCompiler;
import br.com.sysmap.crux.tools.compile.CompilerException;
import br.com.sysmap.crux.tools.compile.CruxModuleCompiler;
import br.com.sysmap.crux.tools.compile.utils.ModuleUtils;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParameterOption;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessingException;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;


/**
 * A tool to export modules
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleExporter
{
	private Compiler compiler;
	private AbstractCruxCompiler cruxCompiler;
	private String excludes;
	private File exporterWorkDir;
	private String includes;

	private JarCreator jarCreator;
	private String javaSource;
	private String javaTarget;
	private String moduleName;
	private File outputDir;
	private String outputModuleName;
	private String pageFileExtension;
	private String pagesOutputCharset;
	private File sourceDir;

	
	/**
	 * Starts the ModuleExporter program
	 * @param args
	 */
	public static void main(String[] args)
    {
		try
		{
			ModuleExporter exporter = new ModuleExporter();
			ConsoleParametersProcessor parametersProcessor = exporter.createParametersProcessor();
			Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

			if (parameters.containsKey("-help") || parameters.containsKey("-h"))
			{
				parametersProcessor.showsUsageScreen();
			}
			else
			{
				exporter.processParameters(parameters.values());
				exporter.execute();
			}
			System.exit(0);
		}
		catch (ConsoleParametersProcessingException e)
		{
			e.printStackTrace();
		}
		catch (ModuleExporterException e)
		{
			e.printStackTrace();
		}
		System.exit(1);
    }

	/**Exports the module to a .module.jar file
	 * @throws ModuleExporterException
	 */
	public void execute() throws ModuleExporterException
	{
		initializeModuleExporter();
		try
		{
    		initializeJavaCompiler();
			compileJavaCode();

			initializeCruxCompiler();
			if (CruxModuleValidator.isDependenciesOk(moduleName))
			{
				compileCruxModule();

	        	initializeJarCreator();
				createModuleOutputFile();
			}
			else
			{
				throw new ModuleExporterException("Module dependecies are broken. ModuleExporter can not export module "+moduleName);
			}
		}
		finally
		{
			cleanTempFiles();
		}
	}

	/**
	 * Remove temporary files
	 */
	protected void cleanTempFiles()
    {
		if (exporterWorkDir != null && exporterWorkDir.exists())
		{
			FileUtils.recursiveDelete(exporterWorkDir);
		}
    }

	/**
	 * Performs the crux module compilation
	 * @throws ModuleExporterException
	 */
	protected void compileCruxModule() throws ModuleExporterException
    {
	    try
	    {
	    	System.out.println("Running Crux Compiler...");
	    	cruxCompiler.execute();
	    }
	    catch (CompilerException e)
	    {
	    	throw new ModuleExporterException("Error compiling crux module", e);
	    }
    }

	/**
	 * Performs the java compilation of all module source files
	 * @throws ModuleExporterException
	 */
	protected void compileJavaCode() throws ModuleExporterException
    {
	    try
	    {
	    	System.out.println("Running Java Compiler...");
	    	if (!compiler.compile(sourceDir))
	    	{
	    		throw new ModuleExporterException("Error compiling java code. See console for details");
	    	}
	    }
	    catch (IOException e)
	    {
	    	throw new ModuleExporterException("Error compiling java code", e);
	    }
    }
	
	/**
	 * Packages the module in a .module.jar file
	 * @throws ModuleExporterException
	 */
	protected void createModuleOutputFile() throws ModuleExporterException
    {
	    try
	    {
	    	System.out.println("Creating module file...");
	    	jarCreator.createJar();
	    }
	    catch (IOException e)
	    {
	    	throw new ModuleExporterException("Error compiling java code", e);
	    }
    }

	/**
	 * Creates a ConsoleParametersProcessor object to process all command line arguments.
	 * @return
	 */
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("ModuleExporter");

		parameter = new ConsoleParameter("moduleName", "The name of the module beeing exported.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("name", "Module name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("outputModuleName", "The name of the jar exported.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("name", "Module name"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("outputDir", "The folder where the compiled files will be created.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("sourceDir", "The module source folder.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("includes", "Pattern informing which files must be included in exporting proccess.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("pattern", "Includes pattern"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("excludes", "Pattern informing which files must be excluded from exporting proccess.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("pattern", "Excludes pattern"));
		parametersProcessor.addSupportedParameter(parameter);
		
        // Java Compiler Parameters
		parameter = new ConsoleParameter("javaSource", "Source version of Java files", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("source", "java source version"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("javaTarget", "Target version for Java compilation", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("target", "java target version"));
		parametersProcessor.addSupportedParameter(parameter);

		// Crux Compiler Parameters
		parameter = new ConsoleParameter("webDir", "The application web root folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("scanAllowedPackages", 
				"A list of packages (separated by commas) that will be scanned to find Controllers, Modules and Templates", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("allowed", "Allowed packages"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter =new ConsoleParameter("scanIgnoredPackages", 
				"A list of packages (separated by commas) that will not be scanned to find Controllers, Modules and Templates", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("ignored", "Ignored packages"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("pagesOutputCharset", "Charset used on output files", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("charset", "Output charset"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("pageFileExtension", "Extension of the pages generated", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("fileExtension", "File Extension"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));

		
		return parametersProcessor;	
		/*		
		parameter = new ConsoleParameter("pagesOutputDir", "The folder where the generated page files will be created.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("output", "Folder name"));
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
*/
	}

	/**
	 * Initialize Crux compiler according with exporter parameters 
	 * @throws ModuleExporterException
	 */
	protected void initializeCruxCompiler() throws ModuleExporterException
    {
		try
        {
	        URL[] urls = ClasspathUrlFinder.findClassPaths();
	        ModuleUtils.initializeScannerURLs(urls);
	        ClassScanner.initialize(urls);

	        cruxCompiler = new CruxModuleCompiler();
	        
	        cruxCompiler.setKeepPagesGeneratedFiles(true);
	        cruxCompiler.setIndentPages(false);
	        
	        File cruxCompilationOutput = new File(exporterWorkDir, "cruxModuleExport");
	        cruxCompilationOutput.mkdirs();
	        cruxCompiler.setOutputDir(cruxCompilationOutput);

	        File cruxPagesOutput = new File(exporterWorkDir, "cruxModuleExportPages");
	        cruxPagesOutput.mkdirs();
	        cruxCompiler.setPagesOutputDir(cruxPagesOutput);
	        
	        if (!StringUtils.isEmpty(pagesOutputCharset))
	        {
	        	cruxCompiler.setOutputCharset(pagesOutputCharset);
	        }
	        if (!StringUtils.isEmpty(pageFileExtension))
	        {
	        	cruxCompiler.setPageFileExtension(pageFileExtension);
	        }
        }
        catch (Exception e)
        {
        	 throw new ModuleExporterException("Error creating crux compiler", e);
        }	    
    }

	/**
	 * Initialize Jar creator according with exporter parameters 
	 * @throws ModuleExporterException
	 */
	protected void initializeJarCreator() throws ModuleExporterException
    {
		Map<String, String> metaInfAttributes = new HashMap<String, String>();
		//TODO: other properties in manifest
		
		try
        {
	        jarCreator = new JarCreator(new File[]{exporterWorkDir, sourceDir}, 
	        							new File(outputDir, outputModuleName+".module.jar"), 
	        							includes, excludes, metaInfAttributes);
        }
        catch (IOException e)
        {
	        throw new ModuleExporterException("Error creating jarCreator object", e);
        }
	    
    }

	/**
	 * Initialize Java compiler according with exporter parameters 
	 * @throws ModuleExporterException
	 */
	protected void initializeJavaCompiler() throws ModuleExporterException
    {
	    compiler = new Compiler();
	    try
        {
	        compiler.setOutputDirectory(exporterWorkDir);
	        compiler.setSourcepath(sourceDir);
	        if (!StringUtils.isEmpty(javaSource))
	        {
	        	compiler.setSource(javaSource);
	        }
	        if (!StringUtils.isEmpty(javaTarget))
	        {
	        	compiler.setTarget(javaTarget);
	        }	        
        }
        catch (IOException e)
        {
	        throw new ModuleExporterException("Error creating java compiler", e);
        }
    }

	/**
	 * Makes ModuleExporter object ready for execute the export process.
	 */
	protected void initializeModuleExporter()
    {
	    if (exporterWorkDir != null && exporterWorkDir.exists())
	    {
	    	exporterWorkDir.delete();
	    }
	    try
	    {
	    
	    	exporterWorkDir = new File (FileUtils.getTempDirFile(), "crux_export"+System.currentTimeMillis());
	    	exporterWorkDir.mkdirs();
	    	ClassPathUtil.addURL(exporterWorkDir.toURI().toURL());
	    	ClassPathUtil.addURL(sourceDir.toURI().toURL());
	    
        	ClassPathResolverInitializer.registerClassPathResolver(new ModuleClassPathResolver());
	        ClassPathResolverInitializer.getClassPathResolver().setWebInfClassesPath(exporterWorkDir.toURI().toURL());
        }
        catch (Exception e)
        {
	        throw new ModuleExporterException("Error initializing Module Exporter", e);
        }
    }
	
	/**
	 * Evaluate all program arguments and initialize the associated ModuleExporter properties.
	 * @param parameters
	 */
	protected void processParameters(Collection<ConsoleParameter> parameters)
    {
	    for (ConsoleParameter parameter : parameters)
        {
	        if (parameter.getName().equals("moduleName"))
	        {
	        	this.moduleName = parameter.getValue();
	        	if (StringUtils.isEmpty(this.outputModuleName))
	        	{
	        		this.outputModuleName = this.moduleName;
	        	}
	        }
	        else if (parameter.getName().equals("outputModuleName"))
	        {
	        	this.outputModuleName = parameter.getValue();
	        }
	        else if (parameter.getName().equals("includes"))
	        {
	        	this.includes = parameter.getValue();
	        }
	        else if (parameter.getName().equals("excludes"))
	        {
	        	this.excludes = parameter.getValue();
	        }
	        else if (parameter.getName().equals("outputDir"))
	        {
	    	    this.outputDir = new File(parameter.getValue());
	        }
	        else if (parameter.getName().equals("sourceDir"))
	        {
	    	    this.sourceDir = new File(parameter.getValue());
	        }
	        // Java Compiler Parameters
	        else if (parameter.getName().equals("javaSource"))
	        {
	    	    this.javaSource = parameter.getValue();
	        }
	        else if (parameter.getName().equals("javaTarget"))
	        {
	    	    this.javaTarget = parameter.getValue();
	        }
	        else if (parameter.getName().equals("pagesOutputCharset"))
	        {
	    	    this.pagesOutputCharset = parameter.getValue();
	        }
	        else if (parameter.getName().equals("pageFileExtension"))
	        {
	    	    this.pageFileExtension = parameter.getValue();
	        }
	        
	        /*if (parameter.getName().equals("-gen") || parameter.getName().equals("-style") || parameter.getName().equals("-extra"))
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
	        }*/
        }
	    /*
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
		*/
    }
	

	public static class ClassPathUtil 
	{

		private static final Class<?>[] parameters = new Class[]{URL.class};

		public static void addURL(URL u) throws IOException 
		{

			URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
			Class<?> sysclass = URLClassLoader.class;

			try 
			{
				Method method = sysclass.getDeclaredMethod("addURL",parameters);
				method.setAccessible(true);
				method.invoke(sysloader,new Object[]{ u });

				String classpath = System.getProperty("java.class.path");
		    	System.setProperty("java.class.path", classpath + File.pathSeparatorChar + new File(u.toURI()).getCanonicalPath());
			} 
			catch (Throwable t) 
			{
				throw new IOException("Error, could not add URL to system classloader");
			}
		}
	}
}
