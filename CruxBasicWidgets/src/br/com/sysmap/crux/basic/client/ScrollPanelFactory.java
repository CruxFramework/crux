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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.factory.HasScrollHandlersFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a ScrollPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="scrollPanel", library="bas")
public class ScrollPanelFactory extends PanelFactory<ScrollPanel> 
       implements HasScrollHandlersFactory<ScrollPanel>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);

	public static enum VerticalScrollPosition{top,bottom};
	public static enum HorizontalScrollPosition{left,right};

	@Override
	public ScrollPanel instantiateWidget(Element element, String widgetId) 
	{
		return new ScrollPanel();
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="alwaysShowScrollBars", type=Boolean.class),
		@TagAttribute(value="verticalScrollPosition", type=VerticalScrollPosition.class, autoProcess=false),
		@TagAttribute(value="horizontalScrollPosition", type=HorizontalScrollPosition.class, autoProcess=false),
		@TagAttribute(value="ensureVisible", autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<ScrollPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		final ScrollPanel widget = context.getWidget();
		final String widgetId = context.getWidgetId();
		
		String verticalScrollPosition = element.getAttribute("_verticalScrollPosition");
		if (verticalScrollPosition != null && verticalScrollPosition.length() > 0)
		{
			if ("top".equals(verticalScrollPosition))
			{
				widget.scrollToTop();
			}
			else if ("bottom".equals(verticalScrollPosition))
			{
				widget.scrollToBottom();
			}
		}
		String horizontalScrollPosition = element.getAttribute("_horizontalScrollPosition");
		if (horizontalScrollPosition != null && horizontalScrollPosition.length() > 0)
		{
			if ("left".equals(horizontalScrollPosition))
			{
				widget.scrollToLeft();
			}
			else if ("right".equals(horizontalScrollPosition))
			{
				widget.scrollToRight();
			}
		}
	
		final String ensureVisible = element.getAttribute("_ensureVisible");
		if (ensureVisible != null && ensureVisible.length() > 0)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event) 
				{
					Widget c = Screen.get(ensureVisible);
					if (c == null)
					{
						throw new NullPointerException(messages.scrollPanelWidgetNotFound(widgetId, ensureVisible));
					}
					widget.ensureVisible(c);
				}
			});
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(ScrollPanel parent, Widget child, Element parentElement, Element childElement) 
	{
		parent.add(child);
	}
}
