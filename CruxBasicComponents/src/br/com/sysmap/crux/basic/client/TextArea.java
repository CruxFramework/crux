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
package br.com.sysmap.crux.basic.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.HasDirection.Direction;


/**
 * A TextArea Component
 * 
 * @author Thiago Bustamante
 *
 */
public class TextArea extends TextBoxBase
{
	protected com.google.gwt.user.client.ui.TextArea textAreaWidget;
	
	public TextArea(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.TextArea());
	}

	protected TextArea(String id, com.google.gwt.user.client.ui.TextArea widget) 
	{
		super(id, widget);
		this.textAreaWidget = widget;
	}
	
	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);

		String characterWidth = element.getAttribute("_characterWidth");
		if (characterWidth != null && characterWidth.trim().length() > 0)
		{
			textAreaWidget.setCharacterWidth(Integer.parseInt(characterWidth));
		}
		String direction = element.getAttribute("_direction");
		if (direction != null && direction.trim().length() > 0)
		{
			setDirection(Direction.valueOf(direction));
		}
		String visibleLines = element.getAttribute("_visibleLines");
		if (visibleLines != null && visibleLines.trim().length() > 0)
		{
			textAreaWidget.setVisibleLines(Integer.parseInt(visibleLines));
		}
	}

	/**
	 * Gets the requested width of the text box (this is not an exact value, as
	 * not all characters are created equal).
	 * 
	 * @return the requested width, in characters
	 */
	public int getCharacterWidth() 
	{
		return textAreaWidget.getCharacterWidth();
	}

	public Direction getDirection() 
	{
		return textAreaWidget.getDirection();
	}	

	/**
	 * Gets the number of text lines that are visible.
	 * 
	 * @return the number of visible lines
	 */
	public int getVisibleLines() 
	{
		return textAreaWidget.getVisibleLines();
	}

	/**
	 * Sets the requested width of the text box (this is not an exact value, as
	 * not all characters are created equal).
	 * 
	 * @param width the requested width, in characters
	 */
	public void setCharacterWidth(int width) 
	{
		textAreaWidget.setCharacterWidth(width);
	}

	public void setDirection(Direction direction) 
	{
		textAreaWidget.setDirection(direction);
	}

	/**
	 * Sets the number of text lines that are visible.
	 * 
	 * @param lines the number of visible lines
	 */
	public void setVisibleLines(int lines) 
	{
		textAreaWidget.setVisibleLines(lines);
	}

}
