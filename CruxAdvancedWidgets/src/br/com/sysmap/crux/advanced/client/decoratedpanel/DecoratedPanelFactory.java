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
package br.com.sysmap.crux.advanced.client.decoratedpanel;

import br.com.sysmap.crux.basic.client.CellPanelFactory;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for Decorated Panel widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DecoratedPanelFactory extends CellPanelFactory<DecoratedPanel>
{
	@Override
	protected DecoratedPanel instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String height = element.getAttribute("_height");
		String width = element.getAttribute("_width");
		return new DecoratedPanel(width, height);
	}

	@Override
	protected void processAttributes(DecoratedPanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		String cellHorizontalAlignment = element.getAttribute("_horizontalAlignment");
		
		if (cellHorizontalAlignment != null && cellHorizontalAlignment.trim().length() > 0)
		{
			if (HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString().equals(cellHorizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}
			else if (HasHorizontalAlignment.ALIGN_DEFAULT.getTextAlignString().equals(cellHorizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_DEFAULT);
			}
			else if (HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString().equals(cellHorizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			}
			else if (HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString().equals(cellHorizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			}
		}
		
		String cellVerticalAlignment = element.getAttribute("_verticalAlignment");
		if (cellVerticalAlignment != null && cellVerticalAlignment.trim().length() > 0)
		{
			if (HasVerticalAlignment.ALIGN_BOTTOM.getVerticalAlignString().equals(cellVerticalAlignment))
			{
				widget.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
			}
			else if (HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString().equals(cellVerticalAlignment))
			{
				widget.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			}
			else if (HasVerticalAlignment.ALIGN_TOP.getVerticalAlignString().equals(cellVerticalAlignment))
			{
				widget.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
			}
		}
		
		
		Element child = ensureFirstChildSpan(element, true);
		
		if(child != null)
		{
			String type = child.getAttribute("_contentType");
			if("html".equals(type))
			{
				widget.setContentHtml(child.getInnerHTML());
			}
			else if("text".equals(type))
			{
				widget.setContentText(child.getInnerText());
			}
			else if("widget".equals(type))
			{
				Widget childWidget = createChildWidget(child, element.getId());
				widget.setContentWidget(childWidget);
				super.add(widget, childWidget, element, child);
			}
		}		
	}	
}