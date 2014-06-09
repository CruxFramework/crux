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

import java.util.Date;

import org.cruxframework.crux.widgets.client.datebox.GWTOverriddenDateBox.DefaultFormat;
import org.cruxframework.crux.widgets.client.datebox.GWTOverriddenDateBox.Format;
import org.cruxframework.crux.widgets.client.datepicker.DatePicker;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.UIObject;

/**
 * DatePicker
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
public class DateBox extends Composite implements IDateBox
{
	//**************************************************
	//Exposing GWT DefaultFormat class
	public static class CruxDefaultFormat extends DefaultFormat
	{
		public CruxDefaultFormat()
		{
			super();
		}
		
		public CruxDefaultFormat(DateTimeFormat dateTimeFormat) 
		{
			super(dateTimeFormat);
		}
	}
	//Exposing GWT Format class (cross reference!)
	public interface CruxFormat extends Format
	{
		public String getPattern();
	}
	//**************************************************
	
	private IDateBox impl;
	
	protected static abstract class CommonDateBox extends Composite implements IDateBox
	{
		@Override
		public String getBaseStyleName() 
		{
			return "crux-DateBox";
		}
	}
	
	public DateBox()
	{
		impl = GWT.create(CommonDateBox.class);
		initWidget(impl.asWidget());
		setStyleName(getBaseStyleName());
	}

	@Override
	public String getBaseStyleName() 
	{
		return impl.getBaseStyleName();
	}

	@Override
	public CruxFormat getFormat() 
	{
		return impl.getFormat();
	}

	@Override
	public int getTabIndex() 
	{
		return impl.getTabIndex();
	}

	@Override
	public MaskedTextBox getTextBox() 
	{
		return impl.getTextBox();
	}

	@Override
	public DatePicker getDatePicker() 
	{
		return impl.getDatePicker();
	}

	@Override
	public Date getValue() 
	{
		return impl.getValue();
	}

	@Override
	public void hideDatePicker() 
	{
		impl.hideDatePicker();	
	}

	@Override
	public boolean isDatePickerShowing() 
	{
		return impl.isDatePickerShowing();
	}

	@Override
	public boolean isEnabled() 
	{
		return impl.isEnabled();
	}

	@Override
	public void setAccessKey(char key) 
	{
		impl.setAccessKey(key);	
	}

	@Override
	public void setEnabled(boolean enabled) 
	{
		impl.setEnabled(enabled);		
	}

	@Override
	public void setFocus(boolean focused) 
	{
		impl.setFocus(focused);	
	}

	@Override
	public void setFormat(CruxFormat format) 
	{
		impl.setFormat(format);
	}

	@Override
	public void setValue(Date date) 
	{
		impl.setValue(date);	
	}

	@Override
	public void showDatePicker() 
	{
		impl.showDatePicker();	
	}

	@Override
	public void setValue(Date value, boolean fireEvents) 
	{
		impl.setValue(value, fireEvents);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) 
	{
		return impl.addValueChangeHandler(handler);
	}

	@Override
	public void setTabIndex(int index) 
	{
		impl.setTabIndex(index);	
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		impl.setReadOnly(readOnly);
	}

	@Override
	public UIObject getPopup() 
	{
		return impl.getPopup();
	}
}