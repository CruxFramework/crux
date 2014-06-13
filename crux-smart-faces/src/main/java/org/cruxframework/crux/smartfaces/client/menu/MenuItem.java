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
import org.cruxframework.crux.smartfaces.client.list.UnorderedList;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu item
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
public class MenuItem extends UIObject
{
	private Element item;
	private Widget itemWidget;
	private MenuItem itemParent;
	private FastList<MenuItem> children;
	private boolean root;
	
	public boolean isRoot()
	{
		return root;
	}
	
	public MenuItem()
	{
		this(null);
	}
	
	public MenuItem(Widget itemWidget)
	{
		if(itemWidget == null)
		{
			NavPanel navPanel = new NavPanel();
			this.root = true;
			this.itemWidget = navPanel;
			this.item = navPanel.getElement();
			
			return;
		}

		RootPanel.get().add(itemWidget);
		this.itemWidget = itemWidget;

		Element li = DOM.createElement("li");
		li.setClassName(Menu.STYLE_FACES_LI);
		li.appendChild(itemWidget.getElement());
		this.item = li;
	}

	public void add(MenuItem menuItem) 
	{
		if(children == null)
		{
			children = new FastList<MenuItem>();
		}
		
		if(children.size() <= 0)
		{
			UnorderedList ul = new UnorderedList(Menu.STYLE_FACES_UL);
			ul.getElement().appendChild(menuItem.item);
			item.appendChild(ul.getElement());
		} else
		{
			item.getFirstChild().appendChild(menuItem.item);
		}
		menuItem.itemParent = menuItem; 
		children.add(menuItem);
		
		if(!root)
		{
			item.addClassName(Menu.STYLE_FACES_HAS_CHILDREN);
		} else
		{
			item.removeClassName(Menu.STYLE_FACES_EMPTY);
		}
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
		if(children.size() <= 0)
		{
			if(!root)
			{
				item.removeClassName(Menu.STYLE_FACES_HAS_CHILDREN);
			} else
			{
				item.addClassName(Menu.STYLE_FACES_EMPTY);
			}
		}
	}

	public FastList<MenuItem> getChildren() 
	{
		return children;
	}

	public Widget getItemWidget() 
	{
		return itemWidget;
	}
	
	public void addClassName(String className)
	{
		item.addClassName(className);
	}
	
	public void removeClassName(String className)
	{
		item.removeClassName(className);
	}

	public MenuItem getParentItem() 
	{
		return itemParent;
	}
}
