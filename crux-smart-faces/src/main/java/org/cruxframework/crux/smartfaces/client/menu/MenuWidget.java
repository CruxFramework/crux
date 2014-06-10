package org.cruxframework.crux.smartfaces.client.menu;

import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWordWrap;
import com.google.gwt.user.client.ui.Widget;

public interface MenuWidget extends HasAnimation, HasEnabled, 
	HasVisibility, HasWidgets, HasWordWrap 
{
	public static enum Orientation
	{
		VERTICAL,
		HORIZONTAL;
	}
	
	public static enum Type
	{
		TREE,
		SLIDE,
		STACK,
		//...
		;
	}

	/**
	 * Define the menu orientation.
	 */
	public void setOrientation(Orientation orientation);
	
	/**
	 * Define how menu will be rendered inside page.
	 */
	public void setType(Type type);
	
	/**
	 * Adds a root item.
	 * @return the inserted item key. 
	 */
	public int addItem(Widget widget);
	
	/**
	 * Adds an item inside a root item.
	 * @return the inserted item key. 
	 */
	public int addItem(int key, Widget widget);
	
	//THIS WILL BE AT MENU_DISPOSAL - CREATE IT
//	/**
//	 * Creates and adds a button item with given label that targets to the given viewName inside the viewContainer.
//	 * @return the inserted item key. 
//	 */
//	public int addItem(Widget root, String label, ViewContainer viewContainer, String viewName);
	
	public boolean removeItem(Widget root);
	public Widget removeItem(int key);
	
	public void collapseAll();
	public void collapse(Widget root);
	
	public void expandAll();
	public void expand(Widget root);
}
