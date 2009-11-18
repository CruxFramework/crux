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
package br.com.sysmap.crux.widgets.client.paging;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.widgets.client.event.paging.PageEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="sequentialPager", library="widgets")
public class SequentialPagerFactory extends WidgetFactory<SequentialPager>
{
	/**
	 * @param cellSpacing 
	 * @param autoLoad 
	 * @see br.com.sysmap.crux.core.client.screen.WidgetFactory#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	public SequentialPager instantiateWidget(Element elem, String widgetId) throws InterfaceConfigException
	{
		return new SequentialPager();
	}

	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("pageable"),
		@TagAttributeDeclaration(value="enabled", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<SequentialPager> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	
		Element element = context.getElement();
		final SequentialPager widget = context.getWidget();

		final String pageableId = element.getAttribute("_pageable");
		final String strEnabled = element.getAttribute("_enabled");
		
		addScreenLoadedHandler(
				
			new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent screenLoadEvent)
				{					
					Widget pageable = null;
					if(pageableId != null)
					{
						pageable = Screen.get(pageableId);
					}
					
					if(pageable != null)
					{
						widget.setPageable((Pageable) pageable);
						if(strEnabled != null && strEnabled.length() > 0)
						{
							widget.setEnabled(Boolean.parseBoolean(strEnabled));
						}
					}
					else
					{
						throw new RuntimeException(""); // TODO
					}							
				}				
			}		
		);
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onPage")
	})
	public void processEvents(WidgetFactoryContext<SequentialPager> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		SequentialPager widget = context.getWidget();

		PageEvtBind.bindEvent(element, widget);
		super.processEvents(context);
	}
}