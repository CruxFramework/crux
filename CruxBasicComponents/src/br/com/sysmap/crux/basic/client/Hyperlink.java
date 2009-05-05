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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.History;

/**
 * Represents a Hyperlink component
 * @author Thiago Bustamante
 */
public class Hyperlink extends Component
{
	protected com.google.gwt.user.client.ui.Hyperlink hyperlinkWidget;

	public Hyperlink(String id) 
	{
		super(id, new com.google.gwt.user.client.ui.Hyperlink());
	}

	protected Hyperlink(String id, com.google.gwt.user.client.ui.Hyperlink widget) 
	{
		super(id, widget);
		this.hyperlinkWidget = widget;
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);

		String targetHistoryToken = element.getAttribute("_targetHistoryToken");
		if (targetHistoryToken != null && targetHistoryToken.length() > 0)
		{
			setTargetHistoryToken(targetHistoryToken);
		}
		
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);

		ClickEvtBind.bindEvent(element, hyperlinkWidget, getId());
	}

	public String getHTML() 
	{
		return hyperlinkWidget.getHTML();
	}

	/**
	 * Gets the history token referenced by this hyperlink.
	 * 
	 * @return the target history token
	 * @see #setTargetHistoryToken
	 */
	public String getTargetHistoryToken() 
	{
		return hyperlinkWidget.getTargetHistoryToken();
	}

	public String getText() 
	{
		return hyperlinkWidget.getText();
	}

	public void setHTML(String html) 
	{
		hyperlinkWidget.setHTML(html);
	}

	/**
	 * Sets the history token referenced by this hyperlink. This is the history
	 * token that will be passed to {@link History#newItem} when this link is
	 * clicked.
	 * 
	 * @param targetHistoryToken the new target history token
	 */
	public void setTargetHistoryToken(String targetHistoryToken) 
	{
		hyperlinkWidget.setTargetHistoryToken(targetHistoryToken);
	}

	public void setText(String text) 
	{
		hyperlinkWidget.setText(text);
	}
}
