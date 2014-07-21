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
package org.cruxframework.crux.smartfaces.client.dialog;


import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.smartfaces.client.WidgetMsgFactory;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.dialog.animation.DialogAnimation;
import org.cruxframework.crux.smartfaces.client.event.HasOkHandlers;
import org.cruxframework.crux.smartfaces.client.event.OkEvent;
import org.cruxframework.crux.smartfaces.client.event.OkHandler;
import org.cruxframework.crux.smartfaces.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.label.HTML;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog which shows messages
 * @author Gesse Dafe
 * @author Thiago da Rosa de Bustamante
 */
public class MessageBox extends AbstractDialogBox implements HasOkHandlers
{
	public static final String DEFAULT_STYLE_NAME = "faces-MessageBox";

	public static enum MessageType
	{
		SUCCESS, INFO, WARN, ERROR
	}
	
	private HTML msgLabel;
	private Button hideButton;

	/**
	 * Creates a message box
	 */
	public MessageBox()
	{
		this(true, true, true, false, DEFAULT_STYLE_NAME);
	}
	
	/**
	 * Creates a message box
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be classed by a button on the title bar
	 * @param modal if true this dialog disables events that does not target the dialog 
	 * @param styleName the dialog base CSS class name
	 */
	public MessageBox(boolean movable, boolean resizable, boolean closable, boolean modal, String styleName)
	{
		super(movable, resizable, closable, modal, styleName);
		setStyleName(styleName);
		Widget content = createMessagePanel();
		super.setWidget(content);
	}
	
	/**
	 * Shows a message box
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 */
	public static MessageBox show(String message, MessageType type)
	{
		return show(null, message, type, true, true, true, false, DEFAULT_STYLE_NAME, null);
	}
	
	/**
	 * Shows a message box
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 * @param animation animation to be used on dialog entrances and exits
	 */
	public static MessageBox show(String message, MessageType type, DialogAnimation animation)
	{
		return show(null, message, type, true, true, true, false, DEFAULT_STYLE_NAME, animation);
	}
	
	/**
	 * Shows a message box
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be clased by a button on the title bar
	 * @param modal if true this dialog disables events that does not target the dialog 
	 * @param styleName the dialog base CSS class name
	 */
	public static MessageBox show(String message, MessageType type, boolean movable, boolean resizable, boolean closable, 
									boolean modal, String styleName)
	{
		return show(null, message, type, movable, resizable, closable, modal, styleName, null);
	}

	/**
	 * Shows a message box
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be clased by a button on the title bar
	 * @param modal if true this dialog disables events that does not target the dialog 
	 * @param styleName the dialog base CSS class name
	 * @param animation animation to be used on dialog entrances and exits
	 */
	public static MessageBox show(String message, MessageType type, boolean movable, boolean resizable, boolean closable, 
									boolean modal, String styleName, DialogAnimation animation)
	{
		return show(null, message, type, movable, resizable, closable, modal, styleName, animation);
	}

	/**
	 * Shows a message box
	 * @param title the dilog box title.
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be clased by a button on the title bar
	 * @param modal if true this dialog disables events that does not target the dialog 
	 * @param styleName the dialog base CSS class name
	 */
	public static MessageBox show(String title, String message, MessageType type, boolean movable, boolean resizable, 
								boolean closable, boolean modal, String styleName)
	{
		return show(title, message, type, movable, resizable, closable, modal, styleName, null);
	}
	
	/**
	 * Shows a message box
	 * @param title the dilog box title.
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be clased by a button on the title bar
	 * @param modal if true this dialog disables events that does not target the dialog 
	 * @param styleName the dialog base CSS class name
	 * @param animation animation to be used on dialog entrances and exits
	 */
	public static MessageBox show(String title, String message, MessageType type, boolean movable, boolean resizable, 
								boolean closable, boolean modal, String styleName, DialogAnimation animation)
	{
		MessageBox msgBox = new MessageBox(movable, resizable, closable, modal, styleName); 
		msgBox.setMessage(message, type);
		if (title != null)
		{
			msgBox.setDialogTitle(title);
		}
		msgBox.setAnimation(animation);
		msgBox.center();
		return msgBox;
	}

	/**
	 * Sets the message to be shown
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 */
	public void setMessage(SafeHtml message, MessageType type)
	{
		this.msgLabel.setHTML(message);
		for(MessageType anyType : MessageType.values())
		{
			this.removeStyleDependentName(anyType.name().toLowerCase());
		}
		this.addStyleDependentName(type.name().toLowerCase());
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
	

	@Override
	public void setWidget(IsWidget w)
	{
		throw new UnsupportedOperationException(WidgetMsgFactory.getMessages().canNotAddWidgetOnThisDialog());
	}
	
	@Override
	public void setWidget(Widget w)
	{
		throw new UnsupportedOperationException(WidgetMsgFactory.getMessages().canNotAddWidgetOnThisDialog());
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
		
		msgLabel = new HTML();
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