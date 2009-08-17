package br.com.sysmap.crux.advanced.client.grid.model;

public class GridLayoutMozImpl extends GridLayout
{
	void setTableLayout(GridHtmlTable table)
	{
		table.getElement().getStyle().setProperty("tableLayout", "fixed");
	}
}
