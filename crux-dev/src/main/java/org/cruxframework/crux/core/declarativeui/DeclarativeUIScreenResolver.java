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
package org.cruxframework.crux.core.declarativeui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolver;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.core.utils.URLUtils;
import org.cruxframework.crux.scanner.URLStreamManager;
import org.w3c.dom.Document;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeclarativeUIScreenResolver implements ScreenResourceResolver
{
	public Set<String> getAllAppModules()
	{
		return DeclarativeUIScreenResourceScanner.getInstance().getAppModules();
	}
	
	/**
	 * 
	 */
	public Set<String> getAllScreenIDs(String module) throws ScreenConfigException
	{
		return DeclarativeUIScreenResourceScanner.getInstance().getPages(module);
	}

	/**
	 * 
	 */
	public Document getRootView(String relativeScreenId, String moduleId, String device) throws CruxGeneratorException
    {
		try
        {
        	Set<String> screenIDs = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllScreenIDs(moduleId);
        	Module module = Modules.getInstance().getModule(moduleId);
        	
        	if (screenIDs == null || module == null)
        	{
        		throw new ScreenConfigException("Can not find pages for module ["+moduleId+"]");
        	}
        	for (String screenID : screenIDs)
        	{
        		if(Modules.getInstance().getRelativeScreenId(module, screenID).equals(relativeScreenId))
        		{
        			return getRootView(screenID, device);
        		}
        	}
        }
        catch (ScreenConfigException e)
        {
			throw new CruxGeneratorException("Error obtaining screen resource. Screen id: ["+relativeScreenId+"].", e);
        }
        return null;
    }
	
	/**
	 * 
	 */
	public Document getRootView(String screenId, String device) throws CruxGeneratorException
    {
		try
		{
			URL webBaseDir = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDir();
			URL screenURL = null;
			InputStream inputStream = null;
			URLStreamManager manager = null;
			
			screenId = RegexpPatterns.REGEXP_BACKSLASH.matcher(screenId).replaceAll("/").replace(".html", ".crux.xml");

			URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(webBaseDir.getProtocol());
			screenURL = resourceHandler.getChildResource(webBaseDir, screenId);
			manager = new URLStreamManager(screenURL);
			inputStream = manager.open();
			
			if (inputStream == null)
			{
				manager.close(); // the possible underlying jar must be closed despite of the existence of the referred resource
				screenURL = URLUtils.isValidURL(screenId);
				
				if (screenURL == null)
				{
					screenURL = new URL("file:///"+screenId);
				}
				
				manager = new URLStreamManager(screenURL);
				inputStream = manager.open();
				
				if (inputStream == null)
				{
					manager.close();
					
					screenURL = getClass().getResource("/"+screenId);
					if (screenURL != null)
					{
						manager = new URLStreamManager(screenURL);
						inputStream = manager.open();
					}
				}
			}

			if (inputStream == null)
			{
				return null;
			}
			
			Document result = ViewProcessor.getView(inputStream, device);
						
			if(manager != null)
			{
				manager.close();
			}
			
			return result;			
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException("Error obtaining screen resource. Screen id: ["+screenId+"].", e);
		}
	}

	/**
	 * 
	 */
	public InputStream getScreenResource(String screenId) throws CruxGeneratorException
    {
		Document screen = getRootView(screenId, null);
		if (screen == null)
		{
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ViewProcessor.generateHTML(screenId, screen, out);			
		return new ByteArrayInputStream(out.toByteArray());
    }

	public InputStream getScreenResource(String screenId, String module) throws CruxGeneratorException
    {
		Document screen = getRootView(screenId, module, null);
		if (screen == null)
		{
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ViewProcessor.generateHTML(screenId, screen, out);			
		return new ByteArrayInputStream(out.toByteArray());
    }
}
