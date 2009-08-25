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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@DeclarativeFactory(id="dockPanel", library="bas")
public class DockPanelFactory extends CellPanelFactory<DockPanel>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);

	@Override
	protected DockPanel instantiateWidget(Element element, String widgetId)
	{
		return new DockPanel();
	}

	@Override
	public void add(DockPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException
	{
		Element childElementParent = childElement.getParentElement();
		// there's no cell attributes . 
		if (!parentElement.getId().equals(childElementParent.getId()))
		{
			String direction = childElementParent.getAttribute("_direction");
			if("center".equals(direction))
			{
				parent.add(child, DockPanel.CENTER);
			}
			else if("line_start".equals(direction))
			{
				parent.add(child, DockPanel.LINE_START);
			}
			else if("line_end".equals(direction))
			{
				parent.add(child, DockPanel.LINE_END);
			}
			else if("east".equals(direction))
			{
				parent.add(child, DockPanel.EAST);
			}
			else if("north".equals(direction))
			{
				parent.add(child, DockPanel.NORTH);
			}
			else if("south".equals(direction))
			{
				parent.add(child, DockPanel.SOUTH);
			}
			else if("west".equals(direction))
			{
				parent.add(child, DockPanel.WEST);
			}
			else
			{
				throw new InterfaceConfigException(messages.dockPanelInvalidDirection(childElement.getId(), parentElement.getId()));
			}
			
			super.add(parent, child, parentElement, childElement);
		}
		else
		{
			parent.add(child, DockPanel.CENTER);
		}
	}
	
	@Override
	protected void processAttributes(DockPanel widget, Element element, String widgetId) throws InterfaceConfigException
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
		
	}
}
