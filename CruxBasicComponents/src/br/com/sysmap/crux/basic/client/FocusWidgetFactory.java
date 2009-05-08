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
import br.com.sysmap.crux.core.client.component.WidgetFactory;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.FocusEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * This is the base factory class for widgets that can receive focus. 
 * 
 * @author Thiago Bustamante
 *
 */
public abstract class FocusWidgetFactory <T extends FocusWidget> extends WidgetFactory<T> 
{
	/**
	 * Process widget attributes
	 * @throws InterfaceConfigException 
	 * @see #WidgetFactory.processAttributes
	 */
	@Override
	protected void processAttributes(T widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
				
		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.length() > 0)
		{
			widget.setTabIndex(Integer.parseInt(tabIndex));
		}
		String enabled = element.getAttribute("_enabled");
		if (enabled != null && enabled.length() > 0)
		{
			widget.setEnabled(Boolean.parseBoolean(enabled));
		}
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.length() == 1)
		{
			widget.setAccessKey(accessKey.charAt(0));
		}
	}

	/**
	 * Render component events
	 * @throws InterfaceConfigException 
	 * @see #WidgetFactory.attachEvents
	 */
	@Override
	protected void processEvents(T widget, Element element, String widgetId) throws InterfaceConfigException
	{	 
		super.processEvents(widget, element, widgetId);

		FocusEvtBind.bindEvents(element, widget, widgetId);
		ClickEvtBind.bindEvent(element, widget, widgetId);
		KeyEvtBind.bindEvents(element, widget, widgetId);
		MouseEvtBind.bindEvents(element, widget, widgetId);
	}
}
