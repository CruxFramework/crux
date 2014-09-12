/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.select;

import org.cruxframework.crux.core.client.screen.widgets.SelectableWidget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * SelectEventsHanler Implementation for touch devices
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SelectEventsHandlerTouchImpl extends SelectEventsHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
{
	private static final int TAP_EVENT_THRESHOLD = 5;
	private int startX;
	private int startY;
	private HandlerRegistration touchMoveHandler;
	private HandlerRegistration touchEndHandler;
	private boolean childOfSelectableWidget = false;
	
	public void handleWidget()
	{
		selectableWidget.addTouchStartHandler(this);
		selectableWidget.addAttachHandler(new Handler() 
		{
			@Override
			public void onAttachOrDetach(AttachEvent event) 
			{
				childOfSelectableWidget = false;
				if (event.isAttached())
				{
					Widget parent = selectableWidget.getParent();
					while (parent != null)
					{
						if (parent instanceof SelectableWidget)
						{
							childOfSelectableWidget = true;
							break;
						}
						parent = parent.getParent();
					}
				}
			}
		});
	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		if (preventDefaultTouchEvents)
		{
			event.preventDefault();
		}
		maybeStopPropagation(event);
		if (isEnabled())
		{
			//TODO: check this! Giving some time to onTouchEnd finalize.
			//If we don't do it, focus events that runs inside any selectHandler
			//will be overridden by (some crazy?) focus events onTouchEnd.
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
				@Override
				public boolean execute() {
					select();
					return false;
				}
			}, 500);
//			select();
		}
		resetHandlers();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		if (preventDefaultTouchEvents)
		{
			event.preventDefault();
		}
		Touch touch = event.getTouches().get(0);
		if (Math.abs(touch.getClientX() - this.startX) > TAP_EVENT_THRESHOLD || Math.abs(touch.getClientY() - this.startY) > TAP_EVENT_THRESHOLD) 
		{
			this.resetHandlers();
		}
	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		maybeStopPropagation(event);
		if (preventDefaultTouchEvents)
		{
			event.preventDefault();
		}
		Touch touch = event.getTouches().get(0);
		startX = touch.getClientX();
		startY = touch.getClientY();
		touchMoveHandler = selectableWidget.addTouchMoveHandler(this);
		touchEndHandler = selectableWidget.addTouchEndHandler(this);
	}
	
	private void maybeStopPropagation(DomEvent<?> event) 
	{
		if (!childOfSelectableWidget)
		{
			event.stopPropagation();
			event.preventDefault();
		}
	}

	private void resetHandlers()
	{
		if(touchMoveHandler != null)
		{
			touchMoveHandler.removeHandler();
			touchMoveHandler = null;
		}
		if(touchEndHandler != null)
		{
			touchEndHandler.removeHandler();
			touchEndHandler = null;
		}
	}
}
