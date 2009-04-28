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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.FocusEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;

import com.google.gwt.dom.client.Element;

/**
 * Represents a FocusPanel
 * @author Thiago Bustamante
 */
public class FocusPanel extends SimplePanel
{
	protected com.google.gwt.user.client.ui.FocusPanel focusPanelWidget;
	protected char accessKey;
	
	public FocusPanel(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.FocusPanel());
	}
	
	protected FocusPanel(String id, com.google.gwt.user.client.ui.FocusPanel widget)
	{
		super(id, widget);
		this.focusPanelWidget = widget;
	}

	public int getTabIndex() 
	{
		return focusPanelWidget.getTabIndex(); 
	}

	public char getAccessKey() 
	{
		return accessKey;
	}

	public void setAccessKey(char key) 
	{
		focusPanelWidget.setAccessKey(key);
	}

	public void setFocus(boolean focused) 
	{
		focusPanelWidget.setFocus(focused);
	}

	public void setTabIndex(int index) 
	{
		focusPanelWidget.setTabIndex(index);
	}

	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		
		FocusEvtBind.bindEvents(element, focusPanelWidget, getId());
		ClickEvtBind.bindEvent(element, focusPanelWidget, getId());
		KeyEvtBind.bindEvents(element, focusPanelWidget, getId());
		MouseEvtBind.bindEvents(element, focusPanelWidget, getId());
		
	}
	
	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);

		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.length() > 0)
		{
			focusPanelWidget.setTabIndex(Integer.parseInt(tabIndex));
		}
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.length() == 1)
		{
			this.accessKey = accessKey.charAt(0);
			focusPanelWidget.setAccessKey(this.accessKey);
		}
	}

}
