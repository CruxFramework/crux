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
package br.com.sysmap.crux.basic.client;

import java.util.Date;

import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;
import br.com.sysmap.crux.core.client.event.bind.HighlightEvtBind;
import br.com.sysmap.crux.core.client.event.bind.ShowRangeEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class DatePickerFactory extends CompositeFactory<DatePicker> 
{
	@Override
	protected void processAttributes(final DatePicker widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String value = element.getAttribute("_value");
		if (value != null && value.length() > 0)
		{
			String datePattern = element.getAttribute("_datePattern");
			if (datePattern == null || datePattern.length() == 0)
			{
				datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
			}
			Date date = DateFormatUtil.getDateTimeFormat(datePattern).parse(value);;
			widget.setValue(date);
		}		
	}
	
	@Override
	protected DatePicker instantiateWidget(Element element, String widgetId) throws InterfaceConfigException 
	{
		return new DatePicker();
	}
	
	@Override
	protected void processEvents(DatePicker widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		ChangeEvtBind.bindValueEvent(element, widget);
		HighlightEvtBind.bindEvent(element, widget);
		ShowRangeEvtBind.bindEvent(element, widget);
	}
}
