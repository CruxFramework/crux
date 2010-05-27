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
package br.com.sysmap.crux.core.rebind.screen;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.core.utils.URLUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class ScreenResourceResolverImpl implements ScreenResourceResolver
{
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	public InputStream getScreenResource(String screenId) throws InterfaceConfigException
	{
		try
		{
			URL[] webBaseDirs = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDirs();
			
			URL screenURL = null;
			InputStream inputStream = null;
			for (URL webBaseDir: webBaseDirs)
			{
				URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(webBaseDir.getProtocol());

				screenId = RegexpPatterns.REGEXP_BACKSLASH.matcher(screenId).replaceAll("/");
				screenURL = resourceHandler.getChildResource(webBaseDir, screenId);

				inputStream = URLUtils.openStream(screenURL);
				if (inputStream != null)
				{
					return inputStream;
				}
			}
			
			screenURL = URLUtils.isrValidURL(screenId);
			
			if (screenURL == null)
			{
				screenURL = new URL("file:///"+screenId);
			}

			inputStream = URLUtils.openStream(screenURL);
			if (inputStream != null)
			{
				return inputStream;
			}
			else
			{
				screenURL = getClass().getResource("/"+screenId);
				if (screenURL != null)
				{
					return URLUtils.openStream(screenURL);
				}
				else
				{
					return null;
				}
			}
		}
		catch (Exception e)
		{
			throw new InterfaceConfigException(messages.screenResourceResolverFindResourceError(screenId, e.getMessage()), e);
		}
	}

	public Set<String> getAllScreenIDs(String module) throws ScreenConfigException
	{
		return new ScreenResourcesScannerImpl().getPages(module);
	}
}
