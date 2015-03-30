/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.rebind.input;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.FocusableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllMouseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllTouchHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDirectionEstimatorFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDirectionFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDoubleClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasNameFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasValueFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.smartfaces.client.input.NumberBox;
import org.cruxframework.crux.smartfaces.client.input.NumberBox.FormatterOptions;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * A Factory for NumberBox widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="numberBox", library=Constants.LIBRARY_NAME, targetWidget=NumberBox.class, 
					description="A standard numeric box.")
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="allowNegatives", type=Boolean.class, 
							description="If true, the box will accept negative numbers."),
	@TagAttributeDeclaration(value="fractionDigits", type=Integer.class,
							description="The number of decimal digits for numbers into this box. Default is zero."),
	@TagAttributeDeclaration(value="showGroupSeparators", type=Boolean.class,
							description="If true, the groupSeparator will be shown."),
	@TagAttributeDeclaration(value="groupSize", type=Integer.class,
							description="The number of digits in a group, separated by groupSeparator."),
	@TagAttributeDeclaration(value="groupSeparator", supportsI18N=true,
							description="The string to be used to separe groups of digits. If none is provided, Crux discover it by the user Locale."),
	@TagAttributeDeclaration(value="decimalSeparator", supportsI18N=true,
							description="The string to be used to separe decimal digits. If none is provided, Crux discover it by the user Locale.")
})
@TagAttributes({
	@TagAttribute(value="value", type=Double.class,
				description="The value of the box."),
	@TagAttribute(value="maxValue", type=Double.class,
				description="The maximum value accepted by this box."),
	@TagAttribute(value="minValue", type=Double.class,
			description="The minimum value accepted by this box.")
})
public class NumberBoxFactory extends WidgetCreator<WidgetCreatorContext> 
implements FocusableFactory<WidgetCreatorContext>, HasAllMouseHandlersFactory<WidgetCreatorContext>,  
		   HasAllFocusHandlersFactory<WidgetCreatorContext>, HasAllTouchHandlersFactory<WidgetCreatorContext>, 
		   HasEnabledFactory<WidgetCreatorContext>, HasValueFactory<WidgetCreatorContext>, HasNameFactory<WidgetCreatorContext>,
		   HasDirectionEstimatorFactory<WidgetCreatorContext>, HasDirectionFactory<WidgetCreatorContext>,
		   HasClickHandlersFactory<WidgetCreatorContext>, HasDoubleClickHandlersFactory<WidgetCreatorContext>
{	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		String formatterOptions = createVariableName("formatterOptions");

		String formatterOptionsClass = FormatterOptions.class.getCanonicalName();
		out.println(formatterOptionsClass + " " + formatterOptions + " = new " + formatterOptionsClass + "();");
		
		boolean allowNegatives = context.readBooleanWidgetProperty("allowNegatives", FormatterOptions.DEFAULT_ALLOW_NEGATIVES);
		boolean showGroupSeparators = context.readBooleanWidgetProperty("showGroupSeparators", FormatterOptions.DEFAULT_SHOW_GROUP_SEPARATOR);
		int fractionDigits = context.readIntWidgetProperty("fractionDigits", FormatterOptions.DEFAULT_FRACTION_DIGITS);
		int groupSize = context.readIntWidgetProperty("groupSize", FormatterOptions.DEFAULT_GROUP_SIZE);
		String groupSeparator = context.readWidgetProperty("groupSeparator");
		String decimalSeparator = context.readWidgetProperty("decimalSeparator");
		
		if (allowNegatives != FormatterOptions.DEFAULT_ALLOW_NEGATIVES)
		{
			out.println(formatterOptions + ".setAllowNegatives(" + allowNegatives + ");");
		}
		if (showGroupSeparators != FormatterOptions.DEFAULT_SHOW_GROUP_SEPARATOR)
		{
			out.println(formatterOptions + ".setShowGroupSeparators(" + showGroupSeparators + ");");
		}
		if (fractionDigits != FormatterOptions.DEFAULT_FRACTION_DIGITS)
		{
			out.println(formatterOptions + ".setFractionDigits(" + fractionDigits + ");");
		}
		if (groupSize != FormatterOptions.DEFAULT_GROUP_SIZE)
		{
			out.println(formatterOptions + ".setGroupSize(" + groupSize + ");");
		}
		if (!StringUtils.isEmpty(groupSeparator))
		{
			out.println(formatterOptions + ".setGroupSeparator(" + getDeclaredMessage(groupSeparator) + ");");
		}
		if (!StringUtils.isEmpty(decimalSeparator))
		{
			out.println(formatterOptions + ".setDecimalSeparator(" + getDeclaredMessage(decimalSeparator) + ");");
		}
		out.println("final "+className + " " + context.getWidget() + " = new "+className+"(" + formatterOptions + ");");
	}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
