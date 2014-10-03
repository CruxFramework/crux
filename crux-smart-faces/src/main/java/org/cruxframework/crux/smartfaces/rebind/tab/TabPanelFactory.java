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
package org.cruxframework.crux.smartfaces.rebind.tab;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasBeforeSelectionHandlersFactory;
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
import org.cruxframework.crux.gwt.rebind.CompositeFactory;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.smartfaces.client.tab.TabPanel;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.json.JSONObject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;


class TabPanelContext extends WidgetCreatorContext
{
	public JSONObject tabElement;
	public boolean isHTMLTitle;
	public boolean isWidgetTitle;
	public String title;
	public String titleWidget;
	public boolean titleWidgetPartialSupport;
	public String titleWidgetClassType;
	
	public void clearAttributes()
    {
	    isHTMLTitle = false;
	    isWidgetTitle = false;
	    title = null;
	    titleWidget = null;
	    tabElement = null;
    }
}

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tabPanel", library=Constants.LIBRARY_NAME, targetWidget=TabPanel.class)
@TagAttributes({
	@TagAttribute(value="visibleTab", type=Integer.class, processor=TabPanelFactory.VisibleTabAttributeParser.class)
})
@TagChildren({
	@TagChild(TabPanelFactory.TabProcessor.class)
})	
public class TabPanelFactory extends PanelFactory<TabPanelContext> 
									implements HasAnimationFactory<TabPanelContext>, 
									HasBeforeSelectionHandlersFactory<TabPanelContext>
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser extends AttributeProcessor<TabPanelContext>
	{
		public VisibleTabAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, TabPanelContext context, final String propertyValue)
        {
			String widget = context.getWidget();
			String widgetClassName = getWidgetCreator().getWidgetClassName();
			printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
			printlnPostProcessing(widget+".selectTab("+String.valueOf(Integer.parseInt(propertyValue) - 1)+");");
        }
	}	
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="tabEnabled", type=Boolean.class, defaultValue="true"),
		@TagAttributeDeclaration(value="tabWordWrap", type=Boolean.class, defaultValue="true")
	})
	@TagChildren({
		@TagChild(TabTitleProcessor.class), 
		@TagChild(TabWidgetProcessor.class)
	})	
	public static class TabProcessor extends WidgetChildProcessor<TabPanelContext>
	{
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException
		{
			context.tabElement = context.getChildElement();
		}
	}
	
	@TagConstraints(minOccurs="0")
	@TagChildren({
		@TagChild(TextTabProcessor.class),
		@TagChild(HTMLTabProcessor.class),
		@TagChild(WidgetTitleTabProcessor.class)
	})		
	public static class TabTitleProcessor extends ChoiceChildProcessor<TabPanelContext> {}
	
	@TagConstraints(tagName="tabText", type=String.class)
	public static class TextTabProcessor extends WidgetChildProcessor<TabPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().getDeclaredMessage(getWidgetCreator().
					ensureTextChild(context.getChildElement(), true, context.getWidgetId(), false));
			context.isHTMLTitle = false;
		}
	}
	
	@TagConstraints(tagName="tabHtml", type=HTMLTag.class)
	public static class HTMLTabProcessor extends WidgetChildProcessor<TabPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().ensureHtmlChild(context.getChildElement(), true, context.getWidgetId());
			context.isHTMLTitle = true;
		}
	}
	
	@TagConstraints(tagName="tabWidget")
	@TagChildren({
		@TagChild(WidgetTitleProcessor.class)
	})	
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<TabPanelContext> {}

	@TagConstraints(type=AnyWidget.class)
	public static class WidgetTitleProcessor extends WidgetChildProcessor<TabPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException
		{
			context.isWidgetTitle = true;
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
	public static class TabWidgetProcessor extends WidgetChildProcessor<TabPanelContext> {}

	@TagConstraints(type=AnyWidget.class)
	public static class WidgetContentProcessor extends WidgetChildProcessor<TabPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			String rootWidget = context.getWidget();
			
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			if (context.titleWidget != null)
			{
				if (context.titleWidgetPartialSupport)
				{
					out.println("if ("+context.titleWidgetClassType+".isSupported()){");
				}
				if(context.isWidgetTitle)
				{
					out.println(rootWidget+".add("+widget+", "+context.titleWidget+");");
				} 
				else
				{
					out.println(rootWidget+".add("+widget+", "+EscapeUtils.quote(context.titleWidget)+");");
				}
				if (context.titleWidgetPartialSupport)
				{
					out.println("}");
				}
			}
			else if (context.isHTMLTitle)
			{
				String safeHtmlStr = "new "+SafeHtmlBuilder.class.getCanonicalName()+"().appendHtmlConstant("+context.title+").toSafeHtml()";
				out.println(rootWidget+".add("+widget+", "+safeHtmlStr+");");
			}
			else
			{
				out.println(rootWidget+".add("+widget+", "+context.title+");");
			}
			updateTabState(out, context);
			if (childPartialSupport)
			{
				out.println("}");
			}
		}
		
		private void updateTabState(SourcePrinter out, TabPanelContext context)
		{
			String rootWidget = context.getWidget();
			String enabled = context.tabElement.optString("tabEnabled");
			if (enabled != null && enabled.length() >0)
			{
				out.println(rootWidget+".setTabEnabled("+rootWidget+".getTabCount()-1, "+Boolean.parseBoolean(enabled)+");");
			}

			String wordWrap = context.tabElement.optString("wordWrap");
			if (wordWrap != null && wordWrap.length() >0)
			{
				out.println(rootWidget+".setWordWrap("+rootWidget+".getTabCount()-1, "+Boolean.parseBoolean(wordWrap)+");");
			}
			
			context.clearAttributes();
		}	
	}
	
	@Override
	public TabPanelContext instantiateContext()
	{
	    return new TabPanelContext();
	}
}
