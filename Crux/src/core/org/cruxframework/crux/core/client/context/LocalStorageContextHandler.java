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

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.storage.client.Storage;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class LocalStorageContextHandler implements ContextHandler, HasContextChangeHandlers
{
	private Storage localStorage;

	public LocalStorageContextHandler() 
	{
		localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage == null)
		{
			//TODO report error.
		}
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#clearContext()
	 */
	public void clearContext()
	{
		localStorage.clear();
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#erase(java.lang.String)
	 */
	public void erase(String key)
	{
		localStorage.removeItem(key);
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
		return localStorage.getItem(key);
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#readData(java.lang.String)
	 */
	@Deprecated
	public Object readData(String key)
	{
		String value = read(key);
		
		if(value != null && value.length() > 0)
		{
			try
			{
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
		localStorage.setItem(key, value);
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
			write(key, serialized);
		}
		catch (org.cruxframework.crux.core.client.screen.ModuleComunicationException e)
		{
			Crux.getErrorHandler().handleError(e);
		}
	}

	/**
	 * @see org.cruxframework.crux.core.client.context.HasContextChangeHandlers#addContextChangeHandler(org.cruxframework.crux.core.client.context.HasContextChangeHandlers.Handler)
	 */
	public HandlerRegistration addContextChangeHandler(final Handler handler)
    {
		return StorageEvents.addStorageEventHandler(handler);
    }
}