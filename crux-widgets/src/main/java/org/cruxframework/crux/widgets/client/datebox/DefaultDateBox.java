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

import org.cruxframework.crux.widgets.client.datebox.gwtoverride.CruxDateBox;
import org.cruxframework.crux.widgets.client.datebox.gwtoverride.CruxFormat;
import org.cruxframework.crux.widgets.client.datepicker.DatePicker;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A default implementation for DatePicker
 * @author Samuel Almeida Cardoso
 */
public class DefaultDateBox extends Composite implements IDateBox
{
	private CruxDateBox impl;
	
	public DefaultDateBox()
	{
		impl = new CruxDateBox();
		initWidget(impl);
		setStyleName(getBaseStyleName());
	}
	
	@Override
	public String getBaseStyleName() 
	{
		return impl.getStyleName();
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
	public TextBox getTextBox() 
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
}