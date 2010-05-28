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
package br.com.sysmap.crux.widgets.client.decoratedbutton;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;
import br.com.sysmap.crux.gwt.client.FocusWidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Decorated Button widget
 * @author Gessé S. F. Dafé
 */
@DeclarativeFactory(id="decoratedButton", library="widgets")
public class DecoratedButtonFactory extends FocusWidgetFactory<DecoratedButton> implements HasTextFactory<DecoratedButton>
{
	@Override
	public DecoratedButton instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new DecoratedButton();
	}
}