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

/**
 * Represents a SimpleCheckBox component
 * @author Thiago Bustamante
 *
 */
public class SimpleCheckBox extends FocusComponent
{
	protected com.google.gwt.user.client.ui.SimpleCheckBox simpleCheckBoxWidget;
	
	public SimpleCheckBox(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.SimpleCheckBox());
	}

	protected SimpleCheckBox(String id, com.google.gwt.user.client.ui.SimpleCheckBox widget) 
	{
		super(id, widget);
		this.simpleCheckBoxWidget = widget;
	}

	public String getName() 
	{
		return simpleCheckBoxWidget.getName();
	}

	/**
	 * Determines whether this check box is currently checked.
	 * 
	 * @return <code>true</code> if the check box is checked
	 */
	public boolean isChecked() 
	{
		return simpleCheckBoxWidget.isChecked();
	}


	/**
	 * Checks or unchecks this check box.
	 * 
	 * @param checked <code>true</code> to check the check box
	 */
	public void setChecked(boolean checked) 
	{	
		simpleCheckBoxWidget.setChecked(checked);
	}
	

	public void setName(String name) 
	{
		simpleCheckBoxWidget.setName(name);
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		String checked = element.getAttribute("_checked");
		if (checked != null && checked.trim().length() > 0)
		{
			simpleCheckBoxWidget.setChecked(Boolean.parseBoolean(checked));
		}
	}
}
