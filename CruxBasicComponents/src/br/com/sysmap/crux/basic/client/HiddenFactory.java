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

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Hidden;

/**
 * Represents a HiddenFactory component
 * @author Thiago Bustamante
 */
public class HiddenFactory extends WidgetFactory<Hidden> 
{

	@Override
	protected void processAttributes(Hidden widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);

		String name = element.getAttribute("_name");
		if (name != null && name.length() > 0)
		{
			widget.setName(name);
		}
		String value = element.getAttribute("_value");
		if (value != null && value.length() > 0)
		{
			widget.setValue(value);
		}
	}

	@Override
	protected Hidden instantiateWidget(Element element, String widgetId) 
	{
		return new Hidden();
	}
}
