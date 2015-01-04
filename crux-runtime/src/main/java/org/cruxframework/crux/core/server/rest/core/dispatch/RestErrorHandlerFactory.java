/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import java.lang.reflect.Method;

import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RestErrorHandlerFactory
{
	private static Class<?> restErrorHandlerClass = null;
	
	public static RestErrorHandler createErrorHandler(Method method)
	{
		if (restErrorHandlerClass == null)
		{
			initializeRestErrorHanlderClass();
		}
		
		try
		{
			RestErrorHandler restErrorHandler = (RestErrorHandler) restErrorHandlerClass.newInstance();
			restErrorHandler.setMethod(method);
			return restErrorHandler;
		}
		catch(Exception e)
		{
			throw new InternalServerErrorException("Can not execute requested service. Error initializing rest error handler: "+restErrorHandlerClass.getCanonicalName(), 
					"Can not execute requested service", e);
		}
	}

	private synchronized static void initializeRestErrorHanlderClass()
    {
		if (restErrorHandlerClass == null)
		{
			try
			{
				String restErrorHandlerClassName = ConfigurationFactory.getConfigurations().restErrorHandler();
				restErrorHandlerClass = Class.forName(restErrorHandlerClassName);
			}
			catch(Exception e)
			{
				throw new InternalServerErrorException("Can not execute requested service. Error initializing rest error handler.", 
						"Can not execute requested service", e);
			}
		}
    }
}
