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
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class DateBoxFactory extends CompositeFactory<DateBox> 
{
	@Override
	protected void processAttributes(final DateBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.length() > 0)
		{
			widget.setTabIndex(Integer.parseInt(tabIndex));
		}
		String enabled = element.getAttribute("_enabled");
		if (enabled != null && enabled.length() > 0)
		{
			widget.setEnabled(Boolean.parseBoolean(enabled));
		}
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.length() == 1)
		{
			widget.setAccessKey(accessKey.charAt(0));
		}
	}
	
	@Override
	protected DateBox instantiateWidget(Element element, String widgetId) 
	{
		return new DateBox();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.component.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(TabPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
		Element childElementParent = childElement.getParentElement();
		if (parentElement.getId().equals(childElementParent.getId()))
		{
			parent.add(child);
		}
		else 
		{
			String tabText = childElementParent.getAttribute("_widgetTitle");
			// tab caption as text
			if (tabText != null && tabText.trim().length() > 0)
			{
				parent.add(child, tabText);
			}
			// tab caption as html
			else
			{
				Element tabTextSpan = childElementParent.getFirstChildElement();
				if (tabTextSpan != null && !isWidget(tabTextSpan))
				{
					parent.add(child, tabTextSpan.getInnerHTML(), true);
				}
				else
				{
					Widget titleWidget = createChildWidget(tabTextSpan, tabTextSpan.getId());
					parent.add(child, titleWidget);
				}
			}
			String enabled = childElementParent.getAttribute("_enabled");
			if (enabled != null && enabled.length() >0)
			{
				int tabCount = parent.getTabBar().getTabCount();
				parent.getTabBar().setTabEnabled(tabCount-1, Boolean.parseBoolean(enabled));
			}
		}
	}
	
	@Override
	protected void processEvents(DateBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		ChangeEvtBind.bindValueEvent(element, widget);
	}
}
