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
package org.cruxframework.crux.module;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.module.config.CruxModuleConfigurationFactory;
import org.cruxframework.crux.module.validation.CruxModuleValidator;
import org.cruxframework.crux.scannotation.URLStreamManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxModuleHandler
{
	private static Map<Module, CruxModule> cruxModules = null;
	private static boolean pagesInitialized = false;
	private static final Lock lock = new ReentrantLock();
	private static final Lock lockPages = new ReentrantLock();
	private static DocumentBuilder documentBuilder;
	
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
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			initializeModules();
		}
		catch (ParserConfigurationException e)
		{
			throw new CruxModuleException("Error initializing the crux module handler.", e);
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
				throw new CruxModuleException("No development module found. Define it using the 'developmentModule' properties on CruxModuleConfig.properties file.");
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
		Module mod = Modules.getInstance().getModule(module);
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
		Iterator<Module> modules = Modules.getInstance().iterateModules();
		while (modules.hasNext())
		{
			Module module = modules.next();
			URLStreamManager streamManager = null;
			try
			{
				int lastDot = module.getFullName().lastIndexOf('.');
				String moduleSimpleName = module.getFullName().substring(lastDot + 1);
				URL gwtXmlPath = module.getDescriptorURL();
				URLResourceHandler navigator = URLResourceHandlersRegistry.getURLResourceHandler(gwtXmlPath.getProtocol());
				URL cruxModuleXml = navigator.getChildResource(navigator.getParentDir(gwtXmlPath), moduleSimpleName + ".module.xml");
				streamManager = new URLStreamManager(cruxModuleXml);
				ModuleInfo info = parseModuleDescriptor(streamManager.open());
				if (info != null)
				{
					cruxModules.put(module, buildCruxModule(module, info));
				}
			}
			catch (Exception e)
			{
				throw new CruxModuleException("Error initializing the crux module handler.", e);
			}
			finally
			{
				if(streamManager != null)
				{
					try{ streamManager.close(); } catch (Exception e) {};
				}
			}
		}
	}

	/**
	 * 
	 * @param stream
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public static ModuleInfo parseModuleDescriptor(InputStream stream) throws SAXException, IOException
	{
		if (stream == null)
		{
			return null;
		}
		
		ModuleInfo info = new ModuleInfo();
		Document descriptor = documentBuilder.parse(stream);
		NodeList childNodes = descriptor.getDocumentElement().getChildNodes();
		for (int i=0; i<childNodes.getLength(); i++)
		{
			Node item = childNodes.item(i);
			
			if (item instanceof Element)
			{
				Element element = (Element) item;
				
				String localName = element.getNodeName();
				if ("description".equalsIgnoreCase(localName))
				{
					info.setDescription(element.getTextContent());
				}
			}
		}
		return info;
	}

	/**
	 * 
	 */
	static void initializeModulesDependencies(CruxModule cruxModule)
	{
		Set<String> inherits = cruxModule.getGwtModule().getInherits();
		List<ModuleRef> requiredModules = new ArrayList<ModuleRef>();
		
		for (String inherit : inherits)
        {
			CruxModule dependency = getCruxModule(inherit);
			if (dependency != null)
			{
				requiredModules.add(new ModuleRef(dependency.getName()));
			}
        }
		
		cruxModule.setRequiredModules(requiredModules.toArray(new ModuleRef[requiredModules.size()]));
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
			String[] modulePages = Modules.getInstance().searchModulePages(cruxModule.getGwtModule());
			cruxModule.setPages(modulePages);
		}
		catch (Exception e)
		{
			throw new CruxModuleException("Error searching for pages into module ["+cruxModule.getName()+"].", e);
		}
	}
}
