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

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Represents a List Box component
 * @author Thiago Bustamante
 */
public class ListBoxFactory extends FocusWidgetFactory<ListBox>
{
	@Override
	protected ListBox instantiateWidget(Element element, String widgetId) 
	{
		String multiple = element.getAttribute("_multiple");
		if (multiple != null && multiple.trim().length() > 0)
		{
			return new ListBox(Boolean.parseBoolean(multiple));

		}
		return new ListBox();
	}
	
	@Override
	protected void processAttributes(ListBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		renderListItens(widget, element);
				
		String visibleItemCount = element.getAttribute("_visibleItemCount");
		if (visibleItemCount != null && visibleItemCount.trim().length() > 0)
		{
			widget.setVisibleItemCount(Integer.parseInt(visibleItemCount));

		}

		super.processAttributes(widget, element, widgetId);

	}
	
	/**
	 * Populate the listBox with declared itens
	 * @param element
	 */
	protected void renderListItens(ListBox widget, Element element)
	{
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
		
	@Override
	protected void processEvents(ListBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		ChangeEvtBind.bindEvent(element, widget, widgetId);
	}
}
