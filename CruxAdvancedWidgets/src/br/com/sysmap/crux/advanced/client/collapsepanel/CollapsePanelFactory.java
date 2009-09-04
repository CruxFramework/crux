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
package br.com.sysmap.crux.advanced.client.collapsepanel;

import br.com.sysmap.crux.advanced.client.event.LoadImagesEvent;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeCollapseEvtBind;
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeExpandEvtBind;
import br.com.sysmap.crux.advanced.client.titlepanel.AbstractTitlePanelFactory;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Collapse Panel widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="collapsePanel", library="adv")
public class CollapsePanelFactory extends AbstractTitlePanelFactory<CollapsePanel>
{
	@Override
	public CollapsePanel instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String height = element.getAttribute("_height");
		String width = element.getAttribute("_width");
		String styleName = element.getAttribute("_styleName");

		String strCollapsible = element.getAttribute("_collapsible");
		boolean collapsible = true;
		if(strCollapsible != null && strCollapsible.trim().length() > 0)
		{
			collapsible = Boolean.parseBoolean(strCollapsible);
		}
		
		String strCollapsed = element.getAttribute("_collapsed");
		boolean collapsed = false;
		if(strCollapsed != null && strCollapsed.trim().length() > 0)
		{
			collapsed = Boolean.parseBoolean(strCollapsed);
		}
		
		Event eventLoadImages = EvtBind.getWidgetEvent(element, Events.EVENT_LOAD_IMAGES);
		if (eventLoadImages != null)
		{
			LoadImagesEvent<CollapsePanel> loadEvent = new LoadImagesEvent<CollapsePanel>(widgetId);
			CollapsePanelImages images = (CollapsePanelImages) Events.callEvent(eventLoadImages, loadEvent);
			return new CollapsePanel(width, height, styleName, collapsible, collapsed, images);
		}
		
		return new CollapsePanel(width, height, styleName, collapsible, collapsed);
	}
	
	@Override
	@TagEvents({
		@TagEvent(BeforeCollapseEvtBind.class),
		@TagEvent(BeforeExpandEvtBind.class)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadImage")
	})
	public void processEvents(WidgetFactoryContext<CollapsePanel> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="collapsed", type=Boolean.class, autoProcess=false),
		@TagAttribute(value="collapsible", type=Boolean.class, autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<CollapsePanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
}