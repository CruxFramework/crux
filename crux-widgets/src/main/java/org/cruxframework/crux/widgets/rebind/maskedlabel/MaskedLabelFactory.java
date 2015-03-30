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
package org.cruxframework.crux.widgets.rebind.maskedlabel;

import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.formatter.Formatters;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllMouseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAutoHorizontalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDirectionFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasFormatterFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHorizontalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasWordWrapFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.widgets.client.maskedlabel.MaskedLabel;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="maskedLabel", library="widgets", targetWidget=MaskedLabel.class, 
	description="An information input component that can be associated with a Formatter to guide the user during the filling-out process and ensure the conversion of this information into typed data to be consumed by application")
@TagAttributes({
	@TagAttribute(value="text", processor=MaskedLabelFactory.TextAttributeParser.class)
})
public class MaskedLabelFactory extends WidgetCreator<WidgetCreatorContext> 
				implements HasDirectionFactory<WidgetCreatorContext>, HasClickHandlersFactory<WidgetCreatorContext>, 
						   HasAllMouseHandlersFactory<WidgetCreatorContext>, 
				           HasWordWrapFactory<WidgetCreatorContext>, 
				           HasAutoHorizontalAlignmentFactory<WidgetCreatorContext>, 
				           HasHorizontalAlignmentFactory<WidgetCreatorContext>, 
				           HasFormatterFactory<WidgetCreatorContext>
{
	/**
	 * @param metaElem
	 * @param widgetId
	 * @return
	 * @throws CruxGeneratorException
	 */
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();

		String formatter = context.readWidgetProperty("formatter");
		if (formatter != null && formatter.length() > 0)
		{
			String fmt = createVariableName("fmt");

			out.println(Formatter.class.getCanonicalName()+" "+fmt+" = "+Formatters.getFormatterInstantionCommand(formatter)+";");
			out.println("assert ("+fmt+" != null):"+EscapeUtils.quote("The formatter ["+formatter+"] was not found on this screen.")+";");
			out.println(className + " " + context.getWidget()+" = new "+className+"("+fmt+");");
		}	
		else
		{
			throw new CruxGeneratorException("The attribute formatter is required for MaskedLabel.");	
		}
	}	
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class TextAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public TextAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue)
        {
			String widget = context.getWidget();
			out.println(widget+".setUnformattedValue("+widget+".getFormatter().unformat("+EscapeUtils.quote(propertyValue)+"));");
        }
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
