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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for TabLayoutPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tabLayoutPanel", library="gwt")
public class TabLayoutPanelFactory extends CompositeFactory<TabLayoutPanel> 
       implements HasBeforeSelectionHandlersFactory<TabLayoutPanel>, HasSelectionHandlersFactory<TabLayoutPanel>
{
	@Override
	public TabLayoutPanel instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String height = getProperty(element,"barHeight");
		if (StringUtils.isEmpty(height))
		{
			return new TabLayoutPanel(20, Unit.PX);
		}
		else
		{
			return new TabLayoutPanel(Double.parseDouble(height), AbstractLayoutPanelFactory.getUnit(getProperty(element,"unit")));
		}
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="barHeight", type=Integer.class, defaultValue="20"),
		@TagAttributeDeclaration(value="unit", type=Unit.class),
		@TagAttributeDeclaration(value="visibleTab", type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<TabLayoutPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);

		final TabLayoutPanel widget = context.getWidget();
		
		final String visibleTab = context.readWidgetProperty("visibleTab");
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
	public void processChildren(WidgetFactoryContext<TabLayoutPanel> context) throws InterfaceConfigException 
	{
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	public static class TabProcessor extends WidgetChildProcessor<TabLayoutPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(TabTitleProcessor.class), 
			@TagChild(TabWidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabLayoutPanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(minOccurs="0")
	public static class TabTitleProcessor extends ChoiceChildProcessor<TabLayoutPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTitleTabProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<TabLayoutPanel> context) throws InterfaceConfigException {}
		
	}
	
	@TagChildAttributes(tagName="tabText", type=String.class)
	public static class TextTabProcessor extends WidgetChildProcessor<TabLayoutPanel>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<TabLayoutPanel> context) throws InterfaceConfigException 
		{
			String title = ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getInnerHTML());
			context.setAttribute("titleText", title);
		}
	}
	
	@TagChildAttributes(tagName="tabHtml", type=AnyTag.class)
	public static class HTMLTabProcessor extends WidgetChildProcessor<TabLayoutPanel>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<TabLayoutPanel> context) throws InterfaceConfigException 
		{
			String title = context.getChildElement().getInnerHTML();
			context.setAttribute("titleHtml", title);
		}
	}
	
	@TagChildAttributes(tagName="tabWidget")
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<TabLayoutPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetTitleProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabLayoutPanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetTitleProcessor extends WidgetChildProcessor<TabLayoutPanel> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<TabLayoutPanel> context) throws InterfaceConfigException
		{
			Element childElement = context.getChildElement();
			Widget titleWidget = createChildWidget(childElement, childElement.getId());
			context.setAttribute("titleWidget", titleWidget);
		}
	}
	
	@TagChildAttributes(tagName="panelContent")
	public static class TabWidgetProcessor extends WidgetChildProcessor<TabLayoutPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetContentProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<TabLayoutPanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetContentProcessor extends WidgetChildProcessor<TabLayoutPanel> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<TabLayoutPanel> context) throws InterfaceConfigException
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
		}
	}
}
