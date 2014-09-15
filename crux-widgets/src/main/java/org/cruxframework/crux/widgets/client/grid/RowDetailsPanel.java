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
package org.cruxframework.crux.widgets.client.grid;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple panel where the details of a grid's row will be attached.
 * @author Gesse Dafe
 */
public class RowDetailsPanel extends Composite
{
	private SimplePanel base = new SimplePanel();
	private DataRow row;
	private final RowDetailWidgetCreator rowDetailWidgetCreator;

	RowDetailsPanel(DataRow row, RowDetailWidgetCreator rowDetailWidgetCreator) 
	{
		this.row = row;
		this.rowDetailWidgetCreator = rowDetailWidgetCreator;
		initWidget(base);
		setStyleName("crux-RowDetailsPanel");
	}

	Row getRow() 
	{
		return row;
	}
	
	/**
	 * @param <T>
	 * @param id
	 * @return The widget with the given id contained in this detail panel.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Widget> T getWidget(String id)
	{
		return (T) rowDetailWidgetCreator.getWidget(row, id);
	}

	/**
	 * Adds a widget to this panel 
	 * @param w
	 */
	public void add(Widget w) 
	{
		base.add(w);
	}
	
	/**
	 * Removes all child widgets.
	 */
	public void clear()
	{
		base.clear();
	}
}
