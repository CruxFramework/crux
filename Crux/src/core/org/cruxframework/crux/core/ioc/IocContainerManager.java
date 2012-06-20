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
package org.cruxframework.crux.core.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.datasource.DataSources;
import org.cruxframework.crux.core.server.scan.ClassScanner;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IocContainerManager
{
	private static final Log logger = LogFactory.getLog(IocContainerManager.class);

	private static boolean initialized = false;
	
	public synchronized static void initialize()
	{
		if (!initialized)
		{
			initialized = true;
			try
			{
				Set<String> configurations =  ClassScanner.searchClassesByInterface(IocConfiguration.class);
				if (configurations != null)
				{
					for (String configurationClassName : configurations)
					{
						Class<?> configurationClass = Class.forName(configurationClassName);
						if (!Modifier.isAbstract(configurationClass.getModifiers()) && IocContainerConfigurations.class.isAssignableFrom(configurationClass))
						{
							IocContainerConfigurations configuration = (IocContainerConfigurations)configurationClass.newInstance();
							if (configuration.isEnabled())
							{
								if (logger.isInfoEnabled())
								{
									logger.info("Configuring new ioc module ["+configurationClassName+"]...");
								}
								configuration.configure();
							}
						}
					}
				}
				bindImplicityInjectcionsForControllers();
				bindImplicityInjectcionsForDatasources();
			}
			catch (Exception e)
			{
				logger.error("Error initializing ioc container.", e);
			}
		}
	}

	/**
	 * 
	 * @param className
	 * @return
	 */
	public static IocConfigImpl<?> getConfigurationForType(String className)
	{
		if (!initialized)
		{
			initialize();
		}
		
		return (IocConfigImpl<?>) IocContainerConfigurations.getConfigurationForType(className);
	}
	
	/**
	 * 
	 * @return
	 */
	public static Iterator<String> iterateClasses()
	{
		if (!initialized)
		{
			initialize();
		}
		return IocContainerConfigurations.iterateClasses();
	}

	/**
	 * 
	 */
	private static void bindImplicityInjectcionsForControllers()
	{
		Iterator<String> controllers = ClientControllers.iterateControllers();
		while (controllers.hasNext())
		{
			Class<?> controllerClass = ClientControllers.getControllerClass(controllers.next());
			bindImplicityInjectcions(controllerClass, new HashSet<String>(), new HashSet<String>());
		}
	}
	
	/**
	 * 
	 */
	private static void bindImplicityInjectcionsForDatasources()
	{
		Iterator<String> datasources = DataSources.iterateDataSources();
		while (datasources.hasNext())
		{
			Class<?> controllerClass = DataSources.getDataSourceClass(datasources.next());
			bindImplicityInjectcions(controllerClass, new HashSet<String>(), new HashSet<String>());
		}
	}
	
	/**
	 * 
	 * @param type
	 * @param added
	 * @param path
	 */
	private static void bindImplicityInjectcions(Class<?> type, Set<String> added, Set<String> path)
    {
        for (Field field : type.getDeclaredFields()) 
        {
        	String fieldName = field.getName();
			if (!added.contains(fieldName))
        	{
				added.add(fieldName);
				Class<?> fieldType = field.getType();
				Inject inject = field.getAnnotation(Inject.class);
				if (inject != null)
				{
					if (path.contains(fieldType.getCanonicalName()))
					{
						throw new IoCException("IoC Create Looping Error between classes ["+type.getCanonicalName()+"] and ["+fieldType.getCanonicalName()+"].");
					}
		        	Set<String> fieldPath = new HashSet<String>();
		        	fieldPath.addAll(path);
		        	fieldPath.add(fieldType.getCanonicalName());
		        	IocContainerConfigurations.bindTypeImplicitly(fieldType);
					bindImplicityInjectcions(fieldType, added, fieldPath);
				}
        	}
        }
        if (type.getSuperclass() != null)
        {
        	bindImplicityInjectcions(type.getSuperclass(), added, path);
        }
    }
}
