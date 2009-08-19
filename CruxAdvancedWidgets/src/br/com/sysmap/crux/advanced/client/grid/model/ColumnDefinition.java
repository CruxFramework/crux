package br.com.sysmap.crux.advanced.client.grid.model;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class ColumnDefinition
{
	String key;
	
	String width;
	boolean visible;
	String label;
	
	HorizontalAlignmentConstant horizontalAlign;
	VerticalAlignmentConstant verticalAlign;

	public ColumnDefinition(String label, String width, boolean visible, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign)
	{
		this.label = label;
		this.width = width;
		this.visible = visible;
		this.horizontalAlign = horizontalAlign == null ? HasHorizontalAlignment.ALIGN_CENTER : horizontalAlign;
		this.verticalAlign = verticalAlign == null ? HasVerticalAlignment.ALIGN_MIDDLE : verticalAlign;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	protected void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * @return the width
	 */
	public String getWidth()
	{
		return width;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @return the key
	 */
	public String getKey()
	{
		return key;
	}
}