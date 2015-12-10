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
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ExpressionDataBinding;
import org.cruxframework.crux.core.rebind.screen.widget.PropertyBindInfo;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasValueChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * Factory for DatePicker
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="datePicker", library="gwt", targetWidget=DatePicker.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="value", type=String.class)
})
public class DatePickerFactory extends CompositeFactory<WidgetCreatorContext> 
implements HasValueChangeHandlersFactory<WidgetCreatorContext> 
{
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);

		String value = context.readWidgetProperty("value");
		if (value != null && value.length() > 0)
		{
			PropertyBindInfo binding = getObjectDataBinding(value, "value", true, context.getDataBindingProcessor());
			if (binding != null)
			{
				context.registerObjectDataBinding(binding);
				return;
			}
			else
			{
				ExpressionDataBinding expressionBinding = getExpressionDataBinding(value, "value", context.getDataBindingProcessor());
				if (expressionBinding != null)
				{
					context.registerExpressionDataBinding(expressionBinding);
					return;
				}
			}	
		}
	}

	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}
}