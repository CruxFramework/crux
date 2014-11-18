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
package org.cruxframework.crux.core.declarativeui.filter;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.server.CruxAbstractFilter;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.utils.StreamUtils;


/**
 * Intercept requests to .html pages and redirect to the correspondent .crux.xml file. Then transform this xml  
 * into the expected .html file. Used only for development.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class DeclarativeUIFilter extends CruxAbstractFilter
{
	private static final Log log = LogFactory.getLog(DeclarativeUIFilter.class);
	protected String modulesUrlPrefix;

	@Override
	public void init(FilterConfig config) throws ServletException
	{
		super.init(config);
		if (!production)
		{
			modulesUrlPrefix = config.getInitParameter("modulesUrlPrefix");
		}
	}
	
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
				CruxBridge.getInstance().registerLastPageRequested(requestedScreen);
				String module = null;
			    int index = requestedScreen.indexOf("/");
			    if (index > 0)
			    {
			    	String relativeScreenId = Modules.getInstance().removeModuleIfPresent(requestedScreen);
			    	if (!relativeScreenId.equals(requestedScreen))
			    	{
			    		module = requestedScreen.substring(0, index);
			    		requestedScreen = relativeScreenId;
			    	}
			    }
				try
				{
					InputStream screenResource;
					if (module == null)
					{
						screenResource = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenResource(requestedScreen);
					}
					else
					{
						screenResource = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenResource(requestedScreen, module);
					}
					if (screenResource != null)
					{
						StreamUtils.write(screenResource, resp.getOutputStream(), false);
						return;
					}
					else
					{
						log.info("The page ["+requestedScreen+"] is not declarative... accessing directly.");
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
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	@Override
	protected String getRequestedScreen(ServletRequest req)
	{
		String result = super.getRequestedScreen(req);
		
		if (result != null)
		{
			if (!StringUtils.isEmpty(modulesUrlPrefix))
			{
				result = removeStringPrefix(result, modulesUrlPrefix);
			}
			return result;
		}
		return null;
	}
}
