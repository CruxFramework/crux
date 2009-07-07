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

import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class CookieContextHandler implements ContextHandler
{
	private static final String CONTEXT_PREFIX = "__cruxContext";
	private Date expires = new Date(2240532000000L); 
	
	/**
	 * 
	 * @return
	 */
	public Date getExpires()
	{
		return expires;
	}

	/**
	 * 
	 * @param expires
	 */
	public void setExpires(Date expires)
	{
		this.expires = expires;
	}

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
		try
		{
			return Screen.getCruxSerializer().deserialize(value);
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 
	 */
	public void writeData(String key, Object value)
	{
		try
		{
			Cookies.setCookie(CONTEXT_PREFIX+key, Screen.getCruxSerializer().serialize(value), expires);
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
		}
	}

	/**
	 * 
	 */
	public void eraseData(String key)
	{
		Cookies.removeCookie(CONTEXT_PREFIX+key);
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
				Cookies.removeCookie(cookie);
			}
		}
	}
}
