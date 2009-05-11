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
import br.com.sysmap.crux.core.client.event.EventFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Helper Class for key events binding
 * @author Thiago Bustamante
 *
 */
public class KeyEvtBind extends EvtBind
{
	public static void bindEvents(Element element, HasAllKeyHandlers widget)
	{
		final Event eventKeyDown = getWidgetEvent(element, EventFactory.EVENT_KEY_DOWN);
		if (eventKeyDown != null)
		{
			widget.addKeyDownHandler(new KeyDownHandler()
			{
				public void onKeyDown(KeyDownEvent event) 
				{
					EventFactory.callEvent(eventKeyDown, event);
				}
			});
		}

		final Event eventKeyPress = getWidgetEvent(element, EventFactory.EVENT_KEY_PRESS);
		if (eventKeyPress != null)
		{
			widget.addKeyPressHandler(new KeyPressHandler()
			{
				public void onKeyPress(KeyPressEvent event) 
				{
					EventFactory.callEvent(eventKeyPress, event);
				}
			});
		}
		
		final Event eventKeyUp = getWidgetEvent(element, EventFactory.EVENT_KEY_UP);
		if (eventKeyUp != null)
		{
			widget.addKeyUpHandler(new KeyUpHandler()
			{
				public void onKeyUp(KeyUpEvent event) 
				{
					EventFactory.callEvent(eventKeyUp, event);
				}
			});
		}
	}

}
