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
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PopupData implements Serializable
{
    private static final long serialVersionUID = 317809212002910317L;

    private String styleName;
	private String title;
	private String url;
	private boolean animationEnabled;
	private boolean closeable;
	private String width;
	private String height;

	public PopupData()
	{
	}
	
	public PopupData(String title, String url, String width, String height, String styleName, boolean animationEnabled, boolean closeable)
	{
		this.styleName = styleName;
		this.title = title;
		this.url = url;
		this.width = width;
		this.height = height;
		this.animationEnabled = animationEnabled;
		this.closeable = closeable;
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

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}

	public boolean isCloseable()
	{
		return closeable;
	}

	public void setCloseable(boolean closeable)
	{
		this.closeable = closeable;
	}

	/**
	 * @return the width
	 */
	public String getWidth()
	{
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String width)
	{
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public String getHeight()
	{
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height)
	{
		this.height = height;
	}
}
