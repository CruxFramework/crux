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

import java.util.HashMap;

import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;

/**
 * SelectEventsHanler Implementation for touch devices that may have mouse support
 * @author Thiago da Rosa de Bustamante
 * @author Samuel Almeida Cardoso
 *
 */
public class SelectEventsHandlerTouchAndMouseImpl extends SelectEventsHandlerTouchImpl implements ClickHandler
{
	//Map events to widgets 
	public static HashMap<SelectableWidget, Boolean> eventProcessed = new HashMap<SelectableWidget, Boolean>();	
	
	@Override
	public void handleWidget()
	{
		super.handleWidget();
		//iPad handles onTouchStart and onMouseMove as the same event
		if(!Screen.isIPad())
		{
			selectableWidget.addClickHandler(this);
		}
	}

	@Override
    public void onClick(final ClickEvent event)
    {
		if(!Screen.isIPad())
		{
			Boolean boolEventProcessed = eventProcessed.get(selectableWidget);
			if(
				//if the event is already processed
				boolEventProcessed != null && boolEventProcessed)
			{
				eventProcessed.remove(selectableWidget);
				return;
			}
		}
		
		handleClickEvent(event);
    }
	
	private void handleClickEvent(ClickEvent event)
	{
		SelectEvent selectEvent = SelectEvent.fire(selectableWidget);
		if (selectEvent.isCanceled())
		{
			event.preventDefault();
		}
		if (selectEvent.isStopped())
		{
			event.stopPropagation();
		}
	}
		
	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		if(!Screen.isIPad())
		{
			eventProcessed.put(selectableWidget, true);
		}
		super.onTouchStart(event);
	}
}
