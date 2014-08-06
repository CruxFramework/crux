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
package org.cruxframework.crux.core.server;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;



/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class Environment
{
	private static final String CRUX_DEV_PROPERTY = "Crux.dev";
	private static Boolean isProduction = null;
	private static final Lock lock = new ReentrantLock();

	/**
	 * Determine if we are running in GWT Hosted Mode 
	 * @return
	 */
	public static boolean isProduction()
	{
		if (isProduction == null)
		{
			lock.lock();
			try
			{
				if (isProduction == null)
				{

					try
					{
						String developement = System.getProperty(CRUX_DEV_PROPERTY);
						if (StringUtils.isEmpty(developement))
						{
							isProduction = true;
						}
						else
						{
							isProduction = !Boolean.parseBoolean(developement);
						}
					}
					catch (Throwable e) 
					{
						isProduction = true;
					}
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		return isProduction;
	}
}
