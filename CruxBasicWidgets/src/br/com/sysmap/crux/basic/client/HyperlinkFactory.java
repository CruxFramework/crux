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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Represents a HyperlinkFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="hyperlink", library="gwt")
public class HyperlinkFactory extends WidgetFactory<Hyperlink>
	   implements HasTextFactory<Hyperlink>, HasClickHandlersFactory<Hyperlink>
{

	@Override
	@TagAttributes({
		@TagAttribute("targetHistoryToken")
	})
	public void processAttributes(WidgetFactoryContext<Hyperlink> context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		Hyperlink widget = context.getWidget();
		
		String innerHtml = element.getInnerHTML();
		String text = element.getAttribute("_text");
		if ((text == null || text.length() ==0) && innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(innerHtml);
		}
	}

	@Override
	public Hyperlink instantiateWidget(Element element, String widgetId) 
	{
		return new Hyperlink();
	}
}
