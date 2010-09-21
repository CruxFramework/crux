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
package br.com.sysmap.crux.widgets.client.maskedlabel;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasWordWrapFactory;
import br.com.sysmap.crux.gwt.client.align.AlignmentAttributeParser;
import br.com.sysmap.crux.gwt.client.align.HorizontalAlignment;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="maskedLabel", library="widgets")
public class MaskedLabelFactory extends WidgetFactory<MaskedLabel> 
				implements HasDirectionFactory<MaskedLabel>, HasClickHandlersFactory<MaskedLabel>, HasAllMouseHandlersFactory<MaskedLabel>, 
				           HasWordWrapFactory<MaskedLabel>
{
	@Override
	public MaskedLabel instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String formatter = getProperty(element,"formatter");
		if (formatter != null && formatter.length() > 0)
		{
			Formatter fmt = Screen.getFormatter(formatter);
			if (fmt == null)
			{
				throw new InterfaceConfigException(WidgetMsgFactory.getMessages().maskedLabelFormatterNotFound(formatter));
			}
			return new MaskedLabel(fmt);
		}
		throw new InterfaceConfigException(WidgetMsgFactory.getMessages().maskedLabelFormatterRequired());	
	}

	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="formatter", required=true),
		@TagAttributeDeclaration("text")
	})
	public void processAttributes(WidgetFactoryContext<MaskedLabel> context) throws InterfaceConfigException
	{
		MaskedLabel widget = context.getWidget();

		super.processAttributes(context);

		String text = context.readWidgetProperty("text");
		if (text != null && text.length() > 0)
		{
			widget.setUnformattedValue(widget.getFormatter().unformat(text));
		}
		
		String horizontalAlignment = context.readWidgetProperty("horizontalAlignment");
		if (horizontalAlignment != null && horizontalAlignment.length() > 0)
		{
			widget.setHorizontalAlignment(AlignmentAttributeParser.getHorizontalAlignment(horizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
		}
	}
}
