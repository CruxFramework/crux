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

import java.util.List;

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyDownEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyPressEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyUpEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.factory.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TabBar.Tab;

/**
 * Factory for TabBar widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public abstract class AbstractTabBarFactory<T extends TabBar> extends CompositeFactory<T> 
       implements HasBeforeSelectionHandlersFactory<T>, HasSelectionHandlersFactory<T>
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
	
	@Override
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException 
	{
		Element element = context.getElement();
		final T widget = context.getWidget();

		List<Element> tabs = ensureChildrenSpans(element, true);
		for (Element tabElement : tabs)
		{
			processTab(widget, tabElement);
		}
	}		
		
	protected void processTab(T widget, Element element) throws InterfaceConfigException
	{
		String tabText = element.getAttribute("_widgetTitle");
		// tab caption as text
		if (tabText != null && tabText.trim().length() > 0)
		{
			widget.addTab(tabText);
		}
		// tab caption as html
		else
		{
			Element tabTextSpan = ensureFirstChildSpan(element, true); 
			if (tabTextSpan != null && !isWidget(tabTextSpan))
			{
				widget.addTab(tabTextSpan.getInnerHTML(), true);
			}
			else
			{
				Widget titleWidget = createChildWidget(tabTextSpan, tabTextSpan.getId());
				widget.addTab(titleWidget);
			}
		}
		String enabled = element.getAttribute("_enabled");
		int tabCount = widget.getTabCount();
		if (enabled != null && enabled.length() >0)
		{
			widget.setTabEnabled(tabCount-1, Boolean.parseBoolean(enabled));
		}

		Tab currentTab = widget.getTab(tabCount-1);

		String wordWrap = element.getAttribute("_wordWrap");
		if (wordWrap != null && wordWrap.trim().length() > 0)
		{
			currentTab.setWordWrap(Boolean.parseBoolean(wordWrap));
		}

		new ClickEvtBind().bindEvent(element, currentTab);
		new KeyUpEvtBind().bindEvent(element, currentTab);
		new KeyPressEvtBind().bindEvent(element, currentTab);
		new KeyDownEvtBind().bindEvent(element, currentTab);
	}
}
