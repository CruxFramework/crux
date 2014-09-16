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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDirectionEstimatorFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDirectionFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasNameFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasTextFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;


/**
 * Base class for text box based widget factories
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="readOnly", type=Boolean.class),
	@TagAttribute(value="alignment", type=ValueBoxBaseFactory.TextAlign.class, processor=ValueBoxBaseFactory.TextAlignmentProcessor.class)
})
public abstract class ValueBoxBaseFactory extends FocusWidgetFactory<WidgetCreatorContext>
                implements HasChangeHandlersFactory<WidgetCreatorContext>, HasNameFactory<WidgetCreatorContext>, 
                           HasTextFactory<WidgetCreatorContext>, HasDirectionEstimatorFactory<WidgetCreatorContext>, 
                           HasDirectionFactory<WidgetCreatorContext>
{	
	public static enum TextAlign{center, justify, left, right}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class TextAlignmentProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public TextAlignmentProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
		{
			TextAlign align = TextAlign.valueOf(attributeValue);
			String textAlignClassName = TextAlignment.class.getCanonicalName();
			switch (align) {
				case center: out.println(context.getWidget() + ".setAlignment(" + textAlignClassName + ".CENTER);");
				break;
				case justify: out.println(context.getWidget() + ".setAlignment(" + textAlignClassName + ".JUSTIFY);");
				break;
				case left: out.println(context.getWidget() + ".setAlignment(" + textAlignClassName + ".LEFT);");
				break;
				case right: out.println(context.getWidget() + ".setAlignment(" + textAlignClassName + ".RIGHT);");
				break;
			}
		}
	}
	
	/**
	 * @author Bruno M. Rafael (bruno@triggolabs.com)
	 *
	 */
	public static class PlaceHolderProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public PlaceHolderProcessor(WidgetCreator<?> widgetCreator) 
		{
			super(widgetCreator);
		}

		@Override
		public void processAttribute(SourcePrinter out,
				WidgetCreatorContext context, String attributeValue) 
		{
			out.println(context.getWidget() + ".getElement().setPropertyString(\"placeholder\", " + getWidgetCreator().getDeclaredMessage(attributeValue) + ");");
		}
	}
	
	@Override
	public WidgetCreatorContext instantiateContext()
	{
	    return new WidgetCreatorContext();
	}
}
