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
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.event.HasSelectHandlers;
import org.cruxframework.crux.smartfaces.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.label.HTML;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device menu item
 * 
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * @author Thiago da Rosa de Bustamante
 */
public class MenuItem extends UIObject implements HasSelectHandlers
{
	private SelectablePanel itemPanel;
	private MenuItem parentItem;
	private FastList<MenuItem> children = new FastList<MenuItem>();
	private boolean root;
	private Element childrenContainer;
	private HandlerManager handlerManager;
	private Menu menu;

	protected MenuItem(Widget itemWidget)
	{
		if (itemWidget == null)
		{
			root = true;
			childrenContainer = DOM.createElement("ul");
			setElement(childrenContainer);
			setStyleName(Menu.STYLE_FACES_UL);
		}
		else
		{
			this.itemPanel = new SelectablePanel();
			this.itemPanel.add(itemWidget);
			setElement(DOM.createElement("li"));
			setStyleName(Menu.STYLE_FACES_LI);
			DOM.appendChild(getElement(), itemPanel.getElement());
		}
	}

	protected void setMenu(Menu menu)
	{
		if (this.menu != menu)
		{
			if (menu == null)
			{
				this.menu.orphan(this);
			}
			else
			{
				if (this.menu != null)
				{
					this.menu.orphan(this);
				}
				menu.adopt(this);
			}
			this.menu = menu;
			for (int i = 0; i < children.size(); i++)
			{
				children.get(i).setMenu(menu);
			}
		}
	}

	public boolean isRoot()
	{
		return root;
	}

	public void open()
	{
		MenuUtils.addOrRemoveClass(Menu.STYLE_FACES_OPEN, true, this);
	}

	public void close()
	{
		MenuUtils.addOrRemoveClass(Menu.STYLE_FACES_OPEN, false, this);
	}

	public void clear()
	{
		for (int i = 0; i < children.size(); i++)
		{
			children.get(i).setMenu(null);
		}
		if (childrenContainer != null)
		{
			childrenContainer.removeFromParent();
		}
		children.clear();
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return addHandler(handler, SelectEvent.getType());
	}
	
	public void addItem(final MenuItem menuItem)
	{
		if (childrenContainer == null)
		{
			childrenContainer = DOM.createElement("ul");
			DOM.appendChild(getElement(), childrenContainer);
			setStyleName(childrenContainer, Menu.STYLE_FACES_UL);
		}
		childrenContainer.appendChild(menuItem.getElement());
		menuItem.parentItem = this;
		children.add(menuItem);
		menuItem.setMenu(menu);

		menuItem.getItemPanel().addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				SelectEvent.fire(menuItem);
			}
		});

		if (!root)
		{
			getElement().addClassName(Menu.STYLE_FACES_HAS_CHILDREN);
		}
		else
		{
			getElement().removeClassName(Menu.STYLE_FACES_EMPTY);
		}
	}

	public MenuItem addItem(Widget widget)
	{
		MenuItem menuItem = new MenuItem(widget);
		addItem(menuItem);
		return menuItem;
	}

	public MenuItem addItem(String labelText)
	{
		return addItem(new Label(labelText));
	}

	public MenuItem addItem(SafeHtml html)
	{
		return addItem(new HTML(html));
	}

	public boolean removeItem(MenuItem item)
	{
		return removeItem(indexOf(item));
	}

	public boolean removeItem(int index)
	{
		if (index >= 0 && index < children.size())
		{
			MenuItem item = getItem(index);
			item.getElement().removeFromParent();
			children.remove(index);
			item.setMenu(null);
			if (children.size() <= 0)
			{
				if (!root)
				{
					item.removeClassName(Menu.STYLE_FACES_HAS_CHILDREN);
				}
				else
				{
					item.addClassName(Menu.STYLE_FACES_EMPTY);
				}
			}
			return true;
		}
		return false;
	}

	public int getItemCount()
	{
		return children.size();
	}

	public MenuItem getItem(int index)
	{
		return children.get(index);
	}

	public int indexOf(MenuItem item)
	{
		return children.indexOf(item);
	}

	public MenuItem getItem(String path)
	{
		if (StringUtils.isEmpty(path))
		{
			return null;
		}
		String[] items = path.split("-");
		MenuItem result = null;
		MenuItem current = this;
		for (String index : items)
		{
			result = current.getItem(Integer.parseInt(index));
			current = result;
		}

		return result;
	}

	public void removeFromMenu()
	{
		if (parentItem != null)
		{
			parentItem.removeItem(this);
		}
	}

	public Widget getItemWidget()
	{
		return itemPanel.getChildWidget();
	}
	
	public void addClassName(String className)
	{
		getElement().addClassName(className);
	}

	public void removeClassName(String className)
	{
		getElement().removeClassName(className);
	}

	public MenuItem getParentItem()
	{
		return parentItem;
	}

	@Override
	public void fireEvent(GwtEvent<?> event)
	{
		if (handlerManager != null)
		{
			handlerManager.fireEvent(event);
		}
	}	

	FastList<MenuItem> getChildren()
	{
		return children;
	}
	
	/**
	 * Ensures the existence of the handler manager.
	 * 
	 * @return the handler manager
	 * */
	HandlerManager ensureHandlers()
	{
		return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
	}

	<H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type)
	{
		return ensureHandlers().addHandler(type, handler);
	}
	
	SelectablePanel getItemPanel()
	{
		return itemPanel;
	}
}
