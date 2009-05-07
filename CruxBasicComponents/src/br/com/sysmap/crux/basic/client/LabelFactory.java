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

import br.com.sysmap.crux.core.client.component.WidgetFactory;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

/**
 * Represents a LabelFactory WidgetFactory
 * @author Thiago Bustamante
 *
 */
public class LabelFactory extends WidgetFactory<Label>
{
	@Override
	protected void processAttributes(Label widget, Element element, String widgetId)
	{
		super.processAttributes(widget, element, widgetId);

		String direction = element.getAttribute("_direction");
		if (direction != null && direction.trim().length() > 0)
		{
			widget.setDirection(Direction.valueOf(direction));
		}
		
		String horizontalAlignment = element.getAttribute("_horizontalAlignment");
		if (horizontalAlignment != null && horizontalAlignment.trim().length() > 0)
		{
			if (HasHorizontalAlignment.ALIGN_CENTER.equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}
			else if (HasHorizontalAlignment.ALIGN_DEFAULT.equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_DEFAULT);
			}
			else if (HasHorizontalAlignment.ALIGN_LEFT.equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			}
			else if (HasHorizontalAlignment.ALIGN_RIGHT.equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			}
		}

		String wordWrap = element.getAttribute("_wordWrap");
		if (wordWrap != null && wordWrap.trim().length() > 0)
		{
			widget.setWordWrap(Boolean.parseBoolean(wordWrap));
		}
	}
	
	@Override
	protected void processEvents(Label widget, Element element, String widgetId)
	{
		super.processEvents(widget, element, widgetId);
		
		ClickEvtBind.bindEvent(element, widget, widgetId);
		MouseEvtBind.bindEvents(element, widget, widgetId);
	}

	@Override
	protected Label instantiateWidget(Element element, String widgetId) 
	{
		return new Label();
	}	
}
