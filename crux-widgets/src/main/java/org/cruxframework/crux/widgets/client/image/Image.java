/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.image;

import org.cruxframework.crux.widgets.client.event.HasSelectHandlers;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasErrorHandlers;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class Image extends Composite implements HasSelectHandlers, HasLoadHandlers, HasErrorHandlers, HasEnabled
{
	static abstract class ImageImpl extends com.google.gwt.user.client.ui.Image implements HasSelectHandlers, HasEnabled
	{
		protected boolean preventDefaultTouchEvents = false;
		private boolean enabled = true;
		protected abstract void select();

		public HandlerRegistration addSelectHandler(SelectHandler handler)
		{
			return addHandler(handler, SelectEvent.getType());
		}

		protected void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
		{
			this.preventDefaultTouchEvents = preventDefaultTouchEvents;
		}
		
		@Override
		public boolean isEnabled()
		{
		    return enabled;
		}
		
		@Override
		public void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
		}
	}

	static class NoTouchImpl extends ImageImpl 
	{
		public NoTouchImpl()
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
					if (isEnabled())
					{
						SelectEvent selectEvent = SelectEvent.fire(NoTouchImpl.this);
						if (selectEvent.isCanceled())
						{
							event.preventDefault();
						}
					}
				}
			});
		}

		@Override
		protected void select()
		{
			SelectEvent.fire(NoTouchImpl.this);
		}
	}

	static class TouchImpl extends NoTouchImpl implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		private static final int TAP_EVENT_THRESHOLD = 5;
		private int startX;
		private int startY;
		private HandlerRegistration touchMoveHandler;
		private HandlerRegistration touchEndHandler;

		public TouchImpl()
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
				SelectEvent.fire(this);
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

	private ImageImpl impl;

	public Image()
	{
		impl = GWT.create(ImageImpl.class);
		initWidget(impl.asWidget());
		setStyleName("crux-Image");
		getStyleElement().getStyle().setDisplay(Display.BLOCK);
	}

	public Image(ImageResource resource)
	{
		this();
		setResource(resource);
	}

	public Image(String url)
	{
		this();
		setUrl(url);
	}

	public Image(SafeUri url)
	{
		this();
		setUrl(url);
	}

	public Image(final String url, final int left, final int top, final int width, final int height)
	{
		this();
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				setUrlAndVisibleRect(url, left, top, width, height);
			}
		});
	}

	public Image(final SafeUri url, final int left, final int top, final int width, final int height)
	{
		this();
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				setUrlAndVisibleRect(url, left, top, width, height);
			}
		});
	}

	public int getWidth()
	{
		return impl.getWidth();
	}

	public String getUrl()
	{
		return impl.getUrl();
	}

	public int getOriginTop()
	{
		return impl.getOriginTop();
	}

	public int getOriginLeft()
	{
		return impl.getOriginLeft();
	}

	public int getHeight()
	{
		return impl.getHeight();
	}

	public String getAltText()
	{
		return impl.getAltText();
	}

	public void setAltText(String altText)
	{
		impl.setAltText(altText);
	}

	public void setResource(ImageResource resource)
	{
		impl.setResource(resource);
	}

	public void setUrl(SafeUri url)
	{
		impl.setUrl(url);
	}

	public void setUrl(String url)
	{
		impl.setUrl(url);
	}

	public void setTitle(String title)
	{
		impl.setTitle(title);
	}

	public void setVisible(boolean visible)
	{
		impl.setVisible(visible);
	}
	
	public void setStyleName(String style)
	{
		impl.setStyleName(style);
	}
	
	public void setUrlAndVisibleRect(final SafeUri url, final int left, final int top, final int width, final int height)
	{
		new GWTFixImage() {
			@Override
			public void callHowToImplementInnerSetVisibleRect() {
				impl.setUrlAndVisibleRect(url, left, top, width, height);		
			}
		};
	}

	public void setUrlAndVisibleRect(final String url, final int left, final int top, final int width, final int height)
	{
		new GWTFixImage() {
			@Override
			public void callHowToImplementInnerSetVisibleRect() {
				impl.setUrlAndVisibleRect(url, left, top, width, height);		
			}
		};
	}

	public void setVisibleRect(final int left, final int top, final int width, final int height)
	{
		new GWTFixImage() {
			@Override
			public void callHowToImplementInnerSetVisibleRect() {
				impl.setVisibleRect(left, top, width, height);		
			}
		};
	}

	/*
	   note1: Issue submitted to GWT:
	   Issue 8325: 	GWT Image: using 'VisibleRect' method erases previous stylesheets declared
	   
	   Until version 2.5.1 GWT doesn't copy any old 
	   properties to the new element created inside a Clipped 
	   or NotClipped constructor. So they are lost at some point.
	   
	   In order to copy the properties I had to use something like: 
	   impl.setProperty(...);
	   for each one of them because the following method 
	   (intended to copy all properties) doesn't work here:
	   getElement().setAttribute("style", currentStyle + oldStyle);
	 */
	private abstract class GWTFixImage
	{
		public GWTFixImage() {
			setVisibleRect();
		}
		
		public abstract void callHowToImplementInnerSetVisibleRect();
		
		public void setVisibleRect()
		{
			boolean oldStyleHasDisplayNone = impl.getElement().getAttribute("style").contains("display: none");
			String title = impl.getElement().getTitle();
			String styleName = impl.getStyleName();
			callHowToImplementInnerSetVisibleRect();
			impl.setVisible(!oldStyleHasDisplayNone);
			impl.setTitle(title);
			impl.setStyleName(styleName);
		}
	}
	
	@Override
	public boolean isEnabled()
	{
		return impl.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		if (enabled != isEnabled())
		{
			impl.setEnabled(enabled);
			if (enabled)
			{
				removeStyleDependentName("disabled");
			}
			else
			{
				addStyleDependentName("disabled");
			}
		}
	}
	
	@Override
	public HandlerRegistration addErrorHandler(ErrorHandler handler)
	{
		return impl.addErrorHandler(handler);
	}

	@Override
	public HandlerRegistration addLoadHandler(LoadHandler handler)
	{
		return impl.addLoadHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return impl.addSelectHandler(handler);
	}

	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		impl.setPreventDefaultTouchEvents(preventDefaultTouchEvents);
	}
}
