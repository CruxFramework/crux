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

import br.com.sysmap.crux.core.client.component.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.event.bind.BeforeSelectionEvtBind;
import br.com.sysmap.crux.core.client.event.bind.SelectionEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class TabPanelFactory extends CompositeFactory<TabPanel> implements HasWidgetsFactory<TabPanel>
{
	@Override
	protected void processAttributes(final TabPanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			widget.setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		final String visibleTab = element.getAttribute("_visibleTab");
		if (visibleTab != null && visibleTab.length() > 0)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				public void onLoad()
				{
					widget.selectTab(Integer.parseInt(visibleTab));
				}
			});
		}
	}
	
	@Override
	protected TabPanel instantiateWidget(Element element, String widgetId) 
	{
		return new TabPanel();
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
		}
	}
	
	@Override
	protected void processEvents(TabPanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		BeforeSelectionEvtBind.bindEvent(element, widget, widgetId);
		SelectionEvtBind.bindEvent(element, widget, widgetId);
	}
}
