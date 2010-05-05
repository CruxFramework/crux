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
package br.com.sysmap.crux.widgets.client.grid;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * TODO - Gessé - Comment this
 * TODO - Gessé - widget columns should not be sortable 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class WidgetColumnDefinition extends ColumnDefinition
{
	Element widgetTemplate;

	public WidgetColumnDefinition(String label, String width, Element widgetTemplate, boolean visible, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign)
	{
		super(label, width, visible, horizontalAlign, verticalAlign);
		this.widgetTemplate = widgetTemplate;
	}

	public Element getWidgetTemplate()
	{
		return widgetTemplate;
	}
}