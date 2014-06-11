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

import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public interface MenuWidget extends HasAnimation, HasEnabled, HasVisibility, HasWidgets
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
		ACCORDION,
		DROPDOWN,
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
	public MenuItem addItem(Widget widget);
	
	/**
	 * Adds an item inside a root item.
	 * @return the inserted item key. 
	 */
	public MenuItem addItem(MenuItem menuItem, Widget widget);
	
	//THIS WILL BE AT MENU_DISPOSAL - CREATE IT
//	/**
//	 * Creates and adds a button item with given label that targets to the given viewName inside the viewContainer.
//	 * @return the inserted item key. 
//	 */
//	public int addItem(Widget root, String label, ViewContainer viewContainer, String viewName);
	
	public boolean removeItem(Widget root);
	public MenuItem removeItem(int key);
	
	public void collapseAll();
	public void collapse(Widget root);
	
	public void expandAll();
	public void expand(Widget root);
}
