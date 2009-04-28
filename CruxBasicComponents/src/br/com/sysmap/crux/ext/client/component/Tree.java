/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.ext.client.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.component.Container;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.event.bind.FocusEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;
import br.com.sysmap.crux.core.client.event.bind.OpenEvtBind;
import br.com.sysmap.crux.core.client.event.bind.SelectionEvtBind;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a Tree Component
 * @author Thiago Bustamante
 */
public class Tree extends Container
{
	protected com.google.gwt.user.client.ui.Tree treeWidget;
	protected char accessKey;
	protected Map<com.google.gwt.user.client.ui.TreeItem,Component> componentsForItens = 
							new HashMap<com.google.gwt.user.client.ui.TreeItem, Component>();
	
	public Tree(String id, Element element) 
	{
		this(id, createTreeWidget(id, element));
	}

	public Tree(String id, boolean useLeafImages, TreeImages treeImages) 
	{
		this(id, new com.google.gwt.user.client.ui.Tree(treeImages, useLeafImages));
	}
	
	public Tree(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.Tree());
	}

	protected Tree(String id, com.google.gwt.user.client.ui.Tree widget) 
	{
		super(id, widget);
		this.treeWidget = widget;
	}

	/**
	 * Creates the Tree widget
	 * @param element
	 * @return
	 */
	private static com.google.gwt.user.client.ui.Tree createTreeWidget(String id, Element element)
	{
		Event eventLoadImage = EvtBind.getComponentEvent(element, EventFactory.EVENT_LOAD_IMAGES);
		if (eventLoadImage != null)
		{
			TreeImages treeImages = (TreeImages) EventFactory.callEvent(eventLoadImage, id);

			String useLeafImagesStr = element.getAttribute("_useLeafImages");
			boolean useLeafImages = true;
			if (useLeafImagesStr != null && useLeafImagesStr.length() > 0)
			{
				useLeafImages = (Boolean.parseBoolean(useLeafImagesStr));
			}
			
			return new com.google.gwt.user.client.ui.Tree(treeImages, useLeafImages);
		}
		return new com.google.gwt.user.client.ui.Tree();
	}
	
	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		
		String openSelectedItem = element.getAttribute("_openSelectedItem");
		if (openSelectedItem != null && openSelectedItem.length() > 0)
		{
			if(Boolean.parseBoolean(openSelectedItem))
			{
				ensureSelectedItemVisible();
			}
		}
		
		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.length() > 0)
		{
			setTabIndex(Integer.parseInt(tabIndex));
		}

		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.length() == 1)
		{
			this.accessKey = accessKey.charAt(0);
			setAccessKey(this.accessKey);
		}
		
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		
		try
		{
			renderTreeItens(element);

		} 
		catch (InterfaceConfigException e1) 
		{
			GWT.log(e1.getLocalizedMessage(), e1);
		}
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		FocusEvtBind.bindEvents(element, treeWidget, getId());
		OpenEvtBind.bindEvent(element, treeWidget, getId());
		CloseEvtBind.bindEvent(element, treeWidget, getId());
		MouseEvtBind.bindEvents(element, treeWidget, getId());
		KeyEvtBind.bindEvents(element, treeWidget, getId());
		SelectionEvtBind.bindEvent(element, treeWidget, getId());
	}

	@Override
	protected void addWidget(Widget widget)
	{
		treeWidget.add(widget);
	}

	/**
	 * Adds a simple tree item containing the specified text.
	 * 
	 * @param itemText the text of the item to be added
	 * @return the item that was added
	 */
	public TreeItem addItem(String itemText) 
	{
		return new TreeItem(treeWidget.addItem(itemText), this);
	}

	/**
	 * Adds an item to the root level of this tree.
	 * 
	 * @param item the item to be added
	 */
	public void addItem(TreeItem item) 
	{
		treeWidget.addItem(item.treeItem);
	}

	/**
	 * Adds a new tree item containing the specified widget.
	 * 
	 * @param widget the widget to be added
	 * @return the new item
	 */
	public TreeItem addItem(Component component) 
	{
		TreeItem ret = new TreeItem(treeWidget.addItem(getComponentWidget(component)), this);
		addComponentForItem(ret.treeItem, component);
		return ret;
	}
	
	/**
	 * Ensures that the currently-selected item is visible, opening its parents
	 * and scrolling the tree as necessary.
	 */
	public void ensureSelectedItemVisible() 
	{
		treeWidget.ensureSelectedItemVisible();
	}

	/**
	 * Gets the top-level tree item at the specified index.
	 * 
	 * @param index the index to be retrieved
	 * @return the item at that index
	 */
	public TreeItem getItem(int index) 
	{
		return new TreeItem(treeWidget.getItem(index), this);
	}

	/**
	 * Gets the number of items contained at the root of this tree.
	 * 
	 * @return this tree's item count
	 */
	public int getItemCount() 
	{
		return treeWidget.getItemCount();
	}

	/**
	 * Gets the currently selected item.
	 * 
	 * @return the selected item
	 */
	public TreeItem getSelectedItem() 
	{
		return new TreeItem(treeWidget.getSelectedItem(), this);
	}

	public int getTabIndex() 
	{
		return treeWidget.getTabIndex();
	}

	public boolean isAnimationEnabled() 
	{
		return treeWidget.isAnimationEnabled();
	}
	
	@Override
	protected void removeWidget(Widget widget) 
	{
		treeWidget.remove(widget);
	}

	/**
	 * Removes an item from the root level of this tree.
	 * 
	 * @param item the item to be removed
	 */
	public void removeItem(TreeItem item) 
	{
		treeWidget.removeItem(item.treeItem);
		removeComponentForItem(item.treeItem);
	}

	/**
	 * Removes all items from the root level of this tree.
	 */
	public void removeItems() 
	{
		treeWidget.removeItems();
		componentsForItens.clear();
	}	

	public void setAccessKey(char key) 
	{
		treeWidget.setAccessKey(key);
	}

	public void setAnimationEnabled(boolean enable) 
	{
		treeWidget.setAnimationEnabled(enable);
	}

	public void setFocus(boolean focus) 
	{
		treeWidget.setFocus(focus);
	}

	/**
	 * Selects a specified item.
	 * 
	 * @param item the item to be selected, or <code>null</code> to deselect all
	 *          items
	 */
	public void setSelectedItem(TreeItem item) 
	{
		treeWidget.setSelectedItem(item.treeItem);
	}

	/**
	 * Selects a specified item.
	 * 
	 * @param item the item to be selected, or <code>null</code> to deselect all
	 *          items
	 * @param fireEvents <code>true</code> to allow selection events to be fired
	 */
	public void setSelectedItem(TreeItem item, boolean fireEvents) 
	{
		treeWidget.setSelectedItem(item.treeItem, fireEvents);
	}

	public void setTabIndex(int index) 
	{
		treeWidget.setTabIndex(index);
	}

	/**
	 * Iterator of tree items.
	 * 
	 * @return the iterator
	 */
	public Iterator<TreeItem> treeItemIterator() 
	{
		final Iterator<com.google.gwt.user.client.ui.TreeItem> it = treeWidget.treeItemIterator();
		final Tree tree = this;
		return new Iterator<TreeItem>()
		{
			@Override
			public boolean hasNext() 
			{
				return it.hasNext();
			}

			@Override
			public TreeItem next() 
			{
				return new TreeItem(it.next(), tree);
			}

			@Override
			public void remove() 
			{
				it.remove();
			}
		};
	}

	/**
	 * Return the component accessKey
	 * @return accessKey
	 */
	public char getAccessKey() 
	{
		return accessKey;
	}
	
	/**
	 * Populate the tree with declared itens
	 * @param element
	 * @throws InterfaceConfigException 
	 */
	protected void renderTreeItens(Element element) throws InterfaceConfigException
	{
		renderTreeItens(element, null);
	}
	
	protected void renderTreeItens(Element element, TreeItem parent) throws InterfaceConfigException
	{
		NodeList<Node> itensCandidates = element.getChildNodes();
		for (int i=0; i<itensCandidates.getLength(); i++)
		{
			if (isValidItem(itensCandidates.getItem(i)))
			{
				Element e = (Element)itensCandidates.getItem(i);
				TreeItem item = renderTreeItem(e, parent);
				renderTreeItens(e, item);
			}
		}
	}
	
	protected TreeItem renderTreeItem(Element e, TreeItem parent) throws InterfaceConfigException
	{
		String type = e.getAttribute("_type");
		if (type != null && type.length() > 0)
		{
			if (parent != null)
			{
				return parent.addItem(createChildComponent(e, e.getAttribute("id")));
			}
			else
			{
				return addItem(createChildComponent(e, e.getAttribute("id")));
			}
		}
		else
		{
			String text = e.getAttribute("_text");
			if (parent != null)
			{
				return parent.addItem(text);
			}
			else
			{
				return addItem(text);
			}
		}
	}

	/**
	 * Verify if the span tag found is a valid item declaration for trees
	 * @param element
	 * @return
	 */
	protected boolean isValidItem(Node node)
	{
		if (node instanceof Element)
		{
			Element element = (Element)node;
			if ("span".equalsIgnoreCase(element.getTagName()))
			{
				String text = element.getAttribute("_text");
				String type = element.getAttribute("_type");
				String id = element.getAttribute("id");
				if ((text != null && text.length() > 0) || 
					((type != null && type.length() > 0) && (id != null && id.length() > 0)))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected Widget getComponentWidget(Component component)
	{
		return super.getComponentWidget(component);
	}
	
	protected Component getComponent(com.google.gwt.user.client.ui.TreeItem treeItem)
	{
		return componentsForItens.get(treeItem);
	}
	
	protected void addComponentForItem(com.google.gwt.user.client.ui.TreeItem treeItem, Component component)
	{
		componentsForItens.put(treeItem, component);
	}

	protected void removeComponentForItem(com.google.gwt.user.client.ui.TreeItem treeItem)
	{
		componentsForItens.remove(treeItem);
	}

	@Override
	protected void clearWidgetChildren(Widget widget) 
	{
		this.treeWidget.clear();
		componentsForItens.clear();
	}
}
