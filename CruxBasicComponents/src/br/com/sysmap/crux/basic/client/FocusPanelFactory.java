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

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.FocusEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Represents a FocusPanelFactory
 * @author Thiago Bustamante
 */
public class FocusPanelFactory extends SimplePanelFactory
{
	@Override
	protected void processEvents(SimplePanel widget, Element element, String widgetId) throws InterfaceConfigException 
	{
		super.processEvents(widget, element, widgetId);
		FocusPanel focusPanel = (FocusPanel) widget; 
		
		FocusEvtBind.bindEvents(element, focusPanel);
		ClickEvtBind.bindEvent(element, focusPanel);
		KeyEvtBind.bindEvents(element, focusPanel);
		MouseEvtBind.bindEvents(element, focusPanel);
		
	}
	
	@Override
	protected void processAttributes(SimplePanel widget, Element element, String widgetId) throws InterfaceConfigException 
	{
		super.processAttributes(widget, element, widgetId);
		FocusPanel focusPanel = (FocusPanel) widget; 

		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.length() > 0)
		{
			focusPanel.setTabIndex(Integer.parseInt(tabIndex));
		}
		
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.length() == 1)
		{
			focusPanel.setAccessKey(accessKey.charAt(0));
		}
		
		String focus = element.getAttribute("_focus");
		if (focus != null && focus.trim().length() > 0)
		{
			focusPanel.setFocus(Boolean.parseBoolean(focus));
		}
	}
	
	@Override
	protected FocusPanel instantiateWidget(Element element, String widgetId) 
	{
		return new FocusPanel();
	}

}
