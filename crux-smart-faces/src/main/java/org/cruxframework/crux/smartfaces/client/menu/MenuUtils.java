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

import org.cruxframework.crux.core.client.collection.FastList;

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
	public static FastList<MenuItem> getAllMenuItems(MenuItem menuItem)
	{
		FastList<MenuItem> ans = new FastList<MenuItem>();
		
		FastList<MenuItem> ansFound = getAllMenuItems(menuItem, ans);
		
		return ansFound;
	}
	
	private static FastList<MenuItem> getAllMenuItems(MenuItem menuItem, FastList<MenuItem> found) 
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
		
		for(int i=0;i<menuItem.getChildren().size();i++)
		{
			FastList<MenuItem> foundTypesInMenu = findHasEnabledInMenu(menuItem.getChildren().get(i), found);
			if(foundTypesInMenu != null)
			{
				for(int j=0;i<foundTypesInMenu.size();j++)
				{
					found.add(foundTypesInMenu.get(j));	
				}
			}
		}
		
		return found;
	}

	/**
	 * @param menuItem
	 * @return
	 */
	public static FastList<MenuItem> findHasEnabledInMenu(MenuItem menuItem)
	{
		FastList<MenuItem> ans = new FastList<MenuItem>();
		
		FastList<MenuItem> ansFound = findHasEnabledInMenu(menuItem, ans);
		
		return ansFound;
	}
	
	private static FastList<MenuItem> findHasEnabledInMenu(MenuItem menuItem, FastList<MenuItem> found) 
	{
		if(menuItem == null)
		{
			return found;
		}
		
		if(menuItem.getItemWidget() instanceof HasEnabled)
		{
			found.add(menuItem);
		}
		
		if(menuItem.getChildren() == null)
		{
			return found;
		}
		
		for(int i=0;i<menuItem.getChildren().size();i++)
		{
			FastList<MenuItem> foundTypesInMenu = findHasEnabledInMenu(menuItem.getChildren().get(i), found);
			if(foundTypesInMenu != null)
			{
				for(int j=0; j<foundTypesInMenu.size(); j++)
				{
					found.add(foundTypesInMenu.get(j));	
				}
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
		
		if(menuItem.getItemWidget() != null && menuItem.getItemWidget().equals(widget))
		{
			return menuItem;
		}
		
		if(menuItem.getChildren() == null)
		{
			return null;
		}
		
		for(int i=0; i<menuItem.getChildren().size();i++)
		{
			MenuItem found = findInMenu(menuItem.getChildren().get(i), widget);
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
		
		for(int i=0; i<menuItem.getChildren().size();i++)
		{
			MenuItem found = findInMenu(menuItem.getChildren().get(i), key);
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
		FastList<MenuItem> items = new FastList<MenuItem>();
		items.add(menuItem);
		
		addOrRemoveClass(className, apply, items);
	}
	
	/**
	 * @param className
	 * @param apply
	 * @param menuItems
	 */
	public static void addOrRemoveClass(String className, boolean apply, FastList<MenuItem> menuItems) 
	{
		if(menuItems == null)
		{
			return;
		}
		
		for(int i=0; i<menuItems.size(); i++)
		{
			if(apply)
			{
				menuItems.get(i).addClass(className);
			} else
			{
				menuItems.get(i).removeClass(className);
			}
		}
	}
}
