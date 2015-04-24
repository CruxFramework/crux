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
package org.cruxframework.crux.core.rebind.provider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.template.TemplateProvider;
import org.cruxframework.crux.core.declarativeui.view.ViewException;
import org.cruxframework.crux.core.declarativeui.view.ViewProvider;

import com.google.gwt.core.ext.GeneratorContext;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewContextProvider implements ViewProvider
{
	private GeneratorContext context;
	private TemplateContextProvider templateContextProvider;
	private Map<String, String> views = new HashMap<String, String>();
	
	public ViewContextProvider(GeneratorContext context)
    {
		this.context = context;
		templateContextProvider = new TemplateContextProvider(context);
		buildViewsMap();
    }	
	
	@Override
    public TemplateProvider getTemplateProvider()
    {
		return templateContextProvider;
    }
	
	@Override
    public InputStream getView(String id) throws ViewException
    {
		if (views.containsKey(id))
		{
			return context.getResourcesOracle().getResourceAsStream(views.get(id));
		}
		return null;
    }

	@Override
    public List<String> getViews(String viewsLocator, String moduleId)
    {
		List<String> result = new ArrayList<String>();
		if (viewsLocator.equals("*"))
		{
			result.addAll(views.keySet());
		}
		else if (isViewName(viewsLocator))
		{
			result.add(viewsLocator);
		}
		else if (isModuleLocator(viewsLocator))
		{
			extractModuleViews(viewsLocator, result);
		}
			
		return result;
    }

	@Override
    public boolean isValidViewLocator(String viewsLocator)
    {
	    return !isViewName(viewsLocator) || !views.containsKey(viewsLocator);
    }

	private void buildViewsMap()
	{
		Set<String> pathNames = context.getResourcesOracle().getPathNames();

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
				views.put(fileName.substring(0, fileName.length()-9), pathName);
			}
		}
	}

	private void extractModuleViews(String viewsLocator, List<String> result)
    {
	    viewsLocator = viewsLocator.substring(1);
	    int index = viewsLocator.indexOf("/");
	    String moduleId = viewsLocator.substring(0, index);
	   
	    for (String viewPath : views.values())
        {
	        if (viewPath.startsWith(moduleId+"/"))
	        {
	        	result.add(viewPath);
	        }
        }
    }
	
	private boolean isModuleLocator(String viewsLocator)
	{
		return (viewsLocator != null && viewsLocator.startsWith("/") && viewsLocator.lastIndexOf("/") > 1);
	}

	private boolean isViewName(String viewsLocator)
	{
		return (viewsLocator != null && viewsLocator.matches("[\\w\\.]*"));
	}
}
