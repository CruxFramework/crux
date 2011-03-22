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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.classpath.PackageFileURLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.scanner.module.Module;
import br.com.sysmap.crux.core.rebind.scanner.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.scanner.screen.ScreenResourceResolverInitializer;
import br.com.sysmap.crux.core.utils.URLUtils;
import br.com.sysmap.crux.module.CruxModule;
import br.com.sysmap.crux.module.CruxModuleBridge;
import br.com.sysmap.crux.module.CruxModuleException;
import br.com.sysmap.crux.module.CruxModuleHandler;
import br.com.sysmap.crux.module.ModuleRef;
import br.com.sysmap.crux.module.config.CruxModuleConfigurationFactory;
import br.com.sysmap.crux.module.validation.CruxModuleValidator;
import br.com.sysmap.crux.tools.export.ModuleExporter;
import br.com.sysmap.crux.tools.jar.JarExtractor;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParameterOption;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxModuleCompiler extends AbstractCruxCompiler
{
	private static final Log logger = LogFactory.getLog(CruxModuleCompiler.class);
	
	private  Map<String, Boolean> alreadyChecked = new HashMap<String, Boolean>();
	private boolean forceModulesCompilation = false;
	private String moduleName;
	private Map<String, Boolean> mustCompileCache = new HashMap<String, Boolean>();
	
	public CruxModuleCompiler()
    {
		ConfigurationFactory.getConfigurations().setEnableWebRootScannerCache(false);
		CruxModuleConfigurationFactory.getConfigurations().setDevelopmentModules("");
    }
	
	
	@Override
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParametersProcessor parametersProcessor = super.createParametersProcessor();
		ConsoleParameter parameter = new ConsoleParameter("-forceModulesCompilation", "Force all modules compilation.", false, false);
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("moduleName", "The folder where the compiled files will be created.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("name", "The name of the module you want to compile. If absent, compiles all modules."));
		parametersProcessor.addSupportedParameter(parameter);
		
		return parametersProcessor;	
	}

	@Override
	protected void doCompileModule(URL url, Module module) throws Exception
	{
		String moduleName = module.getFullName();
		if (forceModulesCompilation || mustCompileModule(moduleName))
		{
			CruxModuleBridge.getInstance().registerCurrentModule(moduleName);
			super.doCompileModule(url, module);
		}
		else
		{
			if (!isModuleCompiled(module))
			{
				extractModuleCompilation(moduleName);
				setModuleAsCompiled(module);
			}
		}
	}
	
	@Override
	protected List<URL> getURLs() throws Exception
	{		
		List<URL> urls = new ArrayList<URL>();
		if(this.moduleName != null)
		{
			CruxModuleBridge.getInstance().registerCurrentModule(this.moduleName);
			urls.addAll(getURLsForRegisteredModule());
		}
		else
		{
			Iterator<CruxModule> cruxModules = CruxModuleHandler.iterateCruxModules();
			while (cruxModules.hasNext())
			{
				CruxModuleBridge.getInstance().registerCurrentModule(cruxModules.next().getName());
				urls.addAll(getURLsForRegisteredModule());
			}
		}
		return urls;
	}

	/**
	 * Gets all URLs for the current registered module.
	 * @return
	 * @throws ScreenConfigException 
	 */
	protected List<URL> getURLsForRegisteredModule() throws ScreenConfigException 
	{
		List<URL> urls = new ArrayList<URL>();
		CruxModule cruxModule = CruxModuleHandler.getCurrentModule();
		URL location = cruxModule.getLocation();
		URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(location.getProtocol());
		String[] pages = getPagesForModule(cruxModule);
		if (pages != null)
		{
			for (String page : pages)
			{
				urls.add(resourceHandler.getChildResource(location, page));
			}
		}
		return urls;		
	}


	/**
	 * @see br.com.sysmap.crux.tools.compile.AbstractCruxCompiler#initializeProcessors()
	 */
	protected void initializeProcessors()
    {
		ModuleDeclarativeUIPreProcessor preProcessor = new ModuleDeclarativeUIPreProcessor();
		preProcessor.setOutputDir(pagesOutputDir);
		preProcessor.setIndent(indentPages);
		preProcessor.setKeepGeneratedFiles(keepPagesGeneratedFiles);
		if (!StringUtils.isEmpty(outputCharset))
		{
			preProcessor.setOutputCharset(outputCharset);
		}
		if (!StringUtils.isEmpty(pageFileExtension))
		{
			preProcessor.setPageFileExtension(pageFileExtension);
		}
		addPreProcessor(preProcessor);
    }
	
	@Override
	protected void processParameters(Collection<ConsoleParameter> parameters)
	{
	    super.processParameters(parameters);
	    
	    for (ConsoleParameter parameter : parameters)
        {
	        if (parameter.getName().equals("-forceModulesCompilation"))
	        {
	        	this.forceModulesCompilation = true;
	        }
	        
	        if (parameter.getName().equals("moduleName"))
	        {
	        	this.setModuleName(parameter.getValue());
	        }
        }
	}	
	
	/**
	 * Checks if all dependencies of the informed module are satisfied.
	 * @param moduleName Name of the module
	 * @return true if dependencies are OK
	 */
	private boolean checkDependenciesForModifications(String moduleName)
    {
		if (alreadyChecked.containsKey(moduleName))
		{
			return alreadyChecked.get(moduleName);
		}
		boolean result = true;
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(moduleName);
	    if (CruxModuleValidator.isDependenciesOk(moduleName))
	    {
	    	ModuleRef[] requiredModules = cruxModule.getRequiredModules();

	    	if (requiredModules != null)
	    	{
	    		Manifest moduleManifest = ModuleExporter.getModuleManifest(moduleName);
	    		for (ModuleRef moduleRef : requiredModules)
	    		{
	    			String foundTimestamp = ModuleExporter.getModuleBuildTimestamp(moduleRef.getName());
	    			String expectedTimestamp = moduleManifest.getMainAttributes().getValue(ModuleExporter.MODULE_DEP_PREFIX+moduleRef.getName());
	    			if (StringUtils.isEmpty(expectedTimestamp) || StringUtils.isEmpty(foundTimestamp) || 
	    					!foundTimestamp.equals(expectedTimestamp))
	    			{
	    				result = false;
	    				break;
	    			}
	    			if (!checkDependenciesForModifications(moduleRef.getName()))
	    			{
	    				result = false;
	    				break;
	    			}
	    		}
	    	}
	    }
	    else
	    {
	    	throw new CruxModuleException("Module dependencies are broken. Module: "+ moduleName);
	    }
	    alreadyChecked.put(moduleName, result);
	    return result;
    }
	
	/**
	 * @param moduleName
	 */
	private void extractModuleCompilation(String moduleName)
    {
		logger.info("Extracting module compiled output from jar file...");
		try
		{
			URL rootPath = ModuleExporter.getModuleJarRootPath(moduleName);
			URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler(rootPath.getProtocol());
			if (handler instanceof PackageFileURLResourceHandler)
			{
				URL jarURL = ((PackageFileURLResourceHandler)handler).getPackageFile(rootPath);
				
				// Extract gwt compilation
				Map<String, String> replacements = new HashMap<String, String>();
				replacements.put(ModuleExporter.CRUX_MODULE_EXPORT+"/", "");
				
				JarExtractor extractor = new JarExtractor(new File[]{new File(jarURL.toURI())}, this.outputDir, 
						ModuleExporter.CRUX_MODULE_EXPORT+"/**", null, replacements, false);

				extractor.extractJar();

				if(keepPagesGeneratedFiles)
				{
					replacements = new HashMap<String, String>();
					replacements.put(ModuleExporter.CRUX_MODULE_EXPORT_PAGES+"/", "");
					extractor = new JarExtractor(new File[]{new File(jarURL.toURI())}, this.pagesOutputDir, 
													ModuleExporter.CRUX_MODULE_EXPORT_PAGES+"/**", null, replacements, false);
					extractor.extractJar();
				}
			}
			else
			{
				throw new CruxModuleException("Module is not packaged as a jar file.");
			}
		}
		catch(Exception e)
		{
			throw new CruxModuleException("Error extracting module compiled output from jar.", e);
		}
	    
    }		

	/**
	 * 
	 * @param cruxModule
	 * @throws ScreenConfigException 
	 */
	protected String[] getPagesForModule(CruxModule cruxModule) throws ScreenConfigException
	{
		Set<String> allScreenIDs = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllScreenIDs(cruxModule.getName());
		if (allScreenIDs != null)
		{
			String[] pages = new String[allScreenIDs.size()];
			int i=0;
			for (String screenID : allScreenIDs)
			{
				if (screenID.startsWith(cruxModule.getLocation().toString()))
				{
					screenID = screenID.substring(cruxModule.getLocation().toString().length());
				}
				if (screenID.startsWith("/"))
				{
					screenID = screenID.substring(1);
				}

				pages[i++] = getScreenID(cruxModule, screenID);
			}

			return pages;
		}
		return new String[0];
	}
	
	/**
	 * 
	 * @param cruxModule
	 * @param screenID
	 * @return
	 */
	private String getScreenID(CruxModule cruxModule, String screenID)
	{
		URL location = cruxModule.getLocation();
		URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(location.getProtocol());
	
		for (String publicPath : cruxModule.getGwtModule().getPublicPaths())
		{
			if (URLUtils.existsResource(resourceHandler.getChildResource(location, publicPath+"/"+screenID))) 
			{
				return publicPath+"/"+screenID;	
			}
			else if (screenID.startsWith(publicPath))
			{
				return screenID;
			}
		}

		return screenID;
	}

	/**
	 * @param moduleName
	 * @return
	 */
	private boolean hasCompilationFolder(String moduleName)
    {
		URL rootPath = ModuleExporter.getModuleJarRootPath(moduleName);
		
		URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler(rootPath.getProtocol());
		URL moduleManifest = handler.getChildResource(rootPath, "cruxModuleExport");
		return URLUtils.existsResource(moduleManifest);
    }


	/**
	 * Checks if the informed module must be compiled 
	 * @param moduleName
	 * @return
	 */
	public boolean mustCompileModule(String moduleName)
	{
		if (mustCompileCache.containsKey(moduleName))
		{
			return mustCompileCache.get(moduleName);
		}
		
		boolean result = true;
		try
		{
			result = !hasCompilationFolder(moduleName);
			if (!result)
			{

				result = !checkDependenciesForModifications(moduleName);
			}
		}
		catch (Exception e) 
		{
			logger.warn("Error validating module pre-compiled output. Forcing a new module compilation...", e);
		}
		mustCompileCache.put(moduleName, result);
		return result;
	}

	/**
	 * @param moduleName
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}