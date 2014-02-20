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
package org.cruxframework.crux.widgets.rebind.filter;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllKeyHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasTextFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasValueChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.gwt.rebind.CompositeFactory;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.filter.Filter;
import org.cruxframework.crux.widgets.client.filter.Filterable;


/**
 * Factory for Filter widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="filter", library="widgets", targetWidget=Filter.class)
@TagAttributes({
	@TagAttribute(value="accessKey", type=Character.class),
	@TagAttribute(value="autoSelectEnabled", type=Boolean.class),
	@TagAttribute(value="focus", type=Boolean.class),
	@TagAttribute(value="limit", type=Integer.class),
	@TagAttribute(value="popupStyleName", supportsResources=true),
	@TagAttribute(value="tabIndex", type=Integer.class),
	@TagAttribute("value"),
	@TagAttribute(value="filterable", processor=FilterFactory.FilterableAttributeParser.class, required=true)
})
public class FilterFactory extends CompositeFactory<WidgetCreatorContext> 
	   implements HasAnimationFactory<WidgetCreatorContext>, HasTextFactory<WidgetCreatorContext>, 
	              HasValueChangeHandlersFactory<WidgetCreatorContext>, HasSelectionHandlersFactory<WidgetCreatorContext>,
	              HasAllKeyHandlersFactory<WidgetCreatorContext>
{
	/**
	 * @author Gesse Dafe
	 */
	public static class FilterableAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public FilterableAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		/**
		 * @see org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor#processAttribute(org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter, org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext, java.lang.String)
		 */
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue)
		{
			String widget = context.getWidget();
			String filterableId =context.readWidgetProperty("filterable");
			String filterableWidget = getWidgetCreator().createVariableName("filterable");

			String widgetClassName = getWidgetCreator().getWidgetClassName();
			printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
			printlnPostProcessing("Widget "+filterableWidget+" = null;");
			printlnPostProcessing(filterableWidget+" = "+getViewVariable()+".getWidget("+EscapeUtils.quote(filterableId)+");");
			printlnPostProcessing("if("+filterableWidget+" != null){");
			printlnPostProcessing(widget+".setFilterable(("+Filterable.class.getCanonicalName()+"<?>) "+filterableWidget+");");
			printlnPostProcessing("}");
			printlnPostProcessing("else{");
			printlnPostProcessing("throw new RuntimeException("+WidgetMsgFactory.class.getCanonicalName()+".getMessages().filterableNotFoundWhenInstantiantingFilter("+
					EscapeUtils.quote(filterableId)+"));");
			printlnPostProcessing("}");							
		}		
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}