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
package br.com.sysmap.crux.core.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import br.com.sysmap.crux.core.rebind.CruxScreenBridge;

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
				CruxScreenBridge.getInstance().registerLastPageRequested(requestedScreen);
			}
			else
			{
				CruxScreenBridge.getInstance().registerLastPageRequested("");
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

				String contextPath = config.getServletContext().getContextPath().replaceAll("\\/", "");
				if (result.startsWith(contextPath))
				{
					result = result.substring(contextPath.length());
				}
				
				if (result.startsWith("/"))
				{
					result = result.substring(1);
				}
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
}
