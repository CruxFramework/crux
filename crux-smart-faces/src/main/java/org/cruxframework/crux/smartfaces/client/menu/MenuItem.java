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

import com.google.gwt.dom.client.Element;

/**
 * A cross device menu item
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
class MenuItem
{
	private Element element;
	private int hashCode;
	private ArrayList<MenuItem> children;
	
//	public MenuItem(Widget item, ArrayList<MenuItem> children)
//	{
//		this.item = item;
//		this.children = children;
//		initWidget(item);
//		setStyleName("crux-Item");
//		//this has to be here otherwise hashcode will change
//		this.hashCode = item.hashCode();
//	}
	
	public MenuItem(Element item, boolean wrapInUL)
	{
		if(item == null)
		{
			this.hashCode = 0;
		} else 
		{
			this.hashCode = item.hashCode();
		}
		
		ListItem li = new ListItem();
		li.setStyleName("crux-LIItem");
		if(item != null)
		{
			li.getElement().appendChild(item);
		}
		
		if(wrapInUL)
		{
			OrderedList ul = new OrderedList();
			ul.setStyleName("crux-ULItem");
			if(item != null)
			{
				ul.add(li);	
			}
			this.element = ul.getElement();
		} else
		{
			this.element = li.getElement();
		}
	}
	
	@Override
	public String toString() 
	{
		return element.toString();
	}
	
	@Override
	public int hashCode() 
	{
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		return element.equals(obj);
	}

	public Integer add(MenuItem w) 
	{
		if(children == null)
		{
			children = new ArrayList<MenuItem>();
		}
		children.add(w);
		return w.hashCode();
	}

	public void clear() 
	{
		children.clear();
	}

	public MenuItem remove(MenuItem w) 
	{
		return children.remove(w.hashCode());
	}

	public ArrayList<MenuItem> getChildren() 
	{
		return children;
	}

	public Element getElement() 
	{
		return element;
	}
}
