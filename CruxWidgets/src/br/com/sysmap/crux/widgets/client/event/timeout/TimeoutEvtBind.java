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
package br.com.sysmap.crux.widgets.client.event.timeout;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.widgets.client.event.Events;

import com.google.gwt.dom.client.Element;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé
 */
public class TimeoutEvtBind extends EvtBind
{
	/**
	 * @param childElement
	 * @param widget
	 */
	public static void bindEventForChildTag(Element childElement, HasTimeoutHandlers widget)
	{
		final String time = WidgetFactory.getProperty(childElement,"time");
		String execute = WidgetFactory.getProperty(childElement,"execute");
		
		if(time != null && execute != null)
		{
			final Event timeoutEvent = br.com.sysmap.crux.core.client.event.Events.getEvent(Events.TIMEOUT, execute);
			
			if(timeoutEvent != null)
			{
				widget.addTimeoutHandler(new TimeoutHandler()
				{
					/**
					 * @see br.com.sysmap.crux.widgets.client.event.timeout.TimeoutHandler#onTimeout(br.com.sysmap.crux.widgets.client.event.timeout.TimeoutEvent)
					 */
					public void onTimeout(TimeoutEvent event)
					{
						br.com.sysmap.crux.core.client.event.Events.callEvent(timeoutEvent, event);					
					}

					/**
					 * @see br.com.sysmap.crux.widgets.client.event.timeout.TimeoutHandler#getScheduledTime()
					 */
					public long getScheduledTime()
					{
						return Long.parseLong(time);
					}
				});
			}
		}
	}
}
