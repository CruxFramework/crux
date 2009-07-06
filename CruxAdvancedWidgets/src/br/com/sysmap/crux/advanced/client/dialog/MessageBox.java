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

import br.com.sysmap.crux.advanced.client.event.dialog.HasOkHandlers;
import br.com.sysmap.crux.advanced.client.event.dialog.OkEvent;
import br.com.sysmap.crux.advanced.client.event.dialog.OkHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.Widget;


/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class MessageBox extends Widget implements HasOkHandlers, HasAnimation
{
	public static final String DEFAULT_STYLE_NAME = "crux-MessageBox" ;
	private static MessageBoxController messageBoxController = null;
	private String title;
	private String message;
	private String styleName;
	private boolean animationEnabled;
	protected static MessageBox messageBox;
	
	/**
	 * 
	 */
	public MessageBox()
	{
		setElement(DOM.createSpan());
	}

	/**
	 * 
	 */
	public HandlerRegistration addOkHandler(OkHandler handler)
	{
		return addHandler(handler, OkEvent.getType());
	}	
	
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getStyleName()
	{
		return styleName;
	}

	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}
	
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}	

	/**
	 * 
	 */
	public void show()
	{
		if (messageBoxController == null)
		{
			messageBoxController = new MessageBoxController(); 
		}
		messageBox = this;
		messageBoxController.showMessageBox(new MessageBoxData(title, message, styleName != null ? styleName : DEFAULT_STYLE_NAME, animationEnabled));
	}
	
	/**
	 * 
	 * @param title
	 * @param message
	 * @param okCall
	 * @param cancelCall
	 */
	public static void show(String title, String message, OkHandler okHandler)
	{
		show(title, message, okHandler, DEFAULT_STYLE_NAME, false);
	}
	
	/**
	 * 
	 * @param title
	 * @param message
	 * @param okCall
	 * @param cancelCall
	 * @param styleName
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
