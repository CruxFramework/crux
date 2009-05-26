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
package br.com.sysmap.crux.advanced.client.decoratedpanel;

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for Decorated Panel widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DecoratedPanelFactory extends AbstractDecoratedPanelFactory<DecoratedPanel>
{
	@Override
	protected DecoratedPanel instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String height = element.getAttribute("_height");
		String width = element.getAttribute("_width");
		String styleName = element.getAttribute("_styleName");
		return new DecoratedPanel(width, height, styleName);
	}

	@Override
	protected void processChildrenTags(DecoratedPanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		Element child = ensureFirstChildSpan(element, true);
		if(child != null)
		{
			String type = child.getAttribute("_contentType");
			if("html".equals(type))
			{
				widget.setContentHtml(child.getInnerHTML());
			}
			else if("text".equals(type))
			{
				widget.setContentText(child.getInnerText());
			}
			else if("widget".equals(type))
			{
				Element widgetElement = ensureFirstChildSpan(child, false);
				Widget childWidget = createChildWidget(ensureWidget(widgetElement), widgetElement.getId());
				widget.setContentWidget(childWidget);
				super.add(widget, childWidget, element, child);
			}
		}		
	}	
}