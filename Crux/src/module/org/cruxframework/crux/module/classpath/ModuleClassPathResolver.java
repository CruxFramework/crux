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
package org.cruxframework.crux.module.classpath;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverImpl;
import org.cruxframework.crux.module.CruxModule;
import org.cruxframework.crux.module.CruxModuleHandler;


/**
 * A classPathResolver that maps all modules public folders as web root folders 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleClassPathResolver extends ClassPathResolverImpl
{
	/**
	 * @see org.cruxframework.crux.core.server.classpath.ClassPathResolverImpl#findWebBaseDirs()
	 */
	@Override
	public URL[] findWebBaseDirs()
	{
		try
		{
			List<URL> urls = new ArrayList<URL>();
 
			CruxModule cruxModule = CruxModuleHandler.getCurrentModule();
			URL moduleLocation = cruxModule.getLocation();
			URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(moduleLocation.getProtocol());
			for(String publicPath: cruxModule.getGwtModule().getPublicPaths())
			{
				urls.add(resourceHandler.getChildResource(moduleLocation, publicPath));
			}
			return urls.toArray(new URL[urls.size()]);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public void initialize()
	{
	    // Do nothing
	}
}
