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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

/**
 * Defines the contract to implement an underlying table for the Grid.
 * 
 * @author Gesse Dafe
 */
public interface GridBaseTable {

	Element getCellElement(int row, int col);
	Element getRowElement(int row);
	Element getBodyElement();
	void setCellSpacing(int cellSpacing);
	void setCellPadding(int i);
	void setWidth(String string);
	com.google.gwt.dom.client.Element getElement();
	Widget asWidget();
	void removeAllRows();
	void resize(int rowCount, int columnCount);
	Widget getWidget(int index, int column);
	void setWidget(int index, int column, Widget widget);
	void setCellAlignment(int index, int colIndex, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign);
	void setCellWidth(int index, int colIndex, String width);
	void setVisible(boolean visible);
	void onAfterRender();
}
