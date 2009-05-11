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
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class CellPanelFactory <T extends CellPanel> extends ComplexPanelFactory<T>
{
	public void add(T parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException
	{
		Element childElementParent = childElement.getParentElement();
		// there're cell attributes . 
		if (!parentElement.getId().equals(childElementParent.getId()))
		{
			String cellHeight = childElementParent.getAttribute("_cellHeight");
			if (cellHeight != null && cellHeight.length() > 0)
			{
				parent.setCellHeight(child, cellHeight);
			}
			
			String cellHorizontalAlignment = childElementParent.getAttribute("_cellHorizontalAlignment");
			if (cellHorizontalAlignment != null && cellHorizontalAlignment.trim().length() > 0)
			{
				if (HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString().equals(cellHorizontalAlignment))
				{
					parent.setCellHorizontalAlignment(child, HasHorizontalAlignment.ALIGN_CENTER);
				}
				else if (HasHorizontalAlignment.ALIGN_DEFAULT.getTextAlignString().equals(cellHorizontalAlignment))
				{
					parent.setCellHorizontalAlignment(child, HasHorizontalAlignment.ALIGN_DEFAULT);
				}
				else if (HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString().equals(cellHorizontalAlignment))
				{
					parent.setCellHorizontalAlignment(child, HasHorizontalAlignment.ALIGN_LEFT);
				}
				else if (HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString().equals(cellHorizontalAlignment))
				{
					parent.setCellHorizontalAlignment(child, HasHorizontalAlignment.ALIGN_RIGHT);
				}
			}			
			
			String cellVerticalAlignment = childElementParent.getAttribute("_cellVerticalAlignment");
			if (cellVerticalAlignment != null && cellVerticalAlignment.trim().length() > 0)
			{
				if (HasVerticalAlignment.ALIGN_BOTTOM.getVerticalAlignString().equals(cellVerticalAlignment))
				{
					parent.setCellVerticalAlignment(child, HasVerticalAlignment.ALIGN_BOTTOM);
				}
				else if (HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString().equals(cellVerticalAlignment))
				{
					parent.setCellVerticalAlignment(child, HasVerticalAlignment.ALIGN_MIDDLE);
				}
				else if (HasVerticalAlignment.ALIGN_TOP.getVerticalAlignString().equals(cellVerticalAlignment))
				{
					parent.setCellVerticalAlignment(child, HasVerticalAlignment.ALIGN_TOP);
				}
			}	
			
			String cellWidth = childElementParent.getAttribute("_cellWidth");
			if (cellWidth != null && cellWidth.length() > 0)
			{
				parent.setCellWidth(child, cellWidth);
			}
			
		}
	}
	
	@Override
	protected void processAttributes(T widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);

		String borderWidth = element.getAttribute("_borderWidth");
		if (borderWidth != null && borderWidth.length() > 0)
		{
			widget.setBorderWidth(Integer.parseInt(borderWidth));
		}
	
		String spacing = element.getAttribute("_spacing");
		if (spacing != null && spacing.length() > 0)
		{
			widget.setSpacing(Integer.parseInt(spacing));
		}
	}
}