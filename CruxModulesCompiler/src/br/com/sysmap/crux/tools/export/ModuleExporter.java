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
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.utils.FileUtils;
import br.com.sysmap.crux.core.utils.URLUtils;
import br.com.sysmap.crux.module.CruxModule;
import br.com.sysmap.crux.module.CruxModuleHandler;
import br.com.sysmap.crux.module.ModuleRef;
import br.com.sysmap.crux.module.classpath.ModuleClassPathResolver;
import br.com.sysmap.crux.module.validation.CruxModuleValidator;
import br.com.sysmap.crux.scannotation.ClasspathUrlFinder;
import br.com.sysmap.crux.tools.compile.CompilerException;
import br.com.sysmap.crux.tools.compile.CruxModuleCompiler;
import br.com.sysmap.crux.tools.compile.JCompiler;
import br.com.sysmap.crux.tools.compile.utils.ClassPathUtils;
import br.com.sysmap.crux.tools.compile.utils.ModuleUtils;
import br.com.sysmap.crux.tools.jar.JarCreator;
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
	public static final String CRUX_MODULE_EXPORT_PAGES = "cruxModuleExportPages";
	public static final String CRUX_MODULE_EXPORT = "cruxModuleExport";
	public static final String MODULE_DEP_PREFIX = "CruxDep-";

	private static String[] DEFAULT_EXCLUDES = {
		 "**/*~",
	     "**/#*#",
	     "**/.#*",
	     "**/%*%",
	     "**/._*",
	     "**/CVS",
	     "**/CVS/**",
	     "**/.cvsignore",
	     "**/SCCS",
	     "**/SCCS/**",
	     "**/vssver.scc",
	     "**/.svn",
	     "**/.svn/**",
	     "**/.DS_Store"
	};
	
	
	private JCompiler compiler;
	private CruxModuleCompiler cruxCompiler;
	private String excludes;
	private boolean doNotExportCruxCompilation = false;
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
	private String scanAllowedPackages;
	private String scanIgnoredPackages;
	private File sourceDir;
	private boolean unpackaged = false;
	
	
	/**
	 * @param moduleRef
	 */
	public static String getModuleBuildTimestamp(String moduleName)
    {
		Manifest manifest = getModuleManifest(moduleName);
		return manifest.getMainAttributes().getValue(JarCreator.MANIFEST_BUILD_TIMESTAMP_PROPERTY);
    }

	/**
	 * @param moduleRef
	 */
	public static Manifest getModuleManifest(String moduleName)
    {
		try
		{
			URL location = getModuleJarRootPath(moduleName);
			URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler(location.getProtocol());
			URL moduleManifest = handler.getChildResource(location, "META-INF/MANIFEST.MF");
			InputStream stream = URLUtils.openStream(moduleManifest);
			if (stream == null)
			{
				throw new ModuleExporterException("Error reading module manifest file. Module: "+ moduleName);
			}
			return new Manifest(stream);
		}
		catch (IOException e)
		{
			throw new ModuleExporterException("Error reading module manifest file. Module: "+ moduleName, e);
		}
    }

	
	/**
	 * @param moduleName
	 * @return
	 */
	public static URL getModuleJarRootPath(String moduleName)
	{
		CruxModule refModule = CruxModuleHandler.getCruxModule(moduleName);
		URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler(refModule.getLocation().getProtocol());
		String[] rootPath = refModule.getGwtModule().getRootPath().split("/");

		URL location = refModule.getLocation();

		for (int i=0; i< rootPath.length; i++)
		{
			location = handler.getParentDir(location);
		}
		return location;
	}
	
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
				if (!this.doNotExportCruxCompilation)
				{
					compileCruxModule();
				}

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
		
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-doNotExportCruxCompilation", "Makes export skip crux compilation.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-unpackaged", "Export the module to an unpacked folder and not to a .module.jar file.", false, true));

		// Java Compiler Parameters
		parameter = new ConsoleParameter("javaSource", "Source version of Java files", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("source", "java source version"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("javaTarget", "Target version for Java compilation", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("target", "java target version"));
		parametersProcessor.addSupportedParameter(parameter);

		// Crux Compiler Parameters
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
	        cruxCompiler.setPreCompileJavaSource(false);
	        
	        File cruxCompilationOutput = new File(exporterWorkDir, CRUX_MODULE_EXPORT);
	        cruxCompilationOutput.mkdirs();
	        cruxCompiler.setOutputDir(cruxCompilationOutput);

	        File cruxPagesOutput = new File(exporterWorkDir, CRUX_MODULE_EXPORT_PAGES);
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
	        if (!StringUtils.isEmpty(scanAllowedPackages))
	        {
	        	cruxCompiler.setScanAllowedPackages(scanAllowedPackages);
	        }
	        if (!StringUtils.isEmpty(scanIgnoredPackages))
	        {
	        	cruxCompiler.setScanIgnoredPackages(scanIgnoredPackages);
	        }
	        if (!StringUtils.isEmpty(moduleName))
	        {
	        	cruxCompiler.setModuleName(moduleName);
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
		Map<String, String> metaInfAttributes = getModuleMetaInfAttributes();
		
		try
        {
        	jarCreator = new JarCreator(new File[]{exporterWorkDir, sourceDir}, 
	        							new File(outputDir, outputModuleName+".module.jar"), 
	        							includes, excludes, metaInfAttributes, unpackaged);
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
	    compiler = new JCompiler();
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
	    	ClassPathUtils.addURL(exporterWorkDir.toURI().toURL());
	    	ClassPathUtils.addURL(sourceDir.toURI().toURL());
	    
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
	        else if (parameter.getName().equals("-doNotExportCruxCompilation"))
	        {
	        	this.doNotExportCruxCompilation = true;
	        }
	        else if (parameter.getName().equals("-unpackaged"))
	        {
	        	this.unpackaged  = true;
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
	        // Crux Compiler Parameters
	        else if (parameter.getName().equals("pagesOutputCharset"))
	        {
	    	    this.pagesOutputCharset = parameter.getValue();
	        }
	        else if (parameter.getName().equals("pageFileExtension"))
	        {
	    	    this.pageFileExtension = parameter.getValue();
	        }
	        else if (parameter.getName().equals("scanAllowedPackages"))
	        {
	    	    this.scanAllowedPackages = parameter.getValue();
	        }
	        else if (parameter.getName().equals("scanIgnoredPackages"))
	        {
	    	    this.scanIgnoredPackages = parameter.getValue();
	        }
        }
	    
	    String defaultExcludes = Arrays.toString(DEFAULT_EXCLUDES);
	    defaultExcludes = defaultExcludes.substring(1, defaultExcludes.length()-1);
	    
	    if (!StringUtils.isEmpty(excludes))
	    {
	    	excludes += ","+defaultExcludes;
	    }
	    else
	    {
	    	excludes = defaultExcludes;
	    }
    }

	/**
	 * @return
	 */
	private Map<String, String> getModuleMetaInfAttributes()
    {
		HashMap<String, String> attributes = new HashMap<String, String>();
		
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(moduleName);
		ModuleRef[] requiredModules = cruxModule.getRequiredModules();
		
		if (requiredModules != null)
		{
			for (ModuleRef moduleRef : requiredModules)
			{
				String timestamp = getModuleBuildTimestamp(moduleRef.getName());
				attributes.put(MODULE_DEP_PREFIX+moduleRef.getName(), timestamp);
			}
		}
		
	    return attributes;
    }
}
