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
package br.com.sysmap.crux.widgets.client.wizard;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@DeclarativeFactory(id="wizardPage", library="widgets")
public class WizardPageFactory extends WidgetFactory<WizardPage>
{
	@Override
    public WizardPage instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
    {
	    return new WizardPage();
    }

	@Override
	@TagEvents({
		@TagEvent(EnterEvtBind.class),
		@TagEvent(LeaveEvtBind.class)
	})
	public void processEvents(WidgetFactoryContext<WizardPage> context) throws InterfaceConfigException
	{
	    super.processEvents(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(CommandsProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<WizardPage> context) throws InterfaceConfigException
	{
	}
	
	@TagChildAttributes(tagName="commands", minOccurs="0")
	public static class CommandsProcessor extends WidgetChildProcessor<WizardPage>
	{
		@Override
		@TagChildren({
			@TagChild(WizardCommandsProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<WizardPage> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="command", maxOccurs="unbounded")
	public static class WizardCommandsProcessor extends WidgetChildProcessor<WizardPage>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="order", required=true, type=Integer.class),
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration(value="onCommand", required=true)
		})
		public void processChildren(WidgetChildProcessorContext<WizardPage> context) throws InterfaceConfigException 
		{
			String id = context.getChildElement().getAttribute("id");
			String label = ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getAttribute("_label"));
			int order = Integer.parseInt(context.getChildElement().getAttribute("_order"));
			
			final Event commandEvent = EvtBind.getWidgetEvent(context.getChildElement(), "onCommand");
			
			WizardCommandHandler handler = new WizardCommandHandler()
			{
				public void onCommand(WizardCommandEvent event)
				{
					Events.callEvent(commandEvent, event);
				}
			};
			
			context.getRootWidget().addCommand(id, label, handler, order);
			
			String styleName = context.getChildElement().getAttribute("_styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				context.getRootWidget().getCommand(id).setStyleName(styleName);
			}
			String width = context.getChildElement().getAttribute("_width");
			if (!StringUtils.isEmpty(width))
			{
				context.getRootWidget().getCommand(id).setWidth(width);
			}
			String height = context.getChildElement().getAttribute("_height");
			if (!StringUtils.isEmpty(height))
			{
				context.getRootWidget().getCommand(id).setHeight(height);
			}
		}
	}
}
