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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Default RestServiceFactory implementation. 
 *  
 * @author Gesse Dafe
 *
 */
public class RestServiceFactoryImpl implements RestServiceFactory 
{
	private static final Log logger = LogFactory.getLog(RestServiceFactoryImpl.class);
	
	@Override
	public Object getService(Class<?> serviceClass)
	{
		try 
		{
			return serviceClass.newInstance();
		} 
		catch (Exception e) 
		{
			String msg = "Error creating REST service for class [" + serviceClass.getCanonicalName() + "].";
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	@Override
	public void initialize(ServletContext context)
	{
		// nothing to do
	}
}
