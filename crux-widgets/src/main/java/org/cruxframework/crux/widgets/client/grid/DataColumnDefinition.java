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

import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.widgets.client.grid.DataColumnEditorCreators.DataColumnEditorCreator;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class DataColumnDefinition extends ColumnDefinition
{
	private Formatter formatter;
	private boolean wrapLine;
	private boolean sortable;
	private DataColumnEditorCreator<?> editorCreator;

	/**
	 * @param label
	 * @param width
	 * @param formatter
	 * @param visible
	 * @param wrapLine
	 * @param horizontalAlign
	 * @param verticalAlign
	 */
	@Deprecated
	public DataColumnDefinition(String label, String width, Formatter formatter, boolean visible, boolean wrapLine, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign)
	{
		this(label, width, formatter, visible, true, wrapLine, false, horizontalAlign, verticalAlign, null);
	}

	/**
	 * @param label
	 * @param width
	 * @param formatter
	 * @param visible
	 * @param sortable
	 * @param wrapLine
	 * @param horizontalAlign
	 * @param verticalAlign
	 */
	public DataColumnDefinition(String label, String width, Formatter formatter, boolean visible, boolean sortable, boolean wrapLine, boolean frozen, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign, DataColumnEditorCreator<?> editorCreator)
	{
		super(label, width, visible, frozen, horizontalAlign, verticalAlign);
		this.formatter = formatter;
		this.wrapLine = wrapLine;
		this.sortable = sortable;
		this.editorCreator = editorCreator;
		super.isDataColumn = true;
	}

	/**
	 * @return the formatter
	 */
	public Formatter getFormatter()
	{
		return formatter;
	}

	/**
	 * @return the wrapLine
	 */
	public boolean isWrapLine()
	{
		return wrapLine;
	}

	public boolean isSortable()
    {
    	return sortable;
    }

	public void setSortable(boolean sortable)
    {
    	this.sortable = sortable;
    }

	public DataColumnEditorCreator<?> getEditorCreator() 
	{
		return this.editorCreator;
	}

	/**
	 * @param editorCreator the editorCreator to set
	 */
	public void setEditorCreator(DataColumnEditorCreator<?> editorCreator) 
	{
		this.editorCreator = editorCreator;
	}
}