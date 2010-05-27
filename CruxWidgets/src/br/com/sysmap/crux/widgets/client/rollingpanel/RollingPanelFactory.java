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
package br.com.sysmap.crux.widgets.client.rollingpanel;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.gwt.client.align.AlignmentAttributeParser;
import br.com.sysmap.crux.gwt.client.align.HorizontalAlignment;
import br.com.sysmap.crux.gwt.client.align.VerticalAlignment;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="rollingPanel", library="widgets")
public class RollingPanelFactory extends WidgetFactory<RollingPanel>
{

	@Override
	public RollingPanel instantiateWidget(Element element, String widgetId)
	{
		String verticalAttr = element.getAttribute("_vertical");
		boolean vertical = false;
		if (!StringUtils.isEmpty(verticalAttr))
		{
			vertical = Boolean.parseBoolean(verticalAttr);
		}
		return new RollingPanel(vertical);
	}

	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="vertical", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	@TagAttributes({
		@TagAttribute("horizontalNextButtonStyleName"),
		@TagAttribute("horizontalPreviousButtonStyleName"),
		@TagAttribute("verticalNextButtonStyleName"),
		@TagAttribute("verticalPreviousButtonStyleName"),
		@TagAttribute(value="spacing", type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<RollingPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);

		Element element = context.getElement();
		RollingPanel widget = context.getWidget();
		
		String cellHorizontalAlignment = element.getAttribute("_horizontalAlignment");
		if (cellHorizontalAlignment != null && cellHorizontalAlignment.length() > 0)
		{
			widget.setHorizontalAlignment(AlignmentAttributeParser.getHorizontalAlignment(cellHorizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
		}
		
		String cellVerticalAlignment = element.getAttribute("_verticalAlignment");
		if (cellVerticalAlignment != null && cellVerticalAlignment.length() > 0)
		{
			widget.setVerticalAlignment(AlignmentAttributeParser.getVerticalAlignment(cellVerticalAlignment));
		}
	}
	
	@Override
	@TagChildren({
		@TagChild(RollingPanelProcessor.class)
	})		
	public void processChildren(WidgetFactoryContext<RollingPanel> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class  RollingPanelProcessor extends ChoiceChildProcessor<RollingPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(RollingCellProcessor.class),
			@TagChild(VerticalWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<RollingPanel> context) throws InterfaceConfigException  {}
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static class RollingCellProcessor extends WidgetChildProcessor<RollingPanel>
	{
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		@TagChildren({
			@TagChild(value=VerticalWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<RollingPanel> context) throws InterfaceConfigException 
		{
			Element childElement = context.getChildElement();
			
			context.setAttribute("height", childElement.getAttribute("_height"));
			context.setAttribute("width", childElement.getAttribute("_width"));
			context.setAttribute("horizontalAlignment", childElement.getAttribute("_horizontalAlignment"));
			context.setAttribute("verticalAlignment", childElement.getAttribute("_verticalAlignment"));
		}
	}
		
	@TagChildAttributes(type=AnyWidget.class)
	public static class VerticalWidgetProcessor extends WidgetChildProcessor<RollingPanel> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<RollingPanel> context) throws InterfaceConfigException
		{
			Widget child = createChildWidget(context.getChildElement(), context.getChildElement().getId());
			context.getRootWidget().add(child);

			String cellHeight = (String) context.getAttribute("height");
			if (cellHeight != null && cellHeight.length() > 0)
			{
				context.getRootWidget().setCellHeight(child, cellHeight);
			}
			
			String cellHorizontalAlignment = (String) context.getAttribute("horizontalAlignment");
			if (cellHorizontalAlignment != null && cellHorizontalAlignment.length() > 0)
			{
				context.getRootWidget().setCellHorizontalAlignment(child, 
					  AlignmentAttributeParser.getHorizontalAlignment(cellHorizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
			}
			String cellVerticalAlignment = (String) context.getAttribute("verticalAlignment");
			if (cellVerticalAlignment != null && cellVerticalAlignment.length() > 0)
			{
				context.getRootWidget().setCellVerticalAlignment(child, AlignmentAttributeParser.getVerticalAlignment(cellVerticalAlignment));
			}
			String cellWidth = (String) context.getAttribute("width");
			if (cellWidth != null && cellWidth.length() > 0)
			{
				context.getRootWidget().setCellWidth(child, cellWidth);
			}
			
			context.setAttribute("height", null);
			context.setAttribute("width", null);
			context.setAttribute("horizontalAlignment", null);
			context.setAttribute("verticalAlignment", null);
		}
	}	
}
