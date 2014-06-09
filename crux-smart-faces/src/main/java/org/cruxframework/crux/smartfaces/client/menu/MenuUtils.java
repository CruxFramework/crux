package org.cruxframework.crux.smartfaces.client.menu;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * A helpful menu class
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
public class MenuUtils 
{

	public static ArrayList<MenuItem> findHasWidgetsInMenu(MenuItem menuItem)
	{
		ArrayList<MenuItem> ans = new ArrayList<MenuItem>();
		
		return findHasWidgetsInMenu(menuItem, ans);
	}
	
	private static ArrayList<MenuItem> findHasWidgetsInMenu(MenuItem menuItem, ArrayList<MenuItem> found) 
	{
		if(menuItem == null)
		{
			return null;
		}
		
		if(menuItem.getWidget() instanceof HasWidgets)
		{
			found.add(menuItem);
		}
		
		if(menuItem.getChildren() == null)
		{
			return null;
		}
		
		for(MenuItem childrenMenuItem : menuItem.getChildren())
		{
			ArrayList<MenuItem> foundTypesInMenu = findHasWidgetsInMenu(childrenMenuItem, found);
			if(foundTypesInMenu != null)
			{
				found.addAll(foundTypesInMenu);
			}
		}
		
		return null;
	}
	
	public static MenuItem findKeyInMenu(MenuItem menuItem, int key) 
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
			MenuItem findKeyInMenu = findKeyInMenu(childrenMenuItem, key);
			if(findKeyInMenu != null)
			{
				return findKeyInMenu;
			}
		}
		
		return null;
	}
	
}
