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

import java.io.IOException;
import java.io.InputStream;
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
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @author Gessé S. F. Dafé <code>gessedafe@gmail.com</code>
 *
 */
public class MessagesFactory
{
	private static Map<Class<?>, Object> cachedProxies = new HashMap<Class<?>, Object>();
	private static final Lock initLock = new ReentrantLock(true);
	
	/**
	 * Create a message helper class that access a resource bundle using the methods present in the given interface.
	 * @param targetInterface
	 * @return
	 * @throws MessageException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getMessages(final Class<T> targetInterface) throws MessageException
	{
		T proxy = (T)cachedProxies.get(targetInterface);
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
	private static <T> T initProxy (final Class<T> targetInterface)
	{
		T proxy = (T)cachedProxies.get(targetInterface);
		if (proxy == null)
		{

			Class<?> proxyClass = Proxy.getProxyClass(targetInterface.getClassLoader(), new Class<?>[] {targetInterface});

			try
			{
				InvocationHandler invocationHandler = new MessagesInvocationHandler(targetInterface);
				proxy = (T) proxyClass.getConstructor(new Class<?>[] { InvocationHandler.class }).newInstance(new Object[] { invocationHandler });
				cachedProxies.put(targetInterface, proxy);
				return proxy;
			}
			catch (Exception e) 
			{
				throw new MessageException(e.getMessage(), e);
			}
		}
		return proxy;
	}	
}

/**
 * Dynamic proxy for message resources.
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @author Gessé S. F. Dafé <code>gessedafe@gmail.com</code>
 */
class MessagesInvocationHandler implements InvocationHandler
{
	Map<Locale, PropertyResourceBundle> localeMessages = new HashMap<Locale, PropertyResourceBundle>();
	private Class<?> targetInterface;
	private static final Lock propertiesLock = new ReentrantLock(true);	
	
	public MessagesInvocationHandler(Class<?> targetInterface) 
	{
		this.targetInterface = targetInterface;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		try
		{
			PropertyResourceBundle properties = getPropertiesForLocale(targetInterface);
			if (properties != null && properties.containsKey(method.getName()))
			{
				return MessageFormat.format(properties.getString(method.getName()),args);
			}
			else
			{
				DefaultMessage annot = method.getAnnotation(DefaultMessage.class);
				if (annot != null)
				{
					return MessageFormat.format(annot.value(),args);
				}
				return null;
			}
		}
		catch (Throwable e)
		{
			return null;
		}
	}
	
	private <T> PropertyResourceBundle getPropertiesForLocale(final Class<T> targetInterface) 
	{
		Locale userLocale = LocaleResolverInitialiser.getLocaleResolver().getUserLocale();
		PropertyResourceBundle properties = null;
		if (!localeMessages.containsKey(userLocale))
		{
			propertiesLock.lock();
			try
			{
				properties = loadProperties(targetInterface, userLocale);
				localeMessages.put(userLocale, properties);
			}
			finally
			{
				propertiesLock.unlock();	
			}
		}
		else
		{
			properties = localeMessages.get(userLocale);
		}
		return properties;
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
				String resourceName = "/"+targetInterface.getName().replaceAll("\\.", "/") + ".properties";
				InputStream input = targetInterface.getClassLoader().getResourceAsStream(resourceName);
				if (input != null)
				{
					properties = new PropertyResourceBundle(input);
				}
			} 
			catch (IOException e1) 
			{
				throw new MessageException(e.getMessage(), e);
			}
		}
		return properties;
	}
}