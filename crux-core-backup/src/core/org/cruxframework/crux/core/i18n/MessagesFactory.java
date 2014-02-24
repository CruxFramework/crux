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
package org.cruxframework.crux.core.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.config.AbstractPropertiesFactory;
import org.cruxframework.crux.core.config.ConstantsInvocationHandler;


/**
 * Factory for messages. Receive an Interface and use it's name to look for 
 * resource bundles in the classpath. Each interface's method is used as key 
 * in that property file.
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 *
 */
public class MessagesFactory extends AbstractPropertiesFactory
{
	protected static final MessagesFactory instance = new MessagesFactory();
	
	private MessagesFactory() 
	{
	}
	
	/**
	 * Create a message helper class that access a resource bundle using the methods present in the given interface.
	 * @param targetInterface
	 * @return
	 * @throws MessageException
	 */
	public static <T> T getMessages(final Class<T> targetInterface) throws MessageException
	{
		return instance.getConstantsFromProperties(targetInterface);
	}

	@Override
	protected ConstantsInvocationHandler getInvocationHandler(Class<?> targetInterface) 
	{
		return new MessagesInvocationHandler(targetInterface);
	}

}

/**
 * Dynamic proxy for message resources.
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 */
class MessagesInvocationHandler extends ConstantsInvocationHandler
{
	Map<Locale, PropertyResourceBundle> localeMessages = new HashMap<Locale, PropertyResourceBundle>();
	private static final Lock propertiesLock = new ReentrantLock(true);	
	
	public MessagesInvocationHandler(Class<?> targetInterface) 
	{
		super(targetInterface, false);
	}
	
	@Override
	protected <T> PropertyResourceBundle getPropertiesForLocale(final Class<T> targetInterface) 
	{
		Locale userLocale = LocaleResolverInitializer.getLocaleResolver().getUserLocale();
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
}