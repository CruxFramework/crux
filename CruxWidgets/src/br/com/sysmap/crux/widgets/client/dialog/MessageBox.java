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

import br.com.sysmap.crux.widgets.client.event.HasOkHandlers;
import br.com.sysmap.crux.widgets.client.event.OkEvent;
import br.com.sysmap.crux.widgets.client.event.OkHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.Widget;


/**
 * A simple widget without layout for showing message boxes across frames. 
 * The rendering is made by <code>CruxInternalMessageBoxController</code>, using the attributes contained in this widget.
 * @author Gessé S. F. Dafé
 */
public class MessageBox extends Widget implements HasOkHandlers, HasAnimation
{
	public static final String DEFAULT_STYLE_NAME = "crux-MessageBox" ;
	private static CruxInternalMessageBoxController messageBoxController = null;
	protected static MessageBox messageBox;
	
	private String title;
	private String message;
	private String styleName;
	private boolean animationEnabled;
	
	/**
	 * Constructor 
	 */
	public MessageBox()
	{
		setElement(DOM.createSpan());
	}

	/**
	 * Adds a handler for the OK button click event
	 */
	public HandlerRegistration addOkHandler(OkHandler handler)
	{
		return addHandler(handler, OkEvent.getType());
	}	
	
	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public void setTitle(String title)
	{
		this.title = title;
	}

	
	/**
	 * Gets the message to be displayed to the user
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message to be displayed to the user
	 * @param message
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	public String getStyleName()
	{
		return styleName;
	}

	@Override
	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.HasAnimation#isAnimationEnabled()
	 */
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasAnimation#setAnimationEnabled(boolean)
	 */
	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}	

	/**
	 * Invokes the <code>CruxInternalMessageBoxController</code> to display the message box
	 */
	public void show()
	{
		if (messageBoxController == null)
		{
			messageBoxController = new CruxInternalMessageBoxController(); 
		}
		messageBox = this;
		messageBoxController.showMessageBox(new MessageBoxData(title, message, styleName != null ? styleName : DEFAULT_STYLE_NAME, animationEnabled));
	}
	
	/**
	 * Shows a cross-frame message box
	 * @param title the text to be displayed as the caption of the message box 
	 * @param message the text to be displayed in the body of the message box
	 * @param okHandler a handler for the OK button click event
	 */
	public static void show(String title, String message, OkHandler okHandler)
	{
		show(title, message, okHandler, DEFAULT_STYLE_NAME, false);
	}
	
	/**
	 * Shows a cross-frame message box
	 * @param title the text to be displayed as the caption of the message box 
	 * @param message the text to be displayed in the body of the message box
	 * @param okHandler a handler for the OK button click event
	 * @param styleName the name of the CSS class to be applied in the message box element 
	 * @param animationEnabled true to enable animations while showing or hiding the message box
	 */
	public static void show(String title, String message, OkHandler okHandler, String styleName, boolean animationEnabled)
	{
		MessageBox messageBox = new MessageBox(); 
		messageBox.setTitle(title);
		messageBox.setMessage(message);
		messageBox.setStyleName(styleName);
		messageBox.setAnimationEnabled(animationEnabled);
		if (okHandler != null)
		{
			messageBox.addOkHandler(okHandler);
		}
		messageBox.show();
	}
}