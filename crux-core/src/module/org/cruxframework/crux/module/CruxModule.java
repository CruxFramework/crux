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

import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.rebind.module.Module;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxModule
{
	private static final Lock lockDependencies = new ReentrantLock();

	private final Module module;
	private final ModuleInfo info;
	private URL location;
	private String[] pages;
	private ModuleRef[] requiredModules;
	
	CruxModule(Module module, ModuleInfo info)
	{
		this.module = module;
		this.info = info;
	}

	public Module getGwtModule()
	{
		return module;
	}

	public ModuleInfo getInfo()
	{
		return info;
	}
	
	public URL getLocation()
	{
		return location;
	}
	
	void setLocation(URL location)
	{
		this.location = location;
	}

	public String getName()
	{
		return getGwtModule().getName();		
	}
	
	public String[] getPages()
	{
		if (pages == null)
		{
			CruxModuleHandler.initializeModulesPages();
		}
		return pages;
	}

	void setPages(String[] pages)
	{
		this.pages = pages;
	}

	public ModuleRef[] getRequiredModules()
	{
		if (requiredModules == null)
		{
			lockDependencies.lock();
			try
			{
				if (requiredModules == null)
				{
				CruxModuleHandler.initializeModulesDependencies(this);
				}
			}
	        finally
	        {
	        	lockDependencies.unlock();
	        }				
		}
		return requiredModules;
	}

	void setRequiredModules(ModuleRef[] requiredModules)
	{
		this.requiredModules = requiredModules;
	}
}
