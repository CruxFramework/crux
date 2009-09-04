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

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyDownEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyPressEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyUpEvtBind;
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TabBar.Tab;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public abstract class AbstractTabPanelFactory<T extends TabPanel> extends CompositeFactory<T> 
       implements HasWidgetsFactory<T>, HasAnimationFactory<T>, 
                  HasBeforeSelectionHandlersFactory<T>, HasSelectionHandlersFactory<T>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleTab", type=Integer.class, autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);

		Element element = context.getElement();
		final T widget = context.getWidget();
		
		final String visibleTab = element.getAttribute("_visibleTab");
		if (visibleTab != null && visibleTab.length() > 0)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					widget.selectTab(Integer.parseInt(visibleTab));
				}
			});
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(T parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
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
				Element tabTextSpan = ensureFirstChildSpan(childElementParent, true); 
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
			int tabCount = parent.getTabBar().getTabCount();
			if (enabled != null && enabled.length() >0)
			{
				parent.getTabBar().setTabEnabled(tabCount-1, Boolean.parseBoolean(enabled));
			}

			Tab currentTab = parent.getTabBar().getTab(tabCount-1);
			
			String wordWrap = childElementParent.getAttribute("_wordWrap");
			if (wordWrap != null && wordWrap.trim().length() > 0)
			{
				currentTab.setWordWrap(Boolean.parseBoolean(wordWrap));
			}

			new ClickEvtBind().bindEvent(childElementParent, currentTab);
			new KeyUpEvtBind().bindEvent(childElementParent, currentTab);
			new KeyPressEvtBind().bindEvent(childElementParent, currentTab);
			new KeyDownEvtBind().bindEvent(childElementParent, currentTab);
		}
	}
}
