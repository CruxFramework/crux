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
package br.com.sysmap.crux.advanced.client.titlepanel;

import java.util.List;

import br.com.sysmap.crux.advanced.client.decoratedpanel.AbstractDecoratedPanelFactory;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for Title Panel widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="titlePanel", library="adv")
public class TitlePanelFactory<T extends TitlePanel> extends AbstractDecoratedPanelFactory<T>
{
	@Override
	@SuppressWarnings("unchecked")
	protected T instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String height = element.getAttribute("_height");
		String width = element.getAttribute("_width");
		String styleName = element.getAttribute("_styleName");
		return (T) new TitlePanel(width, height, styleName);
	}	

	/**
	 * @param widget
	 * @param element
	 * @param child
	 * @param type
	 * @throws InterfaceConfigException
	 */
	private void setTitleContent(T widget, Element element, Element child, String type) throws InterfaceConfigException
	{
		if("html".equals(type))
		{
			widget.setTitleHtml(child.getInnerHTML());
		}
		else if("text".equals(type))
		{
			String innerText = getI18NText(child);			
			widget.setTitleText(innerText);
		}
		else if("widget".equals(type))
		{
			Element widgetElement = ensureFirstChildSpan(child, false);
			Widget childWidget = createChildWidget(ensureWidget(widgetElement), widgetElement.getId());
			widget.setTitleWidget(childWidget);
			super.add(widget, childWidget, element, child);
		}
	}

	private String getI18NText(Element child)
	{
		String innerText = child.getInnerText();
		
		if(innerText != null)
		{
			innerText = innerText.trim();
			
			if(innerText.length() > 0)
			{
				innerText = ScreenFactory.getInstance().getDeclaredMessage(innerText);
			}
			else
			{
				innerText = "";
			}
		}
		else
		{
			innerText = "";
		}
		return innerText;
	}

	/**
	 * @param widget
	 * @param element
	 * @param child
	 * @param type
	 * @throws InterfaceConfigException
	 */
	private void setBodyContent(T widget, Element element, Element child, String type) throws InterfaceConfigException
	{
		if("html".equals(type))
		{
			widget.setContentHtml(child.getInnerHTML());
		}
		else if("text".equals(type))
		{
			String innerText = getI18NText(child);			
			widget.setContentText(innerText);
		}
		else if("widget".equals(type))
		{
			Element widgetElement = ensureFirstChildSpan(child, false);
			Widget childWidget = createChildWidget(ensureWidget(widgetElement), widgetElement.getId());
			widget.setContentWidget(childWidget);
			super.add(widget, childWidget, element, child);
		}
	}

	@Override
	protected void processChildrenTags(T widget, Element element, String widgetId) throws InterfaceConfigException
	{
		List<Element> children = ensureChildrenSpans(element, true);
		
		for (Element child : children)
		{
			String type = child.getAttribute("_contentType");
			String part = child.getAttribute("_part");
			
			if("body".equals(part))
			{
				setBodyContent(widget, element, child, type);
			}
			else if("title".equals(part))
			{
				setTitleContent(widget, element, child, type);
			}
		}		
	}
}