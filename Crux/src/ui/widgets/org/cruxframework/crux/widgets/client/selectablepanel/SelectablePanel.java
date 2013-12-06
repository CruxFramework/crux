/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.selectablepanel;

import org.cruxframework.crux.widgets.client.event.HasSelectHandlers;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SelectablePanel extends SimplePanel implements HasSelectHandlers, HasAllFocusHandlers, HasOneWidget
{
	static final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();
	private PanelEventsHandler impl;

	static abstract class PanelEventsHandler
	{
		protected SelectablePanel selectablePanel;
		protected boolean preventDefaultTouchEvents = false;

		protected void setPanel(SelectablePanel selectablePanel)
		{
			this.selectablePanel = selectablePanel;
			
		}
		
		protected void select()
		{
			SelectEvent.fire(selectablePanel);
		}

		protected void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
		{
			this.preventDefaultTouchEvents = preventDefaultTouchEvents;
		}

		protected abstract void handlePanel();
	}

	/**
	 * Implementation for non touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class PanelEventsHandlerNoTouchImpl extends PanelEventsHandler
	{
		public void handlePanel()
		{
			selectablePanel.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					select();
				}
			});
		}
	}

	/**
	 * Implementation for touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class PanelEventsHandlerTouchImpl extends PanelEventsHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		private static final int TAP_EVENT_THRESHOLD = 5;
		private int startX;
		private int startY;
		private HandlerRegistration touchMoveHandler;
		private HandlerRegistration touchEndHandler;

		public void handlePanel()
		{
			selectablePanel.addTouchStartHandler(this);
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			event.stopPropagation();
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			select();
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
			event.stopPropagation();
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			Touch touch = event.getTouches().get(0);
			startX = touch.getClientX();
			startY = touch.getClientY();
			touchMoveHandler = selectablePanel.addTouchMoveHandler(this);
			touchEndHandler = selectablePanel.addTouchEndHandler(this);
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

	public SelectablePanel() 
	{
		super(focusImpl.createFocusable());
		impl = GWT.create(PanelEventsHandler.class);
		impl.setPanel(this);
		impl.handlePanel();
		setStyleName("crux-SelectablePanel");
	}

	public int getTabIndex() 
	{
		return focusImpl.getTabIndex(getElement());
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return addHandler(handler, SelectEvent.getType());
	}
	
	public void select()
	{
		impl.select();
	}

	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return addDomHandler(handler, FocusEvent.getType());
	}

	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addDomHandler(handler, BlurEvent.getType());
	}	

	public void setAccessKey(char key)
	{
		focusImpl.setAccessKey(getElement(), key);
	}

	public void setFocus(boolean focused)
	{
		if (focused)
		{
			focusImpl.focus(getElement());
		}
		else
		{
			focusImpl.blur(getElement());
		}
	}

	public void setTabIndex(int index)
	{
		focusImpl.setTabIndex(getElement(), index);
	}
	
	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		impl.setPreventDefaultTouchEvents(preventDefaultTouchEvents);
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
