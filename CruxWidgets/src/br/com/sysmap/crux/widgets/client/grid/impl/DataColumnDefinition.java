package br.com.sysmap.crux.widgets.client.grid.impl;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

import br.com.sysmap.crux.widgets.client.grid.model.ColumnDefinition;

public class DataColumnDefinition extends ColumnDefinition
{
	String formatter;

	public DataColumnDefinition(String label, String width, String formatter, boolean visible, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign)
	{
		super(label, width, visible, horizontalAlign, verticalAlign);
		this.formatter = formatter;
	}

	/**
	 * @return the formatter
	 */
	public String getFormatter()
	{
		return formatter;
	}
}