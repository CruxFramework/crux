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
package org.cruxframework.crux.widgets.client.select;

import org.cruxframework.crux.core.client.utils.StringUtils;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A simple single-select listBox that implements HasValue interface.
 * @author Gesse S. F. Dafe - <code>gessedafe@gmail.com</code>

 *
 */
//TODO refactorar isso. esse nome esta ruim
public class SingleSelect extends ListBox implements HasValue<String>
{
	/**
	 * @see com.google.gwt.user.client.ui.HasValue#getValue()
	 */
	public String getValue()
	{
		if(getSelectedIndex() >= 0)
		{
			return getValue(getSelectedIndex());
		}

		return null;
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
	 */
	public void setValue(String value)
	{
		int count = getItemCount();

		for (int i = 0; i < count; i++)
		{
			String itemValue = getValue(i);

			if((StringUtils.isEmpty(itemValue) && StringUtils.isEmpty(value)) || itemValue.equals(value))
			{
				setSelectedIndex(i);
				break;
			}
		}
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
	 */
	public void setValue(String value, boolean fireEvents)
	{
		String oldValue = getValue();
		setValue(value);
		if(fireEvents)
		{
			ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		}
	}

	/**
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}
}