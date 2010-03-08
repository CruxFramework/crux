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
package br.com.sysmap.crux.core.client.context;

import java.util.Collection;
import java.util.Date;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.Cookies;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class CookieContextHandler implements ContextHandler
{
	private static final String CONTEXT_PREFIX = "__cruxContext";
	private static final Date expires = new Date(2240532000000L);
	private static final Date expired = new Date(10L);
	
	/**
	 * 
	 */
	public void initializeContext()
	{
		clearContext();
	}

	/**
	 * 
	 */
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
			catch (ModuleComunicationException e)
			{
				Crux.getErrorHandler().handleError(e);
				return null;
			}
		}
		
		return null;
	}

	private native String decode(String value)/*-{
		return decodeURIComponent(value);
	}-*/;
	
	private native String encode(String value)/*-{
		return encodeURIComponent(value);
	}-*/;

	/**
	 * 
	 */
	public void writeData(String key, Object value)
	{
		try
		{
			String serialized = Screen.getCruxSerializer().serialize(value);
			serialized = encode(serialized);
			Cookies.setCookie(CONTEXT_PREFIX+key, serialized, expires, null, "/", false);
		}
		catch (ModuleComunicationException e)
		{
			Crux.getErrorHandler().handleError(e);
		}
	}

	/**
	 * 
	 */
	public void eraseData(String key)
	{
		Cookies.setCookie(CONTEXT_PREFIX+key, "", expired, null, "/", false);
	}

	/**
	 * 
	 */
	public void clearContext()
	{
		Collection<String> cookieNames = Cookies.getCookieNames();
		for (String cookie : cookieNames)
		{
			if (cookie.startsWith(CONTEXT_PREFIX))
			{
				Cookies.setCookie(cookie, "", expired, null, "/", false);
			}
		}
	}
}