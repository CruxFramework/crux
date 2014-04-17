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
package org.cruxframework.crux.core.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.i18n.MessageException;


/**
 * Abstract Factory for messages based on property files . Receive an Interface and use it's name to look for 
 * resource bundles in the classpath. Each interface's method is used as key 
 * in that property file.
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 *
 */
public abstract class AbstractPropertiesFactory
{
	private Map<String, Object> cachedProxies = new HashMap<String, Object>();
	private final Lock initLock = new ReentrantLock(true);
			
	/**
	 * Create a message helper class that access a resource bundle using the methods present in the given interface.
	 * @param targetInterface
	 * @return
	 * @throws MessageException
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getConstantsFromProperties(final Class<T> targetInterface) throws MessageException
	{
		T proxy = (T)cachedProxies.get(targetInterface.getCanonicalName());
		if (proxy != null)
		{
			return proxy;
		}
		try
		{
			initLock.lock();
			return initProxy(targetInterface);
		}
		finally
		{
			initLock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T initProxy (final Class<T> targetInterface)
	{
		T proxy = (T)cachedProxies.get(targetInterface.getCanonicalName());
		if (proxy == null)
		{

			Class<?> proxyClass = Proxy.getProxyClass(targetInterface.getClassLoader(), new Class<?>[] {targetInterface});

			try
			{
				InvocationHandler invocationHandler = getInvocationHandler(targetInterface);
				proxy = (T) proxyClass.getConstructor(new Class<?>[] { InvocationHandler.class }).newInstance(new Object[] { invocationHandler });
				cachedProxies.put(targetInterface.getCanonicalName(), proxy);
				return proxy;
			}
			catch (Exception e) 
			{
				throw new MessageException(e.getMessage(), e);
			}
		}
		return proxy;
	}
	
	protected abstract ConstantsInvocationHandler getInvocationHandler(Class<?> targetInterface);
}