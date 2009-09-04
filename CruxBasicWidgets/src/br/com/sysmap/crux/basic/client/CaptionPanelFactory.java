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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for CaptionPanel widgets
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="captionPanel", library="bas")
public class CaptionPanelFactory extends CompositeFactory<CaptionPanel> implements HasWidgetsFactory<CaptionPanel>
{
	@Override
	@TagAttributes({
		@TagAttribute("captionText")
	})
	public void processAttributes(WidgetFactoryContext<CaptionPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	public void processChildren(WidgetFactoryContext<CaptionPanel> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		CaptionPanel widget = context.getWidget();

		List<Element> children = ensureChildrenSpans(element, true);
		
		for (Element child : children)
		{
			// It's an HTML caption
			if(!isWidget(child))
			{
				widget.setCaptionHTML(child.getInnerHTML());
			}
			
			// Is a widget content
			else
			{
				widget.setContentWidget(createChildWidget(child, child.getId()));
			}
		}
	}
	
	@Override
	public CaptionPanel instantiateWidget(Element element, String widgetId) 
	{
		return new CaptionPanel();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(CaptionPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
		// nothing to do here
	}
}
