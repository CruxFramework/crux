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

import java.util.List;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Panel;

/**
 * Represents a SplitPanelFactory
 * @author Thiago Bustamante
 */
public abstract class SplitPanelFactory <T extends Panel> extends PanelFactory<T>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);
	
	/**
	 * Render internal widgets
	 */
	@Override
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		T widget = context.getWidget();
		
		List<Element> itensCandidates = ensureChildrenSpans(element, false);
		for (int i=0; i<itensCandidates.size(); i++)
		{
			Element e = (Element)itensCandidates.get(i);
			renderSplitItem(widget, e);
		}
	}	
	
	protected abstract void renderSplitItem(T widget, Element element) throws InterfaceConfigException;
	
	protected Element getComponentChildElement(Element element) throws InterfaceConfigException
	{
		List<Element> itensCandidates = ensureChildrenSpans(element, true);
		for (int i = 0; i<itensCandidates.size(); i++)
		{
			Element e = itensCandidates.get(i);
			String type = e.getAttribute("_type");
			if (type != null && type.length() > 0 && !type.equals("screen"))
			{
				return e;
			}
		}
		return null;
	}
}
