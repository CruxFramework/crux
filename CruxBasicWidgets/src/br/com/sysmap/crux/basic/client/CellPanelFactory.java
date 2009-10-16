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

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;

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
	private static final String DEFAULT_V_ALIGN = HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString();
	private static final String DEFAULT_H_ALIGN = HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString();
	
	@Override
	@TagAttributes({
		@TagAttribute(value="borderWidth",type=Integer.class),
		@TagAttribute(value="spacing",type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(CellPanelProcessor.class)
	})		
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException {}
	
	public static class CellPanelProcessor extends AbstractCellPanelProcessor<CellPanel>{} 

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static abstract class AbstractCellPanelProcessor<T extends CellPanel> extends ChoiceChildProcessor<T> 
	{
		@Override
		@TagChildren({
			@TagChild(CellProcessor.class),
			@TagChild(CellWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			context.setAttribute("horizontalAlignment", DEFAULT_H_ALIGN);
			context.setAttribute("verticalAlignment", DEFAULT_V_ALIGN);
		}
	}
	
	public static class CellProcessor extends AbstractCellProcessor<CellPanel>{}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static abstract class AbstractCellProcessor<T extends CellPanel> extends WidgetChildProcessor<T> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("horizontalAlignment"),
			@TagAttributeDeclaration("verticalAlignment")
		})
		@TagChildren({
			@TagChild(value=CellWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			Element childElement = context.getChildElement();
			
			context.setAttribute("height", childElement.getAttribute("_height"));
			context.setAttribute("width", childElement.getAttribute("_width"));
			context.setAttribute("horizontalAlignment", childElement.getAttribute("_horizontalAlignment"));
			context.setAttribute("verticalAlignment", childElement.getAttribute("_verticalAlignment"));
		}
	}
	
	public static class CellWidgetProcessor extends AbstractCellWidgetProcessor<CellPanel> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<CellPanel> context) throws InterfaceConfigException
		{
			Widget child = createChildWidget(context.getChildElement(), context.getChildElement().getId());
			context.setAttribute("child", child);
			super.processChildren(context);
			context.setAttribute("child", null);
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	static class AbstractCellWidgetProcessor<T extends CellPanel> extends WidgetChildProcessor<T> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			T parent = context.getRootWidget();
			Widget child = (Widget) context.getAttribute("child");
			String cellHeight = (String) context.getAttribute("height");
			if (cellHeight != null && cellHeight.length() > 0)
			{
				parent.setCellHeight(child, cellHeight);
			}
			
			String cellHorizontalAlignment = (String) context.getAttribute("horizontalAlignment");
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
			
			String cellVerticalAlignment = (String) context.getAttribute("verticalAlignment");
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
			
			String cellWidth = (String) context.getAttribute("width");
			if (cellWidth != null && cellWidth.length() > 0)
			{
				parent.setCellWidth(child, cellWidth);
			}
			
			context.setAttribute("height", null);
			context.setAttribute("width", null);
			context.setAttribute("horizontalAlignment", DEFAULT_H_ALIGN);
			context.setAttribute("verticalAlignment", DEFAULT_V_ALIGN);
		}
	}
}