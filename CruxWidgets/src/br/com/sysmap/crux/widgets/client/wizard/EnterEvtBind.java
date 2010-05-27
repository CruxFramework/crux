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
package br.com.sysmap.crux.widgets.client.wizard;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBinder;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class EnterEvtBind implements EvtBinder<HasEnterHandlers>
{
	private static final String EVENT_NAME = "onEnter";

	/**
	 * @param element
	 * @param widget
	 */
	public void bindEvent(Element element, HasEnterHandlers widget)
	{
		final Event enterEvent = EvtBind.getWidgetEvent(element, EVENT_NAME);
		if (enterEvent != null)
		{
			widget.addEnterHandler(new EnterHandler()
			{
				public void onEnter(EnterEvent event)
				{
					br.com.sysmap.crux.core.client.event.Events.callEvent(enterEvent, event);
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
