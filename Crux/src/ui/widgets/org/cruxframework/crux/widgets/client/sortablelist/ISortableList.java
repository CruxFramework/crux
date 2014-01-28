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
	public void addItem(T widget);
	public void removeItem(T widget);
	public void removeItem(int index);
	
	public List<T> getItems();
	public void setItems(List<T> items);
	
	void setBeanRenderer(SortableList.BeanRenderer<Widget> beanRenderer);
	void setHeader(String headerFieldset);
}
