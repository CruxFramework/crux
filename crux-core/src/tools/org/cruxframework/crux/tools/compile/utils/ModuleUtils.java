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
package org.cruxframework.crux.tools.compile.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.module.ModulesScanner;
import org.cruxframework.crux.core.server.scan.ScannerURLS;
import org.cruxframework.crux.scannotation.URLStreamManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleUtils
{
	private static final String MODULE_IMPORT_SUFFIX = ".nocache.js";
	
	public static void initializeScannerURLs(URL[] urls)
	{
		ScannerURLS.setURLsForSearch(urls);
		List<String> classesDir = new ArrayList<String>();
		for (URL url : urls)
		{
			if (!url.toString().endsWith(".jar"))
			{
				classesDir.add(url.toString());
			}
		}
		if (classesDir.size() > 0)
		{
			ModulesScanner.getInstance().setClassesDir(classesDir.toArray(new String[classesDir.size()]));
		}
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
		Document source = null;
		
		URLStreamManager manager = new URLStreamManager(pageFile);
		try
		{
			source = ViewProcessor.getView(manager.open(), null);
			manager.close();
		}
		catch (Exception e)
		{
			throw new InterfaceConfigException("Error parsing screen ["+pageFile.toString()+"].", e);
		}
		
		NodeList elementList = source.getElementsByTagName("script");
		
		int length = elementList.getLength();
		for (int i = 0; i < length; i++)
		{
			Element element = (Element) elementList.item(i);
			
			String src = element.getAttribute("src");
			
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
