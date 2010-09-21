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
import br.com.sysmap.crux.gwt.client.align.AlignmentAttributeParser;
import br.com.sysmap.crux.gwt.client.align.HorizontalAlignment;
import br.com.sysmap.crux.gwt.client.align.VerticalAlignment;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="horizontalPanel", library="gwt")
public class HorizontalPanelFactory extends CellPanelFactory<HorizontalPanel>
{

	@Override
	public HorizontalPanel instantiateWidget(Element element, String widgetId)
	{
		return new HorizontalPanel();
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	public void processAttributes(WidgetFactoryContext<HorizontalPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		
		HorizontalPanel widget = context.getWidget();
		
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
		@TagChild(HorizontalPanelProcessor.class)
	})		
	public void processChildren(WidgetFactoryContext<HorizontalPanel> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class  HorizontalPanelProcessor extends AbstractCellPanelProcessor<HorizontalPanel> 
	{
		@Override
		@TagChildren({
			@TagChild(HorizontalProcessor.class),
			@TagChild(HorizontalWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<HorizontalPanel> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}
	
	public static class HorizontalProcessor extends AbstractCellProcessor<HorizontalPanel>
	{
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		@TagChildren({
			@TagChild(value=HorizontalWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<HorizontalPanel> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}
		
	public static class HorizontalWidgetProcessor extends AbstractCellWidgetProcessor<HorizontalPanel> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<HorizontalPanel> context) throws InterfaceConfigException
		{
			Widget child = createChildWidget(context.getChildElement(), context.getChildElement().getId());
			context.getRootWidget().add(child);
			context.setAttribute("child", child);
			super.processChildren(context);
			context.setAttribute("child", null);
		}
	}	
}
