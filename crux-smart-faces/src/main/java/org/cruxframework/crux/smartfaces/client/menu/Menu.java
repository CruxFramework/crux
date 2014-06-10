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
import java.util.Iterator;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.smartfaces.client.list.ListItem;
import org.cruxframework.crux.smartfaces.client.list.OrderedList;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * @autor Claudio Holanda (claudio.junior@cruxframework.org)
 *
 */
public class Menu extends Composite implements MenuWidget 
{
	private Type currentType;
	private Orientation currentOrientation;
	private boolean enabled = true;

	private NavPanel menu;
	private MenuItem root;
	
	public Menu(Orientation orientation, Type type)
	{
		menu = new NavPanel();
		menu.asWidget().setStyleName("faces-Menu");
		initWidget(menu);

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

	//OK
	@Override
	public void setAnimationEnabled(boolean enable) 
	{
	}

	//OK
	@Override
	public boolean isEnabled() 
	{
		return enabled;
	}

	//OK
	@Override
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
		
		ArrayList<MenuItem> itemsWithEnabledProperty = MenuUtils.findHasEnabledInMenu(root);
		
		if(itemsWithEnabledProperty == null)
		{
			return;
		}
		
		for(MenuItem menuItem : itemsWithEnabledProperty)
		{
			((HasEnabled) menuItem.getWidget()).setEnabled(enabled);
		}
	}

	//OK
	@Override
	public void add(Widget w) 
	{
		addItem(w);
	}

	@Override
	public void clear() 
	{
		if(this.root == null)
		{
			return;
		}
		
		this.root.getLIElement().removeFromParent();
		this.root = new MenuItem(menu, true);
	}

	@Override
	public Iterator<Widget> iterator() 
	{
		ArrayList<MenuItem> allMenuItems = MenuUtils.getAllMenuItems(this.root);
		
		if(allMenuItems == null)
		{
			return null;
		}
		
		ArrayList<Widget> widgets = new ArrayList<Widget>();
		
		for(MenuItem menuItem : allMenuItems)
		{
			widgets.add(menuItem.getWidget());
		}
		
		return widgets.iterator();
	}

	//OK
	@Override
	public boolean remove(Widget w) 
	{
		return removeItem(w);
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
			removeStyleName("faces-vertical");
			addStyleName("faces-horizontal");
			break;
		case VERTICAL:
			removeStyleName("faces-horizontal");
			addStyleName("faces-vertical");
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
			addStyleName(deviceName);
		}

		switch(type)
		{
		case FLIP:
			addStyleName("faces-slide");
			removeStyleName("faces-stack");
			removeStyleName("faces-tree");
			Roles.getSliderRole().set(getElement());
			break;
		case STACK:
			removeStyleName("faces-slide");
			addStyleName("faces-stack");
			removeStyleName("faces-tree");
			Roles.getListRole().set(getElement());
			break;
		case TREE:
			removeStyleName("faces-slide");
			removeStyleName("faces-stack");
			addStyleName("faces-tree");
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
		if(this.root == null)
		{
			this.root = new MenuItem(this.menu, true);
		}
		MenuItem placeToInsert = MenuUtils.findInMenu(this.root, key);
		
		//insert LI as the next child
		if(placeToInsert.getChildren() != null && !placeToInsert.getChildren().isEmpty())
		{
			MenuItem myItem = new MenuItem(item);
			if(placeToInsert.isRoot())
			{
				placeToInsert.getLIElement().getFirstChild().appendChild(myItem.getLIElement());	
			} else
			{
				placeToInsert.getLIElement().getLastChild().appendChild(myItem.getLIElement());
			}
			placeToInsert.add(myItem);
			return myItem.hashCode();
		} 
		//insert UL as the next child
		else 
		{
			OrderedList ul = new OrderedList();
			ListItem li = new ListItem();
			li.setStyleName("faces-li");
			li.getElement().appendChild(item.getElement());
			ul.add(li);
			
			MenuItem myItem = new MenuItem(ul, item);
			placeToInsert.add(myItem);
			placeToInsert.getLIElement().appendChild(ul.getElement());	
			return myItem.hashCode();
		}
	}

	@Override
	public boolean removeItem(Widget root) 
	{
		MenuItem removed = MenuUtils.removeItem(this.root, root);
		
		if(removed == null)
		{
			return false;
		}
		
		return true;
	}

	@Override
	public Widget removeItem(int key) 
	{
		MenuItem removed = MenuUtils.removeItem(this.root, key);
		
		if(removed == null)
		{
			return null;
		}
		
		return removed.getWidget();
	}

	//OK
	@Override
	public void collapseAll() 
	{
		MenuUtils.applyRemoveClass("faces-colapsed", true, MenuUtils.getAllMenuItems(this.root));
	}

	//OK
	@Override
	public void collapse(Widget root) 
	{
		MenuUtils.applyRemoveClass("faces-colapsed", true, MenuUtils.findInMenu(this.root, root));
	}

	//OK
	@Override
	public void expandAll() 
	{
		MenuUtils.applyRemoveClass("faces-colapsed", false, MenuUtils.getAllMenuItems(this.root));
	}

	//OK
	@Override
	public void expand(Widget root) 
	{
		MenuUtils.applyRemoveClass("faces-colapsed", false, MenuUtils.findInMenu(this.root, root));
	}

	public Orientation getCurrentOrientation() 
	{
		return currentOrientation;
	}

	public Type getCurrentType() 
	{
		return currentType;
	}

}
