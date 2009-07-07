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

import java.util.HashMap;

import com.google.gwt.core.client.GWT;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.client.screen.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.Screen;


/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class TopContextHandler implements ContextHandler
{
	/**
	 * @see br.com.sysmap.crux.core.client.context.ContextHandler#eraseData(java.lang.String)
	 */
	public void eraseData(String key)
	{
		try
		{
			Screen.invokeControllerOnAbsoluteTop("__topContextController.eraseData", key);
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.context.ContextHandler#initializeContext()
	 */
	public void initializeContext()
	{
		// Do Nothing
	}

	/**
	 * 
	 */
	public void clearContext()
	{
		try
		{
			Screen.invokeControllerOnAbsoluteTop("__topContextController.clearData", null);
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.context.ContextHandler#readData(java.lang.String)
	 */
	public Object readData(String key)
	{
		try
		{
			return Screen.invokeControllerOnAbsoluteTop("__topContextController.readData", key, Object.class);
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
			return null;
		}
	}

	/** 
	 * @see br.com.sysmap.crux.core.client.context.ContextHandler#writeData(java.lang.String, java.lang.Object)
	 */
	public void writeData(String key, Object value)
	{
		try
		{
			Screen.invokeControllerOnAbsoluteTop("__topContextController.writeData", new Object[]{key, value});
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
		}
	}

	@Global
	@Controller("__topContextController")
	public static class TopContextHanlderController
	{
		@Create
		protected HashMap<String, Object> context;
		
		@ExposeOutOfModule
		public void writeData(InvokeControllerEvent event)
		{
			Object[] data = (Object[])event.getParameter();
			context.put((String) data[0], data[1]);
		}
		
		@ExposeOutOfModule
		public Object readData(InvokeControllerEvent event)
		{
			return context.get((String) event.getParameter());
		}
		
		@ExposeOutOfModule
		public void eraseData(InvokeControllerEvent event)
		{
			context.remove((String) event.getParameter());
		}

		@ExposeOutOfModule
		public void clearData(InvokeControllerEvent event)
		{
			context.clear();
		}
	}
}
