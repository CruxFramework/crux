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
package org.cruxframework.crux.core.rebind.screen;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenResourceResolverInitializer 
{
	private static final Log logger = LogFactory.getLog(ScreenResourceResolverInitializer.class);
	private static ScreenResourceResolver screenResourceResolver;
	private static final Lock lock = new ReentrantLock();

	/**
	 * 
	 * @return
	 */
	public static ScreenResourceResolver getScreenResourceResolver()
	{
		if (screenResourceResolver != null) return screenResourceResolver;
		
		try
		{
			lock.lock();
			if (screenResourceResolver != null) return screenResourceResolver;
			screenResourceResolver = (ScreenResourceResolver) ScreenResourceResolverScanner.getScreenResolver().newInstance(); 
		}
		catch (Throwable e)
		{
			logger.error("Error initializing ScreenResolver.", e);
		}
		finally
		{
			lock.unlock();
		}
		return screenResourceResolver;
	}
}
