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
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanelImages;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a VerticalSplitPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="verticalSplitPanel", library="bas")
public class VerticalSplitPanelFactory extends SplitPanelFactory<VerticalSplitPanel>
{
	@Override
	public VerticalSplitPanel instantiateWidget(Element element, String widgetId) 
	{
		Event eventLoadImage = EvtBind.getWidgetEvent(element, Events.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			LoadImagesEvent<VerticalSplitPanel> loadEvent = new LoadImagesEvent<VerticalSplitPanel>(widgetId);
			VerticalSplitPanelImages splitImages = (VerticalSplitPanelImages) Events.callEvent(eventLoadImage, loadEvent);
			return new com.google.gwt.user.client.ui.VerticalSplitPanel(splitImages);
		}
		return new com.google.gwt.user.client.ui.VerticalSplitPanel();
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadImage")
	})
	public void processEvents(WidgetFactoryContext<VerticalSplitPanel> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	

	@Override
	protected void renderSplitItem(VerticalSplitPanel widget, Element element) throws InterfaceConfigException
	{
		String position = element.getAttribute("_position");
		if (position == null || position.length() == 0)
		{
			throw new InterfaceConfigException(messages.verticalSplitPanelInvalidPosition(element.getId()));
		}
		String id = element.getId();

		Element e = getComponentChildElement(element);
		if (e != null)
		{
			if (position.equals("top"))
			{

				widget.setTopWidget(createChildWidget(e, id));
			}
			else if (position.equals("bottom"))
			{
				widget.setBottomWidget(createChildWidget(e, id));
			}
			else
			{
				throw new InterfaceConfigException(messages.verticalSplitPanelInvalidPosition(element.getId()));
			}
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(VerticalSplitPanel parent, Widget child, Element parentElement, Element childElement) 
	{
		// TODO - does nothing, maybe we don't need extend hasWidgetFactory 
	}
}
