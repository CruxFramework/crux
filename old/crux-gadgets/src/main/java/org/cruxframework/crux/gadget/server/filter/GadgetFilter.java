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
package org.cruxframework.crux.gadget.server.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.Environment;


/**
 * Used to save the path to the current HTML page. This information is necessary
 * to generate the client code.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class GadgetFilter implements Filter 
{
	private static final Log logger = LogFactory.getLog(GadgetFilter.class);
	protected boolean production = true;
	protected FilterConfig config = null;
	
	public void init(FilterConfig config) throws ServletException 
	{
		production = Environment.isProduction();
		this.config = config;
	}
	
	public void destroy() 
	{
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
	throws IOException, ServletException 
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
			}
						
			try
            {
	            chain.doFilter(req, resp);
            }
            catch (Exception e)
            {
				logger.error(e.getMessage(), e);
				throw new ServletException(e.getMessage(),e);
            }
		}
	}
	
	/**
	 * Return any page on current module, once all pages are contained on content tags located inside the gadget manifest file
	 * @param req
	 * @return
	 */
	protected String getRequestedScreen(ServletRequest req)
	{
		HttpServletRequest request = (HttpServletRequest) req;
		String module = request.getPathInfo();
		if (module == null)
		{
			module = request.getRequestURI();
		}
		if (module != null && module.length() > 0)
		{
			int slash = module.lastIndexOf('/');
			if (slash > 0)
			{
				module = module.substring(0, slash);
				slash = module.lastIndexOf('/');
				if (slash >= 0)
				{
					module = module.substring(slash+1);
				}
			}
			
			try
            {
	            Set<String> allScreenIDs = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllScreenIDs(module);
	            if (allScreenIDs != null && allScreenIDs.size() >0)
	            {
	            	return allScreenIDs.iterator().next();
	            }
            }
            catch (ScreenConfigException e)
            {
            	logger.error(e.getMessage(), e);
            }
		}
		
		return null;
	}
}
