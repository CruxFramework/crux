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

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.screen.ScreenException;
import org.cruxframework.crux.core.declarativeui.screen.ScreenLoader;
import org.cruxframework.crux.core.declarativeui.view.ViewLoader;

import com.google.gwt.core.ext.GeneratorContext;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenRebindLoader implements ScreenLoader
{
	private GeneratorContext context;
	private Map<String, String> screens = new HashMap<String, String>();
	private ViewRebindLoader viewContextLoader;
	private boolean initialized = false;

	public ScreenRebindLoader(GeneratorContext context)
    {
		this.context = context;
		viewContextLoader = new ViewRebindLoader(context);
    }

	@Override
    public InputStream getScreen(String id) throws ScreenException
    {
		initialize();
		if (screens.containsKey(id))
		{
			return context.getResourcesOracle().getResourceAsStream(screens.get(id));
		}
		return null;
    }
	
	@Override
    public Set<String> getScreens(String module)
    {
		initialize();
		Set<String> result = new HashSet<String>();
	    for (String viewPath : screens.values())
        {
	        if (viewPath.startsWith(module+"/"))
	        {
	        	result.add(viewPath);
	        }
        }
	    return result;
    }

	@Override
    public ViewLoader getViewLoader()
    {
	    return viewContextLoader;
    }

	private void initialize()
	{
		if (!initialized)
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

				if (fileName.endsWith(".crux.xml"))
				{
					screens.put(fileName.substring(0, fileName.length()-9), pathName);
				}
			}
			initialized = true;
		}
	}
}
