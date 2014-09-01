/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.disposal;

import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author wesley.diniz
 * 
 */
public interface DisposalLayout
{
	public static final String HASH = "#";
	/**
	 * Set the menu of type org.cruxframework.crux.smartfaces.client.menu.Menu
	 * @param menu
	 */
	void setMenu(Menu menu);

	/**
	 * Add a new widget to header's panel
	 * @param header
	 */
	void addHeaderContent(Widget param);

	/**
	 * Add a new widget to footer's panel
	 * @param footer
	 */
	void addFooterContent(Widget param);

	/**
	 * Define a default view that will be displayed as soon as the component is rendered
	 * @param viewName
	 */
	void setDefaultView(String viewName);

	/**
	 * Displays a new view in the Layout's view container
	 * @param viewName
	 * @param saveHistory
	 */
	void showView(String viewName, boolean saveHistory);
	
	/**
	 * Add a new item to an existing menu
	 * @param item
	 * @param label
	 */
	void addMenuItem(MenuItem item,String label);
	
	/**
	 * Add a content that will be used in small devices (phones, small tablets etc.)
	 * @param param
	 */
	void addSmallHeaderContent(Widget param);
	
}
