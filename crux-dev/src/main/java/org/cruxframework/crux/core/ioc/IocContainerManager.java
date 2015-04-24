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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.datasource.annotation.DataSource;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.ioc.IoCResource;
import org.cruxframework.crux.core.client.ioc.IoCResource.NoClass;
import org.cruxframework.crux.core.client.ioc.IoCResource.NoProvider;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.rebind.datasource.DataSources;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IocContainerManager
{
	private static final Log logger = LogFactory.getLog(IocContainerManager.class);
	private RebindContext context;
	
	public IocContainerManager(RebindContext context)
    {
		this.context = context;
		initialize();
    }
	
	/**
	 * 
	 * @param view
	 * @return
	 */
	public Map<String, IocConfig<?>> getConfigurationsForView(View view, Device device)
	{
		Map<String, IocConfig<?>> globalConfigurations = IocContainerConfigurations.getConfigurations();
		Map<String, IocConfig<?>> viewConfigurations = new HashMap<String, IocConfig<?>>();
		viewConfigurations.putAll(globalConfigurations);
		
		if (view != null)
		{
			bindImplicityInjectcionsForControllers(view, viewConfigurations, device);
			bindImplicityInjectcionsForDatasources(view, viewConfigurations, device);
		}
		
		return viewConfigurations;
	}

	/**
	 * 
	 * @param type
	 * @param added
	 * @param path
	 */
	private void bindImplicityInjectcions(Class<?> type, Set<String> added, Set<String> path, boolean iocRootType, Map<String, IocConfig<?>> configurations)
    {
        if (isBindable(type, iocRootType))
        {
        	bindImplicityInjectionsForFields(type, added, path, configurations);
        	bindImplicityInjectionsForMethods(type, added, path, configurations);
        	if (type.getSuperclass() != null)
        	{
        		bindImplicityInjectcions(type.getSuperclass(), added, path, iocRootType, configurations);
        	}
        }
    }

	/**
	 * @param viewConfigurations 
	 * @param view 
	 * 
	 */
	private void bindImplicityInjectcionsForControllers(View view, Map<String, IocConfig<?>> viewConfigurations, Device device)
	{
		Iterator<String> controllers = view.iterateControllers();
		while (controllers.hasNext())
		{
			Class<?> controllerClass = context.getControllers().getControllerClass(controllers.next(), device);
			bindImplicityInjectcions(controllerClass, new HashSet<String>(), new HashSet<String>(), true, viewConfigurations);
		}
	}
	
	/**
	 * @param viewConfigurations 
	 * @param view 
	 * 
	 */
	private void bindImplicityInjectcionsForDatasources(View view, Map<String, IocConfig<?>> viewConfigurations, Device device)
	{
		Iterator<String> datasources = view.iterateDataSources();
		while (datasources.hasNext())
		{
			Class<?> controllerClass = DataSources.getDataSourceClass(datasources.next(), device);
			bindImplicityInjectcions(controllerClass, new HashSet<String>(), new HashSet<String>(), true, viewConfigurations);
		}
	}
	
	/**
	 * 
	 * @param type
	 * @param added
	 * @param path
	 * @param configurations
	 */
	private void bindImplicityInjectionsForFields(Class<?> type, Set<String> added, Set<String> path, Map<String, IocConfig<?>> configurations)
    {
	    for (Field field : type.getDeclaredFields()) 
	    {
	    	String fieldName = field.getName();
	    	if (!added.contains(fieldName))
	    	{
	    		added.add(fieldName);
	    		Class<?> fieldType = field.getType();
	    		if (isBindable(fieldType, false))
	    		{
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
	    				bindTypeImplicitly(fieldType, configurations);
	    				bindImplicityInjectcions(fieldType, new HashSet<String>(), fieldPath, false, configurations);
	    			}
	    		}
	    	}
	    }
    }
	
	/**
	 * 
	 * @param type
	 * @param added
	 * @param path
	 * @param configurations
	 */
	private void bindImplicityInjectionsForMethods(Class<?> type, Set<String> added, Set<String> path, Map<String, IocConfig<?>> configurations)
    {
	    for (Method method : type.getDeclaredMethods()) 
	    {
	    	Inject inject = method.getAnnotation(Inject.class);
	    	Class<?>[] parameterTypes = method.getParameterTypes();
			if (inject != null && !Modifier.isAbstract(method.getModifiers()) && parameterTypes != null && parameterTypes.length > 0)
	    	{
		    	if (!added.contains(method.toString()))
		    	{
		    		added.add(method.toString());
		    		for (int i=0; i< parameterTypes.length; i++)
		    		{
		    			Class<?> parameterType = parameterTypes[i];
		    			if (isBindable(parameterType, false))
		    			{
		    				if (path.contains(parameterType.getCanonicalName()))
		    				{
		    					throw new IoCException("IoC Create Looping Error between classes ["+type.getCanonicalName()+"] and ["+parameterType.getCanonicalName()+"].");
		    				}
		    				Set<String> methodPath = new HashSet<String>();
		    				methodPath.addAll(path);
		    				methodPath.add(parameterType.getCanonicalName());
		    				bindTypeImplicitly(parameterType, configurations);
		    				bindImplicityInjectcions(parameterType, new HashSet<String>(), methodPath, false, configurations);
		    			}
		    		}
		    	}
	    	}
	    }
    }

	/**
	 * 
	 * @param <T>
	 * @param clazz
	 * @param configurations
	 */
	private <T> void bindTypeImplicitly(Class<T> clazz, Map<String, IocConfig<?>> configurations)
	{
		String className = clazz.getCanonicalName();
		if (!configurations.containsKey(className))
		{
			IocConfig<T> iocConfig = new IocConfigImpl<T>(clazz);
			configurations.put(className, iocConfig);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
    private void configureAnnotatedClasses() throws ClassNotFoundException
    {
		Map<String, IocConfig<?>> globalConfigurations = IocContainerConfigurations.getConfigurations();
		JClassType[] configurations =  context.getClassScanner().searchClassesByAnnotation(IoCResource.class);
		if (configurations != null)
		{
			for (JClassType resourceClassType : configurations)
			{
				String resourceClassName = resourceClassType.getQualifiedSourceName();
				if (!globalConfigurations.containsKey(resourceClassName))
				{
					IoCResource ioCResource = resourceClassType.getAnnotation(IoCResource.class);
					Class<?> resourceClass = Class.forName(resourceClassName);
					bindTypeImplicitly(resourceClass, globalConfigurations);
					IocConfig<?> iocConfig = globalConfigurations.get(resourceClassName);
					if (!ioCResource.bindClass().equals(NoClass.class))
					{
						iocConfig.toClass((Class) ioCResource.bindClass());
					}
					if (!ioCResource.provider().equals(NoProvider.class))
					{
						iocConfig.toProvider((Class)ioCResource.provider());
					}
					iocConfig.runtimeAccessible(ioCResource.runtimeAccessible());
					iocConfig.scope(ioCResource.scope());
				}
			}
		}
    }

	private void initialize()
	{
		try
		{
			JClassType[] configurations =  context.getClassScanner().searchClassesByInterface(IocConfiguration.class.getCanonicalName());
			if (configurations != null)
			{
				for (JClassType configurationClassType : configurations)
				{
					String configurationClassName = configurationClassType.getQualifiedSourceName();
					Class<?> configurationClass = Class.forName(configurationClassName);
					if (!Modifier.isAbstract(configurationClass.getModifiers())  && IocContainerConfigurations.class.isAssignableFrom(configurationClass))
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

			configureAnnotatedClasses();
		}
		catch (Exception e)
		{
			logger.error("Error initializing ioc container.", e);
		}
	}

	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private static boolean isBindable(Class<?> type, boolean iocRootType)
    {
	    boolean bindable = !ClassUtils.isSimpleType(type);
	    
	    if (bindable && !iocRootType)
	    {
	    	if (type.getAnnotation(Controller.class) != null || type.getAnnotation(DataSource.class) != null)
	    	{
	    		bindable = false;
	    	}
	    }
	    
		return bindable;
    }
}
