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
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.RangeChangeEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.RowCountChangeEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="rowCount", type=Integer.class),
	@TagAttribute(value="estimatedRowCount", type=Integer.class, processor=HasRowsFactory.EstimatedRowCountProcessor.class)
})
@TagEvents({
	@TagEvent(RangeChangeEvtBind.class), 
	@TagEvent(RowCountChangeEvtBind.class)
})	
public interface HasRowsFactory<C extends WidgetCreatorContext>
{
	class EstimatedRowCountProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public EstimatedRowCountProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			try
			{
				Integer.parseInt(attributeValue);
			}
			catch (Exception e) 
			{
				throw new CruxGeneratorException("Error parsing attribute value ["+attributeValue+"], of widget ["+context.getWidgetId()+"], on screen ["+getWidgetCreator().getView().getId()+"].",e);
			}
			out.println(context.getWidget()+".setRowCount("+attributeValue+", false);");
        }
		
	}
	
}
