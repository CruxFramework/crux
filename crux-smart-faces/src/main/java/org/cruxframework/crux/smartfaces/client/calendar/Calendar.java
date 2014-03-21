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
package org.cruxframework.crux.smartfaces.client.calendar;

import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarView;
import com.google.gwt.user.datepicker.client.DefaultCalendarView;

/**
 * A datepicker
 * @author Samuel Almeida Cardoso
 */
public class Calendar extends CalendarView
{
	private DefaultCalendarView view = new DefaultCalendarView();
	
	@Override
	public void addStyleToDate(String styleName, Date date) 
	{
		view.addStyleToDate(styleName, date);
	}

	@Override
	public Date getFirstDate() 
	{
		return view.getFirstDate();
	}

	@Override
	public Date getLastDate() 
	{
		return view.getLastDate();
	}

	@Override
	public boolean isDateEnabled(Date date) 
	{
		return view.isDateEnabled(date);
	}

	@Override
	public void removeStyleFromDate(String styleName, Date date) 
	{
		view.removeStyleFromDate(styleName, date);	
	}

	@Override
	public void setEnabledOnDate(boolean enabled, Date date) 
	{
		view.setEnabledOnDate(enabled, date);	
	}

	@Override
	protected void refresh() 
	{
		view.refresh();	
	}

	@Override
	protected void setup() 
	{
		view.setup();	
	}
}