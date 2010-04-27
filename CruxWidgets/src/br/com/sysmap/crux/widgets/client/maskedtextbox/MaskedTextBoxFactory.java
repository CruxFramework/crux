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
package br.com.sysmap.crux.widgets.client.maskedtextbox;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllFocusHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllKeyHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasChangeHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasNameFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasValueChangeHandlersFactory;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@DeclarativeFactory(id="maskedTextBox", library="widgets")
public class MaskedTextBoxFactory extends WidgetFactory<MaskedTextBox> 
       implements HasDirectionFactory<MaskedTextBox>, HasNameFactory<MaskedTextBox>, 
                  HasChangeHandlersFactory<MaskedTextBox>, HasValueChangeHandlersFactory<MaskedTextBox>,
                  HasClickHandlersFactory<MaskedTextBox>, HasAllFocusHandlersFactory<MaskedTextBox>,
                  HasAllKeyHandlersFactory<MaskedTextBox>, HasAllMouseHandlersFactory<MaskedTextBox>
{
	@Override
	public MaskedTextBox instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String formatter = element.getAttribute("_formatter");
		if (formatter != null && formatter.length() > 0)
		{
			Formatter fmt = Screen.getFormatter(formatter);
			if (fmt == null)
			{
				throw new InterfaceConfigException(WidgetMsgFactory.getMessages().maskedTextBoxFormatterNotFound(formatter));
			}
			return new MaskedTextBox(fmt);
		}
		throw new InterfaceConfigException(WidgetMsgFactory.getMessages().maskedTextBoxFormatterRequired());
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="readOnly", type=Boolean.class),
		@TagAttribute(value="tabIndex", type=Integer.class),
		@TagAttribute(value="accessKey", type=Character.class),
		@TagAttribute(value="focus", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="formatter", required=true),
		@TagAttributeDeclaration("value")
	})
	public void processAttributes(WidgetFactoryContext<MaskedTextBox> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		MaskedTextBox widget = context.getWidget();

		super.processAttributes(context);

		String value = element.getAttribute("_value");
		if (value != null && value.length() > 0)
		{
			widget.setUnformattedValue(widget.getFormatter().unformat(value));
		}
	}
}
