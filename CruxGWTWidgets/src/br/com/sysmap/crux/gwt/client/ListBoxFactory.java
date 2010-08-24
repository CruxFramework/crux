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
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Represents a List Box component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="listBox", library="gwt")
public class ListBoxFactory extends AbstractListBoxFactory<ListBox>
{
	@Override
	@TagChildren({
		@TagChild(ListBoxItemsProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<ListBox> context) throws InterfaceConfigException {}	
	
	public static class ListBoxItemsProcessor extends ItemsProcessor<ListBox>
	{		
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="multiple", type=Boolean.class)
	})
	public void processAttributes(br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext<ListBox> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}

	@Override
	public ListBox instantiateWidget(Element element, String widgetId) 
	{
		String multiple = element.getAttribute("_multiple");
		if (multiple != null && multiple.trim().length() > 0)
		{
			return new ListBox(Boolean.parseBoolean(multiple));

		}
		
		return new ListBox();
	}
}