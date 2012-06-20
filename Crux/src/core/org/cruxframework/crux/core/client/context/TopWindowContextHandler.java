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

/**
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 */
public class TopWindowContextHandler implements ContextHandler
{
	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#clearContext()
	 */
	public native void clearContext()/*-{
		var target = @org.cruxframework.crux.core.client.screen.JSWindow::getAbsoluteTop()();
		target.__CRUX_TOP_WINDOW_CONTEXT = new Array();
	}-*/;

	/**
	 * @see org.cruxframework.crux.core.client.context.ContextHandler#erase(java.lang.String)
	 */
	public native void erase(String key)/*-{
		var target = @org.cruxframework.crux.core.client.screen.JSWindow::getAbsoluteTop()();
		if(target.__CRUX_TOP_WINDOW_CONTEXT != null){
			target.__CRUX_TOP_WINDOW_CONTEXT[key] = null;
		}
	}-*/;

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
	public native String read(String key)/*-{
		var target = @org.cruxframework.crux.core.client.screen.JSWindow::getAbsoluteTop()();
		if(target.__CRUX_TOP_WINDOW_CONTEXT != null){
			return target.__CRUX_TOP_WINDOW_CONTEXT[key];
		}
		return null;
	}-*/;

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
	public native void write(String key, String value)/*-{
		var target = @org.cruxframework.crux.core.client.screen.JSWindow::getAbsoluteTop()();
		if(target.__CRUX_TOP_WINDOW_CONTEXT == null){
			target.__CRUX_TOP_WINDOW_CONTEXT = new Array();
		}
		target.__CRUX_TOP_WINDOW_CONTEXT[key] = value;
	}-*/;

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
}