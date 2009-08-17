package br.com.sysmap.crux.advanced.client.grid.datagrid;

import br.com.sysmap.crux.advanced.client.grid.model.ColumnDefinition;

public class DataColumnDefinition extends ColumnDefinition
{
	String formatter;

	public DataColumnDefinition(String label, String width, String formatter, boolean visible)
	{
		super(label, width, visible);
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