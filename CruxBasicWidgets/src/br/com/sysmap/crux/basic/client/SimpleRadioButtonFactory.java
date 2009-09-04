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
import br.com.sysmap.crux.core.client.screen.factory.HasNameFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimpleRadioButton;

/**
 * Represents a SimpleRadioButtonFactory component.
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="simpleRadioButton", library="bas")
public class SimpleRadioButtonFactory extends FocusWidgetFactory<SimpleRadioButton> implements HasNameFactory<SimpleRadioButton>
{
	@Override
	@TagAttributes({
		@TagAttribute("checked")
	})
	public void processAttributes(WidgetFactoryContext<SimpleRadioButton> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	public SimpleRadioButton instantiateWidget(Element element, String widgetId) 
	{
		return new SimpleRadioButton(element.getAttribute("_name"));
	}

}
