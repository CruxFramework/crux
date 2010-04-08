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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

/**
 * Class for retrieve the service class, based on the remote interface it implements
 * @author Thiago Bustamante
 */
public class Services 
{
	private static final Log logger = LogFactory.getLog(Services.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	
	/**
	 * Return the service that implements the interface informed.
	 * @param interfaceName
	 * @return
	 */
	public static Class<?> getService(String interfaceName)
	{
		try 
		{
			Set<String> serviceNames =  ClassScanner.searchClassesByInterface(Class.forName(interfaceName));
			if (serviceNames != null)
			{
				for (String service : serviceNames) 
				{
					Class<?> serviceClass = Class.forName(service);
					return serviceClass;
				}
			}
			//TODO - Thiago - notificar 
		} 
		catch (ClassNotFoundException e) 
		{
			logger.error(messages.servicesInitializeError(e.getLocalizedMessage()),e);
		}
		return null;
	}
}
