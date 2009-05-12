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

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.WidgetFactory;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuBar.MenuBarImages;
/**
 * Represents a MenuBarFactory component
 * @author Thiago Bustamante
 */
public class MenuBarFactory extends WidgetFactory<MenuBar> 
{
	public static final String ITEM_TYPE_HTML = "html";
	public static final String ITEM_TYPE_SEPARATOR = "separator";
	public static final String ITEM_TYPE_TEXT = "text";
	
	
	@Override
	protected MenuBar instantiateWidget(Element element, String widgetId) 
	{
		String verticalStr = element.getAttribute("_vertical");
		boolean vertical = false;
		if (verticalStr != null && verticalStr.length() > 0)
		{
			vertical = (Boolean.parseBoolean(verticalStr));
		}

		Event eventLoadImage = EvtBind.getWidgetEvent(element, EventFactory.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			LoadImagesEvent<MenuBar> loadEvent = new LoadImagesEvent<MenuBar>(widgetId);
			MenuBarImages menuBarImages = (MenuBarImages) EventFactory.callEvent(eventLoadImage, loadEvent);
			return new MenuBar(vertical, menuBarImages);
		}
		return new MenuBar(vertical);
	}
	
	@Override
	protected void processAttributes(MenuBar widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String autoOpen = element.getAttribute("_autoOpen");
		if (autoOpen != null && autoOpen.length() > 0)
		{
			widget.setAutoOpen(Boolean.parseBoolean(autoOpen));
		}
		
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			widget.setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		
		processMenuItens(widget, widgetId, element);
	}
	
	@Override
	protected void processEvents(MenuBar widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		CloseEvtBind.bindEvent(element, widget);
	}

	/**
	 * Populate the menuBar with declared itens
	 * @param element
	 * @throws InterfaceConfigException 
	 */
	protected void processMenuItens(MenuBar widget, String widgetId, Element element) throws InterfaceConfigException
	{
		List<Element> itensCandidates = ensureChildrenSpans(element, true);
		for (int i=0; i<itensCandidates.size(); i++)
		{
			Element e = (Element)itensCandidates.get(i);
			String type = e.getAttribute("_itemType");
			if (type == null || type.length() == 0)
			{
				throw new InterfaceConfigException();
				//TODO: add message
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
		String caption = element.getAttribute("_caption");
		if (caption != null && caption.length() > 0)
		{
			Command command = getCommand(widget, element, widgetId);
			if (command != null)
			{
				widget.addItem(caption, command);
			}
			else if (element.getChildNodes().getLength() > 0)
			{
				widget.addItem(caption, getSubMenu(widget, element));
			}
		}
	}

	/**
	 * Process Item declaration for MenuBarFactory
	 * @param element
	 */
	protected void processItemHTMLDeclaration(MenuBar widget, String widgetId, Element element, int index)
	{
		String caption = element.getInnerHTML();
		if (caption != null && caption.length() > 0)
		{
			Command command = getCommand(widget, element, widgetId);
			if (command != null)
			{
				widget.addItem(caption, true, command);
			}
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
		final Event evt = EvtBind.getWidgetEvent(element, "_onexecute");
		if (evt != null)
		{
			return new Command()
			{
				public void execute() 
				{
					EventFactory.callEvent(evt, new ExecuteEvent<MenuBar>(widget, widgetId));
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
		MenuBar subMenu = instantiateWidget(element, subMenuId);
		subMenu.setAutoOpen(widget.getAutoOpen());
		subMenu.setAnimationEnabled(widget.isAnimationEnabled());
		processMenuItens(subMenu, subMenuId, element);
		return subMenu;
	}
}
