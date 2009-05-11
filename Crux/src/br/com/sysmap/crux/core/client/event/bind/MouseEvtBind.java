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
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;

/**
 * Helper Class for mouse events binding
 * @author Thiago Bustamante
 *
 */
public class MouseEvtBind extends EvtBind
{
	public static void bindEvents(Element element, HasAllMouseHandlers widget)
	{
		final Event eventMouseDown = getWidgetEvent(element, EventFactory.EVENT_MOUSE_DOWN);
		if (eventMouseDown != null)
		{
			widget.addMouseDownHandler(new MouseDownHandler()
			{
				public void onMouseDown(MouseDownEvent event) 
				{
					EventFactory.callEvent(eventMouseDown, event);
				}
			});
		}
	
		final Event eventMouseMove = getWidgetEvent(element, EventFactory.EVENT_MOUSE_MOVE);
		if (eventMouseMove != null)
		{
			widget.addMouseMoveHandler(new MouseMoveHandler()
			{
				public void onMouseMove(MouseMoveEvent event) 
				{
					EventFactory.callEvent(eventMouseMove, event);
				}
			});
		}

		final Event eventMouseOut = getWidgetEvent(element, EventFactory.EVENT_MOUSE_OUT);
		if (eventMouseOut != null)
		{
			widget.addMouseOutHandler(new MouseOutHandler()
			{
				public void onMouseOut(MouseOutEvent event) 
				{
					EventFactory.callEvent(eventMouseOut, event);					
				}
			});
		}
	
		final Event eventMouseOver = getWidgetEvent(element, EventFactory.EVENT_MOUSE_OVER);
		if (eventMouseOver != null)
		{
			widget.addMouseOverHandler(new MouseOverHandler()
			{
				public void onMouseOver(MouseOverEvent event) 
				{
					EventFactory.callEvent(eventMouseOver, event);					
				}
			});
		}

		final Event eventMouseUp = getWidgetEvent(element, EventFactory.EVENT_MOUSE_UP);
		if (eventMouseUp != null)
		{
			widget.addMouseUpHandler(new MouseUpHandler()
			{
				public void onMouseUp(MouseUpEvent event) 
				{
					EventFactory.callEvent(eventMouseUp, event);					
				}
			});
		}

		final Event eventMouseWheel = getWidgetEvent(element, EventFactory.EVENT_MOUSE_WHEEL);
		if (eventMouseWheel != null)
		{
			widget.addMouseWheelHandler(new MouseWheelHandler()
			{
				public void onMouseWheel(MouseWheelEvent event) 
				{
					EventFactory.callEvent(eventMouseWheel, event);					
				}
			});
		}
	}
}
