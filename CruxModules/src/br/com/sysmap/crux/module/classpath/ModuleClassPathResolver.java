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
package br.com.sysmap.crux.module.classpath;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverImpl;
import br.com.sysmap.crux.module.CruxModule;
import br.com.sysmap.crux.module.CruxModuleHandler;

/**
 * A classPathResolver that maps all modules public folders as web root folders 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleClassPathResolver extends ClassPathResolverImpl
{
	/**
	 * @see br.com.sysmap.crux.core.server.classpath.ClassPathResolverImpl#findWebBaseDirs()
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
}
