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
 
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Modules 
{
	protected Map<String, Module> modules = null;
	protected Map<String, String> moduleAliases = null;
	private GeneratorMessages messages = MessagesFactory.getMessages(GeneratorMessages.class);
	private static final Log logger = LogFactory.getLog(Modules.class);
	private static final Lock lock = new ReentrantLock();

	private static Modules instance = new Modules();
	
	protected Modules()
	{
	}
	
	public static Modules getInstance()
	{
		return instance;
	}
	
	/**
	 * 
	 */
	public void initialize()
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
	public Module getModule(String id)
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
	public Iterator<Module> iterateModules()
	{
		if (modules == null)
		{
			initialize();
		}
		return modules.values().iterator();
	}
	
	/**
	 * @param controller
	 * @param moduleId
	 * @return
	 */
	public boolean isClassOnModulePath(String controller, String moduleId)
	{
		return isClassOnModulePath(controller.replace('.', '/'), moduleId, new HashSet<String>());
	}

	/**
	 * 
	 * @param controller
	 * @param module
	 * @return
	 */
	protected boolean isClassOnModulePath(String controller, String moduleId, Set<String> alreadySearched)
	{
		if (alreadySearched.contains(moduleId))
		{
			return false;
		}
		alreadySearched.add(moduleId);
		Module module = getModule(moduleId);
		if (module != null)
		{
			
			for(String source: module.getSources())
			{
				if (controller.startsWith(module.getRootPath()+source))
				{
					return true;
				}
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
	protected void initializeModules()
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
	protected Module registerModule(URL moduleDescriptor, String moduleFullName, Document moduleDocument)
	{
		Module module = new Module();
		
		Element element = moduleDocument.getDocumentElement();
		
		module.setFullName(moduleFullName);
		module.setName(getModuleName(moduleFullName, element));
		module.setSources(getModuleSources(element));
		module.setPublicPaths(getModulePublicPaths(element));
		module.setRootPath(getModuleRootPath(moduleFullName));
		module.setInherits(getModuleInherits(element));
		module.setDescriptorURL(moduleDescriptor);
		modules.put(module.getName(), module);
		moduleAliases.put(moduleFullName, module.getName());
		return module;
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	private Set<String> getModuleInherits(Element element)
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
	private String getModuleName(String moduleFullName, Element element)
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
	 * @return
	 */
	private String getModuleRootPath(String moduleFullName)
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
		return moduleFullName+"/";
	}
	
	/**
	 * 
	 * @param moduleFullName
	 * @param element
	 * @return
	 */
	private String[] getModuleSources(Element element)
	{
		NodeList sourceTags = element.getElementsByTagName("source");
		if (sourceTags != null && sourceTags.getLength() > 0)
		{
			List<String> paths = new ArrayList<String>();
			for (int i=0; i < sourceTags.getLength(); i++)
			{
				Element source = (Element)sourceTags.item(i);
				String sourcePath = source.getAttribute("path");
				if (sourcePath != null && sourcePath.length()>0)
				{
					paths.add(sourcePath);
				}
			}

			return paths.toArray(new String[paths.size()]);
		}
		else
		{
			return new String[]{"client"};
		}
	}
	
	/**
	 * 
	 * @param moduleFullName
	 * @param element
	 * @return
	 */
	private String[] getModulePublicPaths(Element element)
	{
		NodeList sourceTags = element.getElementsByTagName("public");
		if (sourceTags != null && sourceTags.getLength() > 0)
		{
			List<String> paths = new ArrayList<String>();
			for (int i=0; i < sourceTags.getLength(); i++)
			{
				Element source = (Element)sourceTags.item(i);
				String sourcePath = source.getAttribute("path");
				if (sourcePath != null && sourcePath.length()>0)
				{
					paths.add(sourcePath);
				}
			}

			return paths.toArray(new String[paths.size()]);
		}
		else
		{
			return new String[]{"public"};
		}
	}
}
