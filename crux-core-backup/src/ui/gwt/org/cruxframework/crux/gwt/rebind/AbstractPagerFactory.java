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
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;

import com.google.gwt.view.client.HasRows;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="display", processor=AbstractPagerFactory.DisplayAttributeProcessor.class, required=true),
	@TagAttribute(value="rangeLimited", type=Boolean.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="pageSize", type=Integer.class)
})
public abstract class AbstractPagerFactory extends WidgetCreator<WidgetCreatorContext>  
{
	public static class DisplayAttributeProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public DisplayAttributeProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			String widget = context.getWidget();

			String widgetClassName = getWidgetCreator().getWidgetClassName();
			printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
	        printlnPostProcessing(widget+".setDisplay(("+HasRows.class.getCanonicalName()+")"+getViewVariable()+".getWidget("+EscapeUtils.quote(attributeValue)+"));");
	        String pageSize = context.readChildProperty("pageSize");
	        if (!StringUtils.isEmpty(pageSize))
	        {
	        	printlnPostProcessing(widget+".setPageSize("+Integer.parseInt(pageSize)+");");
	        }
	        String page = context.readChildProperty("page");
	        if (!StringUtils.isEmpty(page))
	        {
	        	printlnPostProcessing(widget+".setPage("+Integer.parseInt(page)+");");
	        }
	        String pageStart = context.readChildProperty("pageStart");
	        if (!StringUtils.isEmpty(pageStart))
	        {
	        	printlnPostProcessing(widget+".setPageStart("+Integer.parseInt(pageStart)+");");
	        }    
        }
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}

