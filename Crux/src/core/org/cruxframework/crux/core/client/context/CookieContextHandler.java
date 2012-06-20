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
package org.cruxframework.crux.core.client.context;

import java.util.Collection;
import java.util.Date;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.Screen;


import com.google.gwt.user.client.Cookies;

/**
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe 
 */
public class CookieContextHandler implements ContextHandler
{
	private static final String CONTEXT_PREFIX = "__cruxContext";
	private static final Date expires = new Date(2240532000000L);
	
	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#clearContext()
	 */
	public void clearContext()
	{
		Collection<String> cookieNames = Cookies.getCookieNames();
		for (String cookie : cookieNames)
		{
			if (cookie.startsWith(CONTEXT_PREFIX))
			{
				Cookies.removeCookie(cookie,  "/");
			}
		}
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#erase(java.lang.String)
	 */
	public void erase(String key)
	{
		Cookies.removeCookie(CONTEXT_PREFIX + key,  "/");
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#eraseData(java.lang.String)
	 */
	@Deprecated
	public void eraseData(String key)
	{
		erase(key);
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#initializeContext()
	 */
	public void initializeContext()
	{
		clearContext();
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#read(java.lang.String)
	 */
	public String read(String key)
	{
		String value = Cookies.getCookie(CONTEXT_PREFIX+key);
		
		if(value != null && value.length() > 0)
		{
			return decode(value);
		}
		
		return null;
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#readData(java.lang.String)
	 */
	@Deprecated
	public Object readData(String key)
	{
		String value = Cookies.getCookie(CONTEXT_PREFIX+key);
		
		if(value != null && value.length() > 0)
		{
			try
			{
				value = decode(value);
				return Screen.getCruxSerializer().deserialize(value);
			}
			catch (org.cruxframework.crux.core.client.screen.ModuleComunicationException e)
			{
				Crux.getErrorHandler().handleError(e);
				return null;
			}
		}
		
		return null;
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#writeData(java.lang.String, java.lang.String)
	 */
	public void write(String key, String value)
	{
		value = encode(value);
		Cookies.setCookie(CONTEXT_PREFIX+key, value, expires, null, "/", false);
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#writeData(java.lang.String, java.lang.Object)
	 */
	@Deprecated
	public void writeData(String key, Object value)
	{
		try
		{
			String serialized = Screen.getCruxSerializer().serialize(value);
			serialized = encode(serialized);
			Cookies.setCookie(CONTEXT_PREFIX+key, serialized, expires, null, "/", false);
		}
		catch (org.cruxframework.crux.core.client.screen.ModuleComunicationException e)
		{
			Crux.getErrorHandler().handleError(e);
		}
	}
	
	/**
	 * Decodes a cookie value after retrieving it
	 * @param value
	 * @return
	 */
	private native String decode(String value)/*-{
		return decodeURIComponent(value);
	}-*/;

	/**
	 * Encodes a cookie value before storing it
	 * @param value
	 * @return
	 */
	private native String encode(String value)/*-{
		return encodeURIComponent(value);
	}-*/;
}