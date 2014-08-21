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
package org.cruxframework.crux.smartfaces.client.list;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
abstract class AbstractList extends ComplexPanel 
{
	public AbstractList() 
	{
		setElement(createElement());
		setStyleName(getDefaultClassName());
	}	
	
	public AbstractList(String styleName) 
	{
		setElement(createElement());
		setStyleName(styleName);
	}	

	@Override
	public void add(Widget w)
	{
		Element container = DOM.createElement("li");
	    DOM.appendChild(getElement(), container);
		add(w, container);
	}	
	
	public void insert(IsWidget w, int beforeIndex)
	{
		insert(asWidgetOrNull(w), beforeIndex);
	}
	
	public void insert(Widget w, int beforeIndex)
	{
		insert(w, (Element)getElement(), beforeIndex, true);
	}

	@Override
	protected void insert(Widget child, Element container, int beforeIndex, boolean domInsert)
	{
		// Validate index; adjust if the widget is already a child of this
		// panel.
		beforeIndex = adjustIndex(child, beforeIndex);

		// Detach new child.
		child.removeFromParent();

		// Logical attach.
		getChildren().insert(child, beforeIndex);

		// Physical attach.
		Element li = DOM.createElement("li");
		DOM.appendChild(li, child.getElement());
		if (domInsert)
		{
			DOM.insertChild(container, li, beforeIndex);
		}
		else
		{
			DOM.appendChild(container, li);
		}

		// Adopt.
		adopt(child);
	}
	  
	protected abstract Element createElement();
	protected abstract String getDefaultClassName();
	
//	public void add(ListItem w) 
//	{
//		super.add(w, getElement());
//	}
//
//	public void insert(ListItem w, int beforeIndex) 
//	{
//		super.insert(w, getElement(), beforeIndex, true);
//	}
}
