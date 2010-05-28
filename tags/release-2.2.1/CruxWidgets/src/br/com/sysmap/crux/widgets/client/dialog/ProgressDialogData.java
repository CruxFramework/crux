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

import br.com.sysmap.crux.core.client.screen.CruxSerializable;
import br.com.sysmap.crux.core.rebind.screen.serializable.annotation.SerializableName;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé
 */
@SerializableName("progressDialogData")	
public class ProgressDialogData implements CruxSerializable
{
	private String styleName;
	private String message;
	private boolean animationEnabled;

	public ProgressDialogData()
	{
	}
	
	public ProgressDialogData(String message, String styleName, boolean animationEnabled)
	{
		this.styleName = styleName;
		this.message = message;
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

	public Object deserialize(String serializedData)
	{
		if (serializedData != null && serializedData.length() > 0)
		{
			Document root = XMLParser.parse(serializedData);
			Element data = root.getDocumentElement();
			return new ProgressDialogData(
				data.getAttribute("message"), 
				data.getAttribute("styleName"), 
				Boolean.parseBoolean(data.getAttribute("animationEnabled"))
			);
		}
		return null;
	}
	
	public Object[] newArray(int size)
	{
		return new ProgressDialogData[size];
	}

	public String serialize()
	{
		Document document = XMLParser.createDocument();

		Element data = document.createElement("data");
		document.appendChild(data);
		
		if (message != null)
		{
			data.setAttribute("message", message);
		}
		if (styleName != null)
		{
			data.setAttribute("styleName", styleName);
		}

		data.setAttribute("animationEnabled", Boolean.toString(animationEnabled));
		
		return document.toString();
	}
}
