package org.cruxframework.crux.widgets.client.deviceadaptivegrid;


import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinitions;


/**
 * Represents the columns to be rendered by a grid widget.
 * Control columns to show by device in use
 * @author Marcelo Smith
 */
public class DeviceAdaptiveGridColumnDefinitions
{
	private ColumnDefinitions largeColumnDefinitions = new ColumnDefinitions();
	private ColumnDefinitions smallColumnDefinitions = new ColumnDefinitions();


	/**
	 * Register a new column definition
	 * @param size
	 * @param key
	 * @param definition
	 */
	public void add(Size deviceSize, String key, ColumnDefinition definition)
	{
		if (Size.large.equals(deviceSize))
		{
			largeColumnDefinitions.add(key, definition);
		}
		else
		{
			smallColumnDefinitions.add(key, definition);
		}
	}

	public ColumnDefinitions getLargeColumnDefinitions()
	{
		return largeColumnDefinitions;
	}

	public ColumnDefinitions getSmallColumnDefinitions()
	{
		return smallColumnDefinitions;
	}

}