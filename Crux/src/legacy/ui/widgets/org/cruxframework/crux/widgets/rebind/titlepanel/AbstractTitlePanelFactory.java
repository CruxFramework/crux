/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.rebind.titlepanel;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.CellPanelContext;
import org.cruxframework.crux.widgets.rebind.decoratedpanel.AbstractDecoratedPanelFactory;


/**
 * Factory for Title Panel widget
 * @author Gesse S. F. Dafe
 */
@Deprecated
@Legacy
public abstract class AbstractTitlePanelFactory extends AbstractDecoratedPanelFactory
{
	@TagConstraints(tagName="title", minOccurs="0")
	@TagChildren({
		@TagChild(TitleChildrenProcessor.class)
	})
	public static class TitleProcessor extends WidgetChildProcessor<CellPanelContext> {}

	@TagChildren({
		@TagChild(TitlePanelHTMLChildProcessor.class),
		@TagChild(TitlePanelTextChildProcessor.class),
		@TagChild(TitlePanelWidgetChildProcessor.class)
	})
	public static class TitleChildrenProcessor extends ChoiceChildProcessor<CellPanelContext> {}
	
	@TagConstraints(tagName="html", type=HTMLTag.class)
	public static abstract class HTMLChildProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String innerHtml = getWidgetCreator().ensureHtmlChild(context.getChildElement(),true, context.getWidgetId());
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setTitleHtml("+innerHtml+");");
		}
	}

	@TagConstraints(tagName="text", type=String.class)
	public static abstract class TextChildProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String innerText = getWidgetCreator().ensureTextChild(context.getChildElement(),true, context.getWidgetId(), false);
			String i18nText = getWidgetCreator().getDeclaredMessage(innerText);
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setTitleText("+i18nText+");");
		}
	}
		
	@TagConstraints(tagName="widget")
	@TagChildren({
		@TagChild(TitleWidgetTitleProcessor.class)
	})
	public static class TitlePanelWidgetChildProcessor extends WidgetChildProcessor<CellPanelContext> {}

	@TagConstraints(widgetProperty="titleWidget")
	public static class TitleWidgetTitleProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}
	
	
	@TagConstraints(tagName="body", minOccurs="0")
	@TagChildren({
		@TagChild(BodyChildrenProcessor.class)
	})
	public static class BodyProcessor extends WidgetChildProcessor<CellPanelContext> {}

	@TagChildren({
		@TagChild(TitlePanelBodyHTMLChildProcessor.class),
		@TagChild(TitlePanelBodyTextChildProcessor.class),
		@TagChild(TitlePanelBodyWidgetProcessor.class)
	})
	public static class BodyChildrenProcessor extends ChoiceChildProcessor<CellPanelContext> {}	
	
	@TagConstraints(tagName="html", type=HTMLTag.class)
	public static abstract class BodyHTMLChildProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String innerHtml = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId());
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setContentHtml("+innerHtml+");");
		}
	}

	@TagConstraints(tagName="text", type=String.class)
	public static abstract class BodyTextChildProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String innerText = getWidgetCreator().ensureTextChild(context.getChildElement(), true, context.getWidgetId(), false);
			String i18nText = getWidgetCreator().getDeclaredMessage(innerText);
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setContentText("+i18nText+");");
		}
	}
	
	@TagConstraints(tagName="widget")
	@TagChildren({
		@TagChild(BodyWidgetContentProcessor.class)
	})
	public static class TitlePanelBodyWidgetProcessor extends WidgetChildProcessor<CellPanelContext> {}
	
	@TagConstraints(widgetProperty="contentWidget")
	public static class BodyWidgetContentProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}

	public static class TitlePanelHTMLChildProcessor extends HTMLChildProcessor{}
	public static class TitlePanelTextChildProcessor extends TextChildProcessor{}	
	public static class TitlePanelBodyHTMLChildProcessor extends BodyHTMLChildProcessor{}
	public static class TitlePanelBodyTextChildProcessor extends BodyTextChildProcessor {}
	
}