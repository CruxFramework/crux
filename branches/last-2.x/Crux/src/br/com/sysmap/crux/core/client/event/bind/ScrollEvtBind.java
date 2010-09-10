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
package br.com.sysmap.crux.core.client.event.bind;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.dom.client.ScrollEvent;


/**
 * Helper Class for scroll events binding
 * @author Thiago Bustamante
 *
 */
public class ScrollEvtBind implements EvtBinder<HasScrollHandlers>
{
	private static final String EVENT_NAME = "onScroll";

	/**
	 * @see br.com.sysmap.crux.core.client.event.bind.EvtBinder#bindEvent(com.google.gwt.dom.client.Element, com.google.gwt.event.shared.HasHandlers)
	 */
	public void bindEvent(Element element, HasScrollHandlers widget)
	{
		final Event eventScroll = EvtBind.getWidgetEvent(element, EVENT_NAME);
		if (eventScroll != null)
		{
			ScrollHandler handler = new ScrollHandler()
			{
				public void onScroll(ScrollEvent event) 
				{
					Events.callEvent(eventScroll, event);
				}
			};
			widget.addScrollHandler(handler);
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.event.bind.EvtBinder#getEventName()
	 */
	public String getEventName()
	{
		return EVENT_NAME;
	}	
}
