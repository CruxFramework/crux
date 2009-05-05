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

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.component.UIObject;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar.MenuBarImages;
/**
 * Represents a MenuBar component
 * @author Thiago Bustamante
 */
public class MenuBar extends Component 
{
	public static final String ITEM_TYPE_HTML = "html";
	public static final String ITEM_TYPE_SEPARATOR = "separator";
	public static final String ITEM_TYPE_TEXT = "text";
	
	protected com.google.gwt.user.client.ui.MenuBar menuBarWidget;
	protected List<UIObject> itens = new ArrayList<UIObject>();
	
	public MenuBar(String id, Element element) 
	{
		this(id, createMenuBarWidget(id, element));
	}

	public MenuBar(String id, boolean vertical, MenuBarImages menuBarImages) 
	{
		this(id, new com.google.gwt.user.client.ui.MenuBar(vertical, menuBarImages));
	}

	public MenuBar(String id, boolean vertical) 
	{
		this(id, new com.google.gwt.user.client.ui.MenuBar(vertical));
	}

	protected MenuBar(String id, com.google.gwt.user.client.ui.MenuBar widget) 
	{
		super(id, widget);
		this.menuBarWidget = widget;
	}

	/**
	 * Creates the MenuBar widget
	 * @param element
	 * @return
	 */
	private static com.google.gwt.user.client.ui.MenuBar createMenuBarWidget(String id, Element element)
	{
		String verticalStr = element.getAttribute("_vertical");
		boolean vertical = false;
		if (verticalStr != null && verticalStr.length() > 0)
		{
			vertical = (Boolean.parseBoolean(verticalStr));
		}

		Event eventLoadImage = EvtBind.getComponentEvent(element, EventFactory.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			MenuBarImages menuBarImages = (MenuBarImages) EventFactory.callEvent(eventLoadImage, id);
			return new com.google.gwt.user.client.ui.MenuBar(vertical, menuBarImages);
		}
		return new com.google.gwt.user.client.ui.MenuBar(vertical);
	}
	
	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		
		String autoOpen = element.getAttribute("_autoOpen");
		if (autoOpen != null && autoOpen.length() > 0)
		{
			setAutoOpen(Boolean.parseBoolean(autoOpen));
		}
		
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		
		renderMenuItens(element);
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		
		CloseEvtBind.bindEvent(element, menuBarWidget, getId());
	}

	/**
	 * Populate the menuBar with declared itens
	 * @param element
	 */
	protected void renderMenuItens(Element element)
	{
		NodeList<Node> itensCandidates = element.getChildNodes();
		for (int i=0; i<itensCandidates.getLength(); i++)
		{
			if (isValidItem(itensCandidates.getItem(i)))
			{
				Element e = (Element)itensCandidates.getItem(i);
				String type = e.getAttribute("_itemType");
				if (type.equals(ITEM_TYPE_TEXT))
				{
					processItemTextDeclaration(e, i);
				}
				else if (type.equals(ITEM_TYPE_SEPARATOR))
				{
					processItemSeparatorMenuDeclaration(e, i);
				}
				else if (type.equals(ITEM_TYPE_HTML))
				{
					processItemHTMLDeclaration(e, i);
				}	
			}
		}
	}

	/**
	 * Verify if the span tag found is a valid item declaration for menuBars
	 * @param element
	 * @return
	 */
	protected boolean isValidItem(Node node)
	{
		if (node instanceof Element)
		{
			Element element = (Element)node;
			if ("span".equalsIgnoreCase(element.getTagName()))
			{
				String type = element.getAttribute("_itemType");
				if (type != null && type.length() > 0)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Process Item declaration for MenuBar
	 * @param element
	 */
	protected void processItemTextDeclaration(Element element, int index)
	{
		String caption = element.getAttribute("_caption");
		if (caption != null && caption.length() > 0)
		{
			Command command = getCommand(element);
			if (command != null)
			{
				addItem(caption, command);
			}
			else if (element.getChildNodes().getLength() > 0)
			{
				addItem(caption, getSubMenu(element));
			}
		}
	}

	/**
	 * Process Item declaration for MenuBar
	 * @param element
	 */
	protected void processItemHTMLDeclaration(Element element, int index)
	{
		String caption = element.getInnerHTML();
		if (caption != null && caption.length() > 0)
		{
			Command command = getCommand(element);
			if (command != null)
			{
				addItem(caption, true, command);
			}
		}
	}

	/**
	 * Process Item declaration for MenuBar
	 * @param element
	 */
	protected void processItemSeparatorMenuDeclaration(Element element, int index)
	{
		addSeparator();
	}

	/**
	 * Creates a Command for encapsulate the event declared on the span tag
	 * @param element
	 * @return
	 */
	protected Command getCommand(Element element)
	{
		final Event evt = EvtBind.getComponentEvent(element, "_onexecute");
		if (evt != null)
		{
			return new Command()
			{
				public void execute() 
				{
					EventFactory.callEvent(evt, getId());
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
	 */
	protected MenuBar getSubMenu(Element element)
	{
		MenuBar subMenu = new MenuBar(element.getAttribute("id"), element);
		subMenu.setAutoOpen(getAutoOpen());
		subMenu.setAnimationEnabled(isAnimationEnabled());
		subMenu.renderMenuItens(element);
		return subMenu;
	}
	
	/**
	 * Adds a menu item to the bar.
	 * 
	 * @param item the item to be added
	 * @return the {@link MenuItem} object
	 */
	public MenuItem addItem(MenuItem item) 
	{
		menuBarWidget.addItem(item.menuItem);
		itens.add(item);
		item.setParentMenu(this);
		return item;
	}

	/**
	 * Adds a menu item to the bar, that will fire the given command when it is
	 * selected.
	 * 
	 * @param text the item's text
	 * @param asHTML <code>true</code> to treat the specified text as html
	 * @param cmd the command to be fired
	 * @return the {@link MenuItem} object created
	 */
	public MenuItem addItem(String text, boolean asHTML, Command cmd) 
	{
		return addItem(new MenuItem(menuBarWidget.addItem(text, asHTML, cmd)));
	}

	/**
	 * Adds a menu item to the bar, that will open the specified menu when it is
	 * selected.
	 * 
	 * @param text the item's text
	 * @param asHTML <code>true</code> to treat the specified text as html
	 * @param popup the menu to be cascaded from it
	 * @return the {@link MenuItem} object created
	 */
	public MenuItem addItem(String text, boolean asHTML, MenuBar popup) 
	{
		return addItem(new MenuItem(menuBarWidget.addItem(text, asHTML, popup.menuBarWidget)));
	}

	/**
	 * Adds a menu item to the bar, that will fire the given command when it is
	 * selected.
	 * 
	 * @param text the item's text
	 * @param cmd the command to be fired
	 * @return the {@link MenuItem} object created
	 */
	public MenuItem addItem(String text, Command cmd) 
	{
		return addItem(new MenuItem(menuBarWidget.addItem(text, cmd)));
	}

	/**
	 * Adds a menu item to the bar, that will open the specified menu when it is
	 * selected.
	 * 
	 * @param text the item's text
	 * @param popup the menu to be cascaded from it
	 * @return the {@link MenuItem} object created
	 */
	public MenuItem addItem(String text, MenuBar popup) 
	{
		return addItem(new MenuItem(menuBarWidget.addItem(text, popup.menuBarWidget)));
	}

	/**
	 * Adds a thin line to the {@link MenuBar} to separate sections of
	 * {@link MenuItem}s.
	 * 
	 * @return the {@link MenuItemSeparator} object created
	 */
	public MenuItemSeparator addSeparator() 
	{
		return addSeparator(new MenuItemSeparator());
	}

	/**
	 * Adds a thin line to the {@link MenuBar} to separate sections of
	 * {@link MenuItem}s.
	 * 
	 * @param separator the {@link MenuItemSeparator} to be added
	 * @return the {@link MenuItemSeparator} object
	 */
	public MenuItemSeparator addSeparator(MenuItemSeparator separator) 
	{
		menuBarWidget.addSeparator(separator.itemSeparator);
		itens.add(separator);
		return separator;
	}

	/**
	 * Removes all menu items from this menu bar.
	 */
	public void clearItems() 
	{
		menuBarWidget.clearItems();
		itens.clear();
	}

	/**
	 * Gets whether this menu bar's child menus will open when the mouse is moved
	 * over it.
	 * 
	 * @return <code>true</code> if child menus will auto-open
	 */
	public boolean getAutoOpen() 
	{
		return menuBarWidget.getAutoOpen();
	}

	/**
	 * Get the index of a {@link MenuItem}.
	 * 
	 * @return the index of the item, or -1 if it is not contained by this MenuBar
	 */
	public int getItemIndex(MenuItem item) 
	{
		return menuBarWidget.getItemIndex(item.menuItem);
	}

	/**
	 * Get the index of a {@link MenuItemSeparator}.
	 * 
	 * @return the index of the separator, or -1 if it is not contained by this
	 *         MenuBar
	 */
	public int getSeparatorIndex(MenuItemSeparator item) 
	{
		return itens.indexOf(item);
	}

	/**
	 * Adds a menu item to the bar at a specific index.
	 * 
	 * @param item the item to be inserted
	 * @param beforeIndex the index where the item should be inserted
	 * @return the {@link MenuItem} object
	 * @throws IndexOutOfBoundsException if <code>beforeIndex</code> is out of
	 *           range
	 */
	public MenuItem insertItem(MenuItem item, int beforeIndex)
	throws IndexOutOfBoundsException 
	{
		menuBarWidget.insertItem(item.menuItem, beforeIndex);
		itens.add(beforeIndex, item);
		return item;
	}

	/**
	 * Adds a thin line to the {@link MenuBar} to separate sections of
	 * {@link MenuItem}s at the specified index.
	 * 
	 * @param beforeIndex the index where the seperator should be inserted
	 * @return the {@link MenuItemSeparator} object
	 * @throws IndexOutOfBoundsException if <code>beforeIndex</code> is out of
	 *           range
	 */
	public MenuItemSeparator insertSeparator(int beforeIndex) 
	{
		return insertSeparator(new MenuItemSeparator(), beforeIndex);
	}

	/**
	 * Adds a thin line to the {@link MenuBar} to separate sections of
	 * {@link MenuItem}s at the specified index.
	 * 
	 * @param separator the {@link MenuItemSeparator} to be inserted
	 * @param beforeIndex the index where the seperator should be inserted
	 * @return the {@link MenuItemSeparator} object
	 * @throws IndexOutOfBoundsException if <code>beforeIndex</code> is out of
	 *           range
	 */
	public MenuItemSeparator insertSeparator(MenuItemSeparator separator, int beforeIndex) 
		throws IndexOutOfBoundsException 
	{
		menuBarWidget.insertSeparator(separator.itemSeparator, beforeIndex);
		itens.add(beforeIndex, separator);
		return separator;
	}

	public boolean isAnimationEnabled() 
	{
		return menuBarWidget.isAnimationEnabled();
	}

	/**
	 * Removes the specified menu item from the bar.
	 * 
	 * @param item the item to be removed
	 */
	public void removeItem(MenuItem item) 
	{
		menuBarWidget.removeItem(item.menuItem);
		itens.remove(item);
	}

	/**
	 * Removes the specified {@link MenuItemSeparator} from the bar.
	 * 
	 * @param separator the separator to be removed
	 */
	public void removeSeparator(MenuItemSeparator separator) 
	{
		menuBarWidget.removeSeparator(separator.itemSeparator);
		itens.remove(separator);
	}

	public void setAnimationEnabled(boolean enable) 
	{
		menuBarWidget.setAnimationEnabled(enable);
	}

	/**
	 * Sets whether this menu bar's child menus will open when the mouse is moved
	 * over it.
	 * 
	 * @param autoOpen <code>true</code> to cause child menus to auto-open
	 */
	public void setAutoOpen(boolean autoOpen) 
	{
		menuBarWidget.setAutoOpen(autoOpen);
	}
}
