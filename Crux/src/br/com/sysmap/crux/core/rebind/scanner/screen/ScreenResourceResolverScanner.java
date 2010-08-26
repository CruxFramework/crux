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
package br.com.sysmap.crux.core.rebind.scanner.screen;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

/**
 * Class for retrieve the ScreenResourceResolver class
 * @author Thiago Bustamante
 */
public class ScreenResourceResolverScanner 
{
	private static final Log logger = LogFactory.getLog(ScreenResourceResolverScanner.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	
	/**
	 * Return the ScreenResourceResolver to be used to find screen files. If one is configured in Crux.properties, use it, 
	 * else use the first one that implements the interface ScreenResourceResolver in application' s classpath. If none is found, 
	 * use crux default implementation. 
	 * @param interfaceName
	 * @return
	 */
	public static Class<?> getScreenResolver()
	{
		try 
		{
			String resourceResolverClassName = ConfigurationFactory.getConfigurations().screenResourceResolver();
			if (resourceResolverClassName != null && resourceResolverClassName.trim().length() > 0)
			{
				return Class.forName(resourceResolverClassName.trim());
			}
			
			Set<String> resolverNames =  ClassScanner.searchClassesByInterface(ScreenResourceResolver.class);
			if (resolverNames != null)
			{
				for (String resolver : resolverNames) 
				{
					Class<?> resolverClass = Class.forName(resolver);
					return resolverClass;
				}
			}
		} 
		catch (ClassNotFoundException e) 
		{
			logger.debug(messages.screenResourceResolverUsingDefault());
		}
		return ScreenResourceResolverImpl.class;
	}
}
