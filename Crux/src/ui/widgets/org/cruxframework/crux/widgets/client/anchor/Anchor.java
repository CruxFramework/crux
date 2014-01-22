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
package org.cruxframework.crux.widgets.client.anchor;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.event.HasSelectHandlers;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;

/**
 * A cross devdice anchor, that use touch events on touch enabled devices to implement Google Fast Buttons
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Anchor extends Composite implements HasSelectHandlers, HasHTML, HasSafeHtml, HasAllFocusHandlers, HasEnabled
{
	private AnchorImpl impl;

	static abstract class AnchorImpl extends com.google.gwt.user.client.ui.Anchor implements HasSelectHandlers
	{
		protected boolean preventDefaultTouchEvents = false;
		
		@Override
		public HandlerRegistration addSelectHandler(SelectHandler handler)
		{
			return addHandler(handler, SelectEvent.getType());
		}
		
		protected void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
		{
			this.preventDefaultTouchEvents = preventDefaultTouchEvents;
		}

		protected SelectEvent select()
		{
			if (getHandlerCount(SelectEvent.getType()) > 0)
			{
				return SelectEvent.fire(this);
			}
			else if (!StringUtils.isEmpty(getHref()))
			{
				if (!StringUtils.isEmpty(getTarget()))
				{
					Window.open(getHref(), getTarget(), null);
				}
				else
				{
					Window.Location.assign(getHref());
				}
			}
			return null;
		}
	}

	/**
	 * Implementation for non touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class AnchorNoTouchImpl extends AnchorImpl
	{
		public AnchorNoTouchImpl()
		{
			addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					SelectEvent selectEvent = select();
					if (selectEvent!= null && selectEvent.isCanceled())
					{
						event.preventDefault();
					}
				}
			});
		}
	}

	/**
	 * Implementation for touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class AnchorTouchImpl extends AnchorImpl implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		private static final int TAP_EVENT_THRESHOLD = 5;
		private int startX;
		private int startY;
		private HandlerRegistration touchMoveHandler;
		private HandlerRegistration touchEndHandler;

		public AnchorTouchImpl()
		{
			addTouchStartHandler(this);
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
				select();
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

	public Anchor()
	{
		impl = GWT.create(AnchorImpl.class);
		initWidget(impl);
		setStyleName("crux-Anchor");
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
	
	public void setHref(String url)
	{
		impl.setHref(url);
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
	
	public void setTarget(String target)
	{
		impl.setTarget(target);
	}
	
	public String getTarget()
	{
		return impl.getTarget();
	}
}
