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
package br.com.sysmap.crux.widgets.client.event.moveitem;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBinder;

import com.google.gwt.dom.client.Element;

/**
 * Binder for BeforeMoveItemsEvent handlers
 * @author Gessé S. F. Dafé
 */
public class BeforeMoveItemsEvtBind implements EvtBinder<HasBeforeMoveItemsHandlers>
{
	private static final String EVENT_NAME = "onBeforeMoveItems";
	
	/**
	 * @see br.com.sysmap.crux.core.client.event.bind.EvtBinder#bindEvent(com.google.gwt.dom.client.Element, java.lang.Object)
	 */
	public void bindEvent(Element element, HasBeforeMoveItemsHandlers widget)
	{
		final Event beforeMoveItemsEvt = EvtBind.getWidgetEvent(element, EVENT_NAME);
		
		if(beforeMoveItemsEvt != null)
		{
			widget.addBeforeMoveItemsHandler(new BeforeMoveItemsHandler()
			{
				public void onBeforeMoveItems(BeforeMoveItemsEvent event)
				{
					Events.callEvent(beforeMoveItemsEvt, event);					
				}				
			});
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