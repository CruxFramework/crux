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
	private static final String STYLE_FACES_COLAPSED = "faces-colapsed";
	private Type currentType;
	private Orientation currentOrientation;
	private boolean enabled = true;

	private MenuItem root = new MenuItem();
	
	public Menu(Orientation orientation, Type type)
	{
		root.getWidget().asWidget().setStyleName("faces-Menu");
		initWidget(root.getWidget());

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
		case SLIDE:
			addStyleName("faces-slide");
			
			removeStyleName("faces-accordion");
			removeStyleName("faces-tree");
			removeStyleName("faces-dropdown");
			
			Roles.getSliderRole().set(getElement());
			break;
		case ACCORDION:
			addStyleName("faces-accordion");
			
			removeStyleName("faces-slide");
			removeStyleName("faces-tree");
			removeStyleName("faces-dropdown");
			
			Roles.getListRole().set(getElement());
			break;
		case TREE:
			addStyleName("faces-tree");
			
			removeStyleName("faces-slide");
			removeStyleName("faces-accordion");
			removeStyleName("faces-dropdown");
			
			Roles.getTreeRole().set(getElement());
			break;
		case DROPDOWN:
			addStyleName("faces-dropdown");
			
			removeStyleName("faces-slide");
			removeStyleName("faces-accordion");
			removeStyleName("faces-tree");
			
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
		MenuUtils.addOrRemoveClass(STYLE_FACES_COLAPSED, true, MenuUtils.getAllMenuItems(this.root));
	}

	@Override
	public void collapse(Widget root) 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_COLAPSED, true, MenuUtils.findInMenu(this.root, root));
	}

	@Override
	public void expandAll() 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_COLAPSED, false, MenuUtils.getAllMenuItems(this.root));
	}

	@Override
	public void expand(Widget root) 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_COLAPSED, false, MenuUtils.findInMenu(this.root, root));
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
