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
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="absolutePanel", library="gwt")
public class AbsolutePanelFactory extends ComplexPanelFactory<AbsolutePanel>
{

	@Override
	public AbsolutePanel instantiateWidget(Element element, String widgetId)
	{
		return new AbsolutePanel();
	}

	@Override
	@TagChildren({
		@TagChild(AbsoluteChildrenProcessor.class)
	})	
	public void processChildren(WidgetFactoryContext<AbsolutePanel> context) throws InterfaceConfigException
	{
	}	
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="widget" )
	public static class AbsoluteChildrenProcessor extends WidgetChildProcessor<AbsolutePanel> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("left"),
			@TagAttributeDeclaration("top")
		})
		@TagChildren({
			@TagChild(AbsoluteWidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<AbsolutePanel> context) throws InterfaceConfigException
		{
			String left = context.readChildProperty("left");
			String top = context.readChildProperty("top");
			context.setAttribute("left", left);
			context.setAttribute("top", top);
		}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class AbsoluteWidgetProcessor extends WidgetChildProcessor<AbsolutePanel> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<AbsolutePanel> context) throws InterfaceConfigException
		{
			String left = (String) context.getAttribute("left");
			String top = (String) context.getAttribute("top");

			Widget child = createChildWidget(context.getChildElement(), context.getChildElement().getId());
			if (left != null && left.length() > 0 && top != null && top.length() > 0)
			{
				context.getRootWidget().add(child, Integer.parseInt(left), Integer.parseInt(top));
			}
			else
			{
				context.getRootWidget().add(child);
			}
			
			context.setAttribute("left", null);
			context.setAttribute("top", null);
		}
	}	
}
