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

import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * @author Claudio Holanda (claudio.junior@cruxframework.org)
 *
 */
public class Menu extends Composite implements MenuWidget 
{
	private static final String STYLE_FACES_SLIDE = "faces-slide";
	private static final String STYLE_FACES_DROPDOWN = "faces-dropdown";
	private static final String STYLE_FACES_TREE = "faces-tree";
	private static final String STYLE_FACES_ACCORDION = "faces-accordion";
	private static final String STYLE_FACES_HORIZONTAL = "faces-horizontal";
	private static final String STYLE_FACES_VERTICAL = "faces-vertical";
	private static final String STYLE_FACES_MENU = "faces-Menu";
	private static final String STYLE_FACES_COLLAPSED = "faces-collapsed";
	
	private Type currentType;
	private Orientation currentOrientation;
	private boolean enabled = true;

	private MenuItem root = new MenuItem();
	
	public Menu(Orientation orientation, Type type)
	{
		root.getWidget().asWidget().setStyleName(getBaseStyleName());
		initWidget(root.getWidget());

		setType(type);
		setOrientation(orientation);
	}

	public String getBaseStyleName()
	{
		return STYLE_FACES_MENU;
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
		return enabled;
	}

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
			this.root = new MenuItem();
			return;
		}
		this.root.clear();
		this.root = new MenuItem();
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

	@Override
	public boolean remove(Widget w) 
	{
		return removeItem(w);
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
			removeStyleName(STYLE_FACES_VERTICAL);
			addStyleName(STYLE_FACES_HORIZONTAL);
			break;
		case VERTICAL:
			removeStyleName(STYLE_FACES_HORIZONTAL);
			addStyleName(STYLE_FACES_VERTICAL);
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
		case SLIDE:
			addStyleName(STYLE_FACES_SLIDE);
			
			removeStyleName(STYLE_FACES_ACCORDION);
			removeStyleName(STYLE_FACES_TREE);
			removeStyleName(STYLE_FACES_DROPDOWN);
			
			Roles.getSliderRole().set(getElement());
			break;
		case ACCORDION:
			addStyleName(STYLE_FACES_ACCORDION);
			
			removeStyleName(STYLE_FACES_SLIDE);
			removeStyleName(STYLE_FACES_TREE);
			removeStyleName(STYLE_FACES_DROPDOWN);
			
			Roles.getListRole().set(getElement());
			break;
		case TREE:
			addStyleName(STYLE_FACES_TREE);
			
			removeStyleName(STYLE_FACES_SLIDE);
			removeStyleName(STYLE_FACES_ACCORDION);
			removeStyleName(STYLE_FACES_DROPDOWN);
			
			Roles.getTreeRole().set(getElement());
			break;
		case DROPDOWN:
			addStyleName(STYLE_FACES_DROPDOWN);
			
			removeStyleName(STYLE_FACES_SLIDE);
			removeStyleName(STYLE_FACES_ACCORDION);
			removeStyleName(STYLE_FACES_TREE);
			
			Roles.getTreeRole().set(getElement());
			break;
		default:
			break;
		}

		this.currentType = type;
	}

	@Override
	public MenuItem addItem(Widget widget) 
	{
		return addItem(null, widget);
	}

	@Override
	public MenuItem addItem(MenuItem placeToInsert, Widget item) 
	{
		if(placeToInsert == null)
		{
			placeToInsert = this.root;
		}
		
		MenuItem w = new MenuItem(item);
		placeToInsert.add(w);
		return w;
	}

	@Override
	public boolean removeItem(Widget root) 
	{
		MenuItem found = MenuUtils.findInMenu(this.root, root);
		
		if(found == null)
		{
			return false;
		}
		
		found.clear();
		
		return true;
	}

	@Override
	public MenuItem removeItem(int key) 
	{
		MenuItem found = MenuUtils.findInMenu(this.root, key);
		
		if(found == null)
		{
			return null;
		}
		
		found.clear();
		
		return found;
	}

	@Override
	public void collapseAll() 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_COLLAPSED, true, MenuUtils.getAllMenuItems(this.root));
	}

	@Override
	public void collapse(Widget root) 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_COLLAPSED, true, MenuUtils.findInMenu(this.root, root));
	}

	@Override
	public void expandAll() 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_COLLAPSED, false, MenuUtils.getAllMenuItems(this.root));
	}

	@Override
	public void expand(Widget root) 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_COLLAPSED, false, MenuUtils.findInMenu(this.root, root));
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
