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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class DeckPanelFactory extends ComplexPanelFactory<DeckPanel>
{
	@Override
	public void add(DeckPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
			parent.add(child);
	}

	@Override
	protected DeckPanel instantiateWidget(Element element, String widgetId) 
	{
		return new DeckPanel();
	}

	@Override
	protected void processAttributes(DeckPanel widget, Element element, String widgetId) 
	{
		super.processAttributes(widget, element, widgetId);
		
		String visibleWidget = element.getAttribute("_visibleWidget");
		if (visibleWidget != null && visibleWidget.length() > 0)
		{
			widget.showWidget(Integer.parseInt(visibleWidget));
		}
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			widget.setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
	}
	
}
