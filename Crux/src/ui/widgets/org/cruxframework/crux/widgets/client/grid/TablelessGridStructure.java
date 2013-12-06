package org.cruxframework.crux.widgets.client.grid;

/**
 * Tableless implementation to be used as grid's substructure element.
 * 
 * @author Gesse Dafe
 */
public class TablelessGridStructure extends AbstractTablelessGridStructure
{
	public TablelessGridStructure(AbstractGrid<?> grid) 
	{
		super(grid);
	}

	@Override
	protected boolean canFreezeColumns(int lineIndex) 
	{
		return true;
	}
}
