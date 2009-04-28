/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.ext.client.component;

import com.google.gwt.dom.client.Element;

import br.com.sysmap.crux.core.client.component.Component;

/**
 * Represents a Hidden component
 * @author Thiago Bustamante
 */
public class Hidden extends Component 
{
	protected com.google.gwt.user.client.ui.Hidden hiddenWidget;

	public Hidden(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.Hidden());
	}

	protected Hidden(String id, com.google.gwt.user.client.ui.Hidden widget) 
	{
		super(id, widget);
		this.hiddenWidget = widget;
		setId(id);
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);

		String name = element.getAttribute("_name");
		if (name != null && name.length() > 0)
		{
			setName(name);
		}
		String value = element.getAttribute("_value");
		if (value != null && value.length() > 0)
		{
			setValue(value);
		}
	}
	
	/**
	 * Gets the default value of the hidden field.
	 * 
	 * @return the default value
	 */
	public String getDefaultValue() 
	{
		return hiddenWidget.getDefaultValue();
	}

	/**
	 * Gets the id of the hidden field.
	 * 
	 * @return the id
	 */
	public String getId() 
	{
		return hiddenWidget.getID();
	}

	/**
	 * Gets the name of the hidden field.
	 * 
	 * @return the name
	 */

	public String getName() 
	{
		return hiddenWidget.getName();
	}

	/**
	 * Gets the value of the hidden field.
	 * 
	 * @return the value
	 */
	public String getValue() 
	{
		return hiddenWidget.getValue();
	}

	/**
	 * Sets the default value of the hidden field.
	 * 
	 * @param defaultValue default value to set
	 */
	public void setDefaultValue(String defaultValue) 
	{
		hiddenWidget.setDefaultValue(defaultValue);
	}

	/**
	 * Sets the id of the hidden field.
	 * 
	 * @param id id to set
	 */
	public void setId(String id) 
	{
		hiddenWidget.setID(id);
	}

	/**
	 * Sets the name of the hidden field.
	 * 
	 * @param name name of the field
	 */
	public void setName(String name) 
	{
		hiddenWidget.setName(name);
	}

	/**
	 * Sets the value of the hidden field.
	 * 
	 * @param value value to set
	 */
	public void setValue(String value) 
	{
		hiddenWidget.setValue(value);
	}

}
