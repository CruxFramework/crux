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
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Stack Menu
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class StackMenuItemFactory extends WidgetFactory<StackMenuItem>
{
	@Override
	protected StackMenuItem instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String label = element.getAttribute("_label"); 
		return new StackMenuItem(label);
	}
	
	@Override
	protected void processAttributes(StackMenuItem widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String open = element.getAttribute("_open");
		if(open != null && open.trim().length() > 0)
		{
			widget.setOpen(Boolean.parseBoolean(open));
		}
		
		List<Element> items = ensureChildrenSpans(element, true);
		for (Element child : items)
		{
			StackMenuItem item = (StackMenuItem) createChildWidget(child, child.getId());
			widget.add(item);
		}
	}
	
	@Override
	protected void processEvents(StackMenuItem widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		ClickEvtBind.bindEvent(element, widget);
	}
}