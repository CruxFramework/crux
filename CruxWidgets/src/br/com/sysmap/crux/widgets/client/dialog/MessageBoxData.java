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

import java.io.Serializable;

/**
 * A simple DTO to transport the message box attributes across frames
 * @author Gesse S. F. Dafe
 */
public class MessageBoxData implements Serializable
{
    private static final long serialVersionUID = 6094423029319432651L;

    private String styleName;
	private String title;
	private String message;
	private boolean animationEnabled;

	/**
	 * Default constructor
	 */
	public MessageBoxData()
	{
	}
	
	/**
	 * Full constructor
	 * @param title the text to be displayed as the caption of the message box 
	 * @param message the text to be displayed in the body of the message box
	 * @param styleName the name of the CSS class to be applied in the message box element 
	 * @param animationEnabled true to enable animations while showing or hiding the message box
	 */
	public MessageBoxData(String title, String message, String styleName, boolean animationEnabled)
	{
		this.styleName = styleName;
		this.title = title;
		this.message = message;
		this.animationEnabled = animationEnabled;
	}

	/**
	 * @return the styleName
	 */
	public String getStyleName()
	{
		return styleName;
	}

	/**
	 * @param styleName the styleName to set
	 */
	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @return the animationEnabled
	 */
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	/**
	 * @param animationEnabled the animationEnabled to set
	 */
	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}	
}