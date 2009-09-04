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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasOpenHandlersFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DisclosurePanelImages;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for DisclosurePanel widgets
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="disclosurePanel", library="bas")
public class DisclosurePanelFactory extends CompositeFactory<DisclosurePanel> 
       implements HasWidgetsFactory<DisclosurePanel>, HasAnimationFactory<DisclosurePanel>, 
                  HasOpenHandlersFactory<DisclosurePanel>, HasCloseHandlersFactory<DisclosurePanel>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);
	
	@Override
	@TagAttributes({
		@TagAttribute(value="open", type=Boolean.class),
		@TagAttribute(value="headerText", autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<DisclosurePanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}

	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadImage")
	})
	public void processEvents(WidgetFactoryContext<DisclosurePanel> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}

	@Override
	public void processChildren(WidgetFactoryContext<DisclosurePanel> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		DisclosurePanel widget = context.getWidget();
		
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
	public DisclosurePanel instantiateWidget(Element element, String widgetId) 
	{
		String headerText = element.getAttribute("_headerText");
		Event eventLoadImages = EvtBind.getWidgetEvent(element, Events.EVENT_LOAD_IMAGES);
		
		if (eventLoadImages != null)
		{
			LoadImagesEvent<DisclosurePanel> loadEvent = new LoadImagesEvent<DisclosurePanel>(widgetId);
			DisclosurePanelImages images = (DisclosurePanelImages) Events.callEvent(eventLoadImages, loadEvent);
			return new DisclosurePanel(images, headerText, false);
		}
		
		return new DisclosurePanel(headerText, false);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(DisclosurePanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
		// nothing to do here
	}
}
