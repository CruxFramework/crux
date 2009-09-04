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
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractStackPanelFactory<T extends StackPanel> extends ComplexPanelFactory<T>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);
	
	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(T parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
		Element childElementParent = childElement.getParentElement();
		
		String parentId = parentElement.getId();
		String childParentId = childElementParent.getId();
		
		// there's no stack text. 
		if (parentId.equals(childParentId))
		{
			parent.add(child);
		}
		else 
		{
			String stackText = childElementParent.getAttribute("_widgetTitle");
			// stack text as text
			if (stackText != null && stackText.trim().length() > 0)
			{
				parent.add(child, stackText);
			}
			// stack text as html
			else
			{
				Element stackTextSpan = ensureFirstChildSpan(childElementParent, true);
				if (stackTextSpan != null && !isWidget(stackTextSpan))
				{
					parent.add(child, stackTextSpan.getInnerHTML(), true);
				}
				else
				{
					throw new InterfaceConfigException(messages.stackPanelIvalidChild(childElement.getId(), parentElement.getId()));
				}
			}
		}
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="visibleStack", type=Integer.class, autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		final T widget = context.getWidget();

		final String visibleStack = element.getAttribute("_visibleStack");
		if (visibleStack != null && visibleStack.length() > 0)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					widget.showStack(Integer.parseInt(visibleStack));
				}
			});
		}
	}
	
}
