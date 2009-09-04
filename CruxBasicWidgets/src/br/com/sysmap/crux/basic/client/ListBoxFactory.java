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
import br.com.sysmap.crux.core.client.screen.factory.HasChangeHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Represents a List Box component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="listBox", library="bas")
public class ListBoxFactory extends FocusWidgetFactory<ListBox> 
       implements HasChangeHandlersFactory<ListBox>
{
	@Override
	public ListBox instantiateWidget(Element element, String widgetId) 
	{
		String multiple = element.getAttribute("_multiple");
		if (multiple != null && multiple.trim().length() > 0)
		{
			return new ListBox(Boolean.parseBoolean(multiple));

		}
		return new ListBox();
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleItemCount", type=Integer.class),
		@TagAttribute(value="multiple", type=Boolean.class, autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<ListBox> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}

	@Override
	public void processChildren(WidgetFactoryContext<ListBox> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		ListBox widget = context.getWidget();
		
		NodeList<Element> itensCandidates = element.getElementsByTagName("span");
		for (int i=0; i<itensCandidates.getLength(); i++)
		{
			if (isValidItem(itensCandidates.getItem(i)))
			{
				processItemDeclaration(widget, itensCandidates.getItem(i), i);
			}
		}
	}

	/**
	 * Verify if the span tag found is a valid item declaration for listBoxes
	 * @param element
	 * @return
	 */
	protected boolean isValidItem(Element element)
	{
		if ("span".equalsIgnoreCase(element.getTagName()))
		{
			String item = element.getAttribute("_item");
			if (item != null && item.length() > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Process Item declaration for ListBoxFactory
	 * @param element
	 */
	protected void processItemDeclaration(ListBox widget, Element element, int index)
	{
		String item = element.getAttribute("_item");
		String value = element.getAttribute("_value");
		if (value == null || value.length() == 0)
		{
			value = item;
		}
		widget.insertItem(item, value, index);

		String selected = element.getAttribute("_selected");
		if (selected != null && selected.trim().length() > 0)
		{
			widget.setItemSelected(index, Boolean.parseBoolean(selected));
		}
	}
}
