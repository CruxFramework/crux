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
package org.cruxframework.crux.cruxfaces.client.dialog;


import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.cruxfaces.client.button.Button;
import org.cruxframework.crux.cruxfaces.client.event.HasOkHandlers;
import org.cruxframework.crux.cruxfaces.client.event.OkEvent;
import org.cruxframework.crux.cruxfaces.client.event.OkHandler;
import org.cruxframework.crux.cruxfaces.client.event.SelectEvent;
import org.cruxframework.crux.cruxfaces.client.event.SelectHandler;
import org.cruxframework.crux.cruxfaces.client.label.Label;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog which shows messages
 * @author Gesse Dafe
 */
public class MessageBox extends DialogBox implements HasOkHandlers
{
	private static final String DEFAULT_STYLE_NAME = "faces-MessageBox";

	public static enum MessageType
	{
		SUCCESS, INFO, WARN, ERROR
	}
	
	private Label msgLabel;
	private Button hideButton;

	/**
	 * Creates a message box
	 */
	public MessageBox()
	{
		this(true, true, true, DEFAULT_STYLE_NAME);
	}
	
	/**
	 * Creates a message box
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be clased by a button on the title bar
	 * @param styleName the dialog base CSS class name
	 */
	public MessageBox(boolean movable, boolean resizable, boolean closable, String styleName)
	{
		super(movable, resizable, closable, styleName);
		setStyleName(styleName);
		Widget content = createMessagePanel();
		setWidget(content);
	}
	
	/**
	 * Shows a message box
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 */
	public static MessageBox show(String message, MessageType type)
	{
		return show(message, type, true, true, true, DEFAULT_STYLE_NAME);
	}
	
	/**
	 * Shows a message box
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be clased by a button on the title bar
	 * @param styleName the dialog base CSS class name
	 */
	public static MessageBox show(String message, MessageType type, boolean movable, boolean resizable, boolean closable, String styleName)
	{
		MessageBox msgBox = new MessageBox(movable, resizable, closable, styleName); 
		msgBox.setMessage(message, type);
		msgBox.show();
		msgBox.center();
		return msgBox;
	}

	/**
	 * Sets the message to be shown
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 */
	public void setMessage(String message, MessageType type)
	{
		this.msgLabel.setText(message);
		for(MessageType anyType : MessageType.values())
		{
			this.removeStyleDependentName(anyType.name().toLowerCase());
		}
		this.addStyleDependentName(type.name().toLowerCase());
	}
	
	/**
	 * Changes the hide button's text
	 * @param btnText
	 */
	public void setButtonText(String btnText)
	{
		hideButton.setText(btnText);
	}

	/**
	 * Adds a handler for the OK button click event
	 */
	public HandlerRegistration addOkHandler(OkHandler handler)
	{
		return addHandler(handler, OkEvent.getType());
	}	

	/**
	 * Creates a progress bar animation to be inserted in progress box
	 * @return
	 */
	private Widget createMessagePanel() 
	{
		FlowPanel contents = new FlowPanel();
		contents.setStyleName("messageBoxContents");
		
		msgLabel = new Label();
		contents.add(msgLabel);
		
		hideButton = new Button();
		hideButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				hide();
				try
				{
					OkEvent.fire(MessageBox.this);
				}
				catch (Throwable e)
				{
					Crux.getErrorHandler().handleError(e);
				}
			}
		});
		hideButton.setText("OK");
		contents.add(hideButton);

		return contents;
	}
}