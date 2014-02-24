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
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasScrollHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Represents a ScrollPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="scrollPanel", library="gwt", targetWidget=ScrollPanel.class)
@TagAttributes({
	@TagAttribute(value="alwaysShowScrollBars", type=Boolean.class),
	@TagAttribute(value="verticalScrollPosition", type=ScrollPanelFactory.VerticalScrollPosition.class, processor=ScrollPanelFactory.VerticalScrollPositionAttributeParser.class),
	@TagAttribute(value="horizontalScrollPosition", type=ScrollPanelFactory.HorizontalScrollPosition.class, processor=ScrollPanelFactory.HorizontalScrollPositionAttributeParser.class),
	@TagAttribute(value="ensureVisible", processor=ScrollPanelFactory.EnsureVisibleAttributeParser.class)
})
@TagChildren({
	@TagChild(ScrollPanelFactory.WidgetContentProcessor.class)
})
public class ScrollPanelFactory extends PanelFactory<WidgetCreatorContext> 
       implements HasScrollHandlersFactory<WidgetCreatorContext>
{
	public static enum VerticalScrollPosition{top,bottom};
	public static enum HorizontalScrollPosition{left,right};

    @TagConstraints(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}		
	
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VerticalScrollPositionAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public VerticalScrollPositionAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue) 
		{
			String widget = context.getWidget();
			if (StringUtils.unsafeEquals("top", propertyValue))
			{
				out.println(widget+".scrollToTop();");
			}
			else 
			{
				out.println(widget+".scrollToBottom();");
			}
		}
	}

	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class HorizontalScrollPositionAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public HorizontalScrollPositionAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue) 
		{
			String widget = context.getWidget();
			if (StringUtils.unsafeEquals("left", propertyValue))
			{
				out.println(widget+".scrollToLeft();");
			}
			else
			{
				out.println(widget+".scrollToRight();");
			}
		}
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class EnsureVisibleAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public EnsureVisibleAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }
		public void processAttribute(SourcePrinter out, final WidgetCreatorContext context, final String propertyValue) 
		{
			String widget = context.getWidget();
			String widgetClassName = getWidgetCreator().getWidgetClassName();
			printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
					
			String targetWidget = ViewFactoryCreator.createVariableName("c");
			
			printlnPostProcessing("Widget "+targetWidget+" = "+getViewVariable()+".getWidget("+EscapeUtils.quote(propertyValue)+");");
			printlnPostProcessing("if ("+targetWidget+" == null){");
			String widgetId = context.getWidgetId();
			printlnPostProcessing("throw new NullPointerException("+EscapeUtils.quote("Error in ScrollPanel ["+widgetId+"]." +
					"Error ensuring visibility for component ["+propertyValue+"].")+");");
			printlnPostProcessing("}");
			printlnPostProcessing(widget+".ensureVisible("+targetWidget+");");
		}
	}	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
