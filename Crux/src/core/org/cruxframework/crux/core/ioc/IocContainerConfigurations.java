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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cruxframework.crux.core.client.ioc.Inject;

/**
 * Base class for an IoC configuration class. Crux engine search for all subclasses of IocContainerConfig and 
 * invoke their configure method. 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class IocContainerConfigurations implements IocConfiguration
{
	private static Map<String, IocConfig<?>> configurations = new HashMap<String, IocConfig<?>>();
	
	/**
	 * Call this method on your configure method to create a configuration for some Type. Eg: 
	 * <p>
	 * You can write something like:
	 * <p>
	 * <pre>
	 * bindType(List.class).toClass(ArrayList.class);
	 * </pre>
	 * <p>
	 * That would cause Crux to inject a new instance of ArrayList whenever you declare 
	 * a field of type List annotated with {@link Inject}
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	protected <T> IocConfig<T> bindType(Class<T> clazz)
	{
		IocConfig<T> iocConfig = new IocConfigImpl<T>(clazz);
		String className = clazz.getCanonicalName();
		if (configurations.containsKey(className))
		{
			throw new IoCException("Invalid Ioc configuration. Class "+className+" is already bound to the container.");//TODO message.
		}
		configurations.put(className, iocConfig);
		return iocConfig;
	}

	/**
	 * Override this method if you need to specify some conditions that enables your configuration.
	 * Eg: You can enable a configuration for all client classes only when running on 
	 * development environment.
	 * @return
	 */
	protected boolean isEnabled()
	{
		return true;
	}
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	static IocConfig<?> getConfigurationForType(String className)
	{
		IocConfig<?> iocConfig = configurations.get(className);
		return iocConfig;
	}
	
	/**
	 * 
	 * @return
	 */
	static Iterator<String> iterateClasses()
	{
		return configurations.keySet().iterator();
	}
	
	/**
	 * 
	 * @param <T>
	 * @param clazz
	 */
	static <T> void bindTypeImplicitly(Class<T> clazz)
	{
		String className = clazz.getCanonicalName();
		if (!configurations.containsKey(className))
		{
			IocConfig<T> iocConfig = new IocConfigImpl<T>(clazz);
			configurations.put(className, iocConfig);
		}
	}
}