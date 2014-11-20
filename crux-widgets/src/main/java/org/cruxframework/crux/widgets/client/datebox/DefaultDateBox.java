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

import org.cruxframework.crux.widgets.client.datebox.DateBox.CommonDateBox;
import org.cruxframework.crux.widgets.client.datebox.DateBox.CruxFormat;
import org.cruxframework.crux.widgets.client.datepicker.DatePicker;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A default implementation for DatePicker
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
class DefaultDateBox extends CommonDateBox
{
	protected GWTOverriddenDateBox impl;
	
	public DefaultDateBox()
	{
		impl = new GWTOverriddenDateBox();
		initWidget(impl);
		impl.getPopup().setStyleName("crux-popupPanel");
		setStyleName(getBaseStyleName());
	}

	@Override
	public void showDatePicker() 
	{
		impl.showDatePicker();
	}
	
	@Override
	public CruxFormat getFormat() 
	{
		return (CruxFormat) impl.getFormat();
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
		impl.getTextBox().setReadOnly(readOnly);
		impl.setEnabled(!readOnly);
	}

	@Override
	public UIObject getPopup() 
	{
		return impl.getPopup();
	}

	@Override
	public void setFireNullValues(boolean fires)
	{
		impl.setFireNullValues(fires);
		
	}
}