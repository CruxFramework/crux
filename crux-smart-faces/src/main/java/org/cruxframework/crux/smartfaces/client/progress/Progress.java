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
package org.cruxframework.crux.smartfaces.client.progress;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class Progress extends Widget implements HasValue<Integer>
{
	private static Boolean supported = null;
	public static final String DEFAULT_STYLE_NAME = "faces-Progress";

	protected Progress() 
	{
		setElement(Document.get().createElement("progress"));
		setStyleName(DEFAULT_STYLE_NAME);
	}
	
	public void setMax(int maxValue)
	{
		getElement().setPropertyInt("max", maxValue);
	}
	
	public int getMax()
	{
		return getElement().getPropertyInt("max");
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) 
	{
      return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public Integer getValue() 
	{
		return getElement().getPropertyInt("value");
	}

	@Override
	public void setValue(Integer value) 
	{
		setValue(value, false);
	}

	public double getPosition()
	{
		return getElement().getPropertyDouble("position");
	}
	
	@Override
	public void setValue(Integer value, boolean fireEvents) 
	{
	    if (fireEvents) 
	    {
	        Integer oldValue = getValue();
	        ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
	        getElement().setPropertyInt("value", (value!=null?value:0));
	    }
	    else
	    {
	        getElement().setPropertyInt("value", (value!=null?value:0));
	    }
	}
	
	public static Progress createIfSupported()
	{
		if (isSupported())
		{
			return new Progress();
		}
		return null;
	}
	
	public static boolean isSupported()
	{
		if (supported == null)
		{
			supported = supportDetection();
		}
		return (supported);
	}
	
	private static native boolean supportDetection()/*-{
		return ($doc.createElement('progress').max !== undefined);	
	}-*/;
}
