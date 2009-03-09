/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.i18n;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Factory for messages. Receive an Interface and use it's name to look for 
 * resource bundles in the classpath. Each interface's method is used as key 
 * in that property file.
 * @author Thiago
 *
 */
public class MessagesFactory
{
	private static Map<Locale,Map<Class<?>, Object>> cachedProxies = new HashMap<Locale,Map<Class<?>, Object>>();
	private static final Lock lock = new ReentrantLock(true);
	
	/**
	 * Create a message helper class that access a resource bundle using the methods present in the given interface.
	 * @param targetInterface
	 * @return
	 * @throws MessageException
	 */
	public static Object getMessages(final Class<?> targetInterface) throws MessageException
	{
		return getMessages(targetInterface, Locale.getDefault());
	}
	
	/**
	 * Create a message helper class that access a resource bundle using the methods present in the given interface.
	 * @param targetInterface
	 * @param locale
	 * @return
	 * @throws MessageException
	 */
	public static Object getMessages(final Class<?> targetInterface, final Locale locale)
	{
		Object proxy = cachedProxies.get(targetInterface);
		if (proxy != null)
		{
			return proxy;
		}
		try
		{
			lock.lock();
			return initProxy(targetInterface, locale);
		}
		finally
		{
			lock.unlock();
		}
	}

	private static Object initProxy (Class<?> targetInterface, final Locale locale)
	{
		Map<Class<?>, Object> localeProxies = cachedProxies.get(locale);
		if (localeProxies == null)
		{
			localeProxies = new HashMap<Class<?>, Object>();
			cachedProxies.put(locale, localeProxies);
		}
		Object proxy = localeProxies.get(targetInterface);
		if (proxy == null)
		{
			proxy = initProxy(localeProxies, targetInterface, locale);;
		}
		return proxy;
	}
	
	private static Object initProxy (Map<Class<?>, Object> cachedProxies, Class<?> targetInterface, final Locale locale)
	{
		Object proxy = cachedProxies.get(targetInterface);
		if (proxy != null)
		{
			return proxy;
		}
		final PropertyResourceBundle properties = loadProperties(targetInterface, locale);
		if (properties == null)
		{
			throw new NullPointerException("resource bundle not found");
		}
		InvocationHandler invocationHandler = new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				try
				{
					return MessageFormat.format(properties.getString(method.getName()),args);
				}
				catch (Throwable e)
				{
					return null;
				}
			}
		};
		Class<?> proxyClass = Proxy.getProxyClass(targetInterface.getClassLoader(), new Class<?>[] {targetInterface});

		try
		{
			proxy = proxyClass.getConstructor(new Class<?>[] { InvocationHandler.class }).newInstance(new Object[] { invocationHandler });
			cachedProxies.put(targetInterface, proxy);
			return proxy;
		}
		catch (Exception e) 
		{
			throw new MessageException(e.getMessage(), e);
		}
	}	
	private static PropertyResourceBundle loadProperties (Class<?> targetInterface, final Locale locale)
	{
		PropertyResourceBundle properties = null;
		try
		{
			properties = (PropertyResourceBundle) PropertyResourceBundle.getBundle(targetInterface.getSimpleName(), locale);
		}
		catch (Throwable e) 
		{
			try 
			{
				String resourceName = targetInterface.getName().replaceAll("\\.", "/") + ".properties";
				properties = new PropertyResourceBundle(targetInterface.getClassLoader().getResourceAsStream(resourceName));
			} 
			catch (Throwable e1) 
			{
				throw new MessageException(e.getMessage(), e);
			}
		}
		return properties;
	}
	
}
