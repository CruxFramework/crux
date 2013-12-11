package org.cruxframework.crux.widgets.client.grid;


/**
 * Defines the contract to implement an underlying table for the Grid.
 * 
 * @author Gesse Dafe
 */
public interface GridBaseFlexTable extends GridBaseTable 
{
	public void joinCells(int row);
}
