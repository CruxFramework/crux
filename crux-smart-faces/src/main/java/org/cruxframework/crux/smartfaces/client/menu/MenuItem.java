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

import org.cruxframework.crux.smartfaces.client.list.ListItem;
import org.cruxframework.crux.smartfaces.client.list.UnorderedList;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;
import org.cruxframework.crux.smartfaces.client.select.SelectableWidget;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu item
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
public class MenuItem extends SelectableWidget
{
	private Element item;
	private Widget widget;
	private MenuItem parentItem;
	private ArrayList<MenuItem> children;
	private boolean root;
	
	public boolean isRoot()
	{
		return root;
	}
	
	public MenuItem()
	{
		this(null);
	}
	
	public MenuItem(Widget widget)
	{
		if(widget == null)
		{
			NavPanel navPanel = new NavPanel();
			this.root = true;
			this.widget = navPanel;
			this.item = navPanel.getElement();
			
			return;
		}
		
		this.widget = widget;
		ListItem li = new ListItem();
		li.add(widget);
		this.item = li.getElement();
	}

	public void add(MenuItem menuItem) 
	{
		if(children == null)
		{
			children = new ArrayList<MenuItem>();
		}
		
		if(children.isEmpty())
		{
			UnorderedList ul = new UnorderedList();
			ul.getElement().appendChild(menuItem.item);
			item.appendChild(ul.getElement());
		} else
		{
			item.getFirstChild().appendChild(menuItem.item);
		}
		menuItem.parentItem = menuItem; 
		children.add(menuItem);
	}

	public void clear() 
	{
		item.removeFromParent();
		children.clear();
	}

	public void remove(MenuItem w) 
	{
		w.item.removeFromParent();
		children.remove(w);
	}

	public ArrayList<MenuItem> getChildren() 
	{
		return children;
	}

	public Widget getWidget() 
	{
		return widget;
	}
	
	public void addClass(String className)
	{
		item.addClassName(className);
	}
	
	public void removeClass(String className)
	{
		item.removeClassName(className);
	}

	public MenuItem getParentItem() 
	{
		return parentItem;
	}
}
