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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.gwt.client.align.AlignmentAttributeParser;
import br.com.sysmap.crux.gwt.client.align.HorizontalAlignment;
import br.com.sysmap.crux.gwt.client.align.VerticalAlignment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="dockPanel", library="gwt")
public class DockPanelFactory extends CellPanelFactory<DockPanel>
{
	public static enum DockDirection{center, lineStart, lineEnd, east, north, south, west}
	
	@Override
	public DockPanel instantiateWidget(Element element, String widgetId)
	{
		return new DockPanel();
	}

	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	public void processAttributes(WidgetFactoryContext<DockPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		
		DockPanel widget = context.getWidget();
		
		String cellHorizontalAlignment = context.readWidgetProperty("horizontalAlignment");
		if (cellHorizontalAlignment != null && cellHorizontalAlignment.length() > 0)
		{
			widget.setHorizontalAlignment(AlignmentAttributeParser.getHorizontalAlignment(cellHorizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
		}		
		String cellVerticalAlignment = context.readWidgetProperty("verticalAlignment");
		if (cellVerticalAlignment != null && cellVerticalAlignment.length() > 0)
		{
			widget.setVerticalAlignment(AlignmentAttributeParser.getVerticalAlignment(cellVerticalAlignment));
		}
	}
	
	@Override
	@TagChildren({
		@TagChild(DockPanelProcessor.class)
	})		
	public void processChildren(WidgetFactoryContext<DockPanel> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class DockPanelProcessor extends AbstractCellPanelProcessor<DockPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(DockCellProcessor.class),
			@TagChild(DockWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<DockPanel> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
			context.setAttribute("direction", "center");
		}
	}
	
	@TagChildAttributes(tagName="cell", minOccurs="0", maxOccurs="unbounded")
	public static class DockCellProcessor extends AbstractCellProcessor<DockPanel> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="direction", type=DockDirection.class, defaultValue="center")
		})
		@TagChildren({
			@TagChild(value=DockWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<DockPanel> context) throws InterfaceConfigException 
		{
			super.processChildren(context);

			String direction = context.readChildProperty("direction");
			if (StringUtils.isEmpty(direction))
			{
				direction = "center";
			}
			context.setAttribute("direction", direction);
		}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class DockWidgetProcessor extends AbstractCellWidgetProcessor<DockPanel> 
	{
		protected GWTMessages messages = GWT.create(GWTMessages.class);

		@Override
		public void processChildren(WidgetChildProcessorContext<DockPanel> context) throws InterfaceConfigException
		{
			String childId = context.getChildElement().getId();
			Widget child = createChildWidget(context.getChildElement(), childId);
			DockPanel parent = context.getRootWidget();
			
			String direction = (String) context.getAttribute("direction");
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
				throw new InterfaceConfigException(messages.dockPanelInvalidDirection(childId, context.getRootWidgetId()));
			}
			
			context.setAttribute("direction", "center");
			context.setAttribute("child", child);
			super.processChildren(context);
			context.setAttribute("child", null);
		}
	}
}
