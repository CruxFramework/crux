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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.config.ConfigurationFactory;


/**
 * Initializes the LocaleResolver class
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 */
public class LocaleResolverInitializer 
{
	protected static Class<? extends LocaleResolver> localeResolverClass;
	protected static ThreadLocal<LocaleResolver> localeResolver = new ThreadLocal<LocaleResolver>();
	private static final Lock lock = new ReentrantLock();
	private static final Log logger = LogFactory.getLog(LocaleResolverInitializer.class);

	/**
	 * 
	 * @return
	 */
	public static LocaleResolver getLocaleResolver()
	{
		return localeResolver.get();
	}

	/**
	 * 
	 */
	public static void createLocaleResolverThreadData()
	{
		if (localeResolverClass == null)
		{
			initLocaleResolverClass();
		}
		try
		{
			localeResolver.set(localeResolverClass.newInstance());
		}
		catch (Exception e)
		{
			logger.error("Error initializing LocaleResolver.", e);
		}
	}
	
	/**
	 * 
	 */
	public static void clearLocaleResolverThreadData()
	{
		localeResolver.remove();
	}
	
	/**
	 * 
	 * @param localeResolverClass
	 */
	public static void registerLocaleResolverClass(Class<? extends LocaleResolver> localeResolverClass)
	{
		LocaleResolverInitializer.localeResolverClass = localeResolverClass;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static void initLocaleResolverClass()
	{
		if (localeResolverClass != null) return;
		
		lock.lock();
		try
		{
			if (localeResolverClass != null) return;
			localeResolverClass = (Class<? extends LocaleResolver>) Class.forName(ConfigurationFactory.getConfigurations().localeResolver()); 
		}
		catch (Exception e) 
		{
			logger.error("Error initializing LocaleResolver.", e);
			throw new LocaleResolverException(e.getMessage(), e);
		}
		finally
		{
			lock.unlock();
		}
	}
}
