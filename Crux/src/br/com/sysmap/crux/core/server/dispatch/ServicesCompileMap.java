/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.server.dispatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;

/**
 * Builds a map of all remoteServices implementation classes 
 * @author Thiago da Rosa de Bustamante
 */
public class ServicesCompileMap
{
	private static final Log logger = LogFactory.getLog(ServicesCompileMap.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static Map<String, String> remoteServices = new HashMap<String, String>();
	
	/**
	 * @param serviceName
	 * @return
	 */
	public static Class<?> getService(String serviceName)
	{
		try 
		{
			String implementationClassName =  remoteServices.get(serviceName);
			if (implementationClassName != null)
			{
				Class<?> serviceClass = Class.forName(implementationClassName);
				return serviceClass;
			}
			logger.error(messages.servicesNoImplementationFound(serviceName));
		} 
		catch (ClassNotFoundException e) 
		{
			logger.error(messages.servicesErrorCreatingService(serviceName, e.getLocalizedMessage()),e);
		}
		return null;	
	}

	/**
	 * @param context
	 */
	public static boolean initialize(ServletContext context)
	{
		Properties properties = new Properties();
		try
		{
			properties.load(context.getResourceAsStream("/META-INF/crux-remote"));
			Set<String> serviceNames = properties.stringPropertyNames();
			for (String serviceName : serviceNames)
			{
				remoteServices.put(serviceName, properties.getProperty(serviceName));
			}
			return true;
		}
		catch (Exception e)
		{
			logger.info(messages.servicesInitializeError(e.getMessage()),e);
		}
		return false;
	}
}
