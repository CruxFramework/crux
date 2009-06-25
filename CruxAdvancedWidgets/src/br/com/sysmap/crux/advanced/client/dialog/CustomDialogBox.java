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
package br.com.sysmap.crux.advanced.client.dialog;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;

/**
 * TODO - Gessé - Comment this
 * 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CustomDialogBox extends CustomPopupPanel implements HasHTML, HasText
{
	private boolean hideContentOnDragging;

	public interface Caption extends HasAllMouseHandlers
	{
	}

	private class CaptionImpl extends HTML implements Caption
	{
		@Override
		public void onDetach()
		{
			super.onDetach();
		}

		@Override
		public void onAttach()
		{
			super.onAttach();
		}
	}

	private class MouseHandler implements MouseDownHandler, MouseUpHandler, MouseMoveHandler
	{

		public void onMouseDown(MouseDownEvent event)
		{
			beginDragging(event);
		}

		public void onMouseMove(MouseMoveEvent event)
		{
			continueDragging(event);
		}

		public void onMouseUp(MouseUpEvent event)
		{
			endDragging(event);
		}
	}

	private static final String DEFAULT_STYLENAME = "crux-CustomDialogBox";

	private CaptionImpl caption = new CaptionImpl();
	private boolean dragging;
	private int dragStartX, dragStartY;
	private int windowWidth;
	private int clientLeft;
	private int clientTop;

	private HandlerRegistration resizeHandlerRegistration;

	public CustomDialogBox()
	{
		this(false);
	}

	public CustomDialogBox(boolean autoHide)
	{
		this(autoHide, true, true);
	}

	public CustomDialogBox(boolean autoHide, boolean hideContentOnDragging, boolean modal)
	{
		super(autoHide, modal);

		this.hideContentOnDragging = hideContentOnDragging;

		Element td = getTopCenterCell();
		td.setInnerText("");
		td.appendChild(caption.getElement());
		adopt(caption);
		caption.setStyleName("Caption");
		setStyleName(DEFAULT_STYLENAME);

		windowWidth = Window.getClientWidth();
		clientLeft = Document.get().getBodyOffsetLeft();
		clientTop = Document.get().getBodyOffsetTop();

		MouseHandler mouseHandler = new MouseHandler();
		addDomHandler(mouseHandler, MouseDownEvent.getType());
		addDomHandler(mouseHandler, MouseUpEvent.getType());
		addDomHandler(mouseHandler, MouseMoveEvent.getType());
	}

	/**
	 * Provides access to the dialog's caption.
	 * 
	 * This method is final because the Caption interface will expand. Therefore
	 * it is highly likely that subclasses which implemented this method would
	 * end up breaking.
	 * 
	 * @return the logical caption for this dialog box
	 */
	public final Caption getCaption()
	{
		return caption;
	}

	public String getHTML()
	{
		return caption.getHTML();
	}

	public String getText()
	{
		return caption.getText();
	}

	@Override
	public void hide()
	{
		if (resizeHandlerRegistration != null)
		{
			resizeHandlerRegistration.removeHandler();
			resizeHandlerRegistration = null;
		}
		super.hide();
	}

	@Override
	public void onBrowserEvent(Event event)
	{
		switch (event.getTypeInt())
		{
			case Event.ONMOUSEDOWN:
			case Event.ONMOUSEUP:
			case Event.ONMOUSEMOVE:
			case Event.ONMOUSEOVER:
			case Event.ONMOUSEOUT:
				if (!dragging && !isCaptionEvent(event))
				{
					return;
				}
		}

		super.onBrowserEvent(event);
	}

	/**
	 * @param transparent
	 * @param elements
	 */
	private void makeTransparent(boolean transparent, Element element)
	{
		if(transparent)
		{
			element.getStyle().setProperty("opacity", "0.7");
			element.getStyle().setProperty("filter", "alpha(opacity=70)");
		}
		else
		{
			element.getStyle().setProperty("opacity", "1");
			element.getStyle().setProperty("filter", "alpha(opacity=100)");
		}
	}

	public void setHTML(String html)
	{
		caption.setHTML(html);
	}

	public void setText(String text)
	{
		caption.setText(text);
	}

	@Override
	public void show()
	{
		if (resizeHandlerRegistration == null)
		{
			resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler()
			{
				public void onResize(ResizeEvent event)
				{
					windowWidth = event.getWidth();
				}
			});
		}
		super.show();
	}

	protected void beginDragging(MouseDownEvent event)
	{
		dragging = true;
		DOM.setCapture(getElement());
		dragStartX =  event.getX();
		dragStartY = event.getY();
	}

	protected void continueDragging(MouseMoveEvent event)
	{
		if (dragging)
		{
			int absX = event.getX() + getAbsoluteLeft();
			int absY = event.getY() + getAbsoluteTop();

			if (absX < clientLeft || absX >= windowWidth || absY < clientTop)
			{
				return;
			}

			setPopupPosition(absX - dragStartX, absY - dragStartY);

			if (hideContentOnDragging)
			{
				getContentWidget().setVisible(false);
				makeTransparent(true, getElement());
			}
		}
	}

	@Override
	protected void doAttachChildren()
	{
		super.doAttachChildren();
		caption.onAttach();
	}

	@Override
	protected void doDetachChildren()
	{
		super.doDetachChildren();
		caption.onDetach();
	}

	protected void endDragging(MouseUpEvent event)
	{
		dragging = false;
		DOM.releaseCapture(getElement());

		if (hideContentOnDragging)
		{
			getContentWidget().setVisible(true);
			makeTransparent(false, getElement());
		}
		
	}

	@Override
	protected void onEnsureDebugId(String baseID)
	{
		super.onEnsureDebugId(baseID);
		caption.ensureDebugId(baseID + "-caption");
		ensureDebugId(getMiddleCenterCell(), baseID, "content");
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event)
	{
		NativeEvent nativeEvent = event.getNativeEvent();

		if (!event.isCanceled() && (event.getTypeInt() == Event.ONMOUSEDOWN) && isCaptionEvent(nativeEvent))
		{
			nativeEvent.preventDefault();
		}

		super.onPreviewNativeEvent(event);
	}

	private boolean isCaptionEvent(NativeEvent event)
	{
		EventTarget target = event.getEventTarget();
		if (Element.is(target))
		{
			return getTopCenterCell().getParentElement().isOrHasChild(Element.as(target));
		}
		return false;
	}
}