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
package org.cruxframework.crux.widgets.rebind.decoratedpanel;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.CellPanelContext;
import org.cruxframework.crux.widgets.client.decoratedpanel.DecoratedPanel;


/**
 * Factory for Decorated Panel widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="decoratedPanel", library="widgets", targetWidget=DecoratedPanel.class)
@TagChildren({
	@TagChild(DecoratedPanelFactory.ChildrenProcessor.class)
})
public class DecoratedPanelFactory extends AbstractDecoratedPanelFactory
{
	@TagChildren({
		@TagChild(HTMLChildProcessor.class),
		@TagChild(TextChildProcessor.class),
		@TagChild(WidgetProcessor.class)
	})
	public static class ChildrenProcessor extends ChoiceChildProcessor<CellPanelContext> {}
	
	@TagConstraints(tagName="html", type=HTMLTag.class)
	public static class HTMLChildProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setContentHtml("+EscapeUtils.quote(getWidgetCreator().
					ensureHtmlChild(context.getChildElement(), true, context.getWidgetId()))+");");
		}
	}

	@TagConstraints(tagName="text", type=String.class)
	public static class TextChildProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setContentText("+EscapeUtils.quote(getWidgetCreator().
					ensureTextChild(context.getChildElement(), true, context.getWidgetId()))+");");
		}
	}
	
	@TagConstraints(tagName="widget")
	@TagChildren({
		@TagChild(WidgetContentProcessor.class)
	})
	public static class WidgetProcessor extends WidgetChildProcessor<CellPanelContext> {}

	@TagConstraints(widgetProperty="contentWidget")
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}
}