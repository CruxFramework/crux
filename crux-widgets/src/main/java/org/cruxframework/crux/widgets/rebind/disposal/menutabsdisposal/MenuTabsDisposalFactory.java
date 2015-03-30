/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.rebind.disposal.menutabsdisposal;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.widgets.client.disposal.menutabsdisposal.MenuTabsDisposal;

/**
 * 
 * @author Gesse Dafe
 *
 */
@DeclarativeFactory(library="widgets", id="menuTabsDisposal", targetWidget=MenuTabsDisposal.class, 
description="A component to dispose elements on the screen using a menu and a tabPanel.")

@TagChildren({
	@TagChild(MenuTabsDisposalFactory.MenuChildTagProcessor.class)
})
public class MenuTabsDisposalFactory extends WidgetCreator<WidgetCreatorContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(HeaderProcessor.class),
		@TagChild(MenuItemProcessor.class),
		@TagChild(MenuSectionProcessor.class)
	})		
	public static class MenuChildTagProcessor extends ChoiceChildProcessor<WidgetCreatorContext>
	{
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="1", tagName="header")
	@TagChildren({
		@TagChild(HeaderContentProcessor.class),
	})
	public static class HeaderProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="1", type=AnyWidget.class)
	public static class HeaderContentProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
		{
			String headerContentWidgetId = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			out.println(context.getWidget() + ".setHeaderContent(" + headerContentWidgetId + ");");
		}
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="menuEntry")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
		@TagAttributeDeclaration(value="targetView", required=true)
	})
	public static class MenuItemProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String label = context.readChildProperty("label");
			String targetView = context.readChildProperty("targetView");
			out.print(context.getWidget() + ".addMenuEntry(" + getWidgetCreator().getDeclaredMessage(label) + ", " + EscapeUtils.quote(targetView));
			out.println(");");
		}
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="menuSection")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
		@TagAttributeDeclaration(value="additionalStyleName", required=false)
	})
	public static class MenuSectionProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String label = context.readChildProperty("label");
			String style = context.readChildProperty("additionalStyleName");
			out.print(context.getWidget() + ".addMenuSection(" + getWidgetCreator().getDeclaredMessage(label) + ", " + EscapeUtils.quote(style, false) + ");");
		}
	}
	
    @Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }

    @Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}
}
