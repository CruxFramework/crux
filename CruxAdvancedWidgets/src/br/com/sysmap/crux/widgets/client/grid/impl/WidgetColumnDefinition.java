package br.com.sysmap.crux.widgets.client.grid.impl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

import br.com.sysmap.crux.widgets.client.grid.model.ColumnDefinition;

/**
 * TODO - Gess� - Comment this
 * TODO - Gess� - widget columns should not be sortable 
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
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