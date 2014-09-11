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
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.css.FacesResources;
import org.cruxframework.crux.smartfaces.client.menu.MenuRenderer.LargeMenuRenderer;
import org.cruxframework.crux.smartfaces.client.menu.Type.LargeType;
import org.cruxframework.crux.smartfaces.client.menu.Type.SmallType;
import org.cruxframework.crux.smartfaces.client.panel.BasePanel;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * @author Claudio Holanda (claudio.junior@cruxframework.org)
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Menu extends Composite implements HasAnimation, HasEnabled, HasSelectionHandlers<MenuItem>
{
	public    static final String STYLE_FACES_MENU = "faces-Menu";
	protected static final String SPACE = " ";
	protected static final String CLEARFIX = "faces--clearfix";
	protected static final String STYLE_FACES_SLIDE = "faces-Menu-slide";
	protected static final String STYLE_FACES_DROPDOWN = "faces-Menu-dropdown";
	protected static final String STYLE_FACES_TREE = "faces-Menu-tree";
	protected static final String STYLE_FACES_ACCORDION = "faces-Menu-accordion";
	protected static final String STYLE_FACES_HORIZONTAL = "faces-Menu-horizontal";
	protected static final String STYLE_FACES_VERTICAL = "faces-Menu-vertical";
	protected static final String STYLE_FACES_OPEN = "faces-Menu-open";
	protected static final String STYLE_FACES_HAS_CHILDREN = "faces-Menu-hasChildren";
	protected static final String STYLE_FACES_DISABLED = "faces-Menu-disabled";
	protected static final String STYLE_FACES_EMPTY = "faces-Menu-empty";
	protected static final String STYLE_FACES_LI = "faces-Menu-li";
	protected static final String STYLE_FACES_UL = "faces-Menu-ul";
	protected static final String STYLE_AUX_OPEN_CLOSE_TRIGGER_HELPER = "faces-Menu-openCloseTriggerHelper";
	protected static final String STYLE_AUX_CLOSE_TRIGGER_SLIDER_HELPER = "faces-Menu-closeTriggerSliderHelper";
	
	private boolean enabled = true;
	private MenuItem root;
	private MenuPanel menuPanel = new MenuPanel();
	protected Type currentType = null;
	protected MenuRenderer menuRenderer = GWT.create(MenuRenderer.class);
	
	public Menu(LargeType largeType, SmallType smallType)
	{
		FacesResources.INSTANCE.css().ensureInjected();
		initWidget(menuPanel);
		root = new MenuItem(null);
		menuPanel.add(root);
		root.setMenu(this);
		
		setStyleName(getBaseStyleName());
		
		if(menuRenderer instanceof LargeMenuRenderer)
		{
			this.currentType = largeType;	
		} 
		else
		{
			this.currentType = smallType;	
		}
		
		menuRenderer.render(this, largeType, smallType);
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    addStyleName(FacesResources.INSTANCE.css().facesMenu());
		}
	}
	
	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    addStyleName(FacesResources.INSTANCE.css().facesMenu());
	}
	
	public Menu(LargeType largeType)
	{
		this(largeType, null);
	}
	
	public Menu(SmallType smallType)
	{
		this(null, smallType);
	}

	public String getBaseStyleName()
	{
		return STYLE_FACES_MENU;
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<MenuItem> handler)
	{
		return addHandler(handler, SelectionEvent.getType());
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
		
		if(itemsWithEnabledProperty != null)
		{
			for(int i=0; i<itemsWithEnabledProperty.size();i++)
			{
				((HasEnabled) itemsWithEnabledProperty.get(i).getItemWidget()).setEnabled(enabled);
			}
		}
	}

	/**
	 * Remove all menuItems contained into this menu
	 */
	public void clear() 
	{
		if(this.root != null)
		{
			this.root.clear();
		}
	}

	/**
	 * Adds a root item.
	 * @return the inserted item. 
	 */
	public MenuItem addItem(Widget w) 
	{
		return root.addItem(w);
	}

	/**
	 * Adds a label root item.
	 * @return the inserted item. 
	 */
	public MenuItem addItem(String labelText) 
	{
		return root.addItem(labelText);
	}
	
	/**
	 * Adds a html root item.
	 * @return the inserted item. 
	 */
	public MenuItem addItem(SafeHtml html) 
	{
		return root.addItem(html);
	}

	/**
	 * Adds a label item.
	 * @return the inserted item. 
	 */
	public MenuItem addItem(MenuItem placeToInsert, String labelText) 
	{
		if(placeToInsert == null)
		{
			placeToInsert = this.root;
		}
		return placeToInsert.addItem(labelText);
	}
	
	/**
	 * Adds a label item.
	 * @return the inserted item. 
	 */
	public MenuItem addItem(MenuItem placeToInsert, SafeHtml html) 
	{
		if(placeToInsert == null)
		{
			placeToInsert = this.root;
		}
		return placeToInsert.addItem(html);
	}

	public MenuItem addItem(MenuItem placeToInsert, Widget w) 
	{
		if(placeToInsert == null)
		{
			placeToInsert = this.root;
		}
		return placeToInsert.addItem(w);
	}

	public MenuItem getItem(int index)
	{
		return root.getItem(index);
	}
	
	public MenuItem getItem(String path)
	{
		return root.getItem(path);
	}
	
	public int getItemCount()
	{
		return root.getItemCount();
	}
	
	public int indexOf(MenuItem item)
	{
		return root.indexOf(item);
	}
	
	public boolean removeItem(int index)
	{
		return root.removeItem(index);
	}
	
	public void openAll() 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_OPEN, true, MenuUtils.getAllMenuItems(this.root));
	}

	public void open(MenuItem menuItem) 
	{
		if(menuItem == null)
		{
			return;
		}
		
		menuItem.open();
	}

	public void closeAll() 
	{
		MenuUtils.addOrRemoveClass(STYLE_FACES_OPEN, false, MenuUtils.getAllMenuItems(this.root));
	}

	public void close(MenuItem menuItem) 
	{
		if(menuItem == null)
		{
			return;
		}
		
		menuItem.close();
	}

	protected boolean isSlider()
	{
		return currentType.isSlider();
	}
	
	protected boolean isTree()
	{
		return currentType.isTree();
	}
	
	protected void adopt(MenuItem item, Button button)
	{
		menuPanel.adopt(item, button);
	}
	
	protected void adopt(MenuItem item)
	{
		menuPanel.adopt(item);
	}
	
	protected void orphan(MenuItem item)
	{
		menuPanel.orphan(item);
	}
	
	/**
	 * Internal Panel mapped to a nav element around the menu
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	protected static class MenuPanel extends BasePanel
	{
		protected MenuPanel()
        {
	        super("nav");
        }
		
		protected void add(MenuItem item)
		{
		    getElement().appendChild(item.getElement());
		}	

		protected void adopt(MenuItem item, Button button)
		{
			SelectablePanel itemPanel = item.getItemPanel();
			if (itemPanel != null)
			{
			    getChildren().add(button);
				adopt(button);
			}
		}
		
		protected void adopt(MenuItem item)
		{
			SelectablePanel itemPanel = item.getItemPanel();
			if (itemPanel != null)
			{
			    getChildren().add(itemPanel);
				adopt(itemPanel);
			}
		}
		
		protected void orphan(MenuItem item)
		{
			SelectablePanel itemPanel = item.getItemPanel();
			if (itemPanel != null)
			{
				getElement().removeChild(item.getElement());
			    getChildren().remove(itemPanel);
				orphan(itemPanel);
			}
		}
	}
}
