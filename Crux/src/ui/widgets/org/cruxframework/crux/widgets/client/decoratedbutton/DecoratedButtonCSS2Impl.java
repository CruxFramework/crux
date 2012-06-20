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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;

/**
 * PushButton, based on a 3 X 1 table, useful to build rounded corners.
 * 
 * @author Gesse S. F. Dafe
 */
public class DecoratedButtonCSS2Impl extends FocusWidget implements HasText, DecoratedButtonIntf
{
	private DecoratedButtonFace face;
	private boolean allowClick;

	/**
	 * Default constructor
	 */
	public DecoratedButtonCSS2Impl()
	{
		this.face = new DecoratedButtonFace();
		this.setElement(this.face.getElement());
		sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.KEYEVENTS);
		Accessibility.setRole(getElement(), Accessibility.ROLE_BUTTON);
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	public void setText(String text)
	{
		this.face.setText(text);
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
	public String getText()
	{
		return this.face.getText();
	}

	@Override
	public void onBrowserEvent(Event event)
	{
		if (!isEnabled())
		{
			return;
		}

		int type = DOM.eventGetType(event);
		
		if(type == Event.ONCLICK)
		{
			if(!this.allowClick)
			{
				event.stopPropagation();
				return;
			}
		}
		else if(type == Event.ONMOUSEUP)
		{
			this.face.removeStyleDependentName("down");
			onClick();
		}		
		else if (type == Event.ONMOUSEDOWN)
		{
			this.face.addStyleDependentName("down");
			this.setFocus(true);
		}
		else if (type == Event.ONMOUSEOUT)
		{
			this.face.removeStyleDependentName("down");
		}
		else if (wasClickByKeyEvent(event))
		{
			onClick();
		}

		super.onBrowserEvent(event);
	}

	/**
	 * Fires the click event
	 */
	protected void onClick()
	{
		this.allowClick = true;

		NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false);
		getElement().dispatchEvent(evt);

		this.allowClick = false;
	}

	/**
	 * @return true if the click was done by using the ENTER or SPACE keys
	 */
	private boolean wasClickByKeyEvent(Event event)
	{
		int eventType = DOM.eventGetType(event);

		if ((event.getTypeInt() & Event.KEYEVENTS) != 0)
		{
			char keyCode = (char) DOM.eventGetKeyCode(event);

			return (eventType == Event.ONKEYUP && keyCode == ' ') || (eventType == Event.ONKEYPRESS && (keyCode == '\n' || keyCode == '\r'));
		}

		return false;
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		if(enabled)
		{
			this.face.removeStyleDependentName("disabled");
		}
		else
		{
			this.face.addStyleDependentName("disabled");
		}
		
		super.setEnabled(enabled);
	}

	/*
	 * @see org.cruxframework.crux.widgets.client.decoratedbutton.DecoratedButtonIntf#getSpecificStyleName(java.lang.String)
	 */
	public String getSpecificStyleName(String style)
	{
		return style;
	}
}