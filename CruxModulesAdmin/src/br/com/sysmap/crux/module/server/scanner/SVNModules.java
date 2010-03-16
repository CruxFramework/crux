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
package br.com.sysmap.crux.module.server.scanner;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.com.sysmap.crux.module.client.dto.ModuleInfo;
import br.com.sysmap.crux.module.client.dto.ModuleRef;
 

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class SVNModules
{
	private static final Lock lock = new ReentrantLock();
	private Map<String, ModuleInfo> modulesInfo = null;
	private final SVNContext context;
	
	/**
	 * 
	 */
	public SVNModules(SVNContext context)
	{
		this.context = context;
		context.setModules(this);
	}
	
	/**
	 * 
	 * @param module
	 * @return
	 */
	public ModuleInfo getModuleInfo(String module, String version)
	{
		return modulesInfo.get(module+version);
	}
	
	/**
	 * 
	 * @return
	 */
	public Iterator<ModuleInfo> iterateModulesInfo()
	{
		if (modulesInfo == null)
		{
			initialize();
		}
		return modulesInfo.values().iterator();
	}

	/**
	 * 
	 */
	public void initialize()
	{
		if (modulesInfo != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (modulesInfo != null)
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
	 * @param moduleDescriptor
	 * @param moduleFullName
	 * @param moduleDocument
	 * @param dependencies
	 * @return
	 */
	protected ModuleInfo registerModule(URL moduleDescriptor, String moduleFullName, Document moduleDocument, br.com.sysmap.crux.module.ModuleInfo moduleInfo)
	{
		ModuleInfo info = new ModuleInfo();
		info.setName(getModuleName(moduleFullName, moduleDocument.getDocumentElement()));
		info.setDescription(moduleInfo.getDescription());
		info.setStartPage(moduleInfo.getStartPage());
		info.setVersion(moduleInfo.getVersion());
		
		br.com.sysmap.crux.module.ModuleRef[] dependencies = moduleInfo.getDependencies();
		if (dependencies != null)
		{
			ModuleRef[] dep = new ModuleRef[dependencies.length];
			for(int i=0; i< dependencies.length; i++)
			{
				ModuleRef ref = new ModuleRef();
				ref.setMaxVersion(dependencies[i].getMaxVersion());
				ref.setMinVersion(dependencies[i].getMinVersion());
				ref.setName(dependencies[i].getName());
				dep[i] = ref;
			}
			
			info.setRequiredModules(dep);
		}
		modulesInfo.put(info.getName()+info.getVersion(), info);
		return info;
	}
	
	/**
	 * 
	 */
	protected void initializeModules()
	{
		modulesInfo = new HashMap<String, ModuleInfo>();
		SVNScanner.getInstance().scanArchives(context);
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

	public SVNContext getContext()
	{
		return context;
	}
}
