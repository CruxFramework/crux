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
package br.com.sysmap.crux.widgets.client.titlepanel;

import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.widgets.client.decoratedpanel.AbstractDecoratedPanelFactory;

/**
 * Factory for Title Panel widget
 * @author Gesse S. F. Dafe
 */
public abstract class AbstractTitlePanelFactory<T extends TitlePanel> extends AbstractDecoratedPanelFactory<T>
{
	@Override
	@TagChildren({
		@TagChild(TitleProcessor.class),
		@TagChild(BodyProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="title", minOccurs="0")
	public static class TitleProcessor extends WidgetChildProcessor<TitlePanel>
	{
		@Override
		@TagChildren({
			@TagChild(TitleChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<TitlePanel> context) throws InterfaceConfigException	{}
	}

	public static class TitleChildrenProcessor extends ChoiceChildProcessor<TitlePanel>
	{
		@Override
		@TagChildren({
			@TagChild(TitlePanelHTMLChildProcessor.class),
			@TagChild(TitlePanelTextChildProcessor.class),
			@TagChild(TitlePanelWidgetChildProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<TitlePanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="html", type=AnyTag.class)
	public static abstract class HTMLChildProcessor<T extends TitlePanel> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			context.getRootWidget().setTitleHtml(context.getChildElement().getInnerHTML());
		}
	}

	@TagChildAttributes(tagName="text", type=String.class)
	public static abstract class TextChildProcessor<T extends TitlePanel> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			String innerText = context.getChildElement().getInnerText();
			String i18nText = ScreenFactory.getInstance().getDeclaredMessage(innerText);
			context.getRootWidget().setTitleText(i18nText);
		}
	}
		
	@TagChildAttributes(tagName="widget")
	public static class TitlePanelWidgetChildProcessor extends WidgetChildProcessor<TitlePanel>
	{
		@Override
		@TagChildren({
			@TagChild(TitleWidgetTitleProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<TitlePanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(widgetProperty="titleWidget")
	public static class TitleWidgetTitleProcessor extends AnyWidgetChildProcessor<TitlePanel> {}
	
	
	@TagChildAttributes(tagName="body", minOccurs="0")
	public static class BodyProcessor extends WidgetChildProcessor<TitlePanel>
	{
		@Override
		@TagChildren({
			@TagChild(BodyChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<TitlePanel> context) throws InterfaceConfigException {}
	}

	public static class BodyChildrenProcessor extends ChoiceChildProcessor<TitlePanel>
	{
		@Override
		@TagChildren({
			@TagChild(TitlePanelBodyHTMLChildProcessor.class),
			@TagChild(TitlePanelBodyTextChildProcessor.class),
			@TagChild(TitlePanelBodyWidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<TitlePanel> context) throws InterfaceConfigException {}
	}	
	
	@TagChildAttributes(tagName="html", type=AnyTag.class)
	public static abstract class BodyHTMLChildProcessor<T extends TitlePanel> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			context.getRootWidget().setContentHtml(context.getChildElement().getInnerHTML());
		}
	}

	@TagChildAttributes(tagName="text", type=String.class)
	public static abstract class BodyTextChildProcessor<T extends TitlePanel> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			String innerText = context.getChildElement().getInnerText();
			String i18nText = ScreenFactory.getInstance().getDeclaredMessage(innerText);
			context.getRootWidget().setContentText(i18nText);
		}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class TitlePanelBodyWidgetProcessor extends WidgetChildProcessor<TitlePanel>
	{
		@Override
		@TagChildren({
			@TagChild(BodyWidgetContentProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<TitlePanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(widgetProperty="contentWidget")
	public static class BodyWidgetContentProcessor extends AnyWidgetChildProcessor<TitlePanel> {}

	public static class TitlePanelHTMLChildProcessor extends HTMLChildProcessor<TitlePanel>{}
	public static class TitlePanelTextChildProcessor extends TextChildProcessor<TitlePanel>{}	
	public static class TitlePanelBodyHTMLChildProcessor extends BodyHTMLChildProcessor<TitlePanel>{}
	public static class TitlePanelBodyTextChildProcessor extends BodyTextChildProcessor<TitlePanel> {}
	
}