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
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import org.cruxframework.crux.core.client.datasource.pager.Pageable;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.PageEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="pageable", required=true, processor=AbstractPagerFactory.PageableAttributeProcessor.class)
})
@TagEvents({
	@TagEvent(PageEvtBind.class)
})
public class AbstractPagerFactory extends WidgetCreator<WidgetCreatorContext> implements HasEnabledFactory<WidgetCreatorContext>
{
	public static class PageableAttributeProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public PageableAttributeProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue)
		{
			String pageableId = context.readWidgetProperty("pageable");

			if(pageableId != null)
			{
				String widget = context.getWidget();
				String widgetClassName = getWidgetCreator().getWidgetClassName();
				printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
				printlnPostProcessing("assert("+getViewVariable()+".getWidget("+EscapeUtils.quote(pageableId)+") != null):"+EscapeUtils.quote("No pageable widget set for the pager ["+context.getWidgetId()+"], on view ["+getWidgetCreator().getView().getId()+"].")+";");
				printlnPostProcessing(widget+".setPageable(("+Pageable.class.getCanonicalName()+"<?>) "+getViewVariable()+".getWidget("+EscapeUtils.quote(pageableId)+"));");
			}
		}
	}	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
