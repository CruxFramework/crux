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
package br.com.sysmap.crux.widgets.client.stackmenu;

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Stack Menu
 * @author Gessé S. F. Dafé
 */
@br.com.sysmap.crux.core.client.declarative.DeclarativeFactory(id="stackMenuItem", library="widgets")
public class StackMenuItemFactory extends WidgetFactory<StackMenuItem>
       implements HasClickHandlersFactory<StackMenuItem>
{
	@Override
	public StackMenuItem instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String label = element.getAttribute("_label"); 
		StackMenuItem stackMenuItem = new StackMenuItem(ScreenFactory.getInstance().getDeclaredMessage(label));
		stackMenuItem.getElement().setId(widgetId);
		return stackMenuItem;
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="open", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label", supportsI18N=true, required=true)
	})
	public void processAttributes(WidgetFactoryContext<StackMenuItem> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(StackMenuItemProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<StackMenuItem> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded", type=StackMenuItemFactory.class)
	public static class StackMenuItemProcessor extends WidgetChildProcessor<StackMenuItem>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<StackMenuItem> context) throws InterfaceConfigException 
		{
			Element childElement = context.getChildElement();
			StackMenuItem childWidget = (StackMenuItem)createChildWidget(childElement, childElement.getId());
			context.getRootWidget().add(childWidget);
		}
	}
}