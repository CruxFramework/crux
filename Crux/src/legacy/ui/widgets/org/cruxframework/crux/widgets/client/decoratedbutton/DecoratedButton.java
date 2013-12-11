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
package org.cruxframework.crux.widgets.client.decoratedbutton;

import org.cruxframework.crux.core.client.Legacy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * PushButton, based on a 3 X 1 table, useful to build rounded corners.
 * 
 * @author Gesse S. F. Dafe
 */
@Deprecated
@Legacy
public class DecoratedButton extends Composite implements HasText, 
									HasClickHandlers, HasDoubleClickHandlers, HasEnabled,
									HasAllFocusHandlers, HasAllGestureHandlers, HasAllKeyHandlers, 
									HasAllMouseHandlers, HasAllTouchHandlers, Focusable
{
	public static final String DEFAULT_STYLE_NAME = "crux-DecoratedButton";
	
	private DecoratedButtonIntf impl; 

	/**
	 * Default constructor
	 */
	public DecoratedButton()
	{
		impl = GWT.create(DecoratedButtonIntf.class);
		initWidget((Widget)impl);
		setStyleName(DEFAULT_STYLE_NAME);
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	public void setText(String text)
	{
		this.impl.setText(text);
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
	public String getText()
	{
		return this.impl.getText();
	}
	
	public void setEnabled(boolean enabled)
	{
		this.impl.setEnabled(enabled);
	}

	public int getTabIndex()
    {
	    return this.impl.getTabIndex();
    }

	public void setAccessKey(char key)
    {
		this.impl.setAccessKey(key);
    }

	public void setFocus(boolean focused)
    {
		this.impl.setFocus(focused);
    }

	public void setTabIndex(int index)
    {
		this.impl.setTabIndex(index);
    }

	public HandlerRegistration addFocusHandler(FocusHandler handler)
    {
	    return this.impl.addFocusHandler(handler);
    }

	public HandlerRegistration addBlurHandler(BlurHandler handler)
    {
	    return this.impl.addBlurHandler(handler);
    }

	public HandlerRegistration addGestureStartHandler(GestureStartHandler handler)
    {
	    return this.impl.addGestureStartHandler(handler);
    }

	public HandlerRegistration addGestureChangeHandler(GestureChangeHandler handler)
    {
	    return this.impl.addGestureChangeHandler(handler);
    }

	public HandlerRegistration addGestureEndHandler(GestureEndHandler handler)
    {
	    return this.impl.addGestureEndHandler(handler);
    }

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler)
    {
	    return this.impl.addKeyUpHandler(handler);
    }

	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler)
    {
	    return this.impl.addKeyDownHandler(handler);
    }

	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler)
    {
	    return this.impl.addKeyPressHandler(handler);
    }

	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
    {
	    return this.impl.addMouseDownHandler(handler);
    }

	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
    {
	    return this.impl.addMouseUpHandler(handler);
    }

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
    {
	    return this.impl.addMouseOutHandler(handler);
    }

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
    {
	    return this.impl.addMouseOverHandler(handler);
    }

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
    {
	    return this.impl.addMouseMoveHandler(handler);
    }

	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler)
    {
	    return this.impl.addMouseWheelHandler(handler);
    }

	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler)
    {
	    return this.impl.addTouchStartHandler(handler);
    }

	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler)
    {
	    return this.impl.addTouchMoveHandler(handler);
    }

	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler)
    {
	    return this.impl.addTouchEndHandler(handler);
    }

	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler)
    {
	    return this.impl.addTouchCancelHandler(handler);
    }

	public boolean isEnabled()
    {
	    return this.impl.isEnabled();
    }

	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
    {
	    return this.impl.addDoubleClickHandler(handler);
    }

	public HandlerRegistration addClickHandler(ClickHandler handler)
    {
	    return this.impl.addClickHandler(handler);
    }
	
	@Override
	public void setStyleName(String style)
	{
		super.setStyleName(impl.getSpecificStyleName(style));
	}
}