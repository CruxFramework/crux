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

import br.com.sysmap.crux.core.client.component.Component;


/**
 * Represents a Tree Item
 * @author Thiago Bustamante
 */
public class TreeItem 
{
	protected com.google.gwt.user.client.ui.TreeItem treeItem;
	protected Tree tree;
	
	TreeItem(com.google.gwt.user.client.ui.TreeItem treeItem, Tree tree)
	{
		this.treeItem = treeItem;
		this.tree = tree;
	}

	 /**
	   * Adds a child tree item containing the specified text.
	   * 
	   * @param itemText the text to be added
	   * @return the item that was added
	   */
	  public TreeItem addItem(String itemText) 
	  {
	    return new TreeItem(treeItem.addItem(itemText), tree);
	  }

	  /**
	   * Adds another item as a child to this one.
	   * 
	   * @param item the item to be added
	   */
	  public void addItem(TreeItem item) 
	  {
		  treeItem.addItem(item.treeItem);
	  }

	  /**
	   * Adds a child tree item containing the specified widget.
	   * 
	   * @param component the component to be added
	   * @return the item that was added
	   */
	  public TreeItem addItem(Component component) 
	  {
		  com.google.gwt.user.client.ui.TreeItem item = new com.google.gwt.user.client.ui.TreeItem(tree.getComponentWidget(component));
		  TreeItem result = new TreeItem(item, tree);
		  addItem(result);
		  tree.addComponentForItem(result.treeItem, component);
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
	   * Gets the <code>Widget</code> associated with this tree item.
	   * 
	   * @return the widget
	   */
	  public Component getComponent() 
	  {
	    return tree.getComponent(this.treeItem);
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
		  treeItem.remove();
		  tree.removeComponentForItem(treeItem);
	  }

	  /**
	   * Removes one of this item's children.
	   * 
	   * @param item the item to be removed
	   */

	  public void removeItem(TreeItem item) 
	  {
		  treeItem.removeItem(item.treeItem);
		  tree.removeComponentForItem(item.treeItem);
	  }

	  /**
	   * Removes all of this item's children.
	   */
	  public void removeItems() 
	  {
		  while (getChildCount() > 0)
		  {
		      removeItem(getChild(0));
		  }
	  }

	  public void setHTML(String html) 
	  {
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
	   * Sets the current widget. Any existing child widget will be removed.
	   * 
	   * @param newWidget Widget to set
	   */
	  public void setComponent(Component component) 
	  {
		  if (component != null)
		  {
			  treeItem.setWidget(tree.getComponentWidget(component));
			  tree.componentsForItens.put(treeItem, component);
		  }
		  else
		  {
			  treeItem.setWidget(null);
			  tree.componentsForItens.remove(treeItem);
		  }
	  }
}
