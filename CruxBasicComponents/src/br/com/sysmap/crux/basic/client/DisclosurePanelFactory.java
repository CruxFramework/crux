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

import java.util.List;

import br.com.sysmap.crux.core.client.component.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.event.bind.OpenEvtBind;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DisclosurePanelImages;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for DisclosurePanel widgets
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DisclosurePanelFactory extends CompositeFactory<DisclosurePanel> implements HasWidgetsFactory<DisclosurePanel>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);
	
	@Override
	protected void processAttributes(DisclosurePanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String open = element.getAttribute("_open");
		if (open != null && open.trim().length() > 0)
		{
			widget.setOpen(Boolean.parseBoolean(open));
		}
		
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.trim().length() > 0)
		{
			widget.setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		
		addChildWidgets(widget, element);
	}

	/**
	 * @param widget
	 * @param element
	 * @throws InterfaceConfigException
	 */
	private void addChildWidgets(DisclosurePanel widget, Element element) throws InterfaceConfigException
	{
		List<Element> childSpans = ensureChildrenSpans(element, true);
		
		if(childSpans.size() <= 2)
		{
			for (int i = 0; i < childSpans.size(); i++)
			{
				Element childElement = ensureWidget(childSpans.get(0));
				Widget childWidget = createChildWidget(childElement, childElement.getId());
				
				// contains widgets for header and body 
				if(childSpans.size() > 1)
				{
					// it's the header
					if(i == 0)
					{
						widget.setHeader(childWidget);
					}
					
					// it's the body
					else
					{
						widget.setContent(childWidget);
					}
				}
				else
				{
					widget.setContent(childWidget);
				}
			}
		}
		else
		{
			throw new InterfaceConfigException(messages.disclosurePanelInvalidChildrenElements(element.getId()));
		}
	}
	
	@Override
	protected DisclosurePanel instantiateWidget(Element element, String widgetId) 
	{
		String headerText = element.getAttribute("_headerText");
		Event eventLoadImages = EvtBind.getWidgetEvent(element, EventFactory.EVENT_LOAD_IMAGES);
		
		if (eventLoadImages != null)
		{
			LoadImagesEvent<DisclosurePanel> loadEvent = new LoadImagesEvent<DisclosurePanel>(widgetId);
			DisclosurePanelImages images = (DisclosurePanelImages) EventFactory.callEvent(eventLoadImages, loadEvent);
			return new DisclosurePanel(images, headerText, false);
		}
		
		return new DisclosurePanel(headerText, false);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.component.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(DisclosurePanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
		// nothing to do here
	}
	
	@Override
	protected void processEvents(DisclosurePanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		CloseEvtBind.bindEvent(element, widget);
		OpenEvtBind.bindEvent(element, widget);
	}
}
