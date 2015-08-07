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

import org.cruxframework.crux.core.client.event.HasSelectHandlers;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class SelectableWidget extends Composite implements HasSelectHandlers
{
	protected SelectEventsHandler selectEventsHandler;
	
	@Override
	protected void initWidget(Widget widget)
	{
	    super.initWidget(widget);
	    initializeEventsHandler();
	}
	
	protected void initializeEventsHandler()
	{
		selectEventsHandler = GWT.create(SelectEventsHandler.class);
		selectEventsHandler.setSelectableWidget(this);
		selectEventsHandler.handleWidget();
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return addHandler(handler, SelectEvent.getType());
	}
	
	public void select()
	{
		SelectEvent.fire(this);
	}
	
	public void setStopPropagationTouchEvents(boolean stopPropagationTouchEvents)
	{
		selectEventsHandler.setStopPropagationTouchEvents(stopPropagationTouchEvents);
	}

	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		selectEventsHandler.setPreventDefaultTouchEvents(preventDefaultTouchEvents);
	}
	
	public void setAllowPropagationToNonSelectableWidgets(boolean allowPropagationToNonSelectableWidgets) 
	{
		selectEventsHandler.setAllowPropagationToNonSelectableWidgets(allowPropagationToNonSelectableWidgets);
	}
	
	public SelectEventsHandler getSelectEventsHandler()
	{
		return selectEventsHandler;
	}
	
	protected HandlerRegistration addTouchEndHandler(TouchEndHandler handler)
	{
		return addDomHandler(handler, TouchEndEvent.getType());
	}

	protected HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler)
	{
		return addDomHandler(handler, TouchMoveEvent.getType());
	}

	protected HandlerRegistration addTouchStartHandler(TouchStartHandler handler)
	{
		return addDomHandler(handler, TouchStartEvent.getType());
	}
	
	protected HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return addDomHandler(handler, ClickEvent.getType());
	}
}
