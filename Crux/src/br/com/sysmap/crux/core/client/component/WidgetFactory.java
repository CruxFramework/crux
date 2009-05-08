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
package br.com.sysmap.crux.core.client.component;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for gwt widgets. It creates widgets based on a span tag contained
 * in the host HTML page. It provides a declarative way to create widgets.
 * 
 * The following example shows how to create a widget in a declarative way:
 * 
 *  &lt;span id="myWidgetId" _type="textBox" 
 *                           _onclick="myControlClass.myethod" &gt; 
 *  &lt;/span&gt;
 * @author Thiago Bustamante
 */
public abstract class WidgetFactory <T extends Widget>
{	
	/**
	 * Return the current screen 
	 * @return
	 */
	public Screen getScreen() 
	{
		return ScreenFactory.getInstance().getScreen();
	}
	
	public T createWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		T widget = instantiateWidget(element, widgetId);
		getScreen().addWidget(widgetId, widget);
		processAttributes(widget, element, widgetId);
		processEvents(widget, element, widgetId);
		return widget;
	}
	
	protected abstract T instantiateWidget(Element element, String widgetId);
	

	/**
	 * Process widget attributes
	 * @param element page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @throws InterfaceConfigException 
	 */
	protected void processAttributes(T widget, Element element, String widgetId) throws InterfaceConfigException
	{
		String width = element.getAttribute("_width");
		if (width != null && width.length() > 0)
		{
			widget.setWidth(width);
		}
		String height = element.getAttribute("_height");
		if (height != null && height.length() > 0)
		{
			widget.setHeight(height);
		}
		String visible = element.getAttribute("_visible");
		if (visible != null && visible.length() > 0)
		{
			widget.setVisible(Boolean.parseBoolean(visible));
		}
		String tooltip = element.getAttribute("_tooltip");
		if (tooltip != null && tooltip.length() > 0)
		{
			widget.setTitle(tooltip);
		}
		String styleName = element.getAttribute("_styleName");
		if (styleName != null && styleName.length() > 0)
		{
			widget.setStyleName(styleName);
		}
		String style = element.getAttribute("_style");
		if (style != null && style.length() > 0)
		{
			String[] styleAttributes = style.split(";");
			for (int i=0; i<styleAttributes.length; i++)
			{
				String[] attr = styleAttributes[i].split(":");
				if (attr != null && attr.length == 2)
					DOM.setStyleAttribute(widget.getElement(), attr[0], attr[1]);
			}
		}
		
		if (widget instanceof HasHTML)
		{
			String innerHtml = element.getInnerHTML();
			if (innerHtml != null && innerHtml.length() > 0)
			{
				((HasHTML)widget).setHTML(innerHtml);
				element.setInnerHTML("");
			}
		}
		if (widget instanceof HasText)
		{
			String text = element.getAttribute("_text");
			if (text != null && text.length() > 0)
				((HasText)widget).setText(text);
		}
		if (widget instanceof HasName)
		{
			String name = element.getAttribute("_name");
			if (name != null && name.length() > 0)
				((HasName)widget).setName(name);
		}
	}
	
	/**
	 * Process widget events
	 * @param element page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @throws InterfaceConfigException
	 */
	protected void processEvents(T widget, Element element, String widgetId) throws InterfaceConfigException
	{

	}
	
	/**
	 * Adds an event handler, called when the screen is completely loaded
	 * @param loadHandler
	 */
	protected void addScreenLoadedHandler(ScreenLoadHandler loadHandler)
	{
		getScreen().addLoadHandler(loadHandler);
	}

	/**
	 * Used by widgets that need to create new widgets as children, like tree. 
	 * 
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected Widget createChildWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return ScreenFactory.getInstance().newWidget(element, widgetId);
	}
}
