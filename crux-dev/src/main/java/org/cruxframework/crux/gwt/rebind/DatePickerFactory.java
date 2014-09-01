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
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHighlightHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasShowRangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasValueChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.gwt.client.DateFormatUtil;

import com.google.gwt.user.datepicker.client.DatePicker;


/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="datePicker", library="gwt", targetWidget=DatePicker.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="value", type=String.class),
	@TagAttributeDeclaration(value="currentMonth", type=String.class),
	@TagAttributeDeclaration(value="datePattern")
})
public class DatePickerFactory extends CompositeFactory<WidgetCreatorContext> 
       implements HasValueChangeHandlersFactory<WidgetCreatorContext>, 
                  HasShowRangeHandlersFactory<WidgetCreatorContext>, 
                  HasHighlightHandlersFactory<WidgetCreatorContext>
{
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
		
		String widget = context.getWidget();

		String datePattern = context.readWidgetProperty("datePattern");
		if (datePattern == null || datePattern.length() == 0)
		{
			datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
		}
		
		String value = context.readWidgetProperty("value");
		if (value != null && value.length() > 0)
		{
			out.println(widget+".setValue("+
					DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
					EscapeUtils.quote(datePattern)+").parse("+EscapeUtils.quote(value)+"));");
		}		

		String currentMonth = context.readWidgetProperty("currentMonth");
		if (currentMonth != null && currentMonth.length() > 0)
		{
			out.println(widget+".setCurrentMonth("+
					DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
					EscapeUtils.quote(datePattern)+").parse("+EscapeUtils.quote(currentMonth)+"));");
		}		
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
