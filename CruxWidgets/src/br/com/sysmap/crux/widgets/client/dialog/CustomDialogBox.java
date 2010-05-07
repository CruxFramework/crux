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
package br.com.sysmap.crux.widgets.client.dialog;

import br.com.sysmap.crux.core.client.utils.StyleUtils;

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
 * A decorated dialog box
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CustomDialogBox extends CustomPopupPanel implements HasHTML, HasText
{
	private static final String DEFAULT_STYLENAME = "crux-CustomDialogBox";

	private CaptionImpl caption = new CaptionImpl();
	private boolean dragging;
	private int dragStartX, dragStartY;
	private int windowWidth;
	private int clientLeft;
	private int clientTop;
	private boolean hideContentOnDragging;

	private HandlerRegistration resizeHandlerRegistration;

	/**
	 * A caption for the dialog box, able to detect mouse events
	 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
	 */
	public interface Caption extends HasAllMouseHandlers
	{
	}

	/**
	 * A caption for the dialog box, able to detect mouse events
	 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
	 */
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

	/**
	 * A mouse handler for show/hide the contents of the dialog box while it is being dragged
	 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
	 */
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


	/**
	 * Default constructor
	 */
	public CustomDialogBox()
	{
		this(false);
	}

	/**
	 * Constructor
	 * @param autoHide <code>true</code> if the dialog box should be automatically hidden when the user clicks outside of it
	 */
	public CustomDialogBox(boolean autoHide)
	{
		this(autoHide, true, true);
	}

	/**
	 * Full constructor
	 * @param autoHide <code>true</code> if the dialog box should be automatically hidden when the user clicks outside of it
	 * @param hideContentOnDragging <code>true</code> if the content should be hidden while dragging
	 * @param modal <code>true</code> if keyboard or mouse events that do not target the PopupPanel or its children should be ignored
	 */
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
	 * @see com.google.gwt.user.client.ui.HasHTML#getHTML()
	 */
	public String getHTML()
	{
		return caption.getHTML();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
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
	 * Makes an element opaque or transparent
	 * @param apply <code>true</code> for appending a style dependent name
	 * @param element
	 */
	private void applyDraggingStyle(boolean apply, Element element)
	{
		if(apply)
		{
			StyleUtils.addStyleDependentName(element, "dragging");
		}
		else
		{
			StyleUtils.removeStyleDependentName(element, "dragging");
		}
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasHTML#setHTML(java.lang.String)
	 */
	public void setHTML(String html)
	{
		caption.setHTML(html);
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
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

	/**
	 * Called when the dragging starts.
	 * @param event
	 */
	protected void beginDragging(MouseDownEvent event)
	{
		dragging = true;
		DOM.setCapture(getElement());
		dragStartX = event.getX();
		dragStartY = event.getY();
	}

	/**
	 * Called when the mouse moves and the <code>beginDragging</code> has already being called.
	 * Sets the dialog position according to the current mouse position.
	 * @param event
	 */
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
				applyDraggingStyle(true, getElement());
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

	/**
	 * Called when the dragging stops. 
	 * @param event
	 */
	protected void endDragging(MouseUpEvent event)
	{
		dragging = false;
		DOM.releaseCapture(getElement());

		if (hideContentOnDragging)
		{
			getContentWidget().setVisible(true);
			applyDraggingStyle(false, getElement());
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

	/**
	 * @param event
	 * @return true when the event was fired by the caption
	 */
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