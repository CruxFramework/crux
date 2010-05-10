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
package br.com.sysmap.crux.widgets.client.grid;

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
    
    
    @Override
    protected Element getBodyElement()
    {
    	return super.getBodyElement();
    }
}
