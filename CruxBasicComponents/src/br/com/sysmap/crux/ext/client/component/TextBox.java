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
import com.google.gwt.i18n.client.HasDirection.Direction;

/**
 * Represents a TextBox component
 * @author Thiago Bustamante
 */
public class TextBox extends TextBoxBase
{
	protected com.google.gwt.user.client.ui.TextBox textBoxWidget;
	
	public TextBox(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.TextBox());
	}

	public TextBox(String id, com.google.gwt.user.client.ui.TextBox widget) 
	{
		super(id, widget);
		this.textBoxWidget = widget;
	}
	
	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);

		String direction = element.getAttribute("_direction");
		if (direction != null && direction.length() > 0)
		{
			setDirection(Direction.valueOf(direction));
		}
		String maxLength = element.getAttribute("_maxLength");
		if (maxLength != null && maxLength.length() > 0)
		{
			setMaxLength(Integer.parseInt(maxLength));
		}
		String visibleLength = element.getAttribute("_visibleLength");
		if (visibleLength != null && visibleLength.length() > 0)
		{
			setVisibleLength(Integer.parseInt(visibleLength));
		}
		
	}
	
	public Direction getDirection() 
	{
		return textBoxWidget.getDirection();
	}	
	
	/**
	 * Gets the maximum allowable length of the text box.
	 * 
	 * @return the maximum length, in characters
	 */
	public int getMaxLength() 
	{
		return textBoxWidget.getMaxLength();
	}

	/**
	 * Gets the number of visible characters in the text box.
	 * 
	 * @return the number of visible characters
	 */
	public int getVisibleLength() 
	{
		return textBoxWidget.getVisibleLength();
	}

	public void setDirection(Direction direction) 
	{
		textBoxWidget.setDirection(direction);
	}

	/**
	 * Sets the maximum allowable length of the text box.
	 * 
	 * @param length the maximum length, in characters
	 */
	public void setMaxLength(int length) 
	{
		textBoxWidget.setMaxLength(length);
	}

	/**
	 * Sets the number of visible characters in the text box.
	 * 
	 * @param length the number of visible characters
	 */
	public void setVisibleLength(int length) 
	{
		textBoxWidget.setVisibleLength(length);
	}	
}
