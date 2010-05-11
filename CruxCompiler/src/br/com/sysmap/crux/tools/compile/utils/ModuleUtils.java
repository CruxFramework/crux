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
package br.com.sysmap.crux.tools.compile.utils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.rebind.module.Module;
import br.com.sysmap.crux.core.rebind.module.Modules;
import br.com.sysmap.crux.core.rebind.module.ModulesScanner;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ModuleUtils
{
	private static final String MODULE_IMPORT_SUFFIX = ".nocache.js";
	
	
	public static void initializeScannerURLs(URL[] urls)
	{
		ModulesScanner.initialize(urls);
	}
	
	/**
	 * 
	 * @param pageFile
	 * @return
	 * @throws IOException
	 * @throws InterfaceConfigException
	 */
	public static String findModuleNameFromHostedPage(URL pageFile) throws IOException, InterfaceConfigException
	{
		Module module = findModuleFromPageUrl(pageFile);
		if (module != null)
		{
			return module.getFullName();
		}
		return null;
	}

	/**
	 * 
	 * @param pageFile
	 * @return
	 * @throws IOException
	 * @throws InterfaceConfigException
	 */
	public static Module findModuleFromPageUrl(URL pageFile) throws IOException, InterfaceConfigException
	{
		String result = null;
		Source source = new Source(pageFile.openStream());
		List<?> elementList = source.findAllElements("script");
		
		for (Object object : elementList)
		{
			Element element = (Element)object;
			
			String src = element.getAttributeValue("src");
			
			if (src != null && src.endsWith(MODULE_IMPORT_SUFFIX))
			{
				if (result != null)
				{
					throw new InterfaceConfigException("Multiple modules in the same html page is not allowed in CRUX.");
				}
				
				int lastSlash = src.lastIndexOf("/");
				
				if(lastSlash >= 0)
				{
					int indexOfModuleSuffix = src.indexOf(MODULE_IMPORT_SUFFIX, lastSlash);
					result = src.substring(lastSlash + 1, indexOfModuleSuffix);
				}
				else
				{
					int indexOfModuleSuffix = src.indexOf(MODULE_IMPORT_SUFFIX);
					result = src.substring(0, indexOfModuleSuffix);
				}
			}
		}
		return Modules.getInstance().getModule(result);
	}
}
