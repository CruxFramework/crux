package br.com.sysmap.crux.ext.server.component;

import br.com.sysmap.crux.core.server.screen.Component;

public class TextArea extends Component
{
	int rows;

	public int getRows() 
	{
		return rows;
	}

	public void setRows(int rows) 
	{
		if (isCheckChanges() && this.rows != rows)
		{
			dirty = true;
		}
		this.rows = rows;
	}
}
