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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanelImages;

/**
 * Represents a HorizontalSplitPanel
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="horizontalSplitPanel", library="gwt")
public class HorizontalSplitPanelFactory extends PanelFactory<HorizontalSplitPanel>
{
	@Override
	public HorizontalSplitPanel instantiateWidget(Element element, String widgetId) {
		Event eventLoadImage = EvtBind.getWidgetEvent(element, Events.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			LoadImagesEvent<HorizontalSplitPanel> loadEvent = new LoadImagesEvent<HorizontalSplitPanel>(widgetId);
			HorizontalSplitPanelImages splitImages = (HorizontalSplitPanelImages) Events.callEvent(eventLoadImage, loadEvent);
			return new HorizontalSplitPanel(splitImages);
		}
		return new HorizontalSplitPanel();
	}

	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadImage")
	})
	public void processEvents(WidgetFactoryContext<HorizontalSplitPanel> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(LeftProcessor.class),
		@TagChild(RightProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<HorizontalSplitPanel> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="left", minOccurs="0")
	public static class LeftProcessor extends WidgetChildProcessor<HorizontalSplitPanel>
	{
		@Override
		@TagChildren({
			@TagChild(LeftWidgeProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<HorizontalSplitPanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="right", minOccurs="0")
	public static class RightProcessor extends WidgetChildProcessor<HorizontalSplitPanel>
	{
		@Override
		@TagChildren({
			@TagChild(RightWidgeProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<HorizontalSplitPanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(widgetProperty="leftWidget")
	public static class LeftWidgeProcessor extends AnyWidgetChildProcessor<HorizontalSplitPanel> {}
	
	@TagChildAttributes(widgetProperty="rightWidget")
	public static class RightWidgeProcessor extends AnyWidgetChildProcessor<HorizontalSplitPanel> {}
}
