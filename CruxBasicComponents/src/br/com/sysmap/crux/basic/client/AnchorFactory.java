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

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * Represents an AnchorFactory component
 * @author Thiago Bustamante
 */
public class AnchorFactory extends FocusWidgetFactory<Anchor>
{
	@Override
	protected void processAttributes(Anchor widget, Element element, String widgetId) throws InterfaceConfigException
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
			if (HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString().equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}
			else if (HasHorizontalAlignment.ALIGN_DEFAULT.getTextAlignString().equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_DEFAULT);
			}
			else if (HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString().equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			}
			else if (HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString().equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			}
		}

		String href = element.getAttribute("_href");
		if (href != null && href.trim().length() > 0)
		{
			widget.setHref(href);
		}

		String target = element.getAttribute("_target");
		if (target != null && target.trim().length() > 0)
		{
			widget.setTarget(target);
		}

		String wordWrap = element.getAttribute("_wordWrap");
		if (wordWrap != null && wordWrap.trim().length() > 0)
		{
			widget.setWordWrap(Boolean.parseBoolean(wordWrap));
		}		
		
		setTextOrHtml(widget, element);
	}

	private void setTextOrHtml(Anchor widget, Element element) throws InterfaceConfigException
	{
		Node child = element.getFirstChild();
		if(child != null && child instanceof Element)
		{
			Element childElem = (Element) child;
			if(isSpan(childElem))
			{
				String text = childElem.getAttribute("_text");
				if(text != null && text.length() > 0)
				{
					widget.setText(text);
					return;
				}
			}
		}
		
		widget.setHTML(element.getInnerHTML());
	}

	@Override
	protected Anchor instantiateWidget(Element element, String widgetId) 
	{
		return new Anchor();
	}	
}
