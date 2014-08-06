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
package org.cruxframework.crux.widgets.client.button;

import org.cruxframework.crux.widgets.client.event.HasSelectHandlers;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;

/**
 * A cross device button, that use touch events on touch enabled devices to implement Google Fast Buttons
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Button extends Composite implements HasSelectHandlers, HasHTML, HasSafeHtml, HasAllFocusHandlers, HasEnabled
{
	private ButtonImpl impl;

	static abstract class ButtonImpl extends com.google.gwt.user.client.ui.Button implements HasSelectHandlers
	{
		protected boolean preventDefaultTouchEvents = false;
		protected abstract void select();
		
		@Override
		public HandlerRegistration addSelectHandler(SelectHandler handler)
		{
			return addHandler(handler, SelectEvent.getType());
		}
		
		protected void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
		{
			this.preventDefaultTouchEvents = preventDefaultTouchEvents;
		}
	}

	/**
	 * Implementation for non touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class ButtonNoTouchImpl extends ButtonImpl
	{
		public ButtonNoTouchImpl()
		{
			addClickHandler();
		}

		protected void addClickHandler() 
		{
			addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					SelectEvent selectEvent = SelectEvent.fire(ButtonNoTouchImpl.this);
					if (selectEvent.isCanceled())
					{
						event.preventDefault();
					}
					if (selectEvent.isStopped())
					{
						event.stopPropagation();
					}
				}
			});
		}

		@Override
		protected void select()
		{
			click();
		}
	}

	/**
	 * Implementation for touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class ButtonTouchImpl extends ButtonNoTouchImpl implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		private static final int TAP_EVENT_THRESHOLD = 5;
		private int startX;
		private int startY;
		private HandlerRegistration touchMoveHandler;
		private HandlerRegistration touchEndHandler;

		public ButtonTouchImpl()
		{
			if(!GWT.isProdMode())
			{
				addClickHandler();
			}
			addTouchStartHandler(this);
		}

		@Override
		protected void select()
		{
			SelectEvent.fire(this);
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			event.stopPropagation();
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
			event.stopPropagation();
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			Touch touch = event.getTouches().get(0);
			startX = touch.getClientX();
			startY = touch.getClientY();
			touchMoveHandler = addTouchMoveHandler(this);
			touchEndHandler = addTouchEndHandler(this);
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

	public Button()
	{
		impl = GWT.create(ButtonImpl.class);
		initWidget(impl);
		setStyleName("crux-Button");
	}

	public Button(String text, SelectHandler buttonSelectHandler) 
	{
		this();
		impl.setText(text);
		impl.addSelectHandler(buttonSelectHandler);
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return impl.addSelectHandler(handler);
	}

	public void select()
	{
		impl.select();
	}

	@Override
	public String getText()
	{
		return impl.getText();
	}

	@Override
	public void setText(String text)
	{
		impl.setText(text);
	}

	@Override
	public String getHTML()
	{
		return impl.getHTML();
	}

	@Override
	public void setHTML(String html)
	{
		impl.setHTML(html);
	}

	@Override
	public void setHTML(SafeHtml html)
	{
		impl.setHTML(html);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return impl.addFocusHandler(handler);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addBlurHandler(handler);
	}

	@Override
	public boolean isEnabled()
	{
		return impl.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		impl.setEnabled(enabled);
	}
	
	public void setFocus(boolean focused)
	{
		impl.setFocus(focused);
	}
	
	public void setAccessKey(char key)
	{
		impl.setAccessKey(key);
	}
	
	public void setTabIndex(int index)
	{
		impl.setTabIndex(index);
	}
	
	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		impl.setPreventDefaultTouchEvents(preventDefaultTouchEvents);
	}
}
