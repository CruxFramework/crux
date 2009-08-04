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
package br.com.sysmap.crux.advanced.client.maskedtextbox;

import br.com.sysmap.crux.advanced.client.AdvancedWidgetMessages;
import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.FocusEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.HasDirection.Direction;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class MaskedTextBoxFactory extends WidgetFactory<MaskedTextBox>
{
	private AdvancedWidgetMessages messages = GWT.create(AdvancedWidgetMessages.class);
	
	@Override
	protected MaskedTextBox instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String formatter = element.getAttribute("_formatter");
		if (formatter != null && formatter.length() > 0)
		{
			Formatter fmt = Screen.getFormatter(formatter);
			if (fmt == null)
			{
				throw new InterfaceConfigException(messages.maskedTextBoxFormatterNotFound(formatter));
			}
			return new MaskedTextBox(fmt);
		}
		throw new InterfaceConfigException(messages.maskedTextBoxFormatterRequired());
	}

	@Override
	protected void processAttributes(MaskedTextBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);

		String direction = element.getAttribute("_direction");
		if (direction != null && direction.length() > 0)
		{
			widget.setDirection(Direction.valueOf(direction));
		}
		
		String value = element.getAttribute("_value");
		if (value != null && value.length() > 0)
		{
			widget.setUnformattedValue(widget.getFormatter().unformat(value));
		}
		String readOnly = element.getAttribute("_readOnly");
		if (readOnly != null && readOnly.length() > 0)
		{
			widget.setReadOnly(Boolean.parseBoolean(readOnly));
		}
		
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
		
		String focus = element.getAttribute("_focus");
		if (focus != null && focus.trim().length() > 0)
		{
			widget.setFocus(Boolean.parseBoolean(focus));
		}
		
	}
	
	@Override
	protected void processEvents(MaskedTextBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);

		ChangeEvtBind.bindValueEvent(element, widget);
		FocusEvtBind.bindEvents(element, widget);
		ClickEvtBind.bindEvent(element, widget);
		KeyEvtBind.bindEvents(element, widget);
		MouseEvtBind.bindEvents(element, widget);
	}	
	
	
}
