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

import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FormPanel;

/**
 * Represents a List Box component
 * @author Thiago Bustamante
 */
public class ListBox extends FocusComponent
{
	protected com.google.gwt.user.client.ui.ListBox listBoxWidget;

	public ListBox(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.ListBox());
	}

	protected ListBox(String id, com.google.gwt.user.client.ui.ListBox widget) 
	{
		super(id, widget);
		this.listBoxWidget = widget;
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		renderListItens(element);
		
		String multiple = element.getAttribute("_multiple");
		if (multiple != null && multiple.trim().length() > 0)
		{
			getSelectElement().setMultiple(Boolean.parseBoolean(multiple));

		}

		String selectedIndex = element.getAttribute("_selectedIndex");
		if (selectedIndex != null && selectedIndex.trim().length() > 0)
		{
			setSelectedIndex(Integer.parseInt(selectedIndex));

		}
		
		String visibleItemCount = element.getAttribute("_visibleItemCount");
		if (visibleItemCount != null && visibleItemCount.trim().length() > 0)
		{
			setVisibleItemCount(Integer.parseInt(visibleItemCount));

		}

		super.renderAttributes(element);

	}
	
	/**
	 * Populate the listBox with declared itens
	 * @param element
	 */
	protected void renderListItens(Element element)
	{
		NodeList<Element> itensCandidates = element.getElementsByTagName("span");
		for (int i=0; i<itensCandidates.getLength(); i++)
		{
			if (isValidItem(itensCandidates.getItem(i)))
			{
				processItemDeclaration(itensCandidates.getItem(i), i);
			}
		}
		
	}

	/**
	 * Verify if the span tag found is a valid item declaration for listBoxes
	 * @param element
	 * @return
	 */
	protected boolean isValidItem(Element element)
	{
		if ("span".equalsIgnoreCase(element.getTagName()))
		{
			String item = element.getAttribute("_item");
			if (item != null && item.length() > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Process Item declaration for ListBox
	 * @param element
	 */
	protected void processItemDeclaration(Element element, int index)
	{
		String item = element.getAttribute("_item");
		String value = element.getAttribute("_value");
		if (value == null || value.length() == 0)
		{
			value = item;
		}
		insertItem(item, value, index);

		String selected = element.getAttribute("_selected");
		if (selected != null && selected.trim().length() > 0)
		{
			setItemSelected(index, Boolean.parseBoolean(selected));
		}
	}
	
	private SelectElement getSelectElement() 
	{
		return listBoxWidget.getElement().cast();
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		
		ChangeEvtBind.bindEvent(element, listBoxWidget, getId());
	}

	/**
	 * Adds an item to the list box. This method has the same effect as
	 * 
	 * <pre>
	 * addItem(item, item)
	 * </pre>
	 * 
	 * @param item the text of the item to be added
	 */
	public void addItem(String item) 
	{
		listBoxWidget.addItem(item);
	}

	/**
	 * Adds an item to the list box, specifying an initial value for the item.
	 * 
	 * @param item the text of the item to be added
	 * @param value the item's value, to be submitted if it is part of a
	 *          {@link FormPanel}; cannot be <code>null</code>
	 */
	public void addItem(String item, String value) 
	{
		listBoxWidget.addItem(item, value);
	}	

	/**
	 * Removes all items from the list box.
	 */	
	public void clear() 
	{
		listBoxWidget.clear();
	}

	/**
	 * Gets the number of items present in the list box.
	 * 
	 * @return the number of items
	 */
	public int getItemCount() 
	{	
		return listBoxWidget.getItemCount();
	}

	/**
	 * Gets the text associated with the item at the specified index.
	 * 
	 * @param index the index of the item whose text is to be retrieved
	 * @return the text associated with the item
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public String getItemText(int index) 
	{
		return listBoxWidget.getItemText(index);
	}

	public String getName() 
	{
		return listBoxWidget.getName();
	}

	/**
	 * Gets the currently-selected item. If multiple items are selected, this
	 * method will return the first selected item ({@link #isItemSelected(int)}
	 * can be used to query individual items).
	 * 
	 * @return the selected index, or <code>-1</code> if none is selected
	 */
	public int getSelectedIndex() 
	{
		return listBoxWidget.getSelectedIndex();
	}

	/**
	 * Gets the value associated with the item at a given index.
	 * 
	 * @param index the index of the item to be retrieved
	 * @return the item's associated value
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public String getValue(int index) 
	{
		return listBoxWidget.getValue(index);
	}

	/**
	 * Gets the number of items that are visible. If only one item is visible,
	 * then the box will be displayed as a drop-down list.
	 * 
	 * @return the visible item count
	 */
	public int getVisibleItemCount() 
	{
		return listBoxWidget.getVisibleItemCount();
	}

	/**
	 * Inserts an item into the list box. Has the same effect as
	 * 
	 * <pre>
	 * insertItem(item, item, index)
	 * </pre>
	 * 
	 * @param item the text of the item to be inserted
	 * @param index the index at which to insert it
	 */
	public void insertItem(String item, int index) 
	{
		listBoxWidget.insertItem(item, index);
	}

	/**
	 * Inserts an item into the list box, specifying an initial value for the
	 * item. If the index is less than zero, or greater than or equal to the
	 * length of the list, then the item will be appended to the end of the list.
	 * 
	 * @param item the text of the item to be inserted
	 * @param value the item's value, to be submitted if it is part of a
	 *          {@link FormPanel}.
	 * @param index the index at which to insert it
	 */
	public void insertItem(String item, String value, int index) 
	{
		listBoxWidget.insertItem(item, value, index);
	}

	/**
	 * Determines whether an individual list item is selected.
	 * 
	 * @param index the index of the item to be tested
	 * @return <code>true</code> if the item is selected
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public boolean isItemSelected(int index) 
	{
		return listBoxWidget.isItemSelected(index);
	}

	/**
	 * Gets whether this list allows multiple selection.
	 * 
	 * @return <code>true</code> if multiple selection is allowed
	 */
	public boolean isMultipleSelect() 
	{
		return listBoxWidget.isMultipleSelect();
	}

	/**
	 * Removes the item at the specified index.
	 * 
	 * @param index the index of the item to be removed
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void removeItem(int index) 
	{
		listBoxWidget.removeItem(index);
	}

	/**
	 * Sets whether an individual list item is selected.
	 * 
	 * <p>
	 * Note that setting the selection programmatically does <em>not</em> cause
	 * the {@link ChangeHandler#onChange(ChangeEvent)} event to be fired.
	 * </p>
	 * 
	 * @param index the index of the item to be selected or unselected
	 * @param selected <code>true</code> to select the item
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void setItemSelected(int index, boolean selected) 
	{
		listBoxWidget.setItemSelected(index, selected);
	}

	/**
	 * Sets the text associated with the item at a given index.
	 * 
	 * @param index the index of the item to be set
	 * @param text the item's new text
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void setItemText(int index, String text) 
	{
		listBoxWidget.setItemText(index, text);
	}

	public void setName(String name) 
	{
		listBoxWidget.setName(name);
	}

	/**
	 * Sets the currently selected index.
	 * 
	 * After calling this method, only the specified item in the list will remain
	 * selected. For a ListBox with multiple selection enabled, see
	 * {@link #setItemSelected(int, boolean)} to select multiple items at a time.
	 * 
	 * <p>
	 * Note that setting the selected index programmatically does <em>not</em>
	 * cause the {@link ChangeHandler#onChange(ChangeEvent)} event to be fired.
	 * </p>
	 * 
	 * @param index the index of the item to be selected
	 */
	public void setSelectedIndex(int index) 
	{
		listBoxWidget.setSelectedIndex(index);
	}

	/**
	 * Sets the value associated with the item at a given index. This value can be
	 * used for any purpose, but is also what is passed to the server when the
	 * list box is submitted as part of a {@link FormPanel}.
	 * 
	 * @param index the index of the item to be set
	 * @param value the item's new value; cannot be <code>null</code>
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void setValue(int index, String value) 
	{
		listBoxWidget.setValue(index, value);
	}

	/**
	 * Sets the number of items that are visible. If only one item is visible,
	 * then the box will be displayed as a drop-down list.
	 * 
	 * @param visibleItems the visible item count
	 */
	public void setVisibleItemCount(int visibleItems) 
	{
		listBoxWidget.setVisibleItemCount(visibleItems);
	}
}
