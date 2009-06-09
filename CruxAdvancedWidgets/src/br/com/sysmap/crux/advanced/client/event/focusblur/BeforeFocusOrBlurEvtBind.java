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
package br.com.sysmap.crux.advanced.client.event.focusblur;

import br.com.sysmap.crux.advanced.client.event.Events;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;

public class BeforeFocusOrBlurEvtBind extends EvtBind
{
	/**
	 * @param element
	 * @param widget
	 */
	public static void bindEvents(Element element, HasBeforeFocusAndBeforeBlurHandlers widget)
	{
		bindBeforeFocusEvent(element, widget);
		bindBeforeBlurEvent(element, widget);
	}
	
	/**
	 * @param element
	 * @param widget
	 */
	public static void bindBeforeFocusEvent(Element element, HasBeforeFocusHandlers widget)
	{
		final Event beforeFocusEvent = getWidgetEvent(element, Events.BEFORE_FOCUS);
		if (beforeFocusEvent != null)
		{
			widget.addBeforeFocusHandler(new BeforeFocusHandler()
			{
				public void onBeforeFocus(BeforeFocusEvent event)
				{
					EventFactory.callEvent(beforeFocusEvent, event);
				}
			});
		}
	}	

	/**
	 * @param element
	 * @param widget
	 */
	public static void bindBeforeBlurEvent(Element element, HasBeforeBlurHandlers widget)
	{
		final Event beforeBlurEvent = getWidgetEvent(element, Events.BEFORE_BLUR);
		if (beforeBlurEvent != null)
		{
			widget.addBeforeBlurHandler(new BeforeBlurHandler()
			{
				public void onBeforeBlur(BeforeBlurEvent event)
				{
					EventFactory.callEvent(beforeBlurEvent, event);
				}
			});
		}
	}	
}