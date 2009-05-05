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

import br.com.sysmap.crux.core.client.component.UIObject;

import com.google.gwt.user.client.Command;

/**
 * Represents a menu item
 * @author Thiago Bustamante
 */
public class MenuItem extends UIObject
{
	protected com.google.gwt.user.client.ui.MenuItem menuItem;
	protected MenuBar subMenu;
	protected MenuBar parentMenu;

	MenuItem(com.google.gwt.user.client.ui.MenuItem menuItem) 
	{
		super(menuItem);
		this.menuItem = menuItem;
	}

	/**
	 * Constructs a new menu item that fires a command when it is selected.
	 * 
	 * @param text the item's text
	 * @param cmd the command to be fired when it is selected
	 */
	public MenuItem(String text, Command cmd) 
	{
		this(new com.google.gwt.user.client.ui.MenuItem(text, cmd));
	}

	/**
	 * Constructs a new menu item that fires a command when it is selected.
	 * 
	 * @param text the item's text
	 * @param asHTML <code>true</code> to treat the specified text as html
	 * @param cmd the command to be fired when it is selected
	 */
	public MenuItem(String text, boolean asHTML, Command cmd) 
	{
		this(new com.google.gwt.user.client.ui.MenuItem(text, asHTML, cmd));
	}

	/**
	 * Constructs a new menu item that cascades to a sub-menu when it is selected.
	 * 
	 * @param text the item's text
	 * @param subMenu the sub-menu to be displayed when it is selected
	 */
	public MenuItem(String text, MenuBar subMenu) 
	{
		this(new com.google.gwt.user.client.ui.MenuItem(text, subMenu.menuBarWidget));
		this.subMenu = subMenu;
	}

	/**
	 * Constructs a new menu item that cascades to a sub-menu when it is selected.
	 * 
	 * @param text the item's text
	 * @param asHTML <code>true</code> to treat the specified text as html
	 * @param subMenu the sub-menu to be displayed when it is selected
	 */
	public MenuItem(String text, boolean asHTML, MenuBar subMenu) 
	{
		this(new com.google.gwt.user.client.ui.MenuItem(text, asHTML, subMenu.menuBarWidget));
		this.subMenu = subMenu;
	}

	/**
	 * Gets the command associated with this item.
	 * 
	 * @return this item's command, or <code>null</code> if none exists
	 */
	public Command getCommand() 
	{
		return menuItem.getCommand();
	}

	public String getHTML() 
	{
		return menuItem.getHTML();
	}

	/**
	 * Gets the menu that contains this item.
	 * 
	 * @return the parent menu, or <code>null</code> if none exists.
	 */
	public MenuBar getParentMenu() 
	{
		return parentMenu;
	}

	/**
	 * Gets the sub-menu associated with this item.
	 * 
	 * @return this item's sub-menu, or <code>null</code> if none exists
	 */
	public MenuBar getSubMenu() 
	{
		return this.subMenu;
	}

	public String getText() 
	{
		return menuItem.getText();
	}

	/**
	 * Sets the command associated with this item.
	 * 
	 * @param cmd the command to be associated with this item
	 */
	public void setCommand(Command cmd) 
	{
		menuItem.setCommand(cmd);
	}

	public void setHTML(String html) 
	{
		menuItem.setHTML(html);
	}

	/**
	 * Sets the sub-menu associated with this item.
	 * 
	 * @param subMenu this item's new sub-menu
	 */
	public void setSubMenu(MenuBar subMenu) 
	{
		menuItem.setSubMenu(subMenu.menuBarWidget);
		this.subMenu = subMenu;
	}

	public void setText(String text) 
	{
		menuItem.setText(text);
	}

	void setParentMenu(MenuBar parentMenu) 
	{
		this.parentMenu = parentMenu;
	}
}
