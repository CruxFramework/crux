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
package br.com.sysmap.crux.advanced.client.filter;

import br.com.sysmap.crux.advanced.client.AdvancedWidgetMessages;
import br.com.sysmap.crux.basic.client.SuggestBoxFactory;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for Filter widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="filter", library="adv")
public class FilterFactory extends SuggestBoxFactory
{
	AdvancedWidgetMessages messages = GWT.create(AdvancedWidgetMessages.class);
	
	@Override
	protected Filter instantiateWidget(final Element element, String widgetId) throws InterfaceConfigException
	{
		final Filter filter = new Filter();
				
		addScreenLoadedHandler(
			
			new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent screenLoadEvent)
				{					
					String filterableId = element.getAttribute("_filterable");
					Widget filterableWidget = null;
					if(filterableId != null)
					{
						filterableWidget = Screen.get(filterableId);
					}
					
					if(filterableWidget != null)
					{
						filter.setFilterable((Filterable<?>) filterableWidget);
					}
					else
					{
						throw new RuntimeException(messages.filterableNotFoundWhenInstantiantingFilter(filterableId));
					}							
				}				
			}		
		);
		
		return filter;	
	}
}