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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasBeforeSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.StackLayoutPanel;

class StackLayoutContext extends WidgetCreatorContext
{
	boolean selected = false;
	double headerSize;
	String headerWidget;
	String title;
	boolean isHtmlTitle;
	public boolean headerWidgetPartialSupport;
	public String headerWidgetClassType;
	
	public void clearAttributes() 
	{
		title = null;
		isHtmlTitle = false;
		selected = false;
		headerSize = 0;
		headerWidget = null;
	}
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="stackLayoutPanel", library="gwt", targetWidget=StackLayoutPanel.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="unit", type=Unit.class, defaultValue="PX")
})
@TagChildren({
	@TagChild(StackLayoutPanelFactory.StackItemProcessor.class)
})
public class StackLayoutPanelFactory extends WidgetCreator<StackLayoutContext> 
	   implements HasBeforeSelectionHandlersFactory<StackLayoutContext>, 
	   			  HasSelectionHandlersFactory<StackLayoutContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		Unit unit = AbstractLayoutPanelFactory.getUnit(context.readWidgetProperty("unit"));
		out.println(className + " " + context.getWidget()+" = new "+className+"("+Unit.class.getCanonicalName()+"."+unit.toString()+");");
	}

	@TagConstraints(tagName="item", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(StackHeaderProcessor.class),
		@TagChild(StackContentProcessor.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="selected", type=Boolean.class, defaultValue="false")
	})
	public static class StackItemProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
			context.clearAttributes();
			String selected = context.readChildProperty("selected");
			if (!StringUtils.isEmpty(selected))
			{
				context.selected = Boolean.parseBoolean(selected);
			}
		}
	}

	@TagConstraints(tagName="header")
	@TagChildren({
		@TagChild(StackHeader.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="size", type=Double.class, required=true)
	})
	public static class StackHeaderProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
			context.headerSize = Double.parseDouble(context.readChildProperty("size"));
		}
	}

	@TagChildren({
		@TagChild(StackHeaderWidgetProcessor.class),
		@TagChild(StackHeaderTextProcessor.class),
		@TagChild(StackHeaderHTMLProcessor.class)
	})
	public static class StackHeader extends ChoiceChildProcessor<StackLayoutContext> {}
	
	@TagConstraints(tagName="text", type=String.class)
	public static class StackHeaderTextProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().getDeclaredMessage(getWidgetCreator().
					ensureTextChild(context.getChildElement(), true, context.getWidgetId(), false));
			context.isHtmlTitle = false;
		}
	}
	
	@TagConstraints(tagName="html", type=HTMLTag.class)
	public static class StackHeaderHTMLProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId());
			context.isHtmlTitle = true;
		}
	}

	@TagConstraints(tagName="widget", type=AnyWidget.class)
	public static class StackHeaderWidgetProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException
		{
			String childWidget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			context.headerWidget = childWidget;
			context.headerWidgetPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (context.headerWidgetPartialSupport)
			{
				context.headerWidgetClassType = getWidgetCreator().getChildWidgetClassName(context.getChildElement());
			}
		}
	}

	@TagConstraints(tagName="content")
	@TagChildren({
		@TagChild(StackContentWidgetProcessor.class)
	})
	public static class StackContentProcessor extends WidgetChildProcessor<StackLayoutContext> {}

	@TagConstraints(type=AnyWidget.class)
	public static class StackContentWidgetProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException
		{
			String contentWidget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			String rootWidget = context.getWidget();
			
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			if (context.headerWidget != null)
			{
				if (context.headerWidgetPartialSupport)
				{
					out.println("if ("+context.headerWidgetClassType+".isSupported()){");
				}
				out.println(rootWidget+".add("+contentWidget+", "+context.headerWidget+", "+context.headerSize+");");
				if (context.headerWidgetPartialSupport)
				{
					out.println("}");
				}
			}
			else
			{
				out.println(rootWidget+".add("+contentWidget+", "+context.title+", "+context.isHtmlTitle+", "+context.headerSize+");");
			}

			if (context.selected)
			{
				out.println(rootWidget+".showWidget("+contentWidget+");");
			}
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
	}
	
	@Override
	public StackLayoutContext instantiateContext()
	{
	    return new StackLayoutContext();
	}
}