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
package org.cruxframework.crux.core.rebind.context;

import org.cruxframework.crux.core.declarativeui.screen.ScreenLoader;
import org.cruxframework.crux.core.rebind.JClassScanner;
import org.cruxframework.crux.core.rebind.context.loader.ScreenRebindLoader;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RebindContext
{
	private GeneratorContext generatorContext;
	private ControllerScanner controllersManager;
	private ResourceScanner resourcesManager;
	private TreeLogger logger;
	private JClassScanner jClassScanner;
	private ScreenLoader screenLoader;
	
	public RebindContext(GeneratorContext generatorContext, TreeLogger logger)
    {
		this.generatorContext = generatorContext;
		this.logger = logger;
    }

	public ControllerScanner getControllers()
	{
		if (controllersManager == null)
		{
			controllersManager = new ControllerScanner(generatorContext);
		}
		return controllersManager;
	}
	
	public ResourceScanner getResources()
	{
		if (resourcesManager == null)
		{
			resourcesManager = new ResourceScanner(generatorContext);
		}
		return resourcesManager;
	}
	
	public GeneratorContext getGeneratorContext()
	{
		return generatorContext;
	}

	public JClassScanner getClassScanner()
	{
		if (jClassScanner == null)
		{
			jClassScanner = new JClassScanner(generatorContext);
		}
		return jClassScanner;
		
	}
	
	public ScreenLoader getScreenLoader()
	{
		if (screenLoader == null)
		{
			screenLoader = new ScreenRebindLoader(generatorContext);
		}
		return screenLoader;
	}

	public TreeLogger getLogger()
	{
		return logger;
	}
}
