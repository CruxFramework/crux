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

import br.com.sysmap.crux.core.client.event.bind.ScrollEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a ScrollPanelFactory
 * @author Thiago Bustamante
 */
public class ScrollPanelFactory extends SimplePanelFactory 
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);

	@Override
	protected SimplePanel instantiateWidget(Element element, String widgetId) 
	{
		return new ScrollPanel();
	}
	
	@Override
	protected void processAttributes(SimplePanel widget, Element element, final String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		final ScrollPanel scrollPanel = (ScrollPanel)widget; 
		
		String alwaysShowScrollBars = element.getAttribute("_alwaysShowScrollBars");
		if (alwaysShowScrollBars != null && alwaysShowScrollBars.length() > 0)
		{
			scrollPanel.setAlwaysShowScrollBars(Boolean.parseBoolean(alwaysShowScrollBars));
		}
		String verticalScrollPosition = element.getAttribute("_verticalScrollPosition");
		if (verticalScrollPosition != null && verticalScrollPosition.length() > 0)
		{
			if ("top".equals(verticalScrollPosition))
			{
				scrollPanel.scrollToTop();
			}
			else if ("bottom".equals(verticalScrollPosition))
			{
				scrollPanel.scrollToBottom();
			}
		}
		String horizontalScrollPosition = element.getAttribute("_horizontalScrollPosition");
		if (horizontalScrollPosition != null && horizontalScrollPosition.length() > 0)
		{
			if ("left".equals(horizontalScrollPosition))
			{
				scrollPanel.scrollToLeft();
			}
			else if ("right".equals(horizontalScrollPosition))
			{
				scrollPanel.scrollToRight();
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
					scrollPanel.ensureVisible(c);
				}
			});
		}
	}
	
	@Override
	protected void processEvents(SimplePanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
	
		final ScrollPanel scrollPanel = (ScrollPanel)widget; 
		ScrollEvtBind.bindEvent(element, scrollPanel);
	}

}
