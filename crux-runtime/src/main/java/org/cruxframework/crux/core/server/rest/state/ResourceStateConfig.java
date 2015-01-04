/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.server.rest.state;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.config.ConfigurationFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ResourceStateConfig
{
	private static final Log logger = LogFactory.getLog(ResourceStateConfig.class);
	private static final Lock handlerLock = new ReentrantLock();
	private static final Lock enabledLock = new ReentrantLock();
	private static ResourceStateHandler resourceStateHandler  = null;
	private static Boolean enabled;

	public static boolean isResourceStateCacheEnabled()
	{
		if (enabled == null)
		{
			enabledLock.lock();
			try
			{
				if (enabled == null)
				{
					enabled = Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableResourceStateCacheForRestServices());
				}
			}
			finally
			{
				enabledLock.unlock();
			}
		}
		return enabled;
	}
	
	public static ResourceStateHandler getResourceStateHandler()
	{
		if (resourceStateHandler != null) return resourceStateHandler;
		
		try
		{
			handlerLock.lock();
			if (resourceStateHandler != null) return resourceStateHandler;
			resourceStateHandler = (ResourceStateHandler) Class.forName(ConfigurationFactory.getConfigurations().restServiceResourceStateHandler()).newInstance(); 
		}
		catch (Exception e)
		{
			logger.error("Error initializing resourceStateHandler.", e);
		}
		finally
		{
			handlerLock.unlock();
		}
		return resourceStateHandler;
	}
}
