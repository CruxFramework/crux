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

import br.com.sysmap.crux.basic.client.TextBoxFactory;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.ScreenFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class MaskedTextBoxFactory extends TextBoxFactory
{
	@Override
	protected void processAttributes(TextBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);

		MaskedTextBox maskedTextBox = (MaskedTextBox)widget;
		
		String formatter = element.getAttribute("_formatter");
		if (formatter != null && formatter.length() > 0)
		{
			maskedTextBox.setFormatter(ScreenFactory.getInstance().getClientFormatter(formatter));
		}
	}

	@Override
	protected MaskedTextBox instantiateWidget(Element element, String widgetId) 
	{
		return new MaskedTextBox();
	}

}
