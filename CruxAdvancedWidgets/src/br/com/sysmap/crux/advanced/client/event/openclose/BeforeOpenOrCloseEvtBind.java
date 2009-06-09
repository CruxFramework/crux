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
package br.com.sysmap.crux.advanced.client.event.openclose;

import br.com.sysmap.crux.advanced.client.event.Events;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;

public class BeforeOpenOrCloseEvtBind extends EvtBind
{
	/**
	 * @param element
	 * @param widget
	 */
	public static void bindEvents(Element element, HasBeforeOpenAndBeforeCloseHandlers widget)
	{
		bindBeforeCloseEvent(element, widget);
		bindBeforeOpenEvent(element, widget);
	}
	
	/**
	 * @param element
	 * @param widget
	 */
	public static void bindBeforeCloseEvent(Element element, HasBeforeCloseHandlers widget)
	{
		final Event beforeCloseEvent = getWidgetEvent(element, Events.BEFORE_CLOSE);
		if (beforeCloseEvent != null)
		{
			widget.addBeforeCloseHandler(new BeforeCloseHandler()
			{
				public void onBeforeClose(BeforeCloseEvent event)
				{
					EventFactory.callEvent(beforeCloseEvent, event);
				}
			});
		}
	}
	
	/**
	 * @param element
	 * @param widget
	 */
	public static void bindBeforeOpenEvent(Element element, HasBeforeOpenHandlers widget)
	{
		final Event beforeOpenEvent = getWidgetEvent(element, Events.BEFORE_OPEN);
		if (beforeOpenEvent != null)
		{
			widget.addBeforeOpenHandler(new BeforeOpenHandler()
			{
				public void onBeforeOpen(BeforeOpenEvent event)
				{
					EventFactory.callEvent(beforeOpenEvent, event);
				}
			});
		}
	}
}
