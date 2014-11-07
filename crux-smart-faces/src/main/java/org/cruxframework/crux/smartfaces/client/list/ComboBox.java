/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.list;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.dataprovider.DataFilter;
import org.cruxframework.crux.core.client.dataprovider.DataProvider;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;


/**
 * @author wesley.diniz
 *
 * @param <T>
 */
public class ComboBox<T> extends AbstractComboBox<String, T>
{
	public ComboBox(OptionsRenderer<String, T> optionsRenderer)
	{
		super(optionsRenderer);
	}

	@Override
	public void setValue(final String value)
	{
		DataProvider<T> dataProvider = getDataProvider();
		Array<T> filterResult = dataProvider.filter(new DataFilter<T>(){

			@Override
			public boolean accept(T dataObject)
			{
				return optionsRenderer.getValue(dataObject).equals(value);
			}
		});
		
		if(filterResult.size() > 0)
		{
			T obj = filterResult.get(0);
			selectItem(optionsRenderer.getLabel(obj), optionsRenderer.getValue(obj));
		}
	}

	@Override
	public void setValue(String value, boolean fireEvents)
	{
		setValue(value);
	}

	@Override
	protected void setValueByObject(T obj)
	{
		setValue(optionsRenderer.getValue(obj));
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return null;
	}
	
}
