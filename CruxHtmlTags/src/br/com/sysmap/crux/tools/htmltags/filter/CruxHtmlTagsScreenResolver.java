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
package br.com.sysmap.crux.tools.htmltags.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import br.com.sysmap.crux.tools.htmltags.CruxToHtmlTransformer;
import br.com.sysmap.crux.tools.htmltags.HTMLTagsMessages;

/**
 * Custom screen resolver to handle crux HTML tags 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CruxHtmlTagsScreenResolver implements ScreenResourceResolver
{
	HTMLTagsMessages messages = MessagesFactory.getMessages(HTMLTagsMessages.class);	

	/**
	 * 
	 */
	public InputStream getScreenResource(String screenId) throws InterfaceConfigException
	{
		try
		{
			URL webBaseDir = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDir();
			URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(webBaseDir.getProtocol());
			
			screenId = RegexpPatterns.REGEXP_BACKSLASH.matcher(screenId).replaceAll("/").replace(".html", ".crux.xml");
			URL screenURL = resourceHandler.getChildResource(webBaseDir, screenId);

			try
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				CruxToHtmlTransformer.generateHTML(screenURL.openStream(), out);			
				return new ByteArrayInputStream(out.toByteArray());
			}
			catch(Exception e) 
			{
				screenURL = new URL("file:///"+screenId);

				try
				{
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					CruxToHtmlTransformer.generateHTML(screenURL.openStream(), out);			
					return new ByteArrayInputStream(out.toByteArray());
				}
				catch(Exception e1)
				{
					URL url = getClass().getResource("/"+screenId);
					if (url != null)
					{
						return url.openStream();
					}
					else
					{
						return null;
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new InterfaceConfigException(messages.cruxHtmlTagsScreenResolverError(screenId, e.getMessage()), e);
		}
	}

	/**
	 * 
	 */
	public Set<String> getAllScreenIDs(String module) throws ScreenConfigException
	{
		return  new CruxHtmlTagsScreenResourceScanner().getPages(module);
	}
}
