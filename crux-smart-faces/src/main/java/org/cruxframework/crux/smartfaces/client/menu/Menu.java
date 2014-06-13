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

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * @author Claudio Holanda (claudio.junior@cruxframework.org)
 *
 */
public class Menu extends Composite implements HasAnimation, HasEnabled, HasVisibility, HasWidgets 
{
	public static enum Orientation
	{
		VERTICAL,
		HORIZONTAL;
	}
	
	public static enum Type
	{
		TREE,
		SLIDE,
		ACCORDION,
		DROPDOWN,
		//...
		;
	}
	
	protected static final String STYLE_FACES_MENU = "faces-Menu";
	protected static final String STYLE_FACES_SLIDE = "facesMenu-slide";
	protected static final String STYLE_FACES_DROPDOWN = "facesMenu-dropdown";
	protected static final String STYLE_FACES_TREE = "facesMenu-tree";
	protected static final String STYLE_FACES_ACCORDION = "facesMenu-accordion";
	protected static final String STYLE_FACES_HORIZONTAL = "facesMenu-horizontal";
	protected static final String STYLE_FACES_VERTICAL = "facesMenu-vertical";
	protected static final String STYLE_FACES_open = "facesMenu-open";
	protected static final String STYLE_FACES_HAS_CHILDREN = "facesMenu-hasChildren";
	protected static final String STYLE_FACES_EMPTY = "facesMenu-empty";
	protected static final String STYLE_FACES_LI = "facesMenu-li";
	protected static final String STYLE_FACES_UL = "facesMenu-ul";
	
	private Type currentType;
	private Orientation currentOrientation;
	private boolean enabled = true;

	private MenuItem root = new MenuItem();
	
	public Menu(Orientation orientation, Type type)
	{
		root.getItemWidget().asWidget().setStyleName(getBaseStyleName());
		initWidget(root.getItemWidget());

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
		
		FastList<MenuItem> itemsWithEnabledProperty = MenuUtils.findHasEnabledInMenu(root);
		
		if(itemsWithEnabledProperty == null)
		{
			return;
		}
		
		for(int i=0; i<itemsWithEnabledProperty.size();i++)
		{
			((HasEnabled) itemsWithEnabledProperty.get(i).getItemWidget()).setEnabled(enabled);
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
		FastList<MenuItem> allMenuItems = MenuUtils.getAllMenuItems(this.root);
		
		if(allMenuItems == null)
		{
			return null;
		}
		
		ArrayList<Widget> widgets = new ArrayList<Widget>();
		
		for(int i=0; i<allMenuItems.size();i++)
		{
			widgets.add(allMenuItems.get(i).getItemWidget());
		}
		
		return widgets.iterator();
	}

	@Override
	public boolean remove(Widget w) 
	{
		return removeItem(w);
	}

	/**
	 * Define the menu orientation.
	 */
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

	/**
	 * Define how menu will be rendered inside page.
	 */
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

	/**
	 * Adds a root item.
	 * @return the inserted item. 
	 */
	public MenuItem addItem(Widget widget) 
	{
		return addItem(null, widget);
	}

//	/**
//	 * Adds a view root item.
//	 * @return the inserted item. 
//	 */
//	public MenuItem addViewItem(String label, ViewContainer viewContainer) 
//	{
//		return addItem(null, widget);
//	}
//	
//	/**
//	 * Adds a view item.
//	 * @return the inserted item. 
//	 */
//	public MenuItem addViewItem(MenuItem placeToInsert, Widget item) 
//	{
//		return addItem(null, widget);
//	}
	
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

	public void openAll() 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_open, true, MenuUtils.getAllMenuItems(this.root));
	}

	public void open(MenuItem menuItem) 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_open, true, menuItem);
	}

	public void closeAll() 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_open, false, MenuUtils.getAllMenuItems(this.root));
	}

	public void close(MenuItem menuItem) 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_open, false, menuItem);
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
