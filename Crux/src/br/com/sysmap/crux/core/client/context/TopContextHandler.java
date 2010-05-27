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

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.event.EventClientHandlerInvoker;
import br.com.sysmap.crux.core.client.event.EventProcessor;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.screen.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.Screen;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TopContextHandler implements ContextHandler
{
	/**
	 * 
	 */
	public TopContextHandler()
    {
		Events.getRegisteredClientEventHandlers().registerEventHandler("__topContextController", new TopContextHanlderController());
    }
	
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
			Crux.getErrorHandler().handleError(e);
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.context.ContextHandler#initializeContext()
	 */
	public void initializeContext()
	{
		//Do nothing
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
			Crux.getErrorHandler().handleError(e);
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
			Crux.getErrorHandler().handleError(e);
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
			Crux.getErrorHandler().handleError(e);
		}
	}

	/**
	 * @author Thiago da Rosa de Bustamante -
	 *
	 */
	public static class TopContextHanlderController implements EventClientHandlerInvoker
	{
		protected HashMap<String, Object> context = new HashMap<String, Object>();
		
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

		/**
		 * @see br.com.sysmap.crux.core.client.event.EventClientHandlerInvoker#invoke(java.lang.String, java.lang.Object, boolean, br.com.sysmap.crux.core.client.event.EventProcessor)
		 */
		public void invoke(String method, Object sourceEvent, boolean fromOutOfModule, EventProcessor eventProcessor)
                throws Exception
        {
			Object returnValue = null;
			boolean hasReturn = false;

			try
			{
				if ("readData".equals(method) && fromOutOfModule)
				{
					hasReturn = true;
					returnValue = readData((InvokeControllerEvent)sourceEvent);
				}
				else if("writeData".equals(method) && fromOutOfModule)
				{
					writeData((InvokeControllerEvent)sourceEvent);
				}
				else if("eraseData".equals(method) && fromOutOfModule)
				{
					eraseData((InvokeControllerEvent)sourceEvent);
				}
				else if("clearData".equals(method) && fromOutOfModule)
				{
					clearData((InvokeControllerEvent)sourceEvent);
				}
			}
			catch (Throwable e)
			{
				eventProcessor.setException(e);
			} 

			if (hasReturn)
			{
				eventProcessor.setHasReturn(true);
				eventProcessor.setReturnValue(returnValue);
			}
        }

		/**
		 * @see br.com.sysmap.crux.core.client.screen.ScreenBindableObject#isAutoBindEnabled()
		 */
		public boolean isAutoBindEnabled()
        {
	        return false;
        }

		/**
		 * @see br.com.sysmap.crux.core.client.screen.ScreenBindableObject#updateControllerObjects()
		 */
		public void updateControllerObjects()
        {
        }

		/**
		 * @see br.com.sysmap.crux.core.client.screen.ScreenBindableObject#updateScreenWidgets()
		 */
		public void updateScreenWidgets()
        {
        }
	}
}
