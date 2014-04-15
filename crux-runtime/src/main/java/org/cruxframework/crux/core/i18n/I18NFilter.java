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
package org.cruxframework.crux.core.i18n;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Used to initialize the I18N mechanism.
 * 
 * @author Thiago Bustamante
 */
public class I18NFilter implements Filter 
{
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
	throws IOException, ServletException 
	{
		LocaleResolverInitializer.createLocaleResolverThreadData();
		LocaleResolverInitializer.getLocaleResolver().initializeUserLocale((HttpServletRequest) req);
		try
		{
			chain.doFilter(req, resp);
		}
		finally
		{
			LocaleResolverInitializer.clearLocaleResolverThreadData();
		}
	}

	public void destroy()
	{
	}

	public void init(FilterConfig filterConfig) throws ServletException
	{
	}
}
