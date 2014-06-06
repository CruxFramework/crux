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

import java.util.Iterator;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.smartfaces.client.list.ListItem;
import org.cruxframework.crux.smartfaces.client.list.OrderedList;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * http://jsfiddle.net/XvDS2/2/
 *
 */
public class Menu extends Composite implements MenuWidget 
{
	@SuppressWarnings("unused")
	private Type currentType;
	@SuppressWarnings("unused")
	private Orientation currentOrientation;

	private NavPanel menu;
	private MenuItem root;
	
	public Menu(Orientation orientation, Type type)
	{
		menu = new NavPanel();
		menu.asWidget().setStyleName("crux-Menu");
		initWidget((Widget) menu);

		setType(type);
		setOrientation(orientation);
	}

	public String getBaseStyleName()
	{
		return "faces-Menu";
	}

	@Override
	public boolean isAnimationEnabled() 
	{
		return false;
	}

	@Override
	public void setAnimationEnabled(boolean enable) 
	{
	}

	@Override
	public boolean isEnabled() 
	{
		return false;
	}

	@Override
	public void setEnabled(boolean enabled) 
	{
	}

	@Override
	public int getTabIndex() 
	{
		return 0;
	}

	@Override
	public void setAccessKey(char key) 
	{
	}

	@Override
	public void setFocus(boolean focused) 
	{
	}

	@Override
	public void setTabIndex(int index) 
	{
	}

	@Override
	public int getHorizontalScrollPosition() 
	{
		return 0;
	}

	@Override
	public int getMaximumHorizontalScrollPosition() 
	{
		return 0;
	}

	@Override
	public int getMinimumHorizontalScrollPosition() 
	{
		return 0;
	}

	@Override
	public void setHorizontalScrollPosition(int position) 
	{
	}

	@Override
	public int getMaximumVerticalScrollPosition() 
	{
		return 0;
	}

	@Override
	public int getMinimumVerticalScrollPosition() 
	{
		return 0;
	}

	@Override
	public int getVerticalScrollPosition() 
	{
		return 0;
	}

	@Override
	public void setVerticalScrollPosition(int position) 
	{
	}

	@Override
	public HandlerRegistration addScrollHandler(ScrollHandler handler) 
	{
		return null;
	}

	@Override
	public void add(Widget w) 
	{
		addItem(w);
	}

	@Override
	public void clear() 
	{
	}

	@Override
	public Iterator<Widget> iterator() 
	{
		return null;
	}

	@Override
	public boolean remove(Widget w) 
	{
		return false;
	}

	@Override
	public boolean getWordWrap() 
	{
		return false;
	}

	@Override
	public void setWordWrap(boolean wrap) 
	{
	}

	@Override
	public void setOrientation(Orientation orientation) 
	{
		if(orientation == null)
		{
			return;
		}

		switch(orientation)
		{
		case HORIZONTAL:
			removeStyleDependentName("vertical");
			addStyleDependentName("horizontal");
			break;
		case VERTICAL:
			removeStyleDependentName("horizontal");
			addStyleDependentName("vertical");
			break;
		default:
			break;
		}

		this.currentOrientation = orientation;
	}

	@Override
	public void setType(Type type) 
	{
		if(type == null)
		{
			return;
		}

		String deviceName = Screen.getCurrentDevice().toString();
		if(!getStyleName().contains(deviceName))
		{
			addStyleDependentName(deviceName);
		}

		switch(type)
		{
		case FLIP:
			addStyleDependentName("flip");
			Roles.getSliderRole().set(getElement());
			break;
		case STACK:
			addStyleDependentName("stack");
			Roles.getListRole().set(getElement());
			break;
		case TREE:
			addStyleDependentName("tree");
			Roles.getTreeRole().set(getElement());
			break;
		default:
			break;
		}

		this.currentType = type;
	}

	@Override
	public int addItem(Widget widget) 
	{
		return addItem(0, widget);
	}

	@Override
	public int addItem(int key, Widget item) 
	{
		if(root == null)
		{
			root = new MenuItem(menu.getElement(), true);
		}
		MenuItem placeToInsert = findKeyInMenu(root, key);
		
		//insert LI as the next child
		if(placeToInsert.getChildren() != null && !placeToInsert.getChildren().isEmpty())
		{
			MenuItem myItemLI = new MenuItem(item.getElement());
			if(placeToInsert.isRoot())
			{
				placeToInsert.getElement().getFirstChild().appendChild(myItemLI.getElement());	
			} else
			{
				placeToInsert.getElement().getLastChild().appendChild(myItemLI.getElement());
			}
			placeToInsert.add(myItemLI);
			return myItemLI.hashCode();
		} 
		//insert UL as the next child
		else 
		{
			OrderedList ul = new OrderedList();
			ListItem li = new ListItem();
			li.getElement().appendChild(item.getElement());
			ul.add(li);
			
			MenuItem myItemLI = new MenuItem(ul);
			placeToInsert.add(myItemLI);
			placeToInsert.getElement().appendChild(ul.getElement());	
			return myItemLI.hashCode();
		}
	}

	private MenuItem findKeyInMenu(MenuItem menuItem, int key) 
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

	@Override
	public boolean removeItem(Widget root) 
	{
		return false;
	}

	@Override
	public boolean removeItem(Widget root, Widget widget) 
	{
		return false;
	}

	@Override
	public boolean removeItem(int key) 
	{
		return false;
	}

	@Override
	public void collapseAll() 
	{
	}

	@Override
	public void collapseAll(Widget root) 
	{
	}

	@Override
	public void collapse(Widget root) 
	{
	}

	@Override
	public void expandAll() 
	{
	}

	@Override
	public void expandAll(Widget root) 
	{
	}

	@Override
	public void expand(Widget root) 
	{
	}

}
