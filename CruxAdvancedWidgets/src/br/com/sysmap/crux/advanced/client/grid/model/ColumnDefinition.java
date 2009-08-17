package br.com.sysmap.crux.advanced.client.grid.model;

public class ColumnDefinition
{
	String width;
	boolean visible;
	String label;
	String key;

	public ColumnDefinition(String label, String width, boolean visible)
	{
		this.label = label;
		this.width = width;
		this.visible = visible;
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