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

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasBeforeSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class TabLayoutPanelContext extends WidgetCreatorContext
{

	public String title;
	public boolean isTitleHTML;
	public String titleWidget;
	public boolean titleWidgetPartialSupport;
	public String titleWidgetClassType;
}

/**
 * Factory for TabLayoutPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tabLayoutPanel", library="gwt", targetWidget=TabLayoutPanel.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="barHeight", type=Integer.class, defaultValue="20"),
	@TagAttributeDeclaration(value="unit", type=Unit.class)
})
@TagAttributes({
	@TagAttribute(value="visibleTab", type=Integer.class, processor=TabLayoutPanelFactory.VisibleTabAttributeParser.class)
})
@TagChildren({
	@TagChild(TabLayoutPanelFactory.TabProcessor.class)
})	
public class TabLayoutPanelFactory extends CompositeFactory<TabLayoutPanelContext> 
       implements HasBeforeSelectionHandlersFactory<TabLayoutPanelContext>, 
                  HasSelectionHandlersFactory<TabLayoutPanelContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		String height = context.readWidgetProperty("barHeight");
		if (StringUtils.isEmpty(height))
		{
			out.println(className + " " + context.getWidget()+" = new "+className+"(20,"+Unit.class.getCanonicalName()+".PX);");
		}
		else
		{
			Unit unit = AbstractLayoutPanelFactory.getUnit(context.readWidgetProperty("unit"));
			out.println(className + " " + context.getWidget()+" = new "+className+"("+Double.parseDouble(height)+","+Unit.class.getCanonicalName()+"."+unit.toString()+");");
		}
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser extends AttributeProcessor<TabLayoutPanelContext>
	{
		public VisibleTabAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, TabLayoutPanelContext context, final String propertyValue)
        {
			String widget = context.getWidget();
			String widgetClassName = getWidgetCreator().getWidgetClassName();
			printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
			printlnPostProcessing(widget+".selectTab("+Integer.parseInt(propertyValue)+");");
        }
	}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	@TagChildren({
		@TagChild(TabTitleProcessor.class), 
		@TagChild(TabWidgetProcessor.class)
	})	
	public static class TabProcessor extends WidgetChildProcessor<TabLayoutPanelContext> {}

	@TagConstraints(minOccurs="0")
	@TagChildren({
		@TagChild(TextTabProcessor.class),
		@TagChild(HTMLTabProcessor.class),
		@TagChild(WidgetTitleTabProcessor.class)
	})		
	public static class TabTitleProcessor extends ChoiceChildProcessor<TabLayoutPanelContext> {}
	
	@TagConstraints(tagName="tabText", type=String.class)
	public static class TextTabProcessor extends WidgetChildProcessor<TabLayoutPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().getDeclaredMessage(getWidgetCreator().
					ensureTextChild(context.getChildElement(), true, context.getWidgetId(), false));
			context.isTitleHTML = false;
		}
	}
	
	@TagConstraints(tagName="tabHtml", type=HTMLTag.class)
	public static class HTMLTabProcessor extends WidgetChildProcessor<TabLayoutPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId());
			context.isTitleHTML = true;
		}
	}
	
	@TagConstraints(tagName="tabWidget")
	@TagChildren({
		@TagChild(WidgetTitleProcessor.class)
	})	
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<TabLayoutPanelContext> {}

	@TagConstraints(type=AnyWidget.class)
	public static class WidgetTitleProcessor extends WidgetChildProcessor<TabLayoutPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException
		{
			context.titleWidget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			context.titleWidgetPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (context.titleWidgetPartialSupport)
			{
				context.titleWidgetClassType = getWidgetCreator().getChildWidgetClassName(context.getChildElement());
			}
		}
	}
	
	@TagConstraints(tagName="panelContent")
	@TagChildren({
		@TagChild(WidgetContentProcessor.class)
	})	
	public static class TabWidgetProcessor extends WidgetChildProcessor<TabLayoutPanelContext> {}

	@TagConstraints(type=AnyWidget.class)
	public static class WidgetContentProcessor extends WidgetChildProcessor<TabLayoutPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			
			String rootWidget = context.getWidget();
			
			if (context.titleWidget != null)
			{
				if (context.titleWidgetPartialSupport)
				{
					out.println("if ("+context.titleWidgetClassType+".isSupported()){");
				}
				out.println(rootWidget+".add("+widget+", "+context.titleWidget+");");
				if (context.titleWidgetPartialSupport)
				{
					out.println("}");
				}
			}
			else
			{
				out.println(rootWidget+".add("+widget+", "+context.title+", "+context.isTitleHTML+");");
			}
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
	}

	@Override
    public TabLayoutPanelContext instantiateContext()
    {
	    return new TabLayoutPanelContext();
    }
}
