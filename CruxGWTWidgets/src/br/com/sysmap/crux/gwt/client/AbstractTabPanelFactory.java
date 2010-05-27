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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyDownEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyPressEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyUpEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TabBar.Tab;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractTabPanelFactory<T extends TabPanel> extends CompositeFactory<T> 
       implements HasAnimationFactory<T>, 
                  HasBeforeSelectionHandlersFactory<T>, HasSelectionHandlersFactory<T>
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

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	public static abstract class AbstractTabProcessor<T extends TabPanel> extends WidgetChildProcessor<T> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="tabEnabled", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="tabWordWrap", type=Boolean.class, defaultValue="true")
		})
		@TagEventsDeclaration({
			@TagEventDeclaration("onClick"),
			@TagEventDeclaration("onKeyUp"),
			@TagEventDeclaration("onKeyDown"),
			@TagEventDeclaration("onKeyPress")
		})
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			context.setAttribute("tabElement", context.getChildElement());
		}
	}

	@TagChildAttributes(tagName="tabText", type=String.class)
	public static abstract class AbstractTextTabProcessor<T extends TabPanel> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			String title = ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getInnerHTML());
			context.setAttribute("titleText", title);
		}
	}
	
	@TagChildAttributes(tagName="tabHtml", type=AnyTag.class)
	public static abstract class AbstractHTMLTabProcessor<T extends TabPanel> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			String title = context.getChildElement().getInnerHTML();
			context.setAttribute("titleHtml", title);
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public static abstract class AbstractWidgetTitleProcessor<T extends TabPanel> extends WidgetChildProcessor<T> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			Element childElement = context.getChildElement();
			Widget titleWidget = createChildWidget(childElement, childElement.getId());
			context.setAttribute("titleWidget", titleWidget);
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public static abstract class AbstractWidgetContentProcessor<T extends TabPanel> extends WidgetChildProcessor<T> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			Element childElement = context.getChildElement();
			Widget widget = createChildWidget(childElement, childElement.getId());
			
			String titleText = (String) context.getAttribute("titleText");
			if (titleText != null)
			{
				context.getRootWidget().add(widget, titleText);
			}
			else
			{
				String titleHtml = (String) context.getAttribute("titleHtml");
				if (titleHtml != null)
				{
					context.getRootWidget().add(widget, titleHtml, true);
				}
				else
				{
					Widget titleWidget = (Widget) context.getAttribute("titleWidget");
					context.getRootWidget().add(widget, titleWidget);
				}
			}
			updateTabState(context);
		}
		
		private void updateTabState(WidgetChildProcessorContext<T> context)
		{
			Element tabElement = (Element) context.getAttribute("tabElement");
			String enabled = tabElement.getAttribute("_enabled");
			int tabCount = context.getRootWidget().getTabBar().getTabCount();
			if (enabled != null && enabled.length() >0)
			{
				context.getRootWidget().getTabBar().setTabEnabled(tabCount-1, Boolean.parseBoolean(enabled));
			}

			Tab currentTab = context.getRootWidget().getTabBar().getTab(tabCount-1);
			
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
	}
}
