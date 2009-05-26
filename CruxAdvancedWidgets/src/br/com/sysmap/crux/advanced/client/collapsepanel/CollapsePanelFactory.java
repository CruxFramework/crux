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
import br.com.sysmap.crux.advanced.client.event.collapseexpand.BeforeCollapseOrExpandEvtBind;
import br.com.sysmap.crux.advanced.client.titlepanel.TitlePanelFactory;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Collapse Panel widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CollapsePanelFactory extends TitlePanelFactory<CollapsePanel>
{
	@Override
	protected CollapsePanel instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
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
		
		Event eventLoadImages = EvtBind.getWidgetEvent(element, EventFactory.EVENT_LOAD_IMAGES);
		if (eventLoadImages != null)
		{
			LoadImagesEvent<CollapsePanel> loadEvent = new LoadImagesEvent<CollapsePanel>(widgetId);
			CollapsePanelImages images = (CollapsePanelImages) EventFactory.callEvent(eventLoadImages, loadEvent);
			return new CollapsePanel(width, height, styleName, collapsible, collapsed, images);
		}
		
		return new CollapsePanel(width, height, styleName, collapsible, collapsed);
	}
	
	@Override
	protected void processEvents(CollapsePanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		BeforeCollapseOrExpandEvtBind.bindEvents(element, widget);
	}
}