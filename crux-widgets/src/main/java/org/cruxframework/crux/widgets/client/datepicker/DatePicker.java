/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.datepicker;

import java.util.Date;

import org.cruxframework.crux.widgets.client.util.type.CruxWidget;

import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author samuel.cardoso
 *
 */
public class DatePicker extends org.cruxframework.crux.widgets.client.datepicker.gwtoverride.DatePicker implements CruxWidget
{
	private static final CruxMonthSelector MONTH_AND_YEAR_SELECTOR = new CruxMonthSelector();
	private Date monthToOpen;
	
	public DatePicker() 
	{
		super(MONTH_AND_YEAR_SELECTOR, new CruxCalendarView(), new CruxCalendarModel());
		setStyleName(getBaseStyleName());
	}

	@Override
	public String getBaseStyleName() 
	{
		return "crux-DatePicker";
	}

	/**
	 * Add a style name to the given dates.
	 */
	public void addStyleToDates(String styleName, Date initDate, Date finalDate)
	{
		Date currentDate = CalendarUtil.copyDate(initDate);
		int days = CalendarUtil.getDaysBetween(initDate, finalDate);
		for(int i=0; i<days; i++)
		{
			CalendarUtil.addDaysToDate(currentDate, 1);
			addStyleToDates(styleName, currentDate);
		}
	}
	
	public Date getMonthToOpen() 
	{
		return monthToOpen;
	}
	

	public void setMonthToOpen(Date monthToOpen) 
	{
		this.monthToOpen = monthToOpen;
	}
}