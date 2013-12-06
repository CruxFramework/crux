/*
 * Copyright 2011 cruxframework.org.
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

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
public class WidgetColumnDefinition extends ColumnDefinition
{
	private WidgetColumnCreator creator;
	

	public WidgetColumnDefinition(String label, String width, WidgetColumnCreator creator, 
								 boolean visible, HorizontalAlignmentConstant horizontalAlign, 
								 VerticalAlignmentConstant verticalAlign)
	{
		super(label, width, visible, horizontalAlign, verticalAlign);
		this.creator = creator;
	}
	

	public WidgetColumnDefinition(String label, String width, WidgetColumnCreator creator, 
			 boolean visible, boolean frozen, HorizontalAlignmentConstant horizontalAlign, 
			 VerticalAlignmentConstant verticalAlign)
	{
		super(label, width, visible, frozen, horizontalAlign, verticalAlign);
		this.creator = creator;
	}
	
	public WidgetColumnCreator getWidgetColumnCreator()
    {
    	return creator;
    }

	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface WidgetColumnCreator
	{
		Widget createWidgetForColumn();
	}
}