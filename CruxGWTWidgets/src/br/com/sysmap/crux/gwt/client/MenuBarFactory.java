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
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
/**
 * Represents a MenuBarFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="menuBar", library="gwt")
public class MenuBarFactory extends WidgetFactory<MenuBar> 
       implements HasAnimationFactory<MenuBar>
{
	public static final String ITEM_TYPE_HTML = "html";
	public static final String ITEM_TYPE_SEPARATOR = "separator";
	public static final String ITEM_TYPE_TEXT = "text";
	
	protected GWTMessages messages = GWT.create(GWTMessages.class);
	
	@Override
	public MenuBar instantiateWidget(Element element, String widgetId) 
	{
		String verticalStr = element.getAttribute("_vertical");
		boolean vertical = false;
		if (verticalStr != null && verticalStr.length() > 0)
		{
			vertical = (Boolean.parseBoolean(verticalStr));
		}

		return new MenuBar(vertical);
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="autoOpen", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="vertical", type=Boolean.class)
	})
	public void processAttributes(WidgetFactoryContext<MenuBar> context) throws InterfaceConfigException
	{
		super.processAttributes(context);		
	}
	
	@Override
	@TagEvents({
		@TagEvent(CloseEvtBind.class)
	})
	public void processEvents(WidgetFactoryContext<MenuBar> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}

	@Override
	@TagChildren({
		@TagChild(MenuItemsProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<MenuBar> context) throws InterfaceConfigException {}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class MenuItemsProcessor extends ChoiceChildProcessor<MenuBar>
	{
		@Override
		@TagChildren({
			@TagChild(TextProcessor.class),
			@TagChild(HTMLProcessor.class),
			@TagChild(SeparatorProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="text")
	public static class TextProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		@TagChildren({
			@TagChild(CaptionProcessor.class),
			@TagChild(SubItemsProcessor.class)
		})
		public void processChildren(final WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			context.setAttribute("textCaption", null);
			processCommandAttribute(context);
		}
	}
	
	@TagChildAttributes(tagName="caption")
	public static class CaptionProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			String captionText = context.getChildElement().getInnerHTML();
			Command command = (Command) context.getAttribute("command");
			if (command != null)
			{
				
				context.getRootWidget().addItem(captionText, command);
			}
			else
			{
				context.setAttribute("textCaption", captionText);
			}
		}
	}
	
	@TagChildAttributes(tagName="html")
	public static class HTMLProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		@TagChildren({
			@TagChild(HTMLCaptionProcessor.class),
			@TagChild(SubItemsProcessor.class)
		})
		public void processChildren(final WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			context.setAttribute("htmlCaption", null);
			processCommandAttribute(context);
		}
	}
	
	@TagChildAttributes(tagName="caption")
	public static class HTMLCaptionProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			String captionHTML = context.getChildElement().getInnerHTML();
			Command command = (Command) context.getAttribute("command");
			if (command != null)
			{
				
				context.getRootWidget().addItem(captionHTML, true, command);
			}
			else
			{
				context.setAttribute("htmlCaption", captionHTML);
			}
		}
	}

	@TagChildAttributes(tagName="items", minOccurs="0", maxOccurs="1")
	public static class SubItemsProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		@TagChildren({
			@TagChild(SubItemProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public static class SubItemProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			MenuBar subMenu = getSubMenu(context.getRootWidget(), context.getChildElement());
			String textCaption = (String) context.getAttribute("textCaption");
			if (textCaption != null)
			{
				context.getRootWidget().addItem(textCaption, subMenu);
			}
			else
			{
				String htmlCaption = (String)context.getAttribute("htmlCaption");
				context.getRootWidget().addItem(htmlCaption, true, subMenu);
			}
		}
	}
	
	@TagChildAttributes(tagName="separator")
	public static class SeparatorProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		public void processChildren(final WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			context.getRootWidget().addSeparator();
		}
	}
	
	/**
	 * Creates a subMenu, based on inner span tags
	 * @param element
	 * @return
	 * @throws InterfaceConfigException 
	 */
	protected static MenuBar getSubMenu(MenuBar widget, Element element) throws InterfaceConfigException
	{
		String subMenuId = element.getId();
		MenuBar subMenu = (MenuBar) createChildWidget(element, subMenuId);	
		subMenu.setAutoOpen(widget.getAutoOpen());
		subMenu.setAnimationEnabled(widget.isAnimationEnabled());
		return subMenu;
	}
	
	/**
	 * 
	 * @param context
	 */
	protected static void processCommandAttribute(final WidgetChildProcessorContext<MenuBar> context)
	{
		final Event evt = EvtBind.getWidgetEvent(context.getChildElement(), Events.EVENT_EXECUTE_EVENT);
		if (evt != null)
		{
			Command cmd =  new Command()
			{
				public void execute() 
				{
					Events.callEvent(evt, new ExecuteEvent<MenuBar>(context.getRootWidget(), context.getRootWidgetId()));
				}
			};
			context.setAttribute("command", cmd);
		}
	}
}
