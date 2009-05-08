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

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class StackPanelFactory extends ComplexPanelFactory<StackPanel>
{
	@Override
	public void add(StackPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
		Element childElementParent = childElement.getParentElement();
		// there's no stack text. 
		if (parentElement.getId().equals(childElementParent.getId()))
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
				Element stackTextSpan = childElementParent.getFirstChildElement();
				if (stackTextSpan != null && !isWidget(stackTextSpan))
				{
					parent.add(child, stackTextSpan.getInnerHTML(), true);
				}
				else
				{
					throw new InterfaceConfigException();
					//TODO: colocar mensagem
				}
			}
			parentElement.removeChild(childElementParent);
		}
	}

	@Override
	protected StackPanel instantiateWidget(Element element, String widgetId) 
	{
		return new StackPanel();
	}

	@Override
	protected void processAttributes(StackPanel widget, Element element, String widgetId) throws InterfaceConfigException 
	{
		super.processAttributes(widget, element, widgetId);
		
		String visibleStack = element.getAttribute("_visibleStack");
		if (visibleStack != null && visibleStack.length() > 0)
		{
			widget.showStack(Integer.parseInt(visibleStack));
		}
	}
	
}
