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
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
/**
 * Represents a MenuBarFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="menuBar", library="gwt")
public class MenuBarFactory extends WidgetFactory<MenuBar> 
       implements HasAnimationFactory<MenuBar>
{
	private static final String CURRENT_MENU_ITEM_CAPTION = "textItem";
	private static final String CURRENT_MENU_ITEM_IS_HTML = "isHtml";

	protected GWTMessages messages = GWT.create(GWTMessages.class);
	
	@Override
	public MenuBar instantiateWidget(Element element, String widgetId) 
	{
		return new MenuBar(isMenuVertical(element));
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
		@TagChild(MenutItemsProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<MenuBar> context) throws InterfaceConfigException {}

	private boolean isMenuVertical(Element element)
	{
		String verticalStr = element.getAttribute("_vertical");
		boolean vertical = false;
		if (verticalStr != null && verticalStr.length() > 0)
		{
			vertical = (Boolean.parseBoolean(verticalStr));
		}
		return vertical;
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class MenutItemsProcessor extends ChoiceChildProcessor<MenuBar>
	{
		@Override
		@TagChildren({
			@TagChild(MenutItemProcessor.class),
			@TagChild(MenutItemSeparatorProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="menuItem")
	public static class MenutItemProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		@TagChildren({
			@TagChild(CaptionProcessor.class),
			@TagChild(MenuChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="separator")
	public static class MenutItemSeparatorProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		public void processChildren(final WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			context.getRootWidget().addSeparator();
		}
	}
	
	public static class CaptionProcessor extends ChoiceChildProcessor<MenuBar>
	{
		@Override
		@TagChildren({
			@TagChild(TextCaptionProcessor.class),
			@TagChild(HtmlCaptionProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(tagName="textCaption")
	public static class TextCaptionProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="text", required=true, supportsI18N=true)
		})
		public void processChildren(final WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			String captionText = context.getChildElement().getAttribute("_text");
			context.setAttribute(CURRENT_MENU_ITEM_CAPTION, captionText);
			context.setAttribute(CURRENT_MENU_ITEM_IS_HTML, false);
		}
	}
	
	@TagChildAttributes(tagName="htmlCaption", type=AnyTag.class)
	public static class HtmlCaptionProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		public void processChildren(final WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
		{
			String captionHtml = context.getChildElement().getInnerHTML();
			context.setAttribute(CURRENT_MENU_ITEM_CAPTION, captionHtml);
			context.setAttribute(CURRENT_MENU_ITEM_IS_HTML, true);
		}
	}

	public static class MenuChildrenProcessor extends ChoiceChildProcessor<MenuBar>
	{
		@Override
		@TagChildren({
			@TagChild(CommandProcessor.class),
			@TagChild(SubMenuProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="command")
	public static class CommandProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="onExecute", required=true)
		})
		public void processChildren(final WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException 
		{
			final Event evt = EvtBind.getWidgetEvent(context.getChildElement(), "onExecute");
			if (evt != null)
			{
				MenuItem item = createMenuItem(context);
				Command cmd =  new Command()
				{
					public void execute() 
					{
						Events.callEvent(evt, new ExecuteEvent<MenuBar>(context.getRootWidget(), context.getRootWidgetId()));
					}
				};
				item.setCommand(cmd);
			}
			context.clearAttributes();
		}
	}

	@TagChildAttributes(tagName="subMenu", type=MenuBarFactory.class)
	public static class SubMenuProcessor extends WidgetChildProcessor<MenuBar>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException 
		{
			MenuBar subMenu = getSubMenu(context);
			MenuItem item = createMenuItem(context);
			item.setSubMenu(subMenu);
		}
	}
	
	/**
	 * Creates a subMenu, based on inner span tags
	 * @param element
	 * @return
	 * @throws InterfaceConfigException 
	 */
	protected static MenuBar getSubMenu(WidgetChildProcessorContext<MenuBar> context) throws InterfaceConfigException
	{
		MenuBar widget = context.getRootWidget();
		Element element = context.getChildElement();
			
		String subMenuId = element.getId();
		if (StringUtils.isEmpty(subMenuId))
		{
			subMenuId = generateNewId();
		}
		
		MenuBar subMenu = (MenuBar) createChildWidget(element, subMenuId);	
		subMenu.setAutoOpen(widget.getAutoOpen());
		subMenu.setAnimationEnabled(widget.isAnimationEnabled());
		return subMenu;
	}
	
	/**
	 * @param context
	 * @return
	 */
	protected static  MenuItem createMenuItem(final WidgetChildProcessorContext<MenuBar> context)
	{
		String caption = (String) context.getAttribute(CURRENT_MENU_ITEM_CAPTION);
		Boolean isHtml = (Boolean) context.getAttribute(CURRENT_MENU_ITEM_IS_HTML);
		if (isHtml==null)
		{
			isHtml = false;
		}
		return context.getRootWidget().addItem(new MenuItem(caption, isHtml, (Command)null));
	}
}
