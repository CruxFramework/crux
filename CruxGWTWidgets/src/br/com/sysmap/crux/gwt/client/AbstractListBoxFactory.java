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

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.factory.HasChangeHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Base class for implementing factories for many kinds of list boxes.
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractListBoxFactory<T extends ListBox> extends FocusWidgetFactory<T> implements HasChangeHandlersFactory<T>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleItemCount", type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context); 
	}

	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded")
	public abstract static class ItemsProcessor<T extends ListBox> extends WidgetChildProcessor<T>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("value"),
			@TagAttributeDeclaration("label"),
			@TagAttributeDeclaration(value="selected", type=Boolean.class)
		})
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			Integer index = (Integer) context.getAttribute("index");
			if (index == null)
			{
				index = 0;
			}

			Element element = context.getChildElement();
			
			String label = element.getAttribute("_label");
			String value = element.getAttribute("_value");
			
			if(label != null && label.length() > 0)
			{
				label = ScreenFactory.getInstance().getDeclaredMessage(label);
			}			
			if (value == null || value.length() == 0)
			{
				value = label;
			}
			context.getRootWidget().insertItem(label, value, index);

			String selected = element.getAttribute("_selected");
			if (selected != null && selected.trim().length() > 0)
			{
				context.getRootWidget().setItemSelected(index, Boolean.parseBoolean(selected));
			}
			context.setAttribute("index", (index+1));
		}
	}
}