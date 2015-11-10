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
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.template.TemplateLoader;
import org.cruxframework.crux.core.declarativeui.view.ViewException;
import org.cruxframework.crux.core.declarativeui.view.ViewLoader;
import org.cruxframework.crux.core.rebind.context.RebindContext;

import com.google.gwt.dev.resource.Resource;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewRebindLoader extends AbstractViewLoader implements ViewLoader
{
	private boolean initialized = false;
	private TemplateRebindLoader templateContextLoader;
	private Map<String, String> views = new HashMap<String, String>();
	
	public ViewRebindLoader(RebindContext context)
    {
		super(context);
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
			return context.getGeneratorContext().getResourcesOracle().getResource(views.get(id));
		}
		return null;
    }
	
	@Override
    public List<String> getViews()
    {
		initialize();
		List<String> result = new ArrayList<String>();
		result.addAll(views.keySet());
		return result;
    }

	private void initialize()
	{
		if (!initialized)
		{
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
					views.put(fileName.substring(0, fileName.length()-9), pathName);
				}
			}
			initialized = true;
		}
	}
}
