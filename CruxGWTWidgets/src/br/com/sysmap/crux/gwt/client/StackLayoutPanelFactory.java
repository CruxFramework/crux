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
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="stackLayoutPanel", library="gwt")
public class StackLayoutPanelFactory extends WidgetFactory<StackLayoutPanel>
{
	@Override
	public StackLayoutPanel instantiateWidget(Element element, String widgetId) 
	{
		return new StackLayoutPanel(AbstractLayoutPanelFactory.getUnit(getProperty(element,"unit")));
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="unit", type=Unit.class, defaultValue="PX")
	})
	public void processAttributes(WidgetFactoryContext<StackLayoutPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(StackItemProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<StackLayoutPanel> context) throws InterfaceConfigException
	{
		super.processChildren(context);
	}

	@TagChildAttributes(tagName="item", maxOccurs="unbounded")
	public static class StackItemProcessor extends WidgetChildProcessor<StackLayoutPanel>
	{
		@Override
		@TagChildren({
			@TagChild(StackHeaderProcessor.class),
			@TagChild(StackContentProcessor.class)
		})
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="selected", type=Boolean.class, defaultValue="false")
		})
		public void processChildren(WidgetChildProcessorContext<StackLayoutPanel> context) throws InterfaceConfigException 
		{
			context.clearAttributes();
			String selected = context.readChildProperty("selected");
			if (!StringUtils.isEmpty(selected))
			{
				context.setAttribute("selected", Boolean.parseBoolean(selected));
			}
		}
	}

	@TagChildAttributes(tagName="header")
	public static class StackHeaderProcessor extends WidgetChildProcessor<StackLayoutPanel>
	{
		@Override
		@TagChildren({
			@TagChild(StackHeaderWidgetProcessor.class)
		})
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="size", type=Double.class, required=true)
		})
		public void processChildren(WidgetChildProcessorContext<StackLayoutPanel> context) throws InterfaceConfigException 
		{
			context.setAttribute("headerSize", Double.parseDouble(context.readChildProperty("size")));
		}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class StackHeaderWidgetProcessor extends WidgetChildProcessor<StackLayoutPanel>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<StackLayoutPanel> context) throws InterfaceConfigException
		{
			Widget childWidget = createChildWidget(context.getChildElement(), context.getRootElement().getId());
			context.setAttribute("header", childWidget);
		}
	}

	@TagChildAttributes(tagName="content")
	public static class StackContentProcessor extends WidgetChildProcessor<StackLayoutPanel>
	{
		@Override
		@TagChildren({
			@TagChild(StackContentWidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<StackLayoutPanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class StackContentWidgetProcessor extends WidgetChildProcessor<StackLayoutPanel>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<StackLayoutPanel> context) throws InterfaceConfigException
		{
			Widget contentWidget = createChildWidget(context.getChildElement(), context.getRootElement().getId());
			Widget headerWidget = (Widget) context.getAttribute("header");
			Double headerSize = (Double) context.getAttribute("headerSize");
			context.getRootWidget().add(contentWidget, headerWidget, headerSize);

			Boolean selected = (Boolean) context.getAttribute("selected");
			if (selected != null && selected)
			{
				context.getRootWidget().showWidget(contentWidget);
			}
		}
	}
}
