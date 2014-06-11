/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * A helpful menu class
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
class MenuUtils 
{
	
	/**
	 * @param menuItem
	 * @return
	 */
	public static ArrayList<MenuItem> getAllMenuItems(MenuItem menuItem)
	{
		Set<MenuItem> ans = new HashSet<MenuItem>();
		
		Set<MenuItem> ansFound = getAllMenuItems(menuItem, ans);
		
		if(ansFound == null)
		{
			return null;
		}
		
		return new ArrayList<MenuItem>(ansFound);
	}
	
	private static Set<MenuItem> getAllMenuItems(MenuItem menuItem, Set<MenuItem> found) 
	{
		if(menuItem == null)
		{
			return found;
		}
		
		found.add(menuItem);
		
		if(menuItem.getChildren() == null)
		{
			return found;
		}
		
		for(MenuItem childrenMenuItem : menuItem.getChildren())
		{
			Set<MenuItem> foundTypesInMenu = findHasEnabledInMenu(childrenMenuItem, found);
			if(foundTypesInMenu != null)
			{
				found.addAll(foundTypesInMenu);
			}
		}
		
		return found;
	}

	/**
	 * @param menuItem
	 * @return
	 */
	public static ArrayList<MenuItem> findHasEnabledInMenu(MenuItem menuItem)
	{
		Set<MenuItem> ans = new HashSet<MenuItem>();
		
		Set<MenuItem> ansFound = findHasEnabledInMenu(menuItem, ans);
		
		if(ansFound == null)
		{
			return null;
		}
		
		return new ArrayList<MenuItem>(ansFound);
	}
	
	private static Set<MenuItem> findHasEnabledInMenu(MenuItem menuItem, Set<MenuItem> found) 
	{
		if(menuItem == null)
		{
			return found;
		}
		
		if(menuItem.getWidget() instanceof HasEnabled)
		{
			found.add(menuItem);
		}
		
		if(menuItem.getChildren() == null)
		{
			return found;
		}
		
		for(MenuItem childrenMenuItem : menuItem.getChildren())
		{
			Set<MenuItem> foundTypesInMenu = findHasEnabledInMenu(childrenMenuItem, found);
			if(foundTypesInMenu != null)
			{
				found.addAll(foundTypesInMenu);
			}
		}
		
		return found;
	}
	
	/**
	 * @param menuItem
	 * @param widget
	 * @return
	 */
	public static MenuItem findInMenu(MenuItem menuItem, Widget widget) 
	{
		if(menuItem == null)
		{
			return null;
		}
		
		if(menuItem.getWidget() != null && menuItem.getWidget().equals(widget))
		{
			return menuItem;
		}
		
		if(menuItem.getChildren() == null)
		{
			return null;
		}
		
		for(MenuItem childrenMenuItem : menuItem.getChildren())
		{
			MenuItem found = findInMenu(childrenMenuItem, widget);
			if(found != null)
			{
				return found;
			}
		}
		
		return null;
	}
	
	/**
	 * @param menuItem
	 * @param key
	 * @return
	 */
	public static MenuItem findInMenu(MenuItem menuItem, int key) 
	{
		if(menuItem == null)
		{
			return null;
		}
		
		if(menuItem.hashCode() == key)
		{
			return menuItem;
		}
		
		if(menuItem.getChildren() == null)
		{
			return null;
		}
		
		for(MenuItem childrenMenuItem : menuItem.getChildren())
		{
			MenuItem found = findInMenu(childrenMenuItem, key);
			if(found != null)
			{
				return found;
			}
		}
		
		return null;
	}

	/**
	 * @param className
	 * @param apply
	 * @param menuItem
	 */
	public static void addOrRemoveClass(String className, boolean apply, MenuItem menuItem) 
	{
		if(menuItem == null)
		{
			return;
		}
		ArrayList<MenuItem> items = new ArrayList<MenuItem>();
		items.add(menuItem);
		
		addOrRemoveClass(className, apply, items);
	}
	
	/**
	 * @param className
	 * @param apply
	 * @param menuItems
	 */
	public static void addOrRemoveClass(String className, boolean apply, ArrayList<MenuItem> menuItems) 
	{
		if(menuItems == null)
		{
			return;
		}
		for(MenuItem menuItem : menuItems)
		{
			if(apply)
			{
				menuItem.addClass(className);
			} else
			{
				menuItem.removeClass(className);
			}
		}
	}
}
