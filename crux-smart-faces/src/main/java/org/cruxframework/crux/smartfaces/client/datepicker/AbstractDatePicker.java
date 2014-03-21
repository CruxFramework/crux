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
package org.cruxframework.crux.smartfaces.client.datepicker;


import java.util.Date;

import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.event.CancelHandler;
import org.cruxframework.crux.smartfaces.client.event.HasCancelHandlers;
import org.cruxframework.crux.smartfaces.client.event.HasOkHandlers;
import org.cruxframework.crux.smartfaces.client.event.OkHandler;
import org.cruxframework.crux.smartfaces.client.label.Label;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * A datepicker
 * @author Samuel Almeida Cardoso
 */
public abstract class AbstractDatePicker implements HasOkHandlers, HasCancelHandlers, HasValue<Date>
{
	private static final String DEFAULT_STYLE_NAME = "faces-DatePicker";

	private Label msgLabel;
	private Button okButton;
	private Button cancelButton;
	
	@Override
	public void fireEvent(GwtEvent<?> event) 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public HandlerRegistration addCancelHandler(CancelHandler handler) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public HandlerRegistration addOkHandler(OkHandler handler) 
	{
		// TODO Auto-generated method stub
		return null;
	}


}