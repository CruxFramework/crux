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
package br.com.sysmap.crux.core.client.event;

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.ScreenFactory;

import com.google.gwt.core.client.GWT;

public class EventFactory 
{
	public static final String TYPE_SERVER_AUTO = "server-auto";
	public static final String TYPE_SERVER_RPC = "server-rpc";
	public static final String TYPE_CLIENT = "client";
	
	public static final String SYNC_TYPE_SYNCHRONOUS = "synchronous";
	public static final String SYNC_TYPE_ASSYNCHRONOUS = "assynchronous";
	
	public static final String EVENT_CLICK = "_onclick";
	public static final String EVENT_CHANGE = "_onchange";
	public static final String EVENT_FOCUS = "_onfocus";
	public static final String EVENT_BLUR = "_onblur";
	public static final String EVENT_LOAD = "_onload";

	public static final String EVENT_CLOSE = "_onclose";
	public static final String EVENT_CLOSING = "_onclosing";
	public static final String EVENT_RESIZED = "_onresized";

	public static Event getEvent(String evtId, String evt)
	{
		try
		{
			if (evt != null && evt.trim().length() > 0)
			{
				String[] evtProps = evt.split("\\|");
				if (evtProps.length == 0) return null;
				
				String callback = null;
				String type = TYPE_CLIENT;
				String call = evtProps[0];
				boolean sync = false; 
				
				if (evtProps.length>1 && evtProps[1].length() > 0)
				{
					type = evtProps[1];
				}
				if (evtProps.length>2 && evtProps[2].length() > 0)
				{
					callback = evtProps[2];
				}
				if (evtProps.length>3 && evtProps[3].length() > 0)
				{
					sync = SYNC_TYPE_SYNCHRONOUS.equals(evtProps[3]);
				}

				return new Event(evtId, type, call, callback, sync);
			}
		}
		catch (Throwable e)
		{
			GWT.log(e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	public static void callEvent(Event event, String idSender)
	{
		try 
		{
			EventProcessor processor = br.com.sysmap.crux.core.client.event.EventProcessorFactory.getInstance().createEventProcessor(event);
			processor.processEvent(ScreenFactory.getInstance().getScreen(), idSender);
		}
		catch (InterfaceConfigException e) 
		{
			GWT.log(e.getLocalizedMessage(), e);
		}
		
	}
	
}
