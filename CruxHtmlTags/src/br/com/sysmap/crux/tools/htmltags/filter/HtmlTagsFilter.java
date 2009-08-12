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

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import br.com.sysmap.crux.core.server.Environment;
import br.com.sysmap.crux.tools.htmltags.CruxToHtmlTransformer;

/**
 * Used to save the path to the current HTML page. This information is necessary
 * to generate the client handlers and formatters.
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class HtmlTagsFilter implements Filter 
{
	private boolean production = true;
	FilterConfig config = null;
	
	public void init(FilterConfig config) throws ServletException 
	{
		production = Environment.isProduction();
		this.config = config;
	}
	
	public void destroy() 
	{
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)  throws IOException, ServletException 
	{
		if (production)
		{
			chain.doFilter(req, resp);
		}
		else
		{
			HttpServletRequest request = (HttpServletRequest) req;
			String pathInfo = request.getPathInfo();
			if (pathInfo == null)
			{
				pathInfo = request.getRequestURI();
			}
			if (pathInfo != null && pathInfo.length() > 0)
			{
				if (!pathInfo.endsWith("hosted.html"))
				{

					try
					{
						if (pathInfo.startsWith("/"))
						{
							pathInfo = pathInfo.substring(1);
						}

						String contextPath = config.getServletContext().getContextPath().replaceAll("\\/", "");
						if (pathInfo.startsWith(contextPath))
						{
							pathInfo = pathInfo.substring(contextPath.length());
						}
						
						if (pathInfo.startsWith("/"))
						{
							pathInfo = pathInfo.substring(1);
						}
						File input = new File(Environment.getWebBaseDir(), pathInfo.replace(".html", ".crux.xml"));
						if (input != null && input.exists())
						{
							String charset = config.getInitParameter("outputCharset");
							
							if(charset != null)
							{
								CruxToHtmlTransformer.setOutputCharset(charset);
							}
							
							CruxToHtmlTransformer.generateHTML(input.getPath(), resp.getOutputStream());
							
							return;
						}
					}
					catch (Exception e)
					{
						throw new ServletException(e.getMessage(),e);
					}
				}
			}
			chain.doFilter(req, resp);
		}
	}
}
