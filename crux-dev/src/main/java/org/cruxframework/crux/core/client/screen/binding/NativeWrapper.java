/*
 * Copyright 2016 cruxframework.org.
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
package org.cruxframework.crux.core.client.screen.binding;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class NativeWrapper extends Widget
{
	public NativeWrapper(Element element)
    {
		setElement(element);
    }
	
	public String getPropertyString(String property)
	{
		return getElement().getPropertyString(property);
	}

	public void setPropertyString(String property, String value)
	{
		getElement().setPropertyString(property, value);
	}

	public int getPropertyInt(String property)
	{
		return getElement().getPropertyInt(property);
	}

	public void setPropertyInt(String property, int value)
	{
		getElement().setPropertyInt(property, value);
	}

	public double getPropertyDouble(String property)
	{
		return getElement().getPropertyDouble(property);
	}

	public void setPropertyDouble(String property, double value)
	{
		getElement().setPropertyDouble(property, value);
	}

	public boolean getPropertyBoolean(String property)
	{
		return getElement().getPropertyBoolean(property);
	}

	public void setPropertyBoolean(String property, boolean value)
	{
		getElement().setPropertyBoolean(property, value);
	}
}
