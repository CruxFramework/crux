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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.cruxframework.crux.core.server.rest.spi.HttpRequest;


/**
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 */
public class LocaleResolverImpl implements LocaleResolver 
{
	
	Locale userLocale = null;
	
	/**
	 * 
	 */
	public Locale getUserLocale() throws LocaleResolverException
	{
		if (userLocale == null)
		{
			throw new LocaleResolverException("User LocaleResolver not loaded.");
		}
		return userLocale;
	}

	/**
	 * 
	 */
	public void initializeUserLocale(HttpServletRequest request) 
	{
		String locale = request.getParameter("locale");
		if (locale != null)
		{
			String[] localeParams = locale.split("_");
			if (localeParams.length == 2)
			{
				userLocale = new Locale(localeParams[0], localeParams[1]); 
			}
			else if (localeParams.length == 1)
			{
				userLocale = new Locale(localeParams[0]); 
			}
		}
		if (userLocale == null)
		{
			userLocale = Locale.getDefault();
		}
	}

	@Override
    public void initializeUserLocale(HttpRequest request)
    {
		userLocale = request.getLocale();
    }

}
