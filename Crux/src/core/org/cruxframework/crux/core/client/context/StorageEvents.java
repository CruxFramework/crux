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

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.context.HasContextChangeHandlers.ContextEvent;
import org.cruxframework.crux.core.client.context.HasContextChangeHandlers.Handler;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class StorageEvents
{
	private static FastList<StorageEventsHandlerRegistration> handlers = new FastList<StorageEventsHandlerRegistration>();
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class StorageEventsHandlerRegistration implements HandlerRegistration
	{
		private final Handler handler;

		public StorageEventsHandlerRegistration(Handler handler)
        {
			this.handler = handler;
        }

		public void removeHandler()
        {
			removeStorageEventHandler(this);
        }

		public Handler getHandler()
        {
        	return handler;
        }
	}
	
	
	/**
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addStorageEventHandler(Handler handler)
    {
		StorageEventsHandlerRegistration handlerRegistration = new StorageEventsHandlerRegistration(handler);
		if (handlers.size() ==0)
		{
			createNativeEventListener();
		}
		
		handlers.add(handlerRegistration);
		
	    return handlerRegistration;
    }

	/**
	 * @param handler
	 */
	private static void removeStorageEventHandler(StorageEventsHandlerRegistration handler)
	{
		int index = handlers.indexOf(handler);
		if (index >= 0)
		{
			handlers.remove(index);
			if (handlers.size() == 0)
			{
				removeNativeEventListener();
			}
		}
	}
	
	/**
	 * @param key
	 * @param newValue
	 * @param oldValue
	 * @param url
	 */
	private static void fireStorageEvent(String key, String newValue, String oldValue, String url)
	{
		ContextEvent contextEvent = new ContextEvent(key, newValue, oldValue, url);
		for(int i=0; i< handlers.size(); i++)
		{
			StorageEventsHandlerRegistration handlerRegistration = handlers.get(i); 
			handlerRegistration.getHandler().onContextChange(contextEvent);
		}
	}
	
	/**
	 * 
	 */
	private native static void createNativeEventListener()/*-{
		if (!$wnd._crux_storage_function)
		{
			$wnd._crux_storage_function = function(evt)
			{
				@org.cruxframework.crux.core.client.context.StorageEvents::fireStorageEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(evt.key, evt.newValue, evt.oldValue, evt.url);
			};
			if ($wnd.addEventListener) 
			{
			  	$wnd.addEventListener("storage", $wnd._crux_storage_function, false);
			} 
			else 
			{
			  	$wnd.attachEvent("onstorage", $wnd._crux_storage_function);
			};			
		}
	}-*/;
	
	/**
	 * 
	 */
	private native static void removeNativeEventListener()/*-{
		if ($wnd._crux_storage_function)
		{
			if ($wnd.removeEventListener) 
			{
			  	$wnd.removeEventListener("storage", $wnd._crux_storage_function, false);
			} 
			else 
			{
			  	$wnd.detachEvent("onstorage", $wnd._crux_storage_function);
			};			
		}
	}-*/;
}
