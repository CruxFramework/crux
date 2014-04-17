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

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;


/**
 * Base class for crux devmode filters.
 * 
 * @author Thiago Bustamante
 */
public abstract class CruxAbstractFilter implements Filter 
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
			if (!result.endsWith("hosted.html") && !result.endsWith("cache.html"))
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
