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

import com.google.gwt.user.client.Window;

/**
 * A native implementation for DatePicker component
 * @author samuel.cardoso
 */
public class NativeDateBox extends DefaultDateBox
{
	public NativeDateBox()
	{
		super();
//		impl.getPopup().getElement().setId("testeeee");
//		impl.getPopup().setHeight("100%");
//		impl.getPopup().setWidth("100%");
//		
//		impl.getDatePicker().setHeight("100%");
//		impl.getDatePicker().setWidth("100%");
		
		CruxCalendarView view = (CruxCalendarView) impl.getDatePicker().getView();
		view.getGrid().setHeight(Window.getClientHeight()+"px");
		view.getGrid().setWidth(Window.getClientWidth()+"px");
		
//		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
//		{
//			@Override
//			public void execute() 
//			{
//				view.getGrid().setHeight("1000px");
//				view.getGrid().setWidth("1000px");
//			}
//		});
	}
}