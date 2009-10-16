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

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyDownEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyPressEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyUpEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
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
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="visibleTab", type=Integer.class)
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
	@TagChildren({
		@TagChild(TabProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException {}		

	private static void updateTabState(WidgetChildProcessorContext<TabBar> context)
	{
		Element tabElement = (Element) context.getAttribute("tabElement");
		String enabled = tabElement.getAttribute("_enabled");
		int tabCount = context.getRootWidget().getTabCount();
		if (enabled != null && enabled.length() >0)
		{
			context.getRootWidget().setTabEnabled(tabCount-1, Boolean.parseBoolean(enabled));
		}

		Tab currentTab = context.getRootWidget().getTab(tabCount-1);

		String wordWrap = tabElement.getAttribute("_wordWrap");
		if (wordWrap != null && wordWrap.trim().length() > 0)
		{
			currentTab.setWordWrap(Boolean.parseBoolean(wordWrap));
		}

		new ClickEvtBind().bindEvent(tabElement, currentTab);
		new KeyUpEvtBind().bindEvent(tabElement, currentTab);
		new KeyPressEvtBind().bindEvent(tabElement, currentTab);
		new KeyDownEvtBind().bindEvent(tabElement, currentTab);
		
		context.clearAttributes();
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	public static class TabProcessor extends WidgetChildProcessor<TabBar> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="enabled", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="wordWrap", type=Boolean.class, defaultValue="true")
		})
		@TagEventsDeclaration({
			@TagEventDeclaration("onClick"),
			@TagEventDeclaration("onKeyUp"),
			@TagEventDeclaration("onKeyDown"),
			@TagEventDeclaration("onKeyPress")
		})
		@TagChildren({
			@TagChild(TabItemProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException
		{
			context.setAttribute("tabElement", context.getChildElement());
		}
	}
	
	public static class TabItemProcessor extends ChoiceChildProcessor<TabBar> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTabProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException {}
		
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public static class TextTabProcessor extends WidgetChildProcessor<TabBar>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException 
		{
			String title = context.getChildElement().getInnerHTML();
			context.getRootWidget().addTab(title);
			updateTabState(context);
		}
	}
	
	@TagChildAttributes(tagName="html", type=AnyTag.class)//
	public static class HTMLTabProcessor extends WidgetChildProcessor<TabBar>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException 
		{
			String title = context.getChildElement().getInnerHTML();
			context.getRootWidget().addTab(title, true);
			updateTabState(context);
		}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class WidgetTabProcessor extends WidgetChildProcessor<TabBar> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException
		{
		}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetProcessor extends WidgetChildProcessor<TabBar> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<TabBar> context) throws InterfaceConfigException
		{
			Element childElement = context.getChildElement();
			Widget titleWidget = createChildWidget(childElement, childElement.getId());
			context.getRootWidget().addTab(titleWidget);
			updateTabState(context);
		}
	}
}
