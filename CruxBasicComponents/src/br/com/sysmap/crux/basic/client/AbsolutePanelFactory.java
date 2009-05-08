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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class AbsolutePanelFactory extends ComplexPanelFactory<AbsolutePanel>
{

	@Override
	protected AbsolutePanel instantiateWidget(Element element, String widgetId)
	{
		return new AbsolutePanel();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.component.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(AbsolutePanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException
	{
		Element childElementParent = childElement.getParentElement();
		// there're cell attributes . 
		if (!parentElement.getId().equals(childElementParent.getId()))
		{
			String left = childElementParent.getAttribute("_left");
			String top = childElementParent.getAttribute("_top");
			if (left != null && left.length() > 0 && top != null && top.length() > 0)
			{
				parent.add(child, Integer.parseInt(left), Integer.parseInt(top));
			}
			else
			{
				parent.add(child);
			}
			parentElement.removeChild(childElementParent);
		}		
	}
}
