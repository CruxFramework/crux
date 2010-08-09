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
package br.com.sysmap.crux.core.declarativeui.filter;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.declarativeui.CruxToHtmlTransformer;
import br.com.sysmap.crux.core.declarativeui.DeclarativeUIMessages;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import br.com.sysmap.crux.core.server.CruxFilter;
import br.com.sysmap.crux.core.utils.StreamUtils;

/**
 * Intercept requests to .html pages and redirect to the correspondent .crux.xml file. Then transform this xml  
 * into the expected .html file. Used only for development.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class DeclarativeUIFilter extends CruxFilter
{
	private static final Log log = LogFactory.getLog(DeclarativeUIFilter.class);

	private DeclarativeUIMessages messages = MessagesFactory.getMessages(DeclarativeUIMessages.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)  throws IOException, ServletException 
	{
		if (production)
		{
			chain.doFilter(req, resp);
		}
		else
		{
			String requestedScreen = getRequestedScreen(req);
			if (requestedScreen != null)
			{
				try
				{
					String screenId = requestedScreen.replace(".html", ".crux.xml");
					String charset = config.getInitParameter("outputCharset");

					if(charset != null)
					{
						CruxToHtmlTransformer.setOutputCharset(charset);
					}

					InputStream screenResource = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenResource(screenId);
					if (screenResource != null)
					{
						StreamUtils.write(screenResource, resp.getOutputStream(), false);
						return;
					}
					else
					{
						log.info(messages.declarativeUIDoesNotTransformPage(requestedScreen));
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
					throw new ServletException(e.getMessage(),e);
				}
			}
			chain.doFilter(req, resp);
		}
	}
}
