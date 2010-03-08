package br.com.sysmap.crux.widgets.client.grid;

import br.com.sysmap.crux.core.client.utils.ObjectUtils;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class ColumnDefinition
{
	String key;

	Grid grid = null;
	
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
	 * @return
	 */
	public Grid getGrid()
	{
		return grid;
	}

	/**
	 * @param grid
	 */
	protected void setGrid(Grid grid)
	{
		this.grid = grid;
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

	/**
	 * @return the horizontalAlign
	 */
	public HorizontalAlignmentConstant getHorizontalAlign()
	{
		return horizontalAlign;
	}

	/**
	 * @return the verticalAlign
	 */
	public VerticalAlignmentConstant getVerticalAlign()
	{
		return verticalAlign;
	}

	/**
	 * @param width
	 */
	public void setWidth(String width)
	{
		setWidth(width, true);
	}
	
	/**
	 * @param width
	 * @param refreshGrid
	 */
	public void setWidth(String width, boolean refreshGrid)
	{
		if (!ObjectUtils.isEqual(this.width, width))
		{
			this.width = width;
			maybeRefreshesGrid(refreshGrid);
		}
	}

	/**
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		setVisible(visible, true);
	}

	/**
	 * @param visible
	 * @param refreshGrid
	 */
	public void setVisible(boolean visible, boolean refreshGrid)
	{
		if (this.visible != visible)
		{
			this.visible = visible;
			maybeRefreshesGrid(refreshGrid);
		}
	}

	/**
	 * @param label
	 */
	public void setLabel(String label)
	{
		setLabel(label, true);
	}

	/**
	 * @param label
	 * @param refreshGrid
	 */
	public void setLabel(String label, boolean refreshGrid)
	{
		if (!ObjectUtils.isEqual(this.label, label))
		{
			this.label = label;
			maybeRefreshesGrid(refreshGrid);
		}
	}

	/**
	 * @param horizontalAlign
	 */
	public void setHorizontalAlign(HorizontalAlignmentConstant horizontalAlign)
	{
		setHorizontalAlign(horizontalAlign, true);
	}

	/**
	 * @param horizontalAlign
	 * @param refreshGrid
	 */
	public void setHorizontalAlign(HorizontalAlignmentConstant horizontalAlign, boolean refreshGrid)
	{
		if (!ObjectUtils.isEqual(this.horizontalAlign, horizontalAlign))
		{
			this.horizontalAlign = horizontalAlign;
			maybeRefreshesGrid(refreshGrid);
		}
	}

	/**
	 * @param verticalAlign
	 */
	public void setVerticalAlign(VerticalAlignmentConstant verticalAlign)
	{
		setVerticalAlign(verticalAlign, true);
	}

	/**
	 * @param verticalAlign
	 * @param refreshGrid
	 */
	public void setVerticalAlign(VerticalAlignmentConstant verticalAlign, boolean refreshGrid)
	{
		if (!ObjectUtils.isEqual(this.verticalAlign, verticalAlign))
		{
			this.verticalAlign = verticalAlign;
			maybeRefreshesGrid(refreshGrid);
		}
	}

	/**
	 * @param refreshGrid
	 */
	private void maybeRefreshesGrid(boolean refreshGrid)
	{
		if (this.grid != null && refreshGrid && this.grid.isLoaded())
		{
			this.grid.refresh();
		}
	}
}