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
 * 
 * @author Thiago Bustamante
 */
public class ToggleButton extends CustomButton 
{
	protected com.google.gwt.user.client.ui.ToggleButton toggleButtonWidget;
	public ToggleButton(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.ToggleButton());
	}
	
	public ToggleButton(String id, com.google.gwt.user.client.ui.ToggleButton toggleButton) 
	{
		super (id, toggleButton);
		this.toggleButtonWidget = toggleButton;
	}

	public boolean isDown()
	{
		return toggleButtonWidget.isDown();
	}
	
	public void setDown(boolean down)
	{
		if (toggleButtonWidget.isDown() != down)
		{
			modifiedProperties.put("down", Boolean.toString(down));
			toggleButtonWidget.setDown(down);
		}
	}
	
	/**
	 * Render component attributes
	 * @see #Component.renderAttributes
	 */
	protected void renderAttributes(Element element)
	{
		super.renderAttributes(element);

		String down = element.getAttribute("_down");
		if (down != null && down.trim().length() > 0)
		{
			toggleButtonWidget.setDown(Boolean.parseBoolean(down));
		}
	}
	
	/**
	 * Update component attributes
	 * @see #Component.renderAttributes
	 */
	protected void updateAttributes(com.google.gwt.xml.client.Element element)
	{
		super.updateAttributes(element);

		String down = element.getAttribute("_down");
		if (down != null && down.trim().length() > 0)
		{
			toggleButtonWidget.setDown(Boolean.parseBoolean(down));
		}
	}	
}
