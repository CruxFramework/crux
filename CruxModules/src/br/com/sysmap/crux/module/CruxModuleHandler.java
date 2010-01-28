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
package br.com.sysmap.crux.module;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.module.Module;
import br.com.sysmap.crux.core.rebind.module.Modules;
import br.com.sysmap.crux.core.rebind.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import br.com.sysmap.crux.core.server.Environment;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.module.config.CruxModuleConfigurationFactory;
import br.com.sysmap.crux.module.validation.CruxModuleValidator;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class CruxModuleHandler
{
	private static Map<Module, CruxModule> cruxModules = null;
	private static boolean pagesInitialized = false;
	private static final Lock lock = new ReentrantLock();
	private static final Lock lockPages = new ReentrantLock();
	private static final CruxModuleMessages messages = MessagesFactory.getMessages(CruxModuleMessages.class);
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (cruxModules != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (cruxModules != null)
			{
				return;
			}
			
			initializeModules();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 * @return
	 */
	public static Iterator<CruxModule> iterateCruxModules()
	{
		if (cruxModules == null)
		{
			initialize();
		}
		return cruxModules.values().iterator();
	}
	
	/**
	 * 
	 * @return
	 */
	public static CruxModule getCurrentModule()
	{
		String currentModule = CruxModuleBridge.getInstance().getCurrentModule(); 
		
		if (currentModule == null || currentModule.length() == 0)
		{
			Iterator<CruxModule> modules = iterateCruxModules();
			if (modules.hasNext())
			{
				return modules.next();
			}
			else
			{
				throw new CruxModuleException(messages.developmentCruxModuleNotDefined());
			}
		}		
		return getCruxModule(currentModule);
	}

	/**
	 * 
	 * @return
	 */
	public static String[] getDevelopmentModules()
	{
		if (Environment.isProduction())
		{
			return null;
		}
		String developmentModules = CruxModuleConfigurationFactory.getConfigurations().developmentModules();
		if (developmentModules == null)
		{
			return new String[0];
		}
		String[] modules = RegexpPatterns.REGEXP_COMMA.split(developmentModules);
		for (int i=0; i< modules.length; i++)
		{
			modules[i] = modules[i].trim();
		}
		return modules;
	}
	
	
	/**
	 * 
	 * @param module
	 * @return
	 */
	public static CruxModule getCruxModule(String module)
	{
		if (cruxModules == null)
		{
			initialize();
		}
		Module mod = Modules.getModule(module);
		return cruxModules.get(mod);
	}
	
	/**
	 * 
	 * @param module
	 * @return
	 */
	public static boolean isDependenciesOk(String module)
	{
		return CruxModuleValidator.isDependenciesOk(module);
	}

	/**
	 * 
	 */
	private static void initializeModules()
	{
		cruxModules = new HashMap<Module, CruxModule>();
		Iterator<Module> modules = Modules.iterateModules();
		while (modules.hasNext())
		{
			Module module = modules.next();
			try
			{
				Class<?> moduleClass = Class.forName(module.getFullName());
				ModuleInfo info = (ModuleInfo) moduleClass.newInstance();
				cruxModules.put(module, buildCruxModule(module, info));
			}
			catch (ClassNotFoundException cnfe)
			{
				continue;
			}
			catch (Exception e)
			{
				throw new CruxModuleException(messages.errorInitializingCruxModuleHandler(), e);
			}
		}
	}

	/**
	 * 
	 */
	static void initializeModulesPages()
	{
		if (!pagesInitialized)
		{
			lockPages.lock();
			if (!pagesInitialized)
			{
				pagesInitialized = true;
				try
				{
					String currentModule = CruxModuleBridge.getInstance().getCurrentModule(); 
					try
					{
						for (CruxModule cruxModule : cruxModules.values())
						{
							CruxModuleBridge.getInstance().registerCurrentModule(cruxModule.getName());
							searchModulePages(cruxModule);
						}
					}
					finally
					{
						CruxModuleBridge.getInstance().registerCurrentModule(currentModule);
					}
				}
				finally
				{
					lockPages.unlock();
				}
			}
		}
	}

	/**
	 * 
	 * @param module
	 * @param info
	 * @return
	 */
	private static CruxModule buildCruxModule(Module module, ModuleInfo info)
	{
		CruxModule cruxModule = new CruxModule(module, info);
		URL location = module.getDescriptorURL();
		location = URLResourceHandlersRegistry.getURLResourceHandler(location.getProtocol()).getParentDir(location);
		cruxModule.setLocation(location);
		
		return cruxModule;
	}	

	/**
	 * 
	 * @param cruxModule
	 */
	private static void searchModulePages(CruxModule cruxModule)
	{
		try
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
					for (String publicPath : cruxModule.getGwtModule().getPublicPaths())
					{
						if (screenID.startsWith(publicPath))
						{
							screenID = screenID.substring(publicPath.length());
							break;
						}
					}
					if (screenID.startsWith("/"))
					{
						screenID = screenID.substring(1);
					}

					if (screenID.endsWith(".crux.xml"))
					{
						screenID = screenID.substring(0, screenID.length()-9)+".html";
					}

					pages[i++] = screenID;
				}

				cruxModule.setPages(pages);
			}
			else 
			{
				cruxModule.setPages(new String[0]);
			}
		}
		catch (ScreenConfigException e)
		{
			throw new CruxModuleException(messages.errorSearchingForModulePages(cruxModule.getName()));
		}
	}
}
