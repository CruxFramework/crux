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
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAutoHorizontalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDirectionEstimatorFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasWordWrapFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.gwt.client.NumberFormatUtil;

import com.google.gwt.user.client.ui.NumberLabel;

/**
 * A Factory for NumberLabel widgets
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="numberLabel", library="gwt", targetWidget=NumberLabel.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="value", type=String.class),
	@TagAttributeDeclaration(value="numberPattern")
})public class NumberLabelFactory extends WidgetCreator<WidgetCreatorContext> 
		implements HasWordWrapFactory<WidgetCreatorContext>, 
				   HasAutoHorizontalAlignmentFactory<WidgetCreatorContext>, 
				   HasDirectionEstimatorFactory<WidgetCreatorContext>
{
	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName()+"<Double>";
		String numberPattern = context.readWidgetProperty("numberPattern");
		if (numberPattern == null || numberPattern.length() == 0)
		{
			numberPattern = NumberFormatUtil.DECIMAL_PATTERN;
		}
		out.println("final "+className + " " + context.getWidget() +" = new "+className+"("+
						NumberFormatUtil.class.getCanonicalName()+".getNumberFormat("+
						EscapeUtils.quote(numberPattern)+"));");
	}
	
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
		
		String widget = context.getWidget();

		String numberPattern = context.readWidgetProperty("numberPattern");
		if (numberPattern == null || numberPattern.length() == 0)
		{
			numberPattern = NumberFormatUtil.DECIMAL_PATTERN;
		}
		
		String value = context.readWidgetProperty("value");
		if (value != null && value.length() > 0)
		{
			out.println(widget+".setValue("+
					NumberFormatUtil.class.getCanonicalName()+".getNumberFormat("+
					EscapeUtils.quote(numberPattern)+").parse("+EscapeUtils.quote(value)+"));");
		}		
	}
	
}