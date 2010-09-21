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
package br.com.sysmap.crux.widgets.client.filter;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.factory.HasAllKeyHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasValueChangeHandlersFactory;
import br.com.sysmap.crux.gwt.client.CompositeFactory;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for Filter widget
 * @author Gessé S. F. Dafé
 */
@DeclarativeFactory(id="filter", library="widgets")
public class FilterFactory extends CompositeFactory<Filter> 
	   implements HasAnimationFactory<Filter>, HasTextFactory<Filter>, 
	              HasValueChangeHandlersFactory<Filter>, HasSelectionHandlersFactory<Filter>,
	              HasAllKeyHandlersFactory<Filter>
{
	@Override
	public Filter instantiateWidget(final Element element, String widgetId) throws InterfaceConfigException
	{
		return new Filter();	
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="accessKey", type=Character.class),
		@TagAttribute(value="autoSelectEnabled", type=Boolean.class),
		@TagAttribute(value="focus", type=Boolean.class),
		@TagAttribute(value="limit", type=Integer.class),
		@TagAttribute("popupStyleName"),
		@TagAttribute(value="tabIndex", type=Integer.class),
		@TagAttribute("value")
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("filterable")
	})
	public void processAttributes(final WidgetFactoryContext<Filter> context) throws InterfaceConfigException
	{
		final Filter widget = context.getWidget();

		super.processAttributes(context);
		addScreenLoadedHandler(
				new ScreenLoadHandler()
				{
					public void onLoad(ScreenLoadEvent screenLoadEvent)
					{					
						String filterableId =context.readWidgetProperty("filterable");
						Widget filterableWidget = null;
						if(filterableId != null)
						{
							filterableWidget = Screen.get(filterableId);
						}
						
						if(filterableWidget != null)
						{
							widget.setFilterable((Filterable<?>) filterableWidget);
						}
						else
						{
							throw new RuntimeException(WidgetMsgFactory.getMessages().filterableNotFoundWhenInstantiantingFilter(filterableId));
						}							
					}				
				}		
			);
	}
	
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadOracle")
	})
	public void processEvents(WidgetFactoryContext<Filter> context) throws InterfaceConfigException
	{
		super.processEvents(context);		
	}
}