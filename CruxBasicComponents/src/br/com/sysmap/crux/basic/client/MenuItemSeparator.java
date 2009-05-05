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

import com.google.gwt.user.client.ui.MenuBar;

import br.com.sysmap.crux.core.client.component.UIObject;

/**
 * Represents a MenuItemSeparator
 * @author Thiago Bustamante
 */
public class MenuItemSeparator extends UIObject
{
	protected com.google.gwt.user.client.ui.MenuItemSeparator itemSeparator;
	protected MenuBar parentMenu;
	
	protected MenuItemSeparator(com.google.gwt.user.client.ui.MenuItemSeparator itemSeparator) 
	{
		super(itemSeparator);
		this.itemSeparator = itemSeparator;
	}

	public MenuItemSeparator()
	{
		this (new com.google.gwt.user.client.ui.MenuItemSeparator());
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

	void setParentMenu(MenuBar parentMenu) 
	{
		this.parentMenu = parentMenu;
	}

}
