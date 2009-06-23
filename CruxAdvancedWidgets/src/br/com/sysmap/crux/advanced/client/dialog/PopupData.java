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

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

import br.com.sysmap.crux.core.client.component.ModuleShareable;
import br.com.sysmap.crux.core.rebind.screen.moduleshareable.annotation.ModuleShareableName;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@ModuleShareableName("popupData")	
public class PopupData implements ModuleShareable
{
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

	public Object deserialize(String serializedData)
	{
		if (serializedData != null && serializedData.length() > 0)
		{
			Document root = XMLParser.parse(serializedData);
			Element data = root.getDocumentElement();
			return new PopupData(data.getAttribute("title"), 
								 data.getAttribute("url"), 
								 data.getAttribute("width"),
								 data.getAttribute("height"),
								 data.getAttribute("styleName"), 
								 Boolean.parseBoolean(data.getAttribute("animationEnabled")),
								 Boolean.parseBoolean(data.getAttribute("closeable")));
		}
		return null;
	}
	
	public Object[] newArray(int size)
	{
		return new PopupData[size];
	}

	public String serialize()
	{
		Document document = XMLParser.createDocument();

		Element data = document.createElement("data");
		document.appendChild(data);
		
		if (title != null)
		{
			data.setAttribute("title", title);
		}
		data.setAttribute("url", url);
		data.setAttribute("styleName", styleName);
		data.setAttribute("width", width);
		data.setAttribute("height", height);
		data.setAttribute("animationEnabled", Boolean.toString(animationEnabled));
		data.setAttribute("closeable", Boolean.toString(closeable));
		
		return document.toString();
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
