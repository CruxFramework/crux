package br.com.sysmap.crux.advanced.client.grid.model;

import com.google.gwt.user.client.Element;

public class GridHtmlTable extends com.google.gwt.user.client.ui.Grid
{	
	Element getCellElement(int row, int col)
	{
		return getCellElement(this.getElement(), row, col);
	}
	
	Element getRowElement(int row)
	{
		return getRowElement(this.getElement(), row);
	}
	
    private native Element getCellElement(Element table, int row, int col) /*-{
		return table.rows[row].cells[col];
    }-*/;
    
    private native Element getRowElement(Element table, int row) /*-{
    	return table.rows[row];
  }-*/;
}
