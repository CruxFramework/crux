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

import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TextBoxBase;


/**
 * Base class for text box based widgets
 * @author Thiago Bustamante
 *
 */
public abstract class TextBoxBaseFactory<T extends TextBoxBase> extends FocusWidgetFactory<T>
{	
	@Override
	protected void processAttributes(T widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		String value = element.getAttribute("_value");
		if (value != null && value.length() > 0)
		{
			widget.setValue(value);
		}
		String readOnly = element.getAttribute("_readOnly");
		if (readOnly != null && readOnly.length() > 0)
		{
			widget.setReadOnly(Boolean.parseBoolean(readOnly));
		}
		String textAlignment = element.getAttribute("_textAlignment");
		if (textAlignment != null)
		{
			if ("center".equalsIgnoreCase(textAlignment))
			{
				widget.setTextAlignment(com.google.gwt.user.client.ui.TextBoxBase.ALIGN_CENTER);
			}
			else if ("justify".equalsIgnoreCase(textAlignment))
			{
				widget.setTextAlignment(com.google.gwt.user.client.ui.TextBoxBase.ALIGN_JUSTIFY);
			} 
			else if ("left".equalsIgnoreCase(textAlignment))
			{
				widget.setTextAlignment(com.google.gwt.user.client.ui.TextBoxBase.ALIGN_LEFT);
			} 
			else if ("right".equalsIgnoreCase(textAlignment))
			{
				widget.setTextAlignment(com.google.gwt.user.client.ui.TextBoxBase.ALIGN_RIGHT);
			} 
		}
	}
	
	@Override
	protected void processEvents(T widget, Element element, String widgetId) throws InterfaceConfigException
	{
		ChangeEvtBind.bindValueEvent(element, widget);
		super.processEvents(widget, element, widgetId);
	}	
}
