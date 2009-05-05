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
package br.com.sysmap.crux.basic.client;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.component.UIObject;


/**
 * Represents a Tree Item
 * @author Thiago Bustamante
 */
public class TreeItem extends UIObject
{
	protected com.google.gwt.user.client.ui.TreeItem treeItem;
	protected Tree tree;
	protected List<TreeItem> itens = new ArrayList<TreeItem>();
	protected Component component;

	TreeItem(com.google.gwt.user.client.ui.TreeItem treeItem, Tree tree)
	{
		super(treeItem);

		this.treeItem = treeItem;
		this.tree = tree;
	}

	public TreeItem(Tree tree)
	{
		this(new com.google.gwt.user.client.ui.TreeItem(), tree);
	}
	
	public TreeItem(Tree tree, String html)
	{
		this(new com.google.gwt.user.client.ui.TreeItem(html), tree);
	}

	public TreeItem(Component component, Tree tree)
	{
		this(tree);
		setComponent(component);
	}
	
	/**
	 * Adds a child tree item containing the specified text.
	 * 
	 * @param itemText the text to be added
	 * @return the item that was added
	 */
	public TreeItem addItem(String itemText) 
	{
		TreeItem ret = new TreeItem(treeItem.addItem(itemText), tree);
		itens.add(ret);
		return ret;
	}

	/**
	 * Adds another item as a child to this one.
	 * 
	 * @param item the item to be added
	 */
	public void addItem(TreeItem item) 
	{
		treeItem.addItem(item.treeItem);
		itens.add(item);
	}

	/**
	 * Adds a child tree item containing the specified widget.
	 * 
	 * @param component the component to be added
	 * @return the item that was added
	 */
	public TreeItem addItem(Component component) 
	{
		TreeItem result = new TreeItem(component, tree);
		addItem(result);
		return result;
	}

	/**
	 * Gets the child at the specified index.
	 * 
	 * @param index the index to be retrieved
	 * @return the item at that index
	 */

	public TreeItem getChild(int index) 
	{
		return new TreeItem(treeItem.getChild(index), tree);
	}

	/**
	 * Gets the number of children contained in this item.
	 * 
	 * @return this item's child count.
	 */

	public int getChildCount() 
	{
		return treeItem.getChildCount();
	}

	/**
	 * Gets the index of the specified child item.
	 * 
	 * @param child the child item to be found
	 * @return the child's index, or <code>-1</code> if none is found
	 */

	public int getChildIndex(TreeItem child) 
	{
		return treeItem.getChildIndex(child.treeItem);
	}

	public String getHTML() 
	{
		return treeItem.getHTML();
	}

	/**
	 * Gets this item's parent.
	 * 
	 * @return the parent item
	 */
	public TreeItem getParentItem() 
	{
		return new TreeItem(treeItem.getParentItem(), tree);
	}

	/**
	 * Gets whether this item's children are displayed.
	 * 
	 * @return <code>true</code> if the item is open
	 */
	public boolean getState() 
	{
		return treeItem.getState();
	}

	public String getText() 
	{
		return treeItem.getText();
	}

	/**
	 * Gets the tree that contains this item.
	 * 
	 * @return the containing tree
	 */
	public final Tree getTree() 
	{
		return tree;
	}

	/**
	 * Gets the user-defined object associated with this item.
	 * 
	 * @return the item's user-defined object
	 */
	public Object getUserObject() 
	{
		return treeItem.getUserObject();
	}

	/**
	 * Gets the <code>Component</code> associated with this tree item.
	 * 
	 * @return the component
	 */
	public Component getComponent() 
	{
		return component;
	}

	/**
	 * Determines whether this item is currently selected.
	 * 
	 * @return <code>true</code> if it is selected
	 */
	public boolean isSelected() 
	{
		return treeItem.isSelected();
	}

	/**
	 * Removes this item from its tree.
	 */
	public void remove() 
	{
		tree.removeItem(this);
	}

	/**
	 * Removes one of this item's children.
	 * 
	 * @param item the item to be removed
	 */

	public void removeItem(TreeItem item) 
	{
		treeItem.removeItem(item.treeItem);
		itens.remove(item);
	}

	/**
	 * Removes all of this item's children.
	 */
	public void removeItems() 
	{
		treeItem.removeItems();
		itens.clear();
	}

	public void setHTML(String html) 
	{
		setComponent(null);
		treeItem.setHTML(html);
	}

	/**
	 * Selects or deselects this item.
	 * 
	 * @param selected <code>true</code> to select the item, <code>false</code> to
	 *          deselect it
	 */
	public void setSelected(boolean selected) 
	{
		treeItem.setSelected(selected);
	}

	/**
	 * Sets whether this item's children are displayed.
	 * 
	 * @param open whether the item is open
	 */
	public void setState(boolean open) 
	{
		treeItem.setState(open);
	}

	/**
	 * Sets whether this item's children are displayed.
	 * 
	 * @param open whether the item is open
	 * @param fireEvents <code>true</code> to allow open/close events to be
	 */
	public void setState(boolean open, boolean fireEvents) 
	{
		treeItem.setState(open, fireEvents);
	}

	public void setText(String text) 
	{
		setComponent(null);
		treeItem.setText(text);
	}

	/**
	 * Sets the user-defined object associated with this item.
	 * 
	 * @param userObj the item's user-defined object
	 */
	public void setUserObject(Object userObj) 
	{
		treeItem.setUserObject(userObj);
	}

	/**
	 * Sets the current component. Any existing child component will be removed.
	 * 
	 * @param component Component to set
	 */
	public void setComponent(Component component) 
	{
		if (component != null)
		{
			treeItem.setWidget(tree.getComponentWidget(component));
			this.component = component;
		}
		else
		{
			treeItem.setWidget(null);
			this.component = null;
		}
	}
}
