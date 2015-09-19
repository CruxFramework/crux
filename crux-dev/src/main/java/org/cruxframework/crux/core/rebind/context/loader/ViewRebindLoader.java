/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.context.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.template.TemplateLoader;
import org.cruxframework.crux.core.declarativeui.view.ViewException;
import org.cruxframework.crux.core.declarativeui.view.ViewLoader;
import org.cruxframework.crux.core.rebind.GeneratorProperties;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.utils.FilePatternHandler;

import com.google.gwt.dev.resource.Resource;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewRebindLoader implements ViewLoader
{
	private RebindContext context;
	private boolean initialized = false;
	private TemplateRebindLoader templateContextLoader;
	private Map<String, ViewRef> views = new HashMap<String, ViewRef>();
	
	public ViewRebindLoader(RebindContext context)
    {
		this.context = context;
		templateContextLoader = new TemplateRebindLoader(context);
    }
	
	@Override
    public TemplateLoader getTemplateLoader()
    {
		return templateContextLoader;
    }	
	
	@Override
    public Resource getView(String id) throws ViewException
    {
		initialize();
		if (views.containsKey(id))
		{
			return context.getGeneratorContext().getResourcesOracle().getResource(views.get(id).fullPath);
		}
		return null;
    }
	
	@Override
    public List<String> getViews(String viewsLocator)
    {
		initialize();
		List<String> result = new ArrayList<String>();
		if (viewsLocator.equals("*"))
		{
			result.addAll(views.keySet());
		}
		else if (isViewName(viewsLocator))
		{
			result.add(viewsLocator);
		}
		else 
		{
			finsViews(viewsLocator, result);
		}
			
		return result;
    }

	@Override
    public boolean isValidViewLocator(String viewsLocator)
    {
		initialize();
	    return !isViewName(viewsLocator) || !views.containsKey(viewsLocator);
    }

	private void finsViews(String viewsLocator, List<String> result)
    {
		FilePatternHandler filePatternHandler = new FilePatternHandler(viewsLocator, null);
		
		for (Entry<String, ViewRef> entry : views.entrySet())
        {
	        if (filePatternHandler.isValidEntry(entry.getValue().relativePath))
	        {
	        	result.add(entry.getKey());
	        }
        }
    }

	private void initialize()
	{
		if (!initialized)
		{
			List<String> screenFolders = GeneratorProperties.readConfigurationPropertyValues(context, GeneratorProperties.VIEW_BASE_FOLDER);
			Set<String> pathNames = context.getGeneratorContext().getResourcesOracle().getPathNames();
			
			for (String pathName : pathNames)
			{
				int index = pathName.lastIndexOf('/');
				String fileName;
				if (index > 0)
				{
					fileName = pathName.substring(index+1);
				}
				else
				{
					fileName = pathName;
				}
				
				if (fileName.endsWith(".view.xml"))
				{
					for (String baseFolder : screenFolders)
                    {
						if (pathName.startsWith(baseFolder))
						{
							String relativePathName = pathName.substring(baseFolder.length());
							if (relativePathName.startsWith("/"))
							{
								relativePathName = relativePathName.substring(1);
							}
							relativePathName = relativePathName.substring(0,relativePathName.length()-9);
							views.put(fileName.substring(0, fileName.length()-9), new ViewRef(relativePathName, pathName));
						}
                    }
				}
			}
			initialized = true;
		}
	}

	private boolean isViewName(String viewsLocator)
	{
		return (viewsLocator != null && viewsLocator.matches("[\\w\\.]*"));
	}

	private static class ViewRef
	{
		private String fullPath;
		private String relativePath;
		private ViewRef(String relativePath, String fullPath)
        {
			this.relativePath = relativePath;
			this.fullPath = fullPath;
        }
	}
}
