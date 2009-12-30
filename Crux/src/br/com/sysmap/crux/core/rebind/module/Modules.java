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
package br.com.sysmap.crux.core.rebind.module;
 
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class Modules 
{
	private static Map<String, Module> modules = null;
	private static Map<String, String> moduleAliases = null;
	private static GeneratorMessages messages = MessagesFactory.getMessages(GeneratorMessages.class);
	private static final Log logger = LogFactory.getLog(Modules.class);
	private static final Lock lock = new ReentrantLock();

	/**
	 * 
	 */
	public static void initialize()
	{
		if (modules != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (modules != null)
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
	 * @param id
	 * @return
	 */
	public static Module getModule(String id)
	{
		if (modules == null)
		{
			initialize();
		}
		if (moduleAliases.containsKey(id))
		{
			id = moduleAliases.get(id);
		}
		return modules.get(id);
	}
	
	/**
	 * 
	 * @return
	 */
	public static Iterator<Module> iterateModules()
	{
		if (modules == null)
		{
			initialize();
		}
		return modules.values().iterator();
	}
	
	/**
	 * 
	 * @param controller
	 * @param module
	 * @return
	 */
	public static boolean isClassOnModulePath(String controller, String moduleId, Set<String> alreadySearched)
	{
		if (alreadySearched.contains(moduleId))
		{
			return false;
		}
		alreadySearched.add(moduleId);
		Module module = Modules.getModule(moduleId);
		if (module != null)
		{
			if (controller.startsWith(module.getSource()))
			{
				return true;
			}
			
			for (String inheritModule : module.getInherits())
			{
				if (isClassOnModulePath(controller, inheritModule, alreadySearched))
				{
					return true;
				}
			}
		}
		
		return false;
	}	
	

	/**
	 * 
	 */
	protected static void initializeModules()
	{
		modules = new HashMap<String, Module>();
		moduleAliases = new HashMap<String, String>();
		logger.info(messages.modulesScannerSearchingModuleFiles());
		ModulesScanner.getInstance().scanArchives();
	}

	/**
	 * 
	 * @param templateId
	 * @param template
	 */
	static void registerModule(String moduleFullName, Document moduleDocument)
	{
		Module module = new Module();
		
		Element element = moduleDocument.getDocumentElement();
		
		module.setFullName(moduleFullName);
		module.setName(getModuleName(moduleFullName, element));
		module.setSource(getModuleSource(moduleFullName, element));
		module.setPublicPath(getModulePublicPath(moduleFullName, element));
		module.setInherits(getModuleInherits(element));
		modules.put(module.getName(), module);
		moduleAliases.put(moduleFullName, module.getName());
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	private static Set<String> getModuleInherits(Element element)
	{
		Set<String> result = new HashSet<String>();
		NodeList inheritsTags = element.getElementsByTagName("inherits");
		if (inheritsTags != null)
		{
			for (int i=0; i<inheritsTags.getLength(); i++)
			{
				Element inherit = (Element)inheritsTags.item(i);
				result.add(inherit.getAttribute("name"));
			}
		}
		return result;
	}

	/**
	 * 
	 * @param moduleFullName
	 * @param element
	 * @return
	 */
	private static String getModuleName(String moduleFullName, Element element)
	{
		String moduleName = moduleFullName;
		String renameTo = element.getAttribute("rename-to");
		if (renameTo != null && renameTo.length() > 0)
		{
			moduleName = renameTo;
		}
		return moduleName;
	}

	/**
	 * 
	 * @param moduleFullName
	 * @param element
	 * @return
	 */
	private static String getModuleSource(String moduleFullName, Element element)
	{
		moduleFullName = moduleFullName.replace('.', '/');
		int index = moduleFullName.lastIndexOf('/');
		if (index > 0)
		{
			moduleFullName = moduleFullName.substring(0, index);
		}
		else
		{
			moduleFullName = "";
		}
		String sourcePath = moduleFullName+"/client";
		NodeList sourceTags = element.getElementsByTagName("source");
		if (sourceTags != null && sourceTags.getLength() > 0)
		{
			Element source = (Element)sourceTags.item(0);
			sourcePath = source.getAttribute("path");
			if (sourcePath != null && sourcePath.length()>0)
			{
				sourcePath = moduleFullName+"/"+sourcePath;
			}
		}
		return sourcePath;
	}
	
	/**
	 * 
	 * @param moduleFullName
	 * @param element
	 * @return
	 */
	private static String getModulePublicPath(String moduleFullName, Element element)
	{
		moduleFullName = moduleFullName.replace('.', '/');
		int index = moduleFullName.lastIndexOf('/');
		if (index > 0)
		{
			moduleFullName = moduleFullName.substring(0, index);
		}
		else
		{
			moduleFullName = "";
		}
		String publicPath = moduleFullName+"/public";
		NodeList publicTags = element.getElementsByTagName("public");
		if (publicTags != null && publicTags.getLength() > 0)
		{
			Element source = (Element)publicTags.item(0);
			publicPath = source.getAttribute("path");
			if (publicPath != null && publicPath.length()>0)
			{
				publicPath = moduleFullName+"/"+publicPath;
			}
		}
		return publicPath;
	}
}
