/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.listshuttle;

import java.util.List;

import org.cruxframework.crux.widgets.client.listshuttle.ListShuttle.BeanRenderer;

import com.google.gwt.user.client.ui.HasEnabled;

/**
 * @author Jair Elton
 * @author Samuel Almeida Cardoso
 */
public interface IListShuttle<T> extends HasEnabled
{
	public void setAvailableHeader(String availableHeader);
	public void setSelectedHeader(String selectedHeader);
	
	public void setSelectedItems(List<T> selectedItems);
	public List<T> getSelectedItems();
	public void setAvailableItems(List<T> availableItems);
	public List<T> getAvailableItems();
	
	public void setBeanRenderer(BeanRenderer<T> beanRenderer);
	
}
