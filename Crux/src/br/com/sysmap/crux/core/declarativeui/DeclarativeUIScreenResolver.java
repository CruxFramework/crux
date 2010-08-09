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
package br.com.sysmap.crux.core.declarativeui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolver;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.core.utils.URLUtils;

/**
 * Custom screen resolver to handle crux HTML tags 
 * @author Gessé S. F. Dafé
 */
public class DeclarativeUIScreenResolver implements ScreenResourceResolver
{
	DeclarativeUIMessages messages = MessagesFactory.getMessages(DeclarativeUIMessages.class);	

	/**
	 * 
	 */
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

				screenId = RegexpPatterns.REGEXP_BACKSLASH.matcher(screenId).replaceAll("/").replace(".html", ".crux.xml");
				screenURL = resourceHandler.getChildResource(webBaseDir, screenId);
				inputStream = URLUtils.openStream(screenURL);
				if (inputStream != null)
				{
					break;
				}
			}	
			
			if (inputStream == null)
			{
				
				screenURL = URLUtils.isrValidURL(screenId);
				
				if (screenURL == null)
				{
					screenURL = new URL("file:///"+screenId);
				}
				inputStream = URLUtils.openStream(screenURL);
				if (inputStream == null)
				{
					screenURL = getClass().getResource("/"+screenId);
					if (screenURL != null)
					{
						inputStream = URLUtils.openStream(screenURL);
					}
				}
			}

			if (inputStream == null)
			{
				return null;
			}

			return performTransformation(inputStream);
		}
		catch (Exception e)
		{
			throw new InterfaceConfigException(messages.declarativeUIScreenResolverError(screenId, e.getMessage()), e);
		}
	}

	/**
	 * 
	 * @param screenURL
	 * @return
	 * @throws IOException
	 */
	private InputStream performTransformation(InputStream inputStream) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CruxToHtmlTransformer.generateHTML(inputStream, out);			
		return new ByteArrayInputStream(out.toByteArray());
	}

	/**
	 * 
	 */
	public Set<String> getAllScreenIDs(String module) throws ScreenConfigException
	{
		return  new DeclarativeUIScreenResourceScanner().getPages(module);
	}
	
	
}
