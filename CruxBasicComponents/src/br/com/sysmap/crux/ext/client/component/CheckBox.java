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

import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;
import br.com.sysmap.crux.core.client.formatter.InvalidFormatException;

import com.google.gwt.dom.client.Element;

/**
 * CheckBox Component.
 * @author Thiago Bustamante
 *
 */
public class CheckBox extends ButtonBase 
{
	protected com.google.gwt.user.client.ui.CheckBox checkboxWidget;
	
	/**
	 * Constructor
	 * @param id
	 */
	public CheckBox(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.CheckBox());
	}
	
	/**
	 * Constructor
	 * @param id
	 * @param widget
	 */
	protected CheckBox(String id, com.google.gwt.user.client.ui.ButtonBase widget) 
	{
		super(id, widget);
		this.checkboxWidget = (com.google.gwt.user.client.ui.CheckBox) widget;
	}

	/**
	 * Return true if the component is checked
	 * @return
	 */
	public Boolean getValue()
	{
		return checkboxWidget.getValue();
	}

	/**
	 * Check or uncheck component
	 * @param checked
	 */
	public void setValue(Boolean checked)
	{
		setValue(checked, false);
	}
	
	/**
	 * Check or uncheck component
	 * @param checked
	 */
	public void setValue(Boolean checked, boolean fireEvents)
	{
		checkboxWidget.setValue(checked, fireEvents);
	}
	
	/**
	 * Render component attributes
	 * @see #Component.renderAttributes
	 */
	@Override
	protected void renderAttributes(Element element)
	{
		super.renderAttributes(element);
		String checked = element.getAttribute("_checked");
		if (checked != null && checked.trim().length() > 0)
		{
			checkboxWidget.setValue(Boolean.parseBoolean(checked));
		}
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		ChangeEvtBind.bindValueEvent(element, checkboxWidget, getId());
	}
	
	public String getFormValue() throws InvalidFormatException 
	{
		return checkboxWidget.getFormValue();
	}
	
	public void setName(String name) 
	{
		checkboxWidget.setName(name);
	}

}
