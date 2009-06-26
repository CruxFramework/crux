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
package br.com.sysmap.crux.advanced.client.decoratedbutton;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;

/**
 * PushButton, based on a 3 X 1 table, useful to build rounded corners.
 * 
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
public class DecoratedButton extends FocusWidget implements HasText
{
	public static final String DEFAULT_STYLE_NAME = "crux-DecoratedButton";
	
	private FocusPanel widget;
	private DecoratedButtonFace face;
	private boolean allowClick;

	public DecoratedButton()
	{
		this.face = new DecoratedButtonFace();
		this.widget = new FocusPanel(face);
		this.widget.getElement().setPropertyString("display", "inline");
		this.setElement(widget.getElement());
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
	 * 
	 */
	protected void onClick()
	{
		this.allowClick = true;

		NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false);
		getElement().dispatchEvent(evt);

		this.allowClick = false;
	}

	/**
	 * @param event
	 * @return
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
}