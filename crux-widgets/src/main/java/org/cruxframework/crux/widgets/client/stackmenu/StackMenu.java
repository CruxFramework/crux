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
package org.cruxframework.crux.widgets.client.stackmenu;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.widgets.client.filter.Filterable;


import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * A vertical hierarchical menu.
 * 
 * @author Gesse Dafe
 */
//TODO deprecar essa classe.... criar um outro menu semanticamente correto <ul><li> e com base num <nav>
public class StackMenu extends Composite implements Filterable<StackMenuItem>, HasSelectionHandlers<StackMenuItem>
{
	public static final String DEFAULT_STYLE_NAME = "crux-StackMenu";
	private FlowPanel panel;
	private List<StackMenuItem> items = new ArrayList<StackMenuItem>();

	/**
	 * Empty constructor
	 */
	public StackMenu()
	{
		this.panel = new FlowPanel();
		this.panel.setStyleName(DEFAULT_STYLE_NAME);
		initWidget(panel);
		Accessibility.setRole(getElement(), Accessibility.ROLE_MENUBAR);
	}
	
	/**
	 * Adds an item.
	 * @param item
	 */
	public void add(StackMenuItem item)
	{
		items.add(item);
		panel.add(item);
		item.setParentMenu(this);
		controlItemsStyles();
	}	
	
	/**
	 * Removes an item.
	 * @param item
	 */
	public void remove(StackMenuItem item)
	{
		items.remove(item);
		panel.remove(item);
		controlItemsStyles();
	}
	
	/**
	 * Removes all items.
	 */
	public void clear()
	{
		items.clear();
		panel.clear();
	}

	/**
	 * Returns all items. 
	 */
	public List<StackMenuItem> getItems()
	{
		return items;
	}
	
	/**
	 * @see com.google.gwt.event.logical.shared.HasSelectionHandlers#addSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)
	 */
	public HandlerRegistration addSelectionHandler(SelectionHandler<StackMenuItem> handler)
	{
		return addHandler(handler, SelectionEvent.getType());
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.filter.Filterable#onSelectItem(java.lang.Object)
	 */
	public void onSelectItem(StackMenuItem selectedItem)
	{
		if(!selectedItem.hasChildren())
		{
			selectedItem.select();
		}
	}
	
	/**
	 * Adjusts the items' styles, in order to apply a special look 
	 * 	to the first and last top-level items.
	 */
	private void controlItemsStyles()
	{
		for (int i = 0; i < items.size(); i++)
		{
			StackMenuItem item = items.get(i);
			item.setFirst(i == 0);
			item.setLast(i == items.size() - 1);
		}		
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.filter.Filterable#filter(java.lang.String)
	 */
	public List<FilterResult<StackMenuItem>> filter(String query)
	{
		List<FilterResult<StackMenuItem>> result = new ArrayList<FilterResult<StackMenuItem>>();
		
		for (final StackMenuItem item : items)
		{
			addMatchingMenuItem(result, item, query, "");
		}
		
		return result;
	}
	
	/**
	 *  Adds to <code>result<code> all child items that match the filter query and does not coitain children.
	 * @param result
	 * @param item
	 * @param query
	 * @param currentPath
	 */
	private void addMatchingMenuItem(List<FilterResult<StackMenuItem>> result, final StackMenuItem item, String query, String currentPath)
	{
		String label = item.getLabel();
		
		currentPath = currentPath + (currentPath.length() > 0 ? " > " : "") + label;
		
		if(!item.hasChildren() && label != null && label.toUpperCase().contains(query.toUpperCase()))
		{
			FilterResult<StackMenuItem> resultItem = new FilterResult<StackMenuItem>(item, currentPath, label);
			result.add(resultItem);
		}
		
		if(item.getSubItems().size() > 0)
		{
			for (StackMenuItem subItem : item.getSubItems())
			{
				addMatchingMenuItem(result, subItem, query, currentPath);
			}
		}
	}
}