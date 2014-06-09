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
	
	private static Set<MenuItem> getAllMenuItems(MenuItem menuItem, Set<MenuItem> found) {
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
	
	public static MenuItem findInMenuByWidget(MenuItem menuItem, Widget widget) 
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
			MenuItem findKeyInMenu = findInMenuByWidget(childrenMenuItem, widget);
			if(findKeyInMenu != null)
			{
				return findKeyInMenu;
			}
		}
		
		return null;
	}
	
	public static MenuItem findInMenuByKey(MenuItem menuItem, int key) 
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
			MenuItem findKeyInMenu = findInMenuByKey(childrenMenuItem, key);
			if(findKeyInMenu != null)
			{
				return findKeyInMenu;
			}
		}
		
		return null;
	}

	public static void applyRemoveClass(String className, boolean apply, MenuItem menuItem) 
	{
		if(menuItem == null)
		{
			return;
		}
		ArrayList<MenuItem> items = new ArrayList<MenuItem>();
		items.add(menuItem);
		
		applyRemoveClass(className, apply, items);
	}
	
	public static void applyRemoveClass(String className, boolean apply, ArrayList<MenuItem> menuItems) 
	{
		if(menuItems == null)
		{
			return;
		}
		for(MenuItem menuItem : menuItems)
		{
			if(apply)
			{
				menuItem.getLIElement().addClassName(className);
			} else
			{
				menuItem.getLIElement().removeClassName(className);
			}
		}
	}
}
