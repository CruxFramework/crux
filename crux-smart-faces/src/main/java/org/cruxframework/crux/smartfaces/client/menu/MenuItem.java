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
import org.cruxframework.crux.smartfaces.client.list.OrderedList;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu item
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
class MenuItem
{
	private Element liElement;
	private Widget widget;
	private ArrayList<MenuItem> children;
	private boolean root;
	
//	public MenuItem(Widget item, ArrayList<MenuItem> children)
//	{
//		this.item = item;
//		this.children = children;
//		initWidget(item);
//		setStyleName("crux-Item");
//		//this has to be here otherwise hashcode will change
//		this.hashCode = item.hashCode();
//	}
	
	public boolean isRoot()
	{
		return root;
	}
	
	public MenuItem(OrderedList ul, Widget widget) 
	{
		ul.setStyleName("faces-ul");
		Element li = (Element) ul.getElement().getFirstChild();
		this.liElement = li;
		this.widget = widget;
	}
	
	public MenuItem(ListItem li, Widget widget) 
	{
		li.setStyleName("faces-li");
		this.liElement = li.getElement();
		this.widget = widget;
	}
	
	public MenuItem(Widget widget)
	{
		this(widget, false);
	}
	
	public MenuItem(Widget widget, boolean isRoot)
	{
		if(isRoot)
		{
			this.liElement = widget.getElement();
			this.widget = widget;
			this.root = true;
			return;
		}
		
		ListItem li = new ListItem();
		li.setStyleName("faces-li");
		li.getElement().appendChild(widget.getElement());
		this.liElement = li.getElement();
		this.widget = widget;
	}

	public Integer add(MenuItem w) 
	{
		if(children == null)
		{
			children = new ArrayList<MenuItem>();
		}
		children.add(w);
		liElement.addClassName("faces-hasChildren");
		return w.hashCode();
	}

	@Override
	public int hashCode() 
	{
		if(root)
		{
			return 0;
		}
		
		return liElement.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		return liElement.equals(obj);
	}
	
	public void clear() 
	{
		children.clear();
	}

	public MenuItem remove(MenuItem w) 
	{
		MenuItem menuItem = children.remove(w.hashCode());
		
		if(children.isEmpty())
		{
			liElement.removeClassName("faces-hasChildren");
		}
		
		return menuItem;
	}

	public ArrayList<MenuItem> getChildren() 
	{
		return children;
	}

	public Element getLIElement() 
	{
		return liElement;
	}

	public Widget getWidget() 
	{
		return widget;
	}
}
