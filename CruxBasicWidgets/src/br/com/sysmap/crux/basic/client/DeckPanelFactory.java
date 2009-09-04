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
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@DeclarativeFactory(id="deckPanel", library="bas")
public class DeckPanelFactory extends ComplexPanelFactory<DeckPanel>
					implements HasAnimationFactory<DeckPanel>
{
	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(DeckPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
		parent.add(child);
	}

	@Override
	public DeckPanel instantiateWidget(Element element, String widgetId) 
	{
		return new DeckPanel();
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="visibleWidget", type=Integer.class, autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<DeckPanel> context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		DeckPanel widget = context.getWidget();
		
		String visibleWidget = element.getAttribute("_visibleWidget");
		if (visibleWidget != null && visibleWidget.length() > 0)
		{
			widget.showWidget(Integer.parseInt(visibleWidget));
		}
	}	
}