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
import com.google.gwt.user.client.ui.TextBox;

/**
 * Represents a TextBoxFactory component
 * @author Thiago Bustamante
 */
public class TextBoxFactory extends TextBoxBaseFactory<TextBox>
{	
	@Override
	protected void processAttributes(TextBox widget, Element element, String widgetId)
	{
		super.processAttributes(widget, element, widgetId);

		String direction = element.getAttribute("_direction");
		if (direction != null && direction.length() > 0)
		{
			widget.setDirection(Direction.valueOf(direction));
		}
		String maxLength = element.getAttribute("_maxLength");
		if (maxLength != null && maxLength.length() > 0)
		{
			widget.setMaxLength(Integer.parseInt(maxLength));
		}
		String visibleLength = element.getAttribute("_visibleLength");
		if (visibleLength != null && visibleLength.length() > 0)
		{
			widget.setVisibleLength(Integer.parseInt(visibleLength));
		}	
	}

	@Override
	protected TextBox instantiateWidget(Element element, String widgetId) 
	{
		return new TextBox();
	}
}
