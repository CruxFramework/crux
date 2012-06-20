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
package org.cruxframework.crux.tools.quickstart.server.service;

import java.util.PropertyResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.tools.quickstart.client.remote.WelcomeService;


/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class WelcomeServiceImpl implements WelcomeService
{
	private static String cruxVersion = null;
	private static Lock lock = new ReentrantLock();
	
	/**
	 * @see org.cruxframework.crux.tools.quickstart.client.remote.WelcomeService#getCruxVersion()
	 */
	public String getCruxVersion()
	{
		if (cruxVersion == null)
		{
			lock.lock();
			try
			{
				if (cruxVersion == null)
				{
					cruxVersion = PropertyResourceBundle.getBundle("version").getString("version");
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		return cruxVersion;
	}
}
