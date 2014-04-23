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
package org.cruxframework.crux.widgets.client.deviceadaptivegrid;


import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition.WidgetColumnCreator;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * @author wesley.diniz
 *
 */
public class ActionColumnDefinition extends WidgetColumnDefinition 
{

	/**
	 * @param label
	 * @param width
	 * @param creator
	 * @param visible
	 * @param frozen
	 * @param horizontalAlign
	 * @param verticalAlign
	 */
	public ActionColumnDefinition(String label, String width, WidgetColumnCreator creator, boolean visible, 
			boolean frozen, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign)
	{
		super(label, width, creator, visible, frozen, horizontalAlign, verticalAlign);
	}
}
