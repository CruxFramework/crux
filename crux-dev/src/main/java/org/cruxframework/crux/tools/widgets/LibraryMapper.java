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
package org.cruxframework.crux.tools.widgets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfigException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.scanner.ClassScanner;
import org.cruxframework.crux.tools.AbstractMapper;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;
import org.cruxframework.crux.tools.servicemap.ServiceMapperException;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class LibraryMapper extends AbstractMapper
{
	private static final Log logger = LogFactory.getLog(LibraryMapper.class);

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		LibraryMapper libraryMapper = new LibraryMapper();
		ConsoleParametersProcessor parametersProcessor = libraryMapper.createParametersProcessor("libraryMapper");
		Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);
		if (parameters.containsKey("-help") || parameters.containsKey("-h"))
		{
			parametersProcessor.showsUsageScreen();
		}
		else
		{
			libraryMapper.processParameters(parameters.values());
			libraryMapper.generateLibraryMap();
		}
	}

	public void generateLibraryMap()
    {
		try
		{
			File metaInfFile = getMetaInfFile();
			File factoryMapFile = new File(metaInfFile, "crux-widgets-factory");
			File widgetsMapFile = new File(metaInfFile, "crux-widgets-type");
			if (factoryMapFile.exists() && widgetsMapFile.exists() && !isOverride())
			{
				logger.info("Widget factories map already exists. Skipping generation...");
				return;
			}
			initializeScannerURLs();
			Set<String> factoriesNames =  ClassScanner.searchClassesByAnnotation(DeclarativeFactory.class);
			Properties widgetFactories = new Properties();
			Properties widgetTypes = new Properties();
			
			if (factoriesNames != null)
			{
				for (String name : factoriesNames) 
				{
					try 
					{
						@SuppressWarnings("unchecked")
                        Class<? extends WidgetCreator<?>> factoryClass = (Class<? extends WidgetCreator<?>>)Class.forName(name);
						DeclarativeFactory annot = factoryClass.getAnnotation(DeclarativeFactory.class);
						String widgetType = annot.library() + "_" + annot.id();
						
						widgetFactories.put(widgetType, factoryClass.getCanonicalName());
						widgetTypes.put(annot.targetWidget().getCanonicalName(), widgetType);
					} 
					catch (ClassNotFoundException e) 
					{
						throw new WidgetConfigException("Error initializing widgets.",e);
					}
				}
			}

			widgetFactories.store(new FileOutputStream(factoryMapFile), "Widget Factories mapping");
			widgetTypes.store(new FileOutputStream(widgetsMapFile), "Widget Types mapping");
		}
		catch (IOException e)
		{
			throw new ServiceMapperException("Error creating widget factories map", e);
		}
	}
}
