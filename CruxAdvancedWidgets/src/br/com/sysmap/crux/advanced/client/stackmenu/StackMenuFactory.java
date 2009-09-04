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
package br.com.sysmap.crux.advanced.client.stackmenu;

import java.util.List;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Stack Menu
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
@br.com.sysmap.crux.core.client.declarative.DeclarativeFactory(id="stackMenu", library="adv")
public class StackMenuFactory extends WidgetFactory<StackMenu>
{
	@Override
	public StackMenu instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new StackMenu();
	}
	
	@Override
	public void processChildren(WidgetFactoryContext<StackMenu> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		StackMenu widget = context.getWidget();

		List<Element> items = ensureChildrenSpans(element, true);
		for (Element child : items)
		{
			StackMenuItem item = (StackMenuItem) createChildWidget(child, child.getId());
			widget.add(item);
		}
	}
}