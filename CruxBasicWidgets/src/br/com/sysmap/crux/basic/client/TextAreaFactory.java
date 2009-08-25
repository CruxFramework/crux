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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.TextArea;


/**
 * A TextAreaFactory DeclarativeFactory
 * 
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="textArea", library="bas")
public class TextAreaFactory extends TextBoxBaseFactory<TextArea>
{
	
	@Override
	protected void processAttributes(TextArea widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);

		String characterWidth = element.getAttribute("_characterWidth");
		if (characterWidth != null && characterWidth.trim().length() > 0)
		{
			widget.setCharacterWidth(Integer.parseInt(characterWidth));
		}
		String direction = element.getAttribute("_direction");
		if (direction != null && direction.trim().length() > 0)
		{
			widget.setDirection(Direction.valueOf(direction));
		}
		String visibleLines = element.getAttribute("_visibleLines");
		if (visibleLines != null && visibleLines.trim().length() > 0)
		{
			widget.setVisibleLines(Integer.parseInt(visibleLines));
		}
	}

	@Override
	protected TextArea instantiateWidget(Element element, String widgetId) 
	{
		return new TextArea();
	}
}
