package br.com.sysmap.crux.advanced.client.grid.model;

import com.google.gwt.user.client.ui.ScrollPanel;

public class GridLayout
{
	void adjustToBrowser(ScrollPanel scrollingArea, GridHtmlTable table)
	{
		scrollingArea.getElement().getStyle().setProperty("border", "solid 0.5px");
	}
}
