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
package org.cruxframework.crux.core.rebind.screen.parameter;

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
public class ParameterBindGeneratorInitializer 
{
	private static final Log logger = LogFactory.getLog(ParameterBindGeneratorInitializer.class);
	private static ParameterBindGenerator parameterBindGenerator;
	private static final Lock lock = new ReentrantLock();

	public static ParameterBindGenerator getParameterBindGenerator()
	{
		if (parameterBindGenerator != null) return parameterBindGenerator;
		
		try
		{
			lock.lock();
			if (parameterBindGenerator != null) return parameterBindGenerator;

			parameterBindGenerator = (ParameterBindGenerator) Class.forName(ConfigurationFactory.getConfigurations().parameterBindGenerator()).newInstance();
		}
		catch (Throwable e)
		{
			throw new ParameterBindGeneratorException("Error initializing parameterBindGenerator.", e);
		}
		finally
		{
			lock.unlock();
		}
		logger.info("Using parameterBindGenerator: ["+parameterBindGenerator.getClass().getName()+"]");
		return parameterBindGenerator;
	}
}
