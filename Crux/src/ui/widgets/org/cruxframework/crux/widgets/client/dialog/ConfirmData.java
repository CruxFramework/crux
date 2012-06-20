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
package org.cruxframework.crux.widgets.client.dialog;

import java.io.Serializable;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConfirmData implements Serializable
{
    private static final long serialVersionUID = -2174460247930942106L;

    private String styleName;
	private String title;
	private String message;
	private String okButtonText;
	private String cancelButtonText;
	
	private boolean animationEnabled;

	public ConfirmData()
	{
	}
	
	public ConfirmData(String title, String message, String okButtonText, String cancelButtonText, String styleName, boolean animationEnabled)
	{
		this.styleName = styleName;
		this.title = title;
		this.message = message;
		this.okButtonText = okButtonText;
		this.cancelButtonText = cancelButtonText;
		this.animationEnabled = animationEnabled;
	}

	public String getStyleName()
	{
		return styleName;
	}

	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
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

	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}

	/**
	 * @return the okButtonText
	 */
	public String getOkButtonText()
	{
		return okButtonText;
	}

	/**
	 * @param okButtonText the okButtonText to set
	 */
	public void setOkButtonText(String okButtonText)
	{
		this.okButtonText = okButtonText;
	}

	/**
	 * @return the cancelButtonText
	 */
	public String getCancelButtonText()
	{
		return cancelButtonText;
	}

	/**
	 * @param cancelButtonText the cancelButtonText to set
	 */
	public void setCancelButtonText(String cancelButtonText)
	{
		this.cancelButtonText = cancelButtonText;
	}
}
