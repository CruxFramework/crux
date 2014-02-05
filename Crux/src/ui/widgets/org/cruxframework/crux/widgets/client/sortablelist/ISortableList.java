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
package org.cruxframework.crux.widgets.client.sortablelist;

import java.util.List;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso
 */
public interface ISortableList<T extends Widget> extends HasEnabled
{
	/**
	 * @param widget adds a widget to the list.
	 */
	public void addItem(T widget);
	/**
	 * @param widget the widget to be removed from list
	 * @return true if it's removed and false otherwise
	 */
	public boolean removeItem(T widget);
	/**
	 * @param index the widget index to be removed from list 
	 * @return true if it's removed and false otherwise
	 */
	public boolean removeItem(int index);
	/**
	 * @return all the widget items
	 */
	public List<T> getItems();
	/**
	 * @param items add the items to the list
	 */
	public void setItems(List<T> items);
	/**
	 * @param beanRenderer set's how the widget should be rendered in screen
	 */
	void setBeanRenderer(SortableList.BeanRenderer<Widget> beanRenderer);
	/**
	 * @param headerFieldset set's the component header
	 */
	void setHeader(String headerFieldset);
}
