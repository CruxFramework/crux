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

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import br.com.sysmap.crux.core.utils.URLUtils;
import br.com.sysmap.crux.module.CruxModule;
import br.com.sysmap.crux.module.CruxModuleBridge;
import br.com.sysmap.crux.module.CruxModuleHandler;
import br.com.sysmap.crux.module.config.CruxModuleConfigurationFactory;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class CruxModuleCompiler extends AbstractCruxCompiler
{
	
	public CruxModuleCompiler()
    {
		ConfigurationFactory.getConfigurations().setEnableWebRootScannerCache(false);
		CruxModuleConfigurationFactory.getConfigurations().setDevelopmentModules("");
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
	protected List<URL> getURLs() throws Exception
	{
		Iterator<CruxModule> cruxModules = CruxModuleHandler.iterateCruxModules();
		List<URL> urls = new ArrayList<URL>();
		while (cruxModules.hasNext())
		{
			CruxModule cruxModule = cruxModules.next();
			URL location = cruxModule.getLocation();
			URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(location.getProtocol());
			CruxModuleBridge.getInstance().registerCurrentModule(cruxModule.getName());
			String[] pages = getPagesForModule(cruxModule);
			if (pages != null)
			{
				for (String page : pages)
				{
					urls.add(resourceHandler.getChildResource(location, page));
				}
			}
		}

		return urls;
	}

	@Override
	protected void setCompiledModule(String moduleName)
	{
		super.setCompiledModule(moduleName);
		CruxModuleBridge.getInstance().registerCurrentModule(moduleName);
	}

	/**
	 * 
	 * @param cruxModule
	 * @throws ScreenConfigException 
	 */
	private String[] getPagesForModule(CruxModule cruxModule) throws ScreenConfigException
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
}
