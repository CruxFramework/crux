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
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@DeclarativeFactory(id="verticalPanel", library="bas")
public class VerticalPanelFactory extends CellPanelFactory<VerticalPanel>
{

	@Override
	public VerticalPanel instantiateWidget(Element element, String widgetId)
	{
		return new VerticalPanel();
	}

	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("horizontalAlignment"),
		@TagAttributeDeclaration("verticalAlignment")
	})
	public void processAttributes(WidgetFactoryContext<VerticalPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);

		Element element = context.getElement();
		VerticalPanel widget = context.getWidget();
		
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
	
	@Override
	@TagChildren({
		@TagChild(VerticalPanelProcessor.class)
	})		
	public void processChildren(WidgetFactoryContext<VerticalPanel> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class  VerticalPanelProcessor extends AbstractCellPanelProcessor<VerticalPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(VerticalProcessor.class),
			@TagChild(VerticalWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<VerticalPanel> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}
	
	public static class VerticalProcessor extends AbstractCellProcessor<VerticalPanel>
	{
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("horizontalAlignment"),
			@TagAttributeDeclaration("verticalAlignment")
		})
		@TagChildren({
			@TagChild(value=VerticalWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<VerticalPanel> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}
		
	public static class VerticalWidgetProcessor extends AbstractCellWidgetProcessor<VerticalPanel> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<VerticalPanel> context) throws InterfaceConfigException
		{
			Widget child = createChildWidget(context.getChildElement(), context.getChildElement().getId());
			context.getRootWidget().add(child);
			context.setAttribute("child", child);
			super.processChildren(context);
			context.setAttribute("child", null);
		}
	}	}
