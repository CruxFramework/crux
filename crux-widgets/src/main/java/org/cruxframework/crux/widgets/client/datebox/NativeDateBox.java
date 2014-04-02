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
package org.cruxframework.crux.widgets.client.datebox;

import org.cruxframework.crux.widgets.client.datepicker.CruxCalendarView;
import org.cruxframework.crux.widgets.client.datepicker.CruxMonthSelector;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * A native implementation for DatePicker component
 * @author samuel.cardoso
 */
public class NativeDateBox extends DefaultDateBox
{
	public NativeDateBox()
	{
		super();
		CruxCalendarView view = (CruxCalendarView) impl.getDatePicker().getView();
		view.getGrid().setHeight(Window.getClientHeight()+"px");
		view.getGrid().setWidth(Window.getClientWidth()+"px");
		
		CruxMonthSelector monthSelector = (CruxMonthSelector) impl.getDatePicker().getMonthSelector();
		monthSelector.getGrid().setWidth(Window.getClientWidth()+"px");
		
		CellFormatter formatter = monthSelector.getGrid().getCellFormatter();
		formatter.setWidth(0, 0, "15%");
		formatter.setWidth(0, 1, "15%");
		formatter.setWidth(0, 2, "40%");
		formatter.setWidth(0, 3, "15%");
		formatter.setWidth(0, 4, "15%");
	}
}