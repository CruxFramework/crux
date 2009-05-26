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
package br.com.sysmap.crux.advanced.client.event.collapseexpand;

import br.com.sysmap.crux.advanced.client.event.Events;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;

public class BeforeCollapseOrExpandEvtBind extends EvtBind
{
	/**
	 * @param element
	 * @param widget
	 */
	public static void bindEvents(Element element, HasBeforeCollapseAndBeforeExpandHandlers widget)
	{
		final Event beforeCollapseEvent = getWidgetEvent(element, Events.BEFORE_COLLAPSE);
		if (beforeCollapseEvent != null)
		{
			widget.addBeforeCollapseHandler(new BeforeCollapseHandler()
			{
				public void onBeforeCollapse(BeforeCollapseEvent event)
				{
					EventFactory.callEvent(beforeCollapseEvent, event);
				}
			});
		}
		
		final Event beforeExpandEvent = getWidgetEvent(element, Events.BEFORE_EXPAND);
		if (beforeExpandEvent != null)
		{
			widget.addBeforeExpandHandler(new BeforeExpandHandler()
			{
				public void onBeforeExpand(BeforeExpandEvent event)
				{
					EventFactory.callEvent(beforeExpandEvent, event);
				}
			});
		}
	}
}
