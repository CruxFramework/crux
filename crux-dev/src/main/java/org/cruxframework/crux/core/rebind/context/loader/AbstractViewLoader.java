/*
 * Copyright 2015 cruxframework.org.
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
import java.util.List;

import org.cruxframework.crux.core.rebind.GeneratorProperties;
import org.cruxframework.crux.core.rebind.context.RebindContext;

import com.google.gwt.dev.cfg.ModuleDef;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractViewLoader
{
	protected RebindContext context;


	public AbstractViewLoader(RebindContext context)
    {
		this.context = context;
    }
	
	
	protected List<String> getViewBaseFolders()
    {
		ModuleDef currentModule = context.getCurrentModule();
		String name = currentModule.getCanonicalName();
		String moduleBaseFolder = name.substring(0, name.lastIndexOf('.')).replace('.', '/');

		List<String> screenFolders = GeneratorProperties.readConfigurationPropertyValues(context, GeneratorProperties.VIEW_BASE_FOLDER);
		List<String> result = new ArrayList<String>();

		for (String folder : screenFolders)
        {
			folder = folder.replace('\\', '/');
			if (!folder.startsWith("/"))
			{
				folder = "/"+folder;
			}
	        result.add(moduleBaseFolder+folder);
        }
	    
	    return result;
    }

}
