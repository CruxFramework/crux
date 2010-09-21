/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.gwt.client;

import java.util.Date;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasShowRangeHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasValueChangeHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="datePicker", library="gwt")
public class DatePickerFactory extends CompositeFactory<DatePicker> 
       implements HasValueChangeHandlersFactory<DatePicker>, HasShowRangeHandlersFactory<DatePicker>
{
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="value", type=String.class),
		@TagAttributeDeclaration(value="datePattern")
	})
	public void processAttributes(WidgetFactoryContext<DatePicker> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		
		DatePicker widget = context.getWidget();
		
		String value = context.readWidgetProperty("value");
		if (value != null && value.length() > 0)
		{
			String datePattern = context.readWidgetProperty("datePattern");
			if (datePattern == null || datePattern.length() == 0)
			{
				datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
			}
			Date date = DateFormatUtil.getDateTimeFormat(datePattern).parse(value);;
			widget.setValue(date);
		}		
	}
	
	@Override
	public DatePicker instantiateWidget(Element element, String widgetId) throws InterfaceConfigException 
	{
		return new DatePicker();
	}
}
