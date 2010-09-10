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
package br.com.sysmap.crux.widgets.client.dialog;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.widgets.client.event.openclose.HasBeforeCloseHandlersFactory;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="popup", library="widgets")
public class PopupFactory extends WidgetFactory<Popup> 
       implements HasAnimationFactory<Popup>, HasBeforeCloseHandlersFactory<Popup>
{
	@Override
	public Popup instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new Popup();
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="title", supportsI18N=true),
		@TagAttribute("url"),
		@TagAttribute(value="closeable", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<Popup> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
}
