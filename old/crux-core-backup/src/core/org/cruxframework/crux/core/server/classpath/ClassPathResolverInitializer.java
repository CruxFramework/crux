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
package org.cruxframework.crux.core.server.classpath;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.config.ConfigurationFactory;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ClassPathResolverInitializer 
{
	private static final Log logger = LogFactory.getLog(ClassPathResolverInitializer.class);
	private static ClassPathResolver classPathResolver;
	private static final Lock lock = new ReentrantLock();

	public static ClassPathResolver getClassPathResolver()
	{
		if (classPathResolver != null) return classPathResolver;
		
		try
		{
			lock.lock();
			if (classPathResolver != null) return classPathResolver;

			classPathResolver = (ClassPathResolver) Class.forName(ConfigurationFactory.getConfigurations().classPathResolver()).newInstance();
		}
		catch (Throwable e)
		{
			throw new ClassPathResolverException("Error initializing classPathResolver.", e);
		}
		finally
		{
			lock.unlock();
		}
		logger.info("Using classPathResolver: ["+classPathResolver.getClass().getName()+"]");
		return classPathResolver;
	}

	public static void registerClassPathResolver(ClassPathResolver classPathResolver)
	{
		ClassPathResolverInitializer.classPathResolver = classPathResolver;
	}
	
	

}
