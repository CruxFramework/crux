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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.screen.ScreenException;
import org.cruxframework.crux.core.declarativeui.screen.ScreenLoader;
import org.cruxframework.crux.core.declarativeui.view.ViewLoader;
import org.cruxframework.crux.core.rebind.context.RebindContext;

import com.google.gwt.dev.resource.Resource;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenRebindLoader extends AbstractViewLoader implements ScreenLoader
{
	private Map<String, String> screens = new HashMap<String, String>();
	private ViewRebindLoader viewContextLoader;
	private boolean initialized = false;

	public ScreenRebindLoader(RebindContext context)
    {
		super(context);
		viewContextLoader = new ViewRebindLoader(context);
    }

	@Override
    public Resource getScreen(String id) throws ScreenException
    {
		initialize();
		if (screens.containsKey(id))
		{
			return context.getGeneratorContext().getResourcesOracle().getResource(screens.get(id));
		}
		return null;
    }
	
	@Override
    public Set<String> getScreens()
    {
		initialize();
		Set<String> result = new HashSet<String>();
       	result.addAll(screens.keySet());
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
			List<String> screenFolders = getViewBaseFolders();
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

				if (fileName.endsWith(".crux.xml"))
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
							screens.put(relativePathName.substring(0, relativePathName.length()-9), pathName);
							break;
						}
                    }
				}
			}
			initialized = true;
		}
	}
}
