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
package br.com.sysmap.crux.widgets.client.event.focusblur;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBinder;

import com.google.gwt.dom.client.Element;

/**
 * Helper Class for before focus events binding
 * @author Thiago Bustamante
 *
 */
public class BeforeBlurEvtBind implements EvtBinder<HasBeforeBlurHandlers>
{
	private static final String EVENT_NAME = "onBeforeBlur";
	
	public void bindEvent(Element element, HasBeforeBlurHandlers widget)
	{
		final Event eventBeforeBlur = EvtBind.getWidgetEvent(element, EVENT_NAME);
		if (eventBeforeBlur != null)
		{
			widget.addBeforeBlurHandler(new BeforeBlurHandler()
			{
				
				public void onBeforeBlur(BeforeBlurEvent event)
				{
					Events.callEvent(eventBeforeBlur, event);
				}
			});
		}
	}

	public String getEventName()
	{
		return EVENT_NAME;
	}		
}
