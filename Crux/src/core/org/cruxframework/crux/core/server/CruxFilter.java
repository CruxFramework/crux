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
package org.cruxframework.crux.core.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


/**
 * Used to save the path to the current HTML page. This information is necessary
 * to generate the client handlers and formatters.
 * 
 * @author Thiago Bustamante
 */
public class CruxFilter implements Filter 
{
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
						
			chain.doFilter(req, resp);
		}
	}
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	protected String getRequestedScreen(ServletRequest req)
	{
		HttpServletRequest request = (HttpServletRequest) req;
		String result = request.getPathInfo();
		if (result == null)
		{
			result = request.getRequestURI();
		}
		if (result != null && result.length() > 0)
		{
			if (!result.endsWith("hosted.html"))
			{
				if (result.startsWith("/"))
				{
					result = result.substring(1);
				}

				String contextPath = config.getServletContext().getContextPath();
				result = removeStringPrefix(result, contextPath);
			}
			else
			{
				result = null;
			}
		}
		else
		{
			result = null;
		}
		return result;
	}

	/**
	 * @param input
	 * @param prefix
	 * @return
	 */
	protected String removeStringPrefix(String input, String prefix)
	{
		if (prefix.startsWith("/"))
		{
			prefix = prefix.substring(1);
		}
		if (input.startsWith(prefix))
		{
			input = input.substring(prefix.length());
		}
		if (input.startsWith("/"))
		{
			input = input.substring(1);
		}
		return input;
	}
}
