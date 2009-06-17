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

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.WidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Stack Menu
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class StackMenuFactory extends WidgetFactory<StackMenu>
{
	@Override
	protected StackMenu instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new StackMenu();
	}
	
	@Override
	protected void processAttributes(StackMenu widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		List<Element> items = ensureChildrenSpans(element, true);
		for (Element child : items)
		{
			StackMenuItem item = (StackMenuItem) createChildWidget(child, child.getId());
			widget.add(item);
		}
	}
}