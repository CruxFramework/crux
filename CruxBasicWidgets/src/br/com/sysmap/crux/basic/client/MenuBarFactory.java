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
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuBar.MenuBarImages;
/**
 * Represents a MenuBarFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="menuBar", library="bas")
public class MenuBarFactory extends WidgetFactory<MenuBar> 
       implements HasAnimationFactory<MenuBar>
{
	public static final String ITEM_TYPE_HTML = "html";
	public static final String ITEM_TYPE_SEPARATOR = "separator";
	public static final String ITEM_TYPE_TEXT = "text";
	
	protected BasicMessages messages = GWT.create(BasicMessages.class);
	
	@Override
	public MenuBar instantiateWidget(Element element, String widgetId) 
	{
		String verticalStr = element.getAttribute("_vertical");
		boolean vertical = false;
		if (verticalStr != null && verticalStr.length() > 0)
		{
			vertical = (Boolean.parseBoolean(verticalStr));
		}

		Event eventLoadImage = EvtBind.getWidgetEvent(element, Events.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			LoadImagesEvent<MenuBar> loadEvent = new LoadImagesEvent<MenuBar>(widgetId);
			MenuBarImages menuBarImages = (MenuBarImages) Events.callEvent(eventLoadImage, loadEvent);
			return new MenuBar(vertical, menuBarImages);
		}
		return new MenuBar(vertical);
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="autoOpen", type=Boolean.class),
		@TagAttribute(value="vertical", type=Boolean.class, autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<MenuBar> context) throws InterfaceConfigException
	{
		super.processAttributes(context);		
	}
	
	@Override
	@TagEvents({
		@TagEvent(CloseEvtBind.class)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadImage")
	})
	public void processEvents(WidgetFactoryContext<MenuBar> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}

	@Override
	public void processChildren(WidgetFactoryContext<MenuBar> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		MenuBar widget = context.getWidget();
		
		List<Element> itensCandidates = ensureChildrenSpans(element, true);
		for (int i=0; i<itensCandidates.size(); i++)
		{
			Element e = (Element)itensCandidates.get(i);
			String type = e.getAttribute("_itemType");
			String widgetId = context.getWidgetId();
			
			if (type == null || type.length() == 0)
			{
				throw new InterfaceConfigException(messages.menuBarItemTypeEmpty(widgetId));
			}
			if (type.equals(ITEM_TYPE_TEXT))
			{
				processItemTextDeclaration(widget, widgetId, e, i);
			}
			else if (type.equals(ITEM_TYPE_SEPARATOR))
			{
				processItemSeparatorMenuDeclaration(widget, e, i);
			}
			else if (type.equals(ITEM_TYPE_HTML))
			{
				processItemHTMLDeclaration(widget, widgetId, e, i);
			}	
		}
	}

	/**
	 * Process Item declaration for MenuBarFactory
	 * @param element
	 * @throws InterfaceConfigException 
	 */
	protected void processItemTextDeclaration(MenuBar widget, String widgetId, Element element, int index) throws InterfaceConfigException
	{
		List<Element> children = ensureChildrenSpans(element, true);
		
		String caption = null ;
		Command command = null;
		MenuBar itens = null;
		
		// has caption
		if(children.size() > 0)
		{
			Element child = children.get(0);
			caption = child.getInnerText(); 
			command = getCommand(widget, element, widgetId);
		}
		
		// has items
		if(children.size() > 1)
		{
			Element child = children.get(1);
			itens = getSubMenu(widget, ensureFirstChildSpan(child, false));
		}		
		
		if (command != null)
		{
			widget.addItem(caption, command);
		}
		else if(itens != null)
		{
			widget.addItem(caption, itens);
		}
		else
		{
			GWT.log(messages.menubarItemWithoutChildrenOrCommand(caption), null);
		}		
	}

	/**
	 * Process Item declaration for MenuBarFactory
	 * @param element
	 * @throws InterfaceConfigException 
	 */
	protected void processItemHTMLDeclaration(MenuBar widget, String widgetId, Element element, int index) throws InterfaceConfigException
	{
		List<Element> children = ensureChildrenSpans(element, true);
		
		String caption = null ;
		Command command = null;
		MenuBar itens = null;
		
		// has caption
		if(children.size() > 0)
		{
			Element child = children.get(0);
			caption = child.getInnerHTML(); 
			command = getCommand(widget, element, widgetId);
		}
		
		// has items
		if(children.size() > 1)
		{
			Element child = children.get(1);
			itens = getSubMenu(widget, ensureFirstChildSpan(child, false));
		}		
		
		if (command != null)
		{
			widget.addItem(caption, true, command);
		}
		else if(itens != null)
		{
			widget.addItem(caption, true, itens);
		}
		else
		{
			GWT.log(messages.menubarItemWithoutChildrenOrCommand(caption), null);
		}
	}

	/**
	 * Process Item declaration for MenuBarFactory
	 * @param element
	 */
	protected void processItemSeparatorMenuDeclaration(MenuBar widget, Element element, int index)
	{
		widget.addSeparator();
	}

	/**
	 * Creates a Command for encapsulate the event declared on the span tag
	 * @param element
	 * @return
	 */
	protected Command getCommand(final MenuBar widget,Element element, final String widgetId)
	{
		final Event evt = EvtBind.getWidgetEvent(element, Events.EVENT_EXECUTE_EVENT);
		if (evt != null)
		{
			return new Command()
			{
				public void execute() 
				{
					Events.callEvent(evt, new ExecuteEvent<MenuBar>(widget, widgetId));
				}
			};
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Creates a subMenu, based on inner span tags
	 * @param element
	 * @return
	 * @throws InterfaceConfigException 
	 */
	protected MenuBar getSubMenu(MenuBar widget, Element element) throws InterfaceConfigException
	{
		String subMenuId = element.getId();
		MenuBar subMenu = (MenuBar) createChildWidget(element, subMenuId);	
		subMenu.setAutoOpen(widget.getAutoOpen());
		subMenu.setAnimationEnabled(widget.isAnimationEnabled());
		return subMenu;
	}
}
