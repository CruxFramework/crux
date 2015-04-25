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

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.declarativeui.screen.ScreenLoader;
import org.cruxframework.crux.core.rebind.JClassScanner;
import org.cruxframework.crux.core.rebind.context.loader.ScreenRebindLoader;
import org.cruxframework.crux.core.rebind.context.scanner.ControllerScanner;
import org.cruxframework.crux.core.rebind.context.scanner.ConverterScanner;
import org.cruxframework.crux.core.rebind.context.scanner.DataObjectScanner;
import org.cruxframework.crux.core.rebind.context.scanner.DataSourceScanner;
import org.cruxframework.crux.core.rebind.context.scanner.ResourceScanner;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RebindContext
{
	private ControllerScanner controllersScanner;
	private ConverterScanner converterScanner;
	private DataObjectScanner dataObjectScanner;
	private DataSourceScanner dataSourceScanner;
	private GeneratorContext generatorContext;
	private JClassScanner jClassScanner;
	private TreeLogger logger;
	private ResourceScanner resourcesScanner;
	private ScreenLoader screenLoader;
	
	public RebindContext(GeneratorContext generatorContext, TreeLogger logger)
    {
		this.generatorContext = generatorContext;
		this.logger = logger;
    }

	public JClassScanner getClassScanner()
	{
		if (jClassScanner == null)
		{
			jClassScanner = new JClassScanner(generatorContext);
		}
		return jClassScanner;
		
	}
	
	public ControllerScanner getControllers()
	{
		if (controllersScanner == null)
		{
			controllersScanner = new ControllerScanner(generatorContext);
		}
		return controllersScanner;
	}
	
	public ConverterScanner getConverters()
	{
		if (converterScanner == null)
		{
			converterScanner = new ConverterScanner(generatorContext);
		}
		return converterScanner;
	}
	
	@Deprecated
	@Legacy
	public DataSourceScanner getDataSources()
	{
		if (dataSourceScanner == null)
		{
			dataSourceScanner = new DataSourceScanner(generatorContext);
		}
		return dataSourceScanner;
	}

	public DataObjectScanner getDataObjects()
	{
		if (dataObjectScanner == null)
		{
			dataObjectScanner = new DataObjectScanner(generatorContext);
		}
		return dataObjectScanner;
	}
	
	public GeneratorContext getGeneratorContext()
	{
		return generatorContext;
	}

	public TreeLogger getLogger()
	{
		return logger;
	}
	
	public ResourceScanner getResources()
	{
		if (resourcesScanner == null)
		{
			resourcesScanner = new ResourceScanner(generatorContext);
		}
		return resourcesScanner;
	}

	public ScreenLoader getScreenLoader()
	{
		if (screenLoader == null)
		{
			screenLoader = new ScreenRebindLoader(generatorContext);
		}
		return screenLoader;
	}
}
