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
package br.com.sysmap.crux.widgets.client.stackmenu;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.widgets.client.filter.Filterable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * TODO - Gessé - Comment
 * 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class StackMenu extends Composite implements Filterable<StackMenuItem>
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
		this.panel.getElement().getStyle().setProperty("display", "inline");
		initWidget(panel);
	}
	
	/**
	 * @param item
	 */
	public void add(StackMenuItem item)
	{
		items.add(item);
		panel.add(item);
	}	
	
	/**
	 * @param item
	 */
	public void remove(StackMenuItem item)
	{
		items.remove(item);
		panel.remove(item);
	}
	
	/**
	 * 
	 */
	public void clear()
	{
		items.clear();
		panel.clear();
	}

	/**
	 * @return the items
	 */
	public List<StackMenuItem> getItems()
	{
		return items;
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.filter.Filterable#filter(java.lang.String)
	 */
	public List<FilterResult<StackMenuItem>> filter(String query)
	{
		List<FilterResult<StackMenuItem>> result = new ArrayList<FilterResult<StackMenuItem>>();
		
		for (final StackMenuItem item : items)
		{
			addMatchingMenuItem(item, query, result, "");
		}
		
		return result;
	}

	/**
	 * @param query
	 * @param result
	 * @param item
	 */
	private void addMatchingMenuItem(final StackMenuItem item, String query, List<FilterResult<StackMenuItem>> result, String currentPath)
	{
		String label = item.getLabel();
		
		currentPath = currentPath + (currentPath.length() > 0 ? " > " : "") + label;
		
		if(item.hasAction() && label != null && label.toUpperCase().contains(query.toUpperCase()))
		{
			
			FilterResult<StackMenuItem> resultItem = new FilterResult<StackMenuItem>(item, currentPath);
			result.add(resultItem);
		}
		
		if(item.getSubItems().size() > 0)
		{
			for (StackMenuItem subItem : item.getSubItems())
			{
				addMatchingMenuItem(subItem, query, result, currentPath);
			}
		}
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.filter.Filterable#onSelectItem(java.lang.Object)
	 */
	public void onSelectItem(StackMenuItem selectedItem)
	{
		selectedItem.click();	
	}
}