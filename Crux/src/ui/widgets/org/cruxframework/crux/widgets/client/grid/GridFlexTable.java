/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.widgets.client.grid;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class GridFlexTable extends FlexTable implements GridBaseFlexTable
{	
	public Element getCellElement(int row, int col)
	{
		return getCellElement(this.getElement(), row, col);
	}
	
	public Element getRowElement(int row)
	{
		return getRowElement(this.getElement(), row);
	}
	
	public void joinCells(int row)
	{
		TableRowElement tr =  this.getRowElement(row).cast();
		NodeList<TableCellElement> cells = tr.getCells();
		int numTds = cells.getLength();
		
		if(numTds > 1)
		{
			for (int i = 1; i < numTds; i++) 
			{
				// We always remove the second cell. 
				// This is because we want to keep the first one 
				//    and the cell indexes are in movement due 
				//    to the removing process.
				tr.removeChild(cells.getItem(1)); 
			}
			
			TableCellElement td = this.getCellElement(row, 0).cast();
			td.setAttribute("colSpan", ""+ numTds);
			td.setInnerHTML(".");
		}
	}
	
    private native Element getCellElement(Element table, int row, int col) /*-{
		return table.rows[row].cells[col];
    }-*/;
    
    private native Element getRowElement(Element table, int row) /*-{
    	return table.rows[row];
  	}-*/;  
    
    @Override
    public Element getBodyElement()
    {
    	return super.getBodyElement();
    }

	public void resize(int rowCount, int columnCount) 
	{
		int lastRow = rowCount - 1;
		int lastCell = columnCount - 1;
		removeAllRows();
		prepareRow(lastRow);
		for(int i = 0; i <= lastRow; i++)
		{
			prepareCell(i, lastCell);
		}
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#setCellAlignment(int, int, com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant, com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant)
	 */
	public void setCellAlignment(int index, int colIndex,
			HorizontalAlignmentConstant horizontalAlign,
			VerticalAlignmentConstant verticalAlign) 
	{
		getCellFormatter().setAlignment(index, colIndex, horizontalAlign, verticalAlign);
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.GridBaseTable#setCellWidth(int, int, java.lang.String)
	 */
	public void setCellWidth(int index, int colIndex, String width) 
	{
		getCellFormatter().setWidth(index, colIndex, width);
	}
	
	public void onAfterRender() 
	{
		// nothing do to
	}
}
