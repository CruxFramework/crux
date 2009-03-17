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
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * CheckBox Component.
 * @author Thiago Bustamante
 *
 */
public class CheckBox extends FocusComponent 
{
	protected com.google.gwt.user.client.ui.CheckBox checkboxWidget;
	
	/**
	 * Constructor
	 * @param id
	 */
	public CheckBox(String id) 
	{
		super(id, new com.google.gwt.user.client.ui.CheckBox());
	}
	
	/**
	 * Constructor
	 * @param id
	 * @param widget
	 */
	public CheckBox(String id, FocusWidget widget) 
	{
		super(id, widget);
		this.checkboxWidget = (com.google.gwt.user.client.ui.CheckBox) widget;
	}

	/**
	 * Return true if the component is checked
	 * @return
	 */
	public boolean isChecked()
	{
		return checkboxWidget.isChecked();
	}
	
	/**
	 * Check or uncheck component
	 * @param checked
	 */
	public void setChecked(boolean checked)
	{
		if (checkboxWidget.isChecked() != checked)
		{
			modifiedProperties.put("checked", Boolean.toString(checked));
			checkboxWidget.setChecked(checked);
		}		
	}
	
	/**
	 * Render component attributes
	 * @see #Component.renderAttributes
	 */
	protected void renderAttributes(Element element)
	{
		super.renderAttributes(element);
		String checked = element.getAttribute("_checked");
		if (checked != null && checked.trim().length() > 0)
		{
			checkboxWidget.setChecked(Boolean.parseBoolean(checked));
		}
	}

	/**
	 * Update component attributes
	 * @see #Component.renderAttributes
	 */
	protected void updateAttributes(com.google.gwt.xml.client.Element element)
	{
		super.updateAttributes(element);

		String checked = element.getAttribute("_checked");
		if (checked != null && checked.trim().length() > 0)
		{
			checkboxWidget.setChecked(Boolean.parseBoolean(checked));
		}
	}
}
